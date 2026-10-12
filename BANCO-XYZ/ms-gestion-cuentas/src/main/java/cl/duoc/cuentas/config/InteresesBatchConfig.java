package cl.duoc.cuentas.config;

import cl.duoc.cuentas.dto.LegacyDataDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class InteresesBatchConfig {

    @Value("${batch.data.base-path:/data}")
    private String basePath;

    @Bean
    public TaskExecutor interesesTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("batch_cuentas_");
        executor.setConcurrencyLimit(5);
        return executor;
    }

    // =========================================================================
    // WRITERS
    // =========================================================================
    
    // Writer 1: Para Step 1 (SÍ actualiza el saldo real de la cuenta)
    @Bean
    public JdbcBatchItemWriter<LegacyDataDTO> cuentasWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LegacyDataDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO cuentas (id, numero_cuenta, cliente_id, monto, tipo_cuenta) " +
                     "VALUES (:cuentaId, CONCAT('CTA-', :cuentaId), :cuentaId, IFNULL(:saldo, 10000), UPPER(IFNULL(:tipo, 'AHORRO'))) " +
                     "ON DUPLICATE KEY UPDATE monto = VALUES(monto)")
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    // Writer 2: Para Steps 2 y 3 (Solo crea la cuenta si NO existe; ignora si ya existe)
    @Bean
    public JdbcBatchItemWriter<LegacyDataDTO> cuentasFallbackWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LegacyDataDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT IGNORE INTO cuentas (id, numero_cuenta, cliente_id, monto, tipo_cuenta) " +
                     "VALUES (:cuentaId, CONCAT('CTA-', :cuentaId), :cuentaId, 10000, 'AHORRO')")
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    // =========================================================================
    // STEP 1: INTERESES TRIMESTRALES (cuenta_id, nombre, saldo, edad, tipo)
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<LegacyDataDTO> interesesReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "intereses_trimestrales.csv");

        return new FlatFileItemReaderBuilder<LegacyDataDTO>()
                .name("interesesReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LegacyDataDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepIntereses(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              FlatFileItemReader<LegacyDataDTO> interesesReader,
                              JdbcBatchItemWriter<LegacyDataDTO> cuentasWriter) {
        return new StepBuilder("stepIntereses", jobRepository)
                .<LegacyDataDTO, LegacyDataDTO>chunk(100, transactionManager)
                .reader(interesesReader)
                .writer(cuentasWriter)
                .taskExecutor(interesesTaskExecutor())
                .build();
    }

    // =========================================================================
    // STEP 2: ESTADOS FINANCIEROS (cuenta_id, fecha, transaccion, monto, descripcion)
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<LegacyDataDTO> estadosAnualesCuentasReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "estados_financieros_anuales.csv");

        return new FlatFileItemReaderBuilder<LegacyDataDTO>()
                .name("estadosAnualesCuentasReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                // Evitamos mapear "descripcion" a "edad" para impedir NumberFormatException
                .names("cuentaId", "nombre", "tipo", "saldo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LegacyDataDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepEstadosAnualesCuentas(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                          FlatFileItemReader<LegacyDataDTO> estadosAnualesCuentasReader,
                                          JdbcBatchItemWriter<LegacyDataDTO> cuentasFallbackWriter) {
        return new StepBuilder("stepEstadosAnualesCuentas", jobRepository)
                .<LegacyDataDTO, LegacyDataDTO>chunk(100, transactionManager)
                .reader(estadosAnualesCuentasReader)
                .writer(cuentasFallbackWriter) // Usamos Fallback para NO pisar el saldo
                .taskExecutor(interesesTaskExecutor())
                .build();
    }

    // =========================================================================
    // STEP 3: MOVIMIENTOS DIARIOS (id, fecha, monto, tipo)
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<LegacyDataDTO> movimientosDiariosCuentasReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "movimientos_financieros_diarios.csv");

        return new FlatFileItemReaderBuilder<LegacyDataDTO>()
                .name("movimientosDiariosCuentasReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "nombre", "saldo", "tipo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(LegacyDataDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepMovimientosDiariosCuentas(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                              FlatFileItemReader<LegacyDataDTO> movimientosDiariosCuentasReader,
                                              JdbcBatchItemWriter<LegacyDataDTO> cuentasFallbackWriter) {
        return new StepBuilder("stepMovimientosDiariosCuentas", jobRepository)
                .<LegacyDataDTO, LegacyDataDTO>chunk(100, transactionManager)
                .reader(movimientosDiariosCuentasReader)
                .writer(cuentasFallbackWriter) // Usamos Fallback para NO pisar el saldo
                .taskExecutor(interesesTaskExecutor())
                .build();
    }

    // =========================================================================
    // JOBS MULTI-STEP
    // =========================================================================
    @Bean
    public Job importInteresesJob(JobRepository jobRepository, Step stepIntereses) {
        return new JobBuilder("importInteresesJob", jobRepository)
                .start(stepIntereses)
                .build();
    }

    @Bean
    public Job importCargaCompletaJob(JobRepository jobRepository, 
                                      Step stepIntereses, 
                                      Step stepEstadosAnualesCuentas, 
                                      Step stepMovimientosDiariosCuentas) {
        return new JobBuilder("importCargaCompletaJob", jobRepository)
                .start(stepIntereses)
                .next(stepEstadosAnualesCuentas)
                .next(stepMovimientosDiariosCuentas)
                .build();
    }

    private Resource resolverRecurso(String semana, String nombreArchivo) {
        String carpeta = (semana != null && !semana.trim().isEmpty()) ? semana : "semana_1";
        String separador = basePath.endsWith("/") ? "" : "/";
        File fileExterno = new File(basePath + separador + carpeta + "/" + nombreArchivo);

        if (fileExterno.exists()) {
            return new FileSystemResource(fileExterno);
        }
        return new ClassPathResource("input/" + nombreArchivo);
    }
}
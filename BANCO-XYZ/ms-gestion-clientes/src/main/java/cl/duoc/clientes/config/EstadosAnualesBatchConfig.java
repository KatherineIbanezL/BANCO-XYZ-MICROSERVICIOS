package cl.duoc.clientes.config;

import cl.duoc.clientes.dto.EstadoAnualDTO;
import cl.duoc.clientes.model.EstadoFinanciero;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class EstadosAnualesBatchConfig {

    @Value("${batch.data.base-path:/data}")
    private String basePath;

    @Bean
    public TaskExecutor clientesTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("batch_clientes_");
        executor.setConcurrencyLimit(5);
        return executor;
    }

    // =========================================================================
    // STEP 1: LECTURA Y CARGA DE ESTADOS FINANCIEROS Y CLIENTES BASE
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<EstadoAnualDTO> estadosAnualesReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "estados_financieros_anuales.csv");

        return new FlatFileItemReaderBuilder<EstadoAnualDTO>()
                .name("estadosAnualesReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(EstadoAnualDTO.class);
                }})
                .strict(false) // Permite continuar si la ruta no está disponible
                .saveState(false)
                .build();
    }

    @Bean
    public ItemProcessor<EstadoAnualDTO, EstadoFinanciero> estadosAnualesProcessor() {
        return dto -> {
            if (dto.getMonto() == null || dto.getMonto() <= 0) {
                return null;
            }

            LocalDate fechaFormateada = normalizarFecha(dto.getFecha());
            if (fechaFormateada == null) {
                return null;
            }

            EstadoFinanciero ef = new EstadoFinanciero();
            ef.setClienteId(dto.getCuentaId());
            ef.setMonto(dto.getMonto());
            ef.setTipoTransaccion(dto.getTransaccion() != null ? dto.getTransaccion().toUpperCase() : "DESCONOCIDO");
            ef.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : "Sin descripción");
            ef.setFecha(fechaFormateada);

            return ef;
        };
    }

    @Bean
    public JdbcBatchItemWriter<EstadoFinanciero> clienteWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<EstadoFinanciero>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT IGNORE INTO clientes (id, nombre, edad) VALUES (:clienteId, CONCAT('Cliente ', :clienteId), 30)")
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<EstadoFinanciero> clienteRealWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<EstadoFinanciero>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO clientes (id, nombre, edad) VALUES (:clienteId, :descripcion, :monto) " +
                     "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), edad = VALUES(edad)")
                .dataSource(dataSource)
                .assertUpdates(false)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<EstadoFinanciero> estadosAnualesWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<EstadoFinanciero>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO estados_financieros (cliente_id, monto, tipo_transaccion, descripcion, fecha) " +
                     "VALUES (:clienteId, :monto, :tipoTransaccion, :descripcion, :fecha)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public CompositeItemWriter<EstadoFinanciero> compositeWriter(
            JdbcBatchItemWriter<EstadoFinanciero> clienteWriter,
            JdbcBatchItemWriter<EstadoFinanciero> estadosAnualesWriter) {
        return new CompositeItemWriterBuilder<EstadoFinanciero>()
                .delegates(Arrays.asList(clienteWriter, estadosAnualesWriter))
                .build();
    }

    @Bean
    public Step stepEstadosAnuales(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                   FlatFileItemReader<EstadoAnualDTO> estadosAnualesReader, 
                                   ItemProcessor<EstadoAnualDTO, EstadoFinanciero> estadosAnualesProcessor,
                                   CompositeItemWriter<EstadoFinanciero> compositeWriter) {
        return new StepBuilder("stepEstadosAnuales", jobRepository)
                .<EstadoAnualDTO, EstadoFinanciero>chunk(100, transactionManager)
                .reader(estadosAnualesReader)
                .processor(estadosAnualesProcessor)
                .writer(compositeWriter)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NumberFormatException.class)
                .skipLimit(100)
                .retry(org.springframework.dao.DataAccessException.class)
                .retryLimit(3)
                .taskExecutor(clientesTaskExecutor())
                .build();
    }

    // =========================================================================
    // STEP 2: LECTURA Y REGISTRO DE CLIENTES DESDE INTERESES TRIMESTRALES
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<EstadoAnualDTO> interesesReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "intereses_trimestrales.csv");

        return new FlatFileItemReaderBuilder<EstadoAnualDTO>()
                .name("interesesReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                // Mapeo real: cuenta_id -> cuentaId, nombre -> descripcion, saldo -> monto, edad -> transaccion, tipo -> fecha(se ignora)
                .names("cuentaId", "descripcion", "monto", "transaccion", "fecha") 
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(EstadoAnualDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepIntereses(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              FlatFileItemReader<EstadoAnualDTO> interesesReader,
                              JdbcBatchItemWriter<EstadoFinanciero> clienteRealWriter) { // Usaremos un nuevo writer
        return new StepBuilder("stepIntereses", jobRepository)
                .<EstadoAnualDTO, EstadoFinanciero>chunk(100, transactionManager)
                .reader(interesesReader)
                .processor(dto -> {
                    if (dto.getCuentaId() == null) return null;
                    EstadoFinanciero ef = new EstadoFinanciero();
                    ef.setClienteId(dto.getCuentaId());
                    
                    // Rescatamos el nombre real (vino en 'descripcion')
                    ef.setDescripcion(dto.getDescripcion() != null && !dto.getDescripcion().isBlank() ? dto.getDescripcion() : "Cliente " + dto.getCuentaId());
                    
                    // Rescatamos la edad (vino en 'transaccion')
                    try {
                        ef.setMonto(Double.parseDouble(dto.getTransaccion())); // Usaremos monto temporalmente para pasar la edad
                    } catch (Exception e) {
                        ef.setMonto(30.0); // Edad por defecto
                    }
                    
                    return ef;
                })
                .writer(clienteRealWriter) // <-- Llamamos al nuevo writer
                .taskExecutor(clientesTaskExecutor())
                .build();
    }

    // =========================================================================
    // STEP 3: LECTURA Y REGISTRO DE CLIENTES DESDE MOVIMIENTOS DIARIOS
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<EstadoAnualDTO> movimientosDiariosReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "movimientos_financieros_diarios.csv");

        return new FlatFileItemReaderBuilder<EstadoAnualDTO>()
                .name("movimientosDiariosReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                // Columnas reales de movimientos_financieros_diarios.csv: id, fecha, monto, tipo
                .names("cuentaId", "fecha", "monto", "transaccion")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(EstadoAnualDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepMovimientosDiarios(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                      FlatFileItemReader<EstadoAnualDTO> movimientosDiariosReader,
                                      JdbcBatchItemWriter<EstadoFinanciero> clienteWriter) {
        return new StepBuilder("stepMovimientosDiarios", jobRepository)
                .<EstadoAnualDTO, EstadoFinanciero>chunk(100, transactionManager)
                .reader(movimientosDiariosReader)
                .processor(dto -> {
                    if (dto.getCuentaId() == null) return null;
                    EstadoFinanciero ef = new EstadoFinanciero();
                    ef.setClienteId(dto.getCuentaId());
                    return ef;
                })
                .writer(clienteWriter)
                .taskExecutor(clientesTaskExecutor())
                .build();
    }

    // =========================================================================
    // JOBS DISPONIBLES (MULTI-STEP SEQUENTIAL)
    // =========================================================================
    @Bean
    public Job importEstadosAnualesJob(JobRepository jobRepository, Step stepEstadosAnuales) {
        return new JobBuilder("importEstadosAnualesJob", jobRepository)
                .start(stepEstadosAnuales)
                .build();
    }

    @Bean
    public Job importCargaCompletaJob(JobRepository jobRepository, 
                                      Step stepEstadosAnuales, 
                                      Step stepIntereses, 
                                      Step stepMovimientosDiarios) {
        return new JobBuilder("importCargaCompletaJob", jobRepository)
                .start(stepEstadosAnuales)
                .next(stepIntereses)
                .next(stepMovimientosDiarios)
                .build();
    }

    private LocalDate normalizarFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) return null;

        String[] patrones = {"yyyy/MM/dd", "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"};
        for (String patron : patrones) {
            try {
                return LocalDate.parse(fechaStr.trim(), DateTimeFormatter.ofPattern(patron));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private Resource resolverRecurso(String semana, String nombreArchivo) {
        String carpeta = (semana != null && !semana.trim().isEmpty()) ? semana : "semana_1";
        String separador = basePath.endsWith("/") ? "" : "/";
        String rutaCompleta = basePath + separador + carpeta + "/" + nombreArchivo;
        
        File fileExterno = new File(rutaCompleta);

        if (fileExterno.exists()) {
            return new FileSystemResource(fileExterno);
        }
        return new ClassPathResource("input/" + nombreArchivo);
    }
}
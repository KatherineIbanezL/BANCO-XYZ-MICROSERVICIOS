package cl.duoc.pagos.config;

import cl.duoc.pagos.dto.MovimientoDiarioDTO;
import cl.duoc.pagos.model.Transaccion;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class TransaccionesDiariasBatchConfig {

    @Value("${batch.data.base-path:/data}")
    private String basePath;

    @Bean
    public TaskExecutor pagosTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("batch_pagos_");
        executor.setConcurrencyLimit(5);
        return executor;
    }

    // =========================================================================
    // STEP 1: MOVIMIENTOS DIARIOS (id, fecha, monto, tipo)
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<MovimientoDiarioDTO> diarioReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "movimientos_financieros_diarios.csv");

        return new FlatFileItemReaderBuilder<MovimientoDiarioDTO>()
                .name("diarioItemReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(MovimientoDiarioDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public ItemProcessor<MovimientoDiarioDTO, Transaccion> diarioProcessor() {
        return dto -> {
            if (dto.getMonto() == null || dto.getMonto() <= 0) {
                return null;
            }

            LocalDateTime fechaTransaccion = normalizarFecha(dto.getFecha());
            if (fechaTransaccion == null) {
                fechaTransaccion = LocalDateTime.now();
            }

            Transaccion tx = new Transaccion();
            if (dto.getId() != null && !dto.getId().trim().isEmpty()) {
                tx.setCuentaId(Long.parseLong(dto.getId().trim()));
            }
            tx.setMonto(dto.getMonto());
            tx.setTipo(dto.getTipo() != null ? dto.getTipo().toUpperCase() : "DESCONOCIDO");
            tx.setFecha(fechaTransaccion);
            tx.setEstado("COMPLETADA");

            return tx;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Transaccion> diarioWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaccion>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO transacciones (cuenta_id, monto, tipo, fecha, estado) VALUES (:cuentaId, :monto, :tipo, :fecha, :estado)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step stepMovimientosDiarios(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                      FlatFileItemReader<MovimientoDiarioDTO> diarioReader, 
                                      ItemProcessor<MovimientoDiarioDTO, Transaccion> diarioProcessor,
                                      JdbcBatchItemWriter<Transaccion> diarioWriter) {
        return new StepBuilder("stepMovimientosDiarios", jobRepository)
                .<MovimientoDiarioDTO, Transaccion>chunk(100, transactionManager)
                .reader(diarioReader)
                .processor(diarioProcessor)
                .writer(diarioWriter)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NumberFormatException.class)
                .skipLimit(300)
                .taskExecutor(pagosTaskExecutor())
                .build();
    }

    // =========================================================================
    // STEP 2: ESTADOS FINANCIEROS (cuenta_id, fecha, transaccion, monto, descripcion)
    // =========================================================================
    @Bean
    @StepScope
    public FlatFileItemReader<MovimientoDiarioDTO> estadosAnualesPagosReader(
            @Value("#{jobParameters['semana']}") String semana) {

        Resource resource = resolverRecurso(semana, "estados_financieros_anuales.csv");

        return new FlatFileItemReaderBuilder<MovimientoDiarioDTO>()
                .name("estadosAnualesPagosReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                // Se agregan los 5 nombres para coincidir exactamente con el CSV
                .names("id", "fecha", "tipo", "monto", "descripcion")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(MovimientoDiarioDTO.class);
                }})
                .strict(false)
                .saveState(false)
                .build();
    }

    @Bean
    public Step stepEstadosAnualesPagos(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                        FlatFileItemReader<MovimientoDiarioDTO> estadosAnualesPagosReader,
                                        ItemProcessor<MovimientoDiarioDTO, Transaccion> diarioProcessor,
                                        JdbcBatchItemWriter<Transaccion> diarioWriter) {
        return new StepBuilder("stepEstadosAnualesPagos", jobRepository)
                .<MovimientoDiarioDTO, Transaccion>chunk(100, transactionManager)
                .reader(estadosAnualesPagosReader)
                .processor(diarioProcessor)
                .writer(diarioWriter)
                .faultTolerant() 
                .skip(FlatFileParseException.class)
                .skip(NumberFormatException.class)
                .skipLimit(300)
                .taskExecutor(pagosTaskExecutor())
                .build();
    }

    // =========================================================================
    // JOBS MULTI-STEP
    // =========================================================================
    @Bean
    public Job importTransaccionesDiariasJob(JobRepository jobRepository, Step stepMovimientosDiarios) {
        return new JobBuilder("importTransaccionesDiariasJob", jobRepository)
                .start(stepMovimientosDiarios)
                .build();
    }

    @Bean
    public Job importCargaCompletaJob(JobRepository jobRepository, 
                                      Step stepMovimientosDiarios, 
                                      Step stepEstadosAnualesPagos) {
        return new JobBuilder("importCargaCompletaJob", jobRepository)
                .start(stepMovimientosDiarios)
                .next(stepEstadosAnualesPagos)
                .build();
    }

    private LocalDateTime normalizarFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) return null;
        String[] patrones = {"yyyy/MM/dd", "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"};
        for (String patron : patrones) {
            try {
                LocalDate date = LocalDate.parse(fechaStr.trim(), DateTimeFormatter.ofPattern(patron));
                return date.atStartOfDay();
            } catch (Exception ignored) {}
        }
        return null;
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
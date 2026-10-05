package cl.duoc.core.config;

import cl.duoc.core.model.Transaccion;
import cl.duoc.core.repository.TransaccionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransaccionesBatchConfig {

    @Bean
    public FlatFileItemReader<Transaccion> transaccionReader() {
        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(new ClassPathResource("input/transacciones.csv"))
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Transaccion.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Transaccion> transaccionWriter(TransaccionRepository repository) {
        RepositoryItemWriter<Transaccion> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step transaccionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                FlatFileItemReader<Transaccion> transaccionReader, RepositoryItemWriter<Transaccion> transaccionWriter) {
        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, Transaccion>chunk(10, transactionManager)
                .reader(transaccionReader)
                .writer(transaccionWriter)
                .build();
    }

    @Bean(name = "importTransaccionesJob")
    public Job importTransaccionesJob(JobRepository jobRepository, Step transaccionStep) {
        return new JobBuilder("importTransaccionesJob", jobRepository)
                .start(transaccionStep)
                .build();
    }
}
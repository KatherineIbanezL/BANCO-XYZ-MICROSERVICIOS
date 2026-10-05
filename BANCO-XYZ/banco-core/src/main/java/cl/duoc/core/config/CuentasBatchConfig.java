package cl.duoc.core.config;

import cl.duoc.core.model.Cuenta;
import cl.duoc.core.repository.CuentaRepository;
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
public class CuentasBatchConfig {

    @Bean
    public FlatFileItemReader<Cuenta> cuentaReader() {
        return new FlatFileItemReaderBuilder<Cuenta>()
                .name("cuentaReader")
                .resource(new ClassPathResource("input/cuentas_anuales.csv"))
                .delimited()
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Cuenta.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Cuenta> cuentaWriter(CuentaRepository repository) {
        RepositoryItemWriter<Cuenta> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step cuentaStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           FlatFileItemReader<Cuenta> cuentaReader, RepositoryItemWriter<Cuenta> cuentaWriter) {
        return new StepBuilder("cuentaStep", jobRepository)
                .<Cuenta, Cuenta>chunk(10, transactionManager)
                .reader(cuentaReader)
                .writer(cuentaWriter)
                .build();
    }

    @Bean(name = "importCuentasJob")
    public Job importCuentasJob(JobRepository jobRepository, Step cuentaStep) {
        return new JobBuilder("importCuentasJob", jobRepository)
                .start(cuentaStep)
                .build();
    }
}
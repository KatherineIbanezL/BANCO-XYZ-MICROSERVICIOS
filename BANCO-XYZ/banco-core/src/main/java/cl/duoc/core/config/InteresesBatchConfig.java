package cl.duoc.core.config;

import cl.duoc.core.model.Interes;
import cl.duoc.core.repository.InteresRepository;
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
public class InteresesBatchConfig {

    @Bean
    public FlatFileItemReader<Interes> interesReader() {
        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader")
                .resource(new ClassPathResource("input/intereses.csv"))
                .delimited()
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Interes.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Interes> interesWriter(InteresRepository repository) {
        RepositoryItemWriter<Interes> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step interesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            FlatFileItemReader<Interes> interesReader, RepositoryItemWriter<Interes> interesWriter) {
        return new StepBuilder("interesStep", jobRepository)
                .<Interes, Interes>chunk(10, transactionManager)
                .reader(interesReader)
                .writer(interesWriter)
                .build();
    }

    @Bean(name = "importInteresesJob")
    public Job importInteresesJob(JobRepository jobRepository, Step interesStep) {
        return new JobBuilder("importInteresesJob", jobRepository)
                .start(interesStep)
                .build();
    }
}
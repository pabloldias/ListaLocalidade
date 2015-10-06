package batch;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Bean
	public ItemReader<Localidade> reader(DataSource dataSource) {
		JdbcCursorItemReader<Localidade> itemReader = new JdbcCursorItemReader<Localidade>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("select id, uf, localidade, cep, tipo from cidade");
		itemReader.setRowMapper(new LocalidadeMapper());
		return itemReader;
	}
	
	@Bean
	public ItemProcessor<Localidade, Localidade> processor() {
		return new LocalidadeProcessor();
	}
	
	@Bean
	public ItemWriter<Localidade> writer() {
		FlatFileItemWriter<Localidade> itemWriter = new FlatFileItemWriter<Localidade>();
		itemWriter.setResource(new FileSystemResource(new File("target/lista_localidade.csv")));
		itemWriter.setLineAggregator(new DelimitedLineAggregator<Localidade>() {{
			setDelimiter(",");
			setFieldExtractor(new BeanWrapperFieldExtractor<Localidade>() {{
				setNames(new String[] { "id", "uf", "cep", "nome", "tipo" });
			}});
		}});
		return itemWriter;
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource driver = new DriverManagerDataSource();
		driver.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		driver.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
		driver.setUsername("**********");
		driver.setPassword("**********");
		return driver;
	}
	
	@Bean
	public Job importUserJob(JobBuilderFactory factory, Step step, JobExecutionListener listener) {
		
		return factory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step)
				.end()
				.build();
	}
	
	@Bean
	public Step step(StepBuilderFactory factory, ItemReader<Localidade> reader,
			ItemWriter<Localidade> writer, ItemProcessor<Localidade, Localidade> processor) {
		
		return factory.get("step")
				.<Localidade, Localidade> chunk(10)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}

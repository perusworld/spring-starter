package com.yosanai.spring.starter.samplebatch;

import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.yosanai.spring.starter.sampledata.model.CustomerOrder;

@Configuration
@EnableBatchProcessing
@Import(DataSourceAutoConfiguration.class)
public class BatchConfiguration {

	public static final String EXPORT_CUSTOMER_ORDER_JOB = "exportCustomerOrder";

	public static final String EXPORT_CUSTOMER_ORDER_PATH_KEY = "output";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public CustomerOrderProcessor processor;

	@Bean
	public TaskExecutor jobTaskExecutor() {
		return new SimpleAsyncTaskExecutor("job-thread");
	}

	@Bean
	public JobLauncher asyncJobLauncher(@Autowired @Qualifier("jobTaskExecutor") TaskExecutor taskExecutor,
			@Autowired JobRepository jobRepository) throws Exception {
		SimpleJobLauncher ret = new SimpleJobLauncher();
		ret.setJobRepository(jobRepository);
		ret.setTaskExecutor(taskExecutor);
		ret.afterPropertiesSet();
		return ret;
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(@Autowired JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
		postProcessor.setJobRegistry(jobRegistry);
		return postProcessor;
	}

	@Bean
	public CountDownLatch jobDoneLatch(@Value("${samplebatch.latch}") int max) {
		return new CountDownLatch(max);
	}

	@Bean
	public JpaPagingItemReader<CustomerOrder> reader(@Autowired EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<CustomerOrder>().name("customer-order-reader")
				.queryString("Select co from CustomerOrder co").entityManagerFactory(entityManagerFactory).build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<CustomerOrder> writer(@Value("file://#{jobParameters['output']}") Resource out) {
		DelimitedLineAggregator<CustomerOrder> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		BeanWrapperFieldExtractor<CustomerOrder> beanWrapper = new BeanWrapperFieldExtractor<>();
		beanWrapper.setNames(new String[] { "id", "customer.firstName", "customer.lastName", "totalCost" });
		lineAggregator.setFieldExtractor(beanWrapper);
		return new FlatFileItemWriterBuilder<CustomerOrder>().name("customer-order-csv-writer").resource(out)
				.lineAggregator(lineAggregator).build();
	}

	@Bean
	public Job exportCustomerOrder(@Autowired CustomerOrderJobListener listener, Step step1) {
		return jobBuilderFactory.get(EXPORT_CUSTOMER_ORDER_JOB).incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1).end().build();
	}

	@Bean
	public Step step1(@Autowired FlatFileItemWriter<CustomerOrder> writer,
			@Autowired JpaPagingItemReader<CustomerOrder> reader, @Autowired CustomerOrderProcessor processor,
			@Autowired CustomerOrderStepListener listener) {
		return stepBuilderFactory.get("step1").<CustomerOrder, CustomerOrder>chunk(10).reader(reader)
				.processor(processor).writer(writer).listener(listener).build();
	}
}
package com.yosanai.spring.starter.samplebatch.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.yosanai.spring.starter.samplebatch.BatchConfiguration;
import com.yosanai.spring.starter.samplebatch.ExportFilenamePathGenerator;

@Configuration
@ConditionalOnProperty(name = "samplebatch.quartz.enable")
public class QuartzConfig {

	public static final String JOB_NAME = "jobName";
	public static final String JOB_LOCATOR = "jobLocator";
	public static final String JOB_LAUNCHER = "jobLauncher";
	public static final String PATH_GENERATOR = "pathGenerator";

	@Value("${samplebatch.cronExpression}")
	public String cronExpression;

	@Autowired
	private JobLocator jobLocator;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	ExportFilenamePathGenerator pathGenerator;

	@Bean
	public JobDetailFactoryBean jobDetail() {
		JobDetailFactoryBean ret = new JobDetailFactoryBean();
		ret.setGroup("quartz-batch");
		ret.setJobClass(JobLauncherDetails.class);
		ret.setDurability(true);
		Map<String, Object> map = new HashMap<>();
		map.put(JOB_NAME, BatchConfiguration.EXPORT_CUSTOMER_ORDER_JOB);
		map.put(JOB_LOCATOR, jobLocator);
		map.put(JOB_LAUNCHER, jobLauncher);
		map.put(PATH_GENERATOR, pathGenerator);
		ret.setJobDataAsMap(map);
		return ret;
	}

	@Bean
	public CronTriggerFactoryBean cronTrigger(@Autowired @Qualifier("jobDetail") JobDetail jobDetail) {
		CronTriggerFactoryBean ret = new CronTriggerFactoryBean();
		ret.setCronExpression(cronExpression);
		ret.setJobDetail(jobDetail);
		return ret;
	}

	@Bean
	public SchedulerFactoryBean jobScheduler(@Autowired @Qualifier("cronTrigger") CronTrigger cronTrigger,
			@Autowired @Qualifier("jobDetail") JobDetail jobDetail) {
		SchedulerFactoryBean ret = new SchedulerFactoryBean();
		ret.setTriggers(cronTrigger);
		ret.setJobDetails(jobDetail);
		return ret;
	}

}

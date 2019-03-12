package com.yosanai.spring.starter.samplebatch.quartz;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.yosanai.spring.starter.samplebatch.BatchConfiguration;
import com.yosanai.spring.starter.samplebatch.ExportFilenamePathGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobLauncherDetails extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Map<String, Object> jobDataMap = context.getMergedJobDataMap();
		String jobName = (String) jobDataMap.get(QuartzConfig.JOB_NAME);
		log.info("Quartz trigger firing with Spring Batch jobName=" + jobName);
		JobLauncher jobLauncher = (JobLauncher) jobDataMap.get(QuartzConfig.JOB_LAUNCHER);
		JobLocator jobLocator = (JobLocator) jobDataMap.get(QuartzConfig.JOB_LOCATOR);
		JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap);
		try {
			jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
		} catch (Exception e) {
			log.error("Could not execute job.", e);
			throw new JobExecutionException(e);
		}
	}

	private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {

		JobParametersBuilder builder = new JobParametersBuilder();

		for (Entry<String, Object> entry : jobDataMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String && !key.equals(QuartzConfig.JOB_NAME)) {
				builder.addString(key, (String) value);
			} else if (value instanceof Float || value instanceof Double) {
				builder.addDouble(key, ((Number) value).doubleValue());
			} else if (value instanceof Integer || value instanceof Long) {
				builder.addLong(key, ((Number) value).longValue());
			} else if (value instanceof Date) {
				builder.addDate(key, (Date) value);
			} else if (key.equals(QuartzConfig.PATH_GENERATOR)) {
				ExportFilenamePathGenerator pathGenerator = (ExportFilenamePathGenerator) value;
				builder.addString(BatchConfiguration.EXPORT_CUSTOMER_ORDER_PATH_KEY, pathGenerator.getNextPath(2));
			} else {
				log.debug("JobDataMap contains values which are not job parameters (ignoring).");
			}
		}
		return builder.toJobParameters();

	}

}

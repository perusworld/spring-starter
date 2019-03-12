package com.yosanai.spring.starter.samplebatch;

import java.util.concurrent.CountDownLatch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomerOrderJobListener extends JobExecutionListenerSupport {

	@Autowired
	@Qualifier("jobDoneLatch")
	private CountDownLatch latch;

	@Override
	public void afterJob(JobExecution jobExecution) {
		log.debug("Job Done {}", jobExecution.getStatus());
		latch.countDown();
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.debug("Job Starting");
		jobExecution.getExecutionContext().put("someKeyFromJob", "someValue");
	}

}

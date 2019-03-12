package com.yosanai.spring.starter.samplebatch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerOrderStepListener extends StepExecutionListenerSupport {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.debug("Before step");
		stepExecution.getExecutionContext().put("someKeyFromStep", "someValue");
	}

}

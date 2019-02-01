package com.yosanai.spring.starter.sampleamqpservice.integration;

import org.springframework.integration.annotation.MessagingGateway;

import com.yosanai.spring.starter.sampleapi.SampleRequest;

@MessagingGateway(defaultRequestChannel = "sampleSendChannel")
public interface SendSampleRequest {
	void send(SampleRequest request);

}

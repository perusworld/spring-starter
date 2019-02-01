package com.yosanai.spring.starter.sampleamqpservice.integration;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.sampleamqpservice.SampleRequestListener;
import com.yosanai.spring.starter.sampleamqpservice.TestConfig;
import com.yosanai.spring.starter.sampleapi.SampleRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@ActiveProfiles("test")
public class SampleRequestListenerSendTest {

	@Autowired
	private SampleRequestListener listener;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${sample.amqpservice.topicExchange}")
	private String topicExchange;

	@Value("${sample.amqpservice.routingKey}")
	private String routingKey;

	@Autowired
	private SendSampleRequest sender;

	@Test
	public void checkInit() {
		assertNotNull(listener);
		assertNotNull(rabbitTemplate);
	}

	protected String getRndString() {
		return RandomStringUtils.random(10, true, true);
	}

	protected Integer getRndNumber() {
		return Integer.parseInt(RandomStringUtils.random(5, false, true));
	}

	@Test
	public void checkSend() {
		SampleRequest request = new SampleRequest(getRndString(), getRndNumber(), new Date());
		sender.send(request);
		try {
			listener.getLatch().await(10000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
	}

}

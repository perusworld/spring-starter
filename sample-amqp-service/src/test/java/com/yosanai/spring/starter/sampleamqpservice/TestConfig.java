package com.yosanai.spring.starter.sampleamqpservice;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan(basePackages = "com.yosanai.spring.starter.sampleamqpservice")
public class TestConfig {
	@Value("${sample.amqpservice.queue}")
	private String sampleQueue;

	@Bean
	public MessageChannel sampleSendChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "sampleSendChannel")
	public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
		AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
		outbound.setRoutingKey(sampleQueue);
		return outbound;
	}

}

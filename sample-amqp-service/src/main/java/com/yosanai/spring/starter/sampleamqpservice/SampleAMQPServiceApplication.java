package com.yosanai.spring.starter.sampleamqpservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

import com.yosanai.spring.starter.sampleapi.DefaultSampleAPI;
import com.yosanai.spring.starter.sampleapi.SampleAPI;

@SpringBootApplication
public class SampleAMQPServiceApplication {

	@Value("${sample.amqpservice.topicExchange}")
	private String topicExchange;

	@Value("${sample.amqpservice.queue}")
	private String sampleQueue;

	@Value("${sample.amqpservice.routingKey}")
	private String routingKey;

	@Value("${sample.amqpservice.concurrent}")
	private int concurrent;

	@Bean
	public SampleAPI sampleAPI() {
		return new DefaultSampleAPI();
	}

	@Bean
	Queue sampleQueue() {
		return new Queue(sampleQueue, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchange);
	}

	@Bean
	Binding sampleBinding(@Qualifier("sampleQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(sampleQueue);
		container.setConcurrentConsumers(concurrent);
		return container;
	}

	@Bean
	public MessageChannel sampleReceiverChannel() {
		return new DirectChannel();
	}

	@Bean
	public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer,
			@Qualifier("sampleReceiverChannel") MessageChannel channel) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
		adapter.setOutputChannel(channel);
		return adapter;
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleAMQPServiceApplication.class, args);
	}

}

package com.yosanai.spring.starter.sampleemailservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mail.MailSendingMessageHandler;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@EnableAutoConfiguration
@SpringBootConfiguration
public class SendMailConfig {

	@Value("${sampleemail.sendmail.queue}")
	private String sendMailQueue;

	@Value("${sampleemail.sendmail.routingKey}")
	private String sendMailRoutingKey;

	@Value("${sampleemail.concurrent}")
	private int concurrent;

	@Autowired
	private JavaMailSender mailSender;

	@Bean
	Queue sendMailQueue() {
		return new Queue(sendMailQueue, false);
	}

	@Bean
	Binding sendMailBinding(@Qualifier("sendMailQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(sendMailRoutingKey);
	}

	@Bean
	SimpleMessageListenerContainer sendMailContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(sendMailQueue);
		container.setConcurrentConsumers(concurrent);
		return container;
	}

	@Bean
	public MessageChannel sampleReceiverChannel() {
		return new DirectChannel();
	}

	@Bean
	public AmqpInboundChannelAdapter sendMailInbound(
			@Qualifier("sendMailContainer") SimpleMessageListenerContainer listenerContainer,
			@Qualifier("sampleReceiverChannel") MessageChannel channel) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
		adapter.setOutputChannel(channel);
		return adapter;
	}

	@Bean
	public MessageChannel smtpChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "smtpChannel", outputChannel = "nullChannel")
	public MessageHandler mailsSenderMessagingHandler(Message<MailMessage> message) {
		MailSendingMessageHandler mailSendingMessageHandler = new MailSendingMessageHandler(mailSender);
		mailSendingMessageHandler.handleMessage(message);
		return mailSendingMessageHandler;
	}

}

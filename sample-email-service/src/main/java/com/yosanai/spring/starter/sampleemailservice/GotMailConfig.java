package com.yosanai.spring.starter.sampleemailservice;

import javax.mail.internet.MimeMessage;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageChannel;

import com.yosanai.spring.starter.sampleemailservice.mail.AllSearchTermStrategy;

@EnableAutoConfiguration
@SpringBootConfiguration
public class GotMailConfig {

	@Value("${sampleemail.topicExchange}")
	private String topicExchange;

	@Value("${sampleemail.concurrent}")
	private int concurrent;

	@Value("${sampleemail.gotmail.queue}")
	private String gotMailQueue;

	@Value("${sampleemail.gotmail.routingKey}")
	private String gotMailRoutingKey;

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchange);
	}

	@Bean
	Queue gotMailQueue() {
		return new Queue(gotMailQueue, false);
	}

	@Bean
	Binding gotMailBinding(@Qualifier("gotMailQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(gotMailRoutingKey);
	}

	@Bean
	public MessageChannel sampleGotMailChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "sampleGotMailChannel")
	public AmqpOutboundEndpoint gotMailOutbound(AmqpTemplate amqpTemplate) {
		AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
		outbound.setRoutingKey(gotMailQueue);
		return outbound;
	}

	@Bean
	@ConditionalOnProperty("sampleemail.imap")
	public MailReceiver sampleMailReceiver(@Value("${sampleemail.url}") String storeUrl,
			HeaderMapper<MimeMessage> mapper) {
		ImapMailReceiver imapMailReceiver = new ImapMailReceiver(storeUrl);
		imapMailReceiver.setHeaderMapper(mapper);
		imapMailReceiver.setSearchTermStrategy(new AllSearchTermStrategy());
		return imapMailReceiver;
	}

	@Bean
	@InboundChannelAdapter(value = "testReceiveEmailChannel", poller = @Poller(fixedDelay = "${sampleemail.gotmail.pollInterval}"))
	public MessageSource<Object> mailMessageSource(@Qualifier("sampleMailReceiver") MailReceiver mailReceiver) {
		MailReceivingMessageSource mailReceivingMessageSource = new MailReceivingMessageSource(mailReceiver);
		return mailReceivingMessageSource;
	}

	@Bean(name = "testReceiveEmailChannel")
	public MessageChannel mailHandler() {
		return new DirectChannel();
	}

}

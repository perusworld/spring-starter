package com.yosanai.spring.starter.sampleemailservice.incoming;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.SocketUtils;
import org.subethamail.wiser.Wiser;

import com.yosanai.spring.starter.sampleapi.IncomingMail;
import com.yosanai.spring.starter.sampleapi.OutgoingMail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan(basePackages = "com.yosanai.spring.starter.sampleemailservice")
public class TestConfig {

	@Value("${sampleemail.concurrent}")
	private int concurrent;

	@Value("${sampleemail.gotmail.queue}")
	private String gotMailQueue;

	@Value("${sampleemail.sendmail.queue}")
	private String sendMailQueue;

	@Autowired
	@Qualifier("gotMailLatch")
	private CountDownLatch latch;

	@Bean
	public CountDownLatch gotMailLatch(@Value("${sampleemail.latch}") int count) {
		return new CountDownLatch(count);
	}

	@Bean
	public CountDownLatch ignoreMailLatch(@Value("${sampleemail.latch}") int count) {
		return new CountDownLatch(count);
	}

	@Bean
	SimpleMessageListenerContainer gotMailContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(gotMailQueue);
		container.setConcurrentConsumers(concurrent);
		return container;
	}

	@Bean
	public MessageChannel sampleGotMailReceiverChannel() {
		return new DirectChannel();
	}

	@Bean
	public AmqpInboundChannelAdapter gotMailInbound(
			@Qualifier("gotMailContainer") SimpleMessageListenerContainer listenerContainer,
			@Qualifier("sampleGotMailReceiverChannel") MessageChannel channel) {
		AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
		adapter.setOutputChannel(channel);
		return adapter;
	}

	@ServiceActivator(inputChannel = "sampleGotMailReceiverChannel", outputChannel = "sendMailOutboundChannel")
	public OutgoingMail handleMessage(IncomingMail mail) throws Exception {
		OutgoingMail ret = new OutgoingMail();
		log.info("Got to process this mail {}", mail);
		ret.setFrom(TestMailReceiver.IGNORE_MAIL);
		ret.setSubject(mail.getSubject());
		ret.setTo(mail.getFrom());
		ret.setHtml(StringUtils.isNotBlank(mail.getHtml()));
		ret.setContent(ret.isHtml() ? mail.getHtml() : mail.getPlain());
		latch.countDown();
		return ret;
	}

	@Bean
	public MessageChannel sendMailOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "sendMailOutboundChannel")
	public AmqpOutboundEndpoint sendMailOutbound(AmqpTemplate amqpTemplate) {
		AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
		outbound.setRoutingKey(sendMailQueue);
		return outbound;
	}

	@Bean
	public Wiser wiser(@Autowired JavaMailSenderImpl mailSender) {
		Wiser ret = new Wiser();
		int port = SocketUtils.findAvailableTcpPort(2500);
		ret.setPort(port);
		mailSender.setPort(port);
		ret.start();
		log.info("Wiser was started.");
		return ret;
	}

}

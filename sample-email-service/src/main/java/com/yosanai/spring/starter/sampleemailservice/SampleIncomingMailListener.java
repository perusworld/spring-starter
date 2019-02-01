package com.yosanai.spring.starter.sampleemailservice;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.yosanai.spring.starter.sampleapi.IncomingMail;
import com.yosanai.spring.starter.sampleemailservice.mail.CustomContentHeaders;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SampleIncomingMailListener {

	@Autowired
	@Qualifier("sampleIncomingMailListenerLatch")
	private CountDownLatch latch;

	@Autowired
	private GotMail gotMail;

	@ServiceActivator(inputChannel = "testReceiveEmailChannel")
	public void handleMessage(@Header(CustomContentHeaders.PLAIN_CONTENT) String plain,
			@Header(CustomContentHeaders.HTML_CONTENT) String html, @Header(MailHeaders.FROM) String from,
			@Header(MailHeaders.TO) String to, @Header(MailHeaders.SUBJECT) String subject) throws Exception {
		log.info("Got message {},{},{},{},{}", plain, html, from, to, subject);
		latch.countDown();
		gotMail.process(new IncomingMail(plain, html, from, to, subject));
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}

package com.yosanai.spring.starter.samplesftpservice;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IncomingFileListener {

	@Autowired
	@Qualifier("sampleIncomingFileListenerLatch")
	private CountDownLatch latch;

	@ServiceActivator(inputChannel = "inboundChannel")
	public void handleMessage(Message<?> message) throws Exception {
		log.info("Got message {}", message.getPayload());
		latch.countDown();
	}

}

package com.yosanai.spring.starter.sampleamqpservice;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.yosanai.spring.starter.sampleapi.SampleAPI;
import com.yosanai.spring.starter.sampleapi.SampleRequest;
import com.yosanai.spring.starter.sampleapi.SampleResponse;

import lombok.extern.java.Log;

@Log
@Component
public class SampleRequestListener {

	private CountDownLatch latch = new CountDownLatch(1);

	@Autowired
	private SampleAPI sampleAPI;

	@ServiceActivator(inputChannel = "sampleReceiverChannel")
	public void handleMessage(SampleRequest request) throws Exception {
		log.info("Got message " + request);
		SampleResponse response = sampleAPI.call(request).get();
		log.info("And the response " + response);
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}

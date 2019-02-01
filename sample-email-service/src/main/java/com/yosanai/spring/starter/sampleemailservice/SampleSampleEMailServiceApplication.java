package com.yosanai.spring.starter.sampleemailservice;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.yosanai.spring.starter.sampleapi.DefaultSampleAPI;
import com.yosanai.spring.starter.sampleapi.SampleAPI;

@SpringBootApplication
public class SampleSampleEMailServiceApplication {

	@Bean
	public CountDownLatch sampleIncomingMailListenerLatch(@Value("${sampleemail.latch}") int count) {
		return new CountDownLatch(count);
	}

	@Bean
	public CountDownLatch sampleOutgoingMailListenerLatch(@Value("${sampleemail.latch}") int count) {
		return new CountDownLatch(count);
	}

	public SampleAPI sampleAPI() {
		return new DefaultSampleAPI();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleSampleEMailServiceApplication.class, args);
	}

}

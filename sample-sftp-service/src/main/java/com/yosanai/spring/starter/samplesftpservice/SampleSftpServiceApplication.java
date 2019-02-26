package com.yosanai.spring.starter.samplesftpservice;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SampleSftpServiceApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SampleSftpServiceApplication.class).web(WebApplicationType.NONE).run(args);
	}

}
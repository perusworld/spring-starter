package com.yosanai.spring.starter.samplebatch;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class SampleBatchApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SampleBatchApplication.class).web(WebApplicationType.NONE).run(args);
	}

}

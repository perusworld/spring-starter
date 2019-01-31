package com.yosanai.spring.starter.sampleapi;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

@Service
public class SampleBusinessLogicService implements SampleAPI {

	public CompletableFuture<SampleResponse> call(SampleRequest request) {
		return new DefaultSampleAPI().call(request);
	}

}

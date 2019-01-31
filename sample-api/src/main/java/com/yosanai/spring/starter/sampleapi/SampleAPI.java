package com.yosanai.spring.starter.sampleapi;

import java.util.concurrent.CompletableFuture;

public interface SampleAPI {
	public CompletableFuture<SampleResponse> call(SampleRequest request);

}

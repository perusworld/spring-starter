package com.yosanai.spring.starter.sampleapi;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class DefaultSampleAPI implements SampleAPI {

	@Override
	public CompletableFuture<SampleResponse> call(SampleRequest request) {
		return CompletableFuture.supplyAsync(() -> {
			SampleResponse resp = new SampleResponse();
			resp.setADate(request.getADate());
			resp.setAnInteger(request.getAnInteger());
			resp.setAString(request.getAString());
			resp.setUpdatedAt(new Date());
			return resp;
		});
	}

}

package com.yosanai.spring.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.yosanai.spring.starter.sampleapi.SampleRequest;
import com.yosanai.spring.starter.sampleapi.SampleResponse;
import com.yosanai.spring.starter.samplerestservice.controller.SampleRESTServiceController;

import lombok.extern.java.Log;

@Log
public class SampleRESTServiceControllerTest extends BaseControllerTest<SampleRESTServiceController, SampleResponse> {

	public SampleRESTServiceControllerTest() {
		super(SampleRESTServiceController.class, SampleResponse.class);
	}

	@Test
	public void testIndexPage() throws Exception {
		assertEquals(findBy(String.class, "index"), "Sample REST Service");
	}

	@Test
	public void testCallSampleAPI() throws Exception {
		SampleRequest request = new SampleRequest(rndStr(), rndInt(), new Date());
		SampleResponse response = postTo(request, "call-api");
		assertNotNull(response);
		assertEquals(request.getADate(), response.getADate());
		assertEquals(request.getAnInteger(), response.getAnInteger());
		assertEquals(request.getAString(), response.getAString());
		log.info(response.toString());
	}

}

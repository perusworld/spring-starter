package com.yosanai.spring.starter.sampleapi;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SampleBusinessLogicServiceTest {

	private SampleAPI api;

	@Before
	public void init() {
		api = new SampleBusinessLogicService();
	}

	@Test
	public void testApiCall() throws Exception {
		SampleRequest req = new SampleRequest("1234", 1234, new Date());
		SampleResponse resp = api.call(req).get();
		assertEquals(req.getADate(), resp.getADate());
		assertEquals(req.getAnInteger(), resp.getAnInteger());
		assertEquals(req.getAString(), resp.getAString());
	}

}

package com.yosanai.spring.starter.samplespringweb.integration;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.samplespringweb.SampleController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@ActiveProfiles("test")
public class SampleControllerTest {
	@Autowired
	private SampleController controller;

	@Value("#{'http://localhost:'+${local.server.port}+'/%s'}")
	private String localURL;

	@Value("#{'http://localhost:'+${local.management.port}+'/actuator/health'}")
	private String healthCheckURL;

	@Autowired
	private TestRestTemplate restTemplate;

	protected String getURL(String suffix) {
		return String.format(localURL, suffix);
	}

	@Test
	public void testContextLoads() throws Exception {
		assertNotNull(controller);
	}

	@Test
	public void testIndexPage() throws Exception {
		assertTrue(restTemplate.getForObject(getURL(""), String.class).contains("Index - Sample Spring Web"));
	}

	@Test
	public void testHealthCheck() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity(healthCheckURL, Map.class);
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

}

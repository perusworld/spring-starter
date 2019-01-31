package com.yosanai.spring.starter.samplespringweb;

import java.security.Principal;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.yosanai.spring.starter.sampleapi.SampleRequest;
import com.yosanai.spring.starter.sampleapi.SampleResponse;

@Controller
public class SampleController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${sample.restservice.url}")
	private String sampleRESTServiceURL;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${sample.amqpservice.topicExchange}")
	private String topicExchange;

	@Value("${sample.amqpservice.routingKey}")
	private String routingKey;

	protected String getURL(String suffix) {
		return String.format("%s/%s", sampleRESTServiceURL, suffix);
	}

	@GetMapping("/")
	public String getIndex() {
		return "index-page";
	}

	@GetMapping("/sample-page")
	public String getSamplePage(Model model) {
		model.addAttribute("now", new Date());
		return "sample/sample-page";
	}

	@GetMapping("/call-api")
	public String callSampleRestService(Model model) {
		SampleRequest request = new SampleRequest(RandomStringUtils.random(10, true, true),
				Integer.parseInt(RandomStringUtils.random(5, false, true)), new Date());
		SampleResponse response = restTemplate.postForObject(getURL("sample/call-api"), new HttpEntity<>(request),
				SampleResponse.class);
		model.addAttribute("url", getURL(""));
		model.addAttribute("resp", response);
		return "call-api/response";
	}

	@GetMapping("/send-amqp-msg")
	public String sendAMQPMessage(Model model) {
		SampleRequest request = new SampleRequest(RandomStringUtils.random(10, true, true),
				Integer.parseInt(RandomStringUtils.random(5, false, true)), new Date());
		rabbitTemplate.convertAndSend(topicExchange, routingKey.replace("#", "web"), request);
		return "amqp/send";
	}

	@RequestMapping("/user")
	public String user(Principal principal, Model model) {
		model.addAttribute("user", principal);
		return "secure/user";
	}

}

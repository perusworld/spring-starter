package com.yosanai.spring.starter.samplerestservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yosanai.spring.starter.sampleapi.SampleAPI;
import com.yosanai.spring.starter.sampleapi.SampleRequest;
import com.yosanai.spring.starter.sampleapi.SampleResponse;

@RestController
@RequestMapping("/sample")
public class SampleRESTServiceController {

	@Autowired
	private SampleAPI sampleAPI;

	@RequestMapping("index")
	public String getIndex() {
		return "Sample REST Service";
	}

	@RequestMapping(path = "call-api", method = RequestMethod.POST)
	@ResponseBody
	public SampleResponse callSampleAPI(@Valid @RequestBody SampleRequest req) {
		return sampleAPI.call(req).join();
	}

}

package com.yosanai.spring.starter.sampleemailservice;

import org.springframework.integration.annotation.MessagingGateway;

import com.yosanai.spring.starter.sampleapi.IncomingMailProcessor;

@MessagingGateway(defaultRequestChannel = "sampleGotMailChannel")
public interface GotMail extends IncomingMailProcessor {

}

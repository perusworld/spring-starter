package com.yosanai.spring.starter.sampleemailservice;

import java.util.concurrent.CountDownLatch;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.yosanai.spring.starter.sampleapi.OutgoingMail;

import lombok.extern.java.Log;

@Log
@Component
public class SampleOutgoingMailListener {

	@Autowired
	@Qualifier("sampleOutgoingMailListenerLatch")
	private CountDownLatch latch;

	@Autowired
	JavaMailSender mailSender;

	@ServiceActivator(inputChannel = "sampleReceiverChannel", outputChannel = "smtpChannel")
	public MimeMailMessage handleMessage(OutgoingMail request) throws Exception {
		MimeMailMessage ret = null;
		log.info("Got outgoing mail " + request);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(request.getFrom());
		helper.setTo(request.getTo());
		helper.setSubject(request.getSubject());
		helper.setText(request.getContent(), request.isHtml());
		ret = new MimeMailMessage(message);
		latch.countDown();
		log.info("built mime message");
		return ret;
	}

}

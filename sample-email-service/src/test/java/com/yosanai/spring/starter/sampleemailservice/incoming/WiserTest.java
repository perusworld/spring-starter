package com.yosanai.spring.starter.sampleemailservice.incoming;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@RunWith(SpringRunner.class)
public class WiserTest {

	@Test
	public void check() throws Exception {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Wiser wiser = new Wiser();
		int port = SocketUtils.findAvailableTcpPort(2500);
		mailSender.setHost("localhost");
		mailSender.setPort(port);
		wiser.setPort(port);
		wiser.start();

		Date dte = new Date();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo("foo@bar.com");
		String emailSubject = "testSend: " + dte;
		String emailBody = "Body of testSend message sent at: " + dte;
		mailMessage.setSubject(emailSubject);
		mailMessage.setText(emailBody);
		mailSender.send(mailMessage);
		wiser.stop();
		assertTrue(wiser.getMessages().size() == 1);
		WiserMessage wm = wiser.getMessages().get(0);
		assertEquals(emailSubject, wm.getMimeMessage().getSubject());
		assertEquals(emailBody, wm.getMimeMessage().getContent());
	}

}

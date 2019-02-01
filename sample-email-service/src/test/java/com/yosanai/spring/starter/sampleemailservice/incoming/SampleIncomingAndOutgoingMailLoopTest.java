package com.yosanai.spring.starter.sampleemailservice.incoming;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.subethamail.wiser.Wiser;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@ActiveProfiles("test")
public class SampleIncomingAndOutgoingMailLoopTest {

	@Autowired
	private Wiser wiser;

	private String from = "someone@somewhere.com";
	private String to = "someoneto@someplace.com";
	private String voucherText = "Voucher 12345\r\n" + "Card Number 1234567890123456\r\n" + "Merchant Id 1223456";
	private String voucherHTML = "<table><tr><td>Voucher 12345</td></tr><tr><td>Card Number 1234567890123456</td></tr><tr><td>Merchant Id 1223456</td></tr><table>";

	@Autowired
	@Qualifier("sampleIncomingMailListenerLatch")
	private CountDownLatch incomingLatch;

	@Autowired
	@Qualifier("sampleOutgoingMailListenerLatch")
	private CountDownLatch outgoingLatch;

	@Autowired
	@Qualifier("gotMailLatch")
	private CountDownLatch gotMailLatch;

	@Autowired
	@Qualifier("ignoreMailLatch")
	private CountDownLatch ignoreMailLatch;

	public void sendMail(String msg, boolean multipart, boolean html) throws MessagingException {
		final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setPort(wiser.getServer().getPort());
		final MimeMessage message = mailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, multipart);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject("Payment Voucher");
		helper.setText(msg, html);
		mailSender.send(message);
	}

	public void sendMails() throws MessagingException {
		sendMail(voucherText, false, false);
		sendMail(voucherHTML, false, true);
		sendMail(voucherText, true, false);
		sendMail(voucherHTML, true, true);
	}

	@Test
	public void checkMailReceive() throws Exception {
		sendMails();
		try {
			assertTrue(incomingLatch.await(25000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
		}
		try {
			assertTrue(gotMailLatch.await(5000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
		}
		try {
			assertTrue(outgoingLatch.await(5000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
		}
		try {
			assertTrue(ignoreMailLatch.await(5000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
		}
	}

}

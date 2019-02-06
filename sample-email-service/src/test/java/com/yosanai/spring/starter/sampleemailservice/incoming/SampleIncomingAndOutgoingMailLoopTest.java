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
	public static final String SAMPLE_TEXT = "Name Some One\r\n" + "E-Mail someone@somewhere.com\r\n"
			+ "Account Number 1223456";
	public static final String SAMPLE_HTML = "<html>\r\n" + "<head>\r\n"
			+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n"
			+ "    <style type=\"text/css\" style=\"display:none;\">\r\n" + "        P {\r\n"
			+ "            margin-top: 0;\r\n" + "            margin-bottom: 0;\r\n" + "        }\r\n"
			+ "    </style>\r\n" + "</head>\r\n" + "<body dir=\"ltr\">\r\n"
			+ "    <div style=\"color: rgb(0, 0, 0); font-family: Calibri,Helvetica,sans-serif; font-size: 12pt;\">\r\n"
			+ "        <table>\r\n" + "            <tr>\r\n" + "                <td>Name</td><td>Some One</td>\r\n"
			+ "            </tr>\r\n" + "            <tr>\r\n"
			+ "                <td>E-Mail</td><td>someone@somewhere.com</td>\r\n" + "            </tr>\r\n"
			+ "            <tr>\r\n" + "                <td>Account Number</td><td>1223456</td>\r\n"
			+ "            </tr>\r\n" + "            <table>\r\n" + "    </div>\r\n" + "</body>\r\n" + "</html>";

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
		sendMail(SAMPLE_TEXT, false, false);
		sendMail(SAMPLE_HTML, false, true);
		sendMail(SAMPLE_TEXT, true, false);
		sendMail(SAMPLE_HTML, true, true);
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

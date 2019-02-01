package com.yosanai.spring.starter.sampleemailservice.incoming;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import lombok.extern.slf4j.Slf4j;

@Component("sampleMailReceiver")
@Slf4j
public class TestMailReceiver implements MailReceiver {

	public static final String IGNORE_MAIL = "from@us.com";

	@Autowired
	protected Wiser wiser;

	@Autowired
	protected HeaderMapper<MimeMessage> mapper;

	@Autowired
	@Qualifier("ignoreMailLatch")
	private CountDownLatch ignoreMailLatch;

	protected MessageBuilderFactory factory = new DefaultMessageBuilderFactory();

	public TestMailReceiver() {
	}

	private Object extractContent(MimeMessage message, Map<String, Object> headers) {
		Object content;
		try {
			MimeMessage theMessage;
			theMessage = message;
			content = theMessage.getContent();
			if (content instanceof String) {
				String mailContentType = (String) headers.get(MailHeaders.CONTENT_TYPE);
				if (mailContentType != null && mailContentType.toLowerCase().startsWith("text")) {
					headers.put(MessageHeaders.CONTENT_TYPE, mailContentType);
				} else {
					headers.put(MessageHeaders.CONTENT_TYPE, "text/plain");
				}
			} else if (content instanceof InputStream) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileCopyUtils.copy((InputStream) content, baos);
				content = byteArrayToContent(headers, baos);
			} else if (content instanceof Multipart) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((Multipart) content).writeTo(baos);
				content = byteArrayToContent(headers, baos);
			} else if (content instanceof Part) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((Part) content).writeTo(baos);
				content = byteArrayToContent(headers, baos);
			}
			return content;
		} catch (Exception e) {
			throw new org.springframework.messaging.MessagingException("Failed to extract content from " + message, e);
		}
	}

	private Object byteArrayToContent(Map<String, Object> headers, ByteArrayOutputStream baos) {
		headers.put(MessageHeaders.CONTENT_TYPE, "application/octet-stream");
		return baos.toByteArray();
	}

	@Override
	public Object[] receive() throws MessagingException {
		Message<?> ret = null;
		if (!wiser.getMessages().isEmpty()) {
			WiserMessage msg = wiser.getMessages().remove(0);
			while (null != msg && msg.getMimeMessage().getFrom()[0].toString().equals(IGNORE_MAIL)) {
				log.info("Ignoring outgoing email");
				ignoreMailLatch.countDown();
				msg = wiser.getMessages().isEmpty() ? null : wiser.getMessages().remove(0);
			}
			if (null != msg) {
				MimeMessage message = msg.getMimeMessage();
				Map<String, Object> headers = mapper.toHeaders(message);
				ret = factory.withPayload(extractContent(message, headers)).copyHeaders(headers).build();
			}
		}
		return null == ret ? new Object[0] : new Object[] { ret };
	}
}

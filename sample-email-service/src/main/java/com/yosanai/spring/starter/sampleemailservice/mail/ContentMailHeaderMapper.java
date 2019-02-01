package com.yosanai.spring.starter.sampleemailservice.mail;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ContentMailHeaderMapper extends DefaultMailHeaderMapper {

	@Override
	public Map<String, Object> toHeaders(MimeMessage source) {
		Map<String, Object> ret = super.toHeaders(source);
		MimeMessageParser parser;
		try {
			parser = new MimeMessageParser(source).parse();
			log.info("plain-{},html-{},attachments-{},multi-{}", parser.hasPlainContent(), parser.hasHtmlContent(),
					parser.hasAttachments(), parser.isMultipart());
			ret.put(CustomContentHeaders.HAS_PLAIN, parser.hasPlainContent());
			ret.put(CustomContentHeaders.HAS_HTML, parser.hasHtmlContent());
			ret.put(CustomContentHeaders.HAS_ATTACHEMENTS, parser.hasAttachments());
			ret.put(CustomContentHeaders.IS_MULTIPART, parser.isMultipart());
			ret.put(CustomContentHeaders.PLAIN_CONTENT, parser.hasPlainContent() ? parser.getPlainContent() : "");
			ret.put(CustomContentHeaders.HTML_CONTENT, parser.hasHtmlContent() ? parser.getHtmlContent() : "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}

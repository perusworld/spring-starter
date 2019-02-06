package com.yosanai.spring.starter.sampleemailservice.incoming;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.sampleapi.IncomingMail;
import com.yosanai.spring.starter.sampleapi.SampleCustomer;
import com.yosanai.spring.starter.sampleemailservice.CustomerEmailParser;

@RunWith(SpringRunner.class)
public class CustomerEmailParserTest {

	@Test
	public void parseHtmlEmail() throws Exception {
		CustomerEmailParser parser = new CustomerEmailParser();
		IncomingMail mail = new IncomingMail("", SampleIncomingAndOutgoingMailLoopTest.SAMPLE_HTML, "", "", "");
		SampleCustomer parsed = parser.parse(mail);
		assertNotNull(parsed);
		assertEquals("Some One", parsed.getName());
		assertEquals("someone@somewhere.com", parsed.getEmail());
		assertEquals("1223456", parsed.getAccountNumber());
	}

}

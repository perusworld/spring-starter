package com.yosanai.spring.starter.sampleemailservice;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.yosanai.spring.starter.sampleapi.IncomingMail;
import com.yosanai.spring.starter.sampleapi.SampleCustomer;

@Service
public class CustomerEmailParser {

	String extractText(String path, Element element) {
		String ret = null;
		Element match = element.selectFirst(path);
		if (null != match) {
			ret = match.text();
		}
		return ret;
	}

	public SampleCustomer parse(IncomingMail mail) {
		SampleCustomer ret = new SampleCustomer();
		if (null != mail) {
			if (StringUtils.isNotBlank(mail.getHtml())) {
				Document doc = Jsoup.parse(mail.getHtml());
				ret.setName(extractText("td:matches(Name) + td", doc));
				ret.setEmail(extractText("td:matches(E-Mail) + td", doc));
				ret.setAccountNumber(extractText("td:matches(Account Number) + td", doc));
			}
		}
		return ret;
	}

}

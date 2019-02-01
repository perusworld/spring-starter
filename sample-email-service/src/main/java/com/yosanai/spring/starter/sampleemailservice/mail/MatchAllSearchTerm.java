package com.yosanai.spring.starter.sampleemailservice.mail;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

public class MatchAllSearchTerm extends SearchTerm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean match(Message msg) {
		return true;
	}

}

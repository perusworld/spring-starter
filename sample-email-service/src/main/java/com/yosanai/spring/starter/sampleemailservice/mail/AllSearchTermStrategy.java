package com.yosanai.spring.starter.sampleemailservice.mail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.search.SearchTerm;

import org.springframework.integration.mail.SearchTermStrategy;

public class AllSearchTermStrategy implements SearchTermStrategy {

	@Override
	public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
		return new MatchAllSearchTerm();
	}

}

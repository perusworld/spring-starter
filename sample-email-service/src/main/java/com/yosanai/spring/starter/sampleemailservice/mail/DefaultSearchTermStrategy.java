package com.yosanai.spring.starter.sampleemailservice.mail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.SearchTerm;

import org.springframework.integration.mail.AbstractMailReceiver;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultSearchTermStrategy implements SearchTermStrategy {

	@Setter
	@Getter
	private String userFlag = AbstractMailReceiver.DEFAULT_SI_USER_FLAG;

	DefaultSearchTermStrategy() {
		super();
	}

	@Override
	public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
		SearchTerm searchTerm = null;
		boolean recentFlagSupported = false;
		if (supportedFlags != null) {
			recentFlagSupported = supportedFlags.contains(Flags.Flag.RECENT);
			if (recentFlagSupported) {
				searchTerm = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
			}
			if (supportedFlags.contains(Flags.Flag.ANSWERED)) {
				NotTerm notAnswered = new NotTerm(new FlagTerm(new Flags(Flags.Flag.ANSWERED), true));
				if (searchTerm == null) {
					searchTerm = notAnswered;
				} else {
					searchTerm = new AndTerm(searchTerm, notAnswered);
				}
			}
			if (supportedFlags.contains(Flags.Flag.DELETED)) {
				NotTerm notDeleted = new NotTerm(new FlagTerm(new Flags(Flags.Flag.DELETED), true));
				if (searchTerm == null) {
					searchTerm = notDeleted;
				} else {
					searchTerm = new AndTerm(searchTerm, notDeleted);
				}
			}
			if (supportedFlags.contains(Flags.Flag.SEEN)) {
				NotTerm notSeen = new NotTerm(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
				if (searchTerm == null) {
					searchTerm = notSeen;
				} else {
					searchTerm = new AndTerm(searchTerm, notSeen);
				}
			}
		}

		if (!recentFlagSupported) {
			NotTerm notFlagged = null;
			if (folder.getPermanentFlags().contains(Flags.Flag.USER)) {
				log.debug("This email server does not support RECENT flag, but it does support "
						+ "USER flags which will be used to prevent duplicates during email fetch."
						+ " This receiver instance uses flag: " + getUserFlag());
				Flags siFlags = new Flags();
				siFlags.add(getUserFlag());
				notFlagged = new NotTerm(new FlagTerm(siFlags, true));
			} else {
				log.debug("This email server does not support RECENT or USER flags. "
						+ "System flag 'Flag.FLAGGED' will be used to prevent duplicates during email fetch.");
				notFlagged = new NotTerm(new FlagTerm(new Flags(Flags.Flag.FLAGGED), true));
			}
			if (searchTerm == null) {
				searchTerm = notFlagged;
			} else {
				searchTerm = new AndTerm(searchTerm, notFlagged);
			}
		}
		return searchTerm;
	}

}
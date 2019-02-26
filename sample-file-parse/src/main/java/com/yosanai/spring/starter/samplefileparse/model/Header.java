package com.yosanai.spring.starter.samplefileparse.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Header extends BaseRecord {

	Date creationDate;

	public Header() {
		super(RECORD_TYPE_HEADER);
	}

}

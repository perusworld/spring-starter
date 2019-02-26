package com.yosanai.spring.starter.samplefileparse.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Trailer extends BaseRecord {

	int lineCount;

	public Trailer() {
		super(RECORD_TYPE_DEPARTMENT_TRAILER);
	}

}

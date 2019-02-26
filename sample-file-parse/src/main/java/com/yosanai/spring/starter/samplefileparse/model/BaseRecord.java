package com.yosanai.spring.starter.samplefileparse.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseRecord {

	public static final String RECORD_TYPE_HEADER = "01";
	public static final String RECORD_TYPE_TRAILER = "05";
	public static final String RECORD_TYPE_DEPARTMENT_HEADER = "02";
	public static final String RECORD_TYPE_DEPARTMENT_TRAILER = "04";
	public static final String RECORD_TYPE_EMPLOYEE = "03";

	String recordType;

	public BaseRecord(String recordType) {
		super();
		this.recordType = recordType;
	}

}

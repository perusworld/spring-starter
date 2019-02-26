package com.yosanai.spring.starter.samplefileparse.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DepartmentTrailer extends BaseRecord {

	int lineCount;

	public DepartmentTrailer() {
		super(RECORD_TYPE_DEPARTMENT_TRAILER);
	}

}

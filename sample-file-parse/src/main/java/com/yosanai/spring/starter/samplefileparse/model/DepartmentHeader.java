package com.yosanai.spring.starter.samplefileparse.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DepartmentHeader extends BaseRecord {

	String name;

	public DepartmentHeader() {
		super(RECORD_TYPE_DEPARTMENT_HEADER);
	}

}

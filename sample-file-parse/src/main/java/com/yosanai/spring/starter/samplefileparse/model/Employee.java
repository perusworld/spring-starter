package com.yosanai.spring.starter.samplefileparse.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Employee extends BaseRecord {

	String firstName;
	String lastName;
	String role;
	BigDecimal salary;
	Date hireDate;

	public Employee() {
		super(RECORD_TYPE_EMPLOYEE);
	}

}

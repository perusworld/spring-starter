package com.yosanai.spring.starter.samplefileparse.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Department {

	DepartmentHeader header;
	List<Employee> employees;
	DepartmentTrailer trailer;

}

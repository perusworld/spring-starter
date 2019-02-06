package com.yosanai.spring.starter.sampleapi;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampleCustomer implements Serializable {

	private String name;
	private String email;
	private String accountNumber;

}

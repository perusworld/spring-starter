package com.yosanai.spring.starter.sampleapi;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampleResponse implements Serializable {

	private String aString;
	private Integer anInteger;
	private Date aDate;
	private Date updatedAt;

}

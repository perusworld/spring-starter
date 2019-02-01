package com.yosanai.spring.starter.sampleapi;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingMail implements Serializable {
	private String plain;
	private String html;
	private String from;
	private String to;
	private String subject;
}

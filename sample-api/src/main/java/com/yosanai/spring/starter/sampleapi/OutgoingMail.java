package com.yosanai.spring.starter.sampleapi;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingMail implements Serializable {
	private String content;
	private String from;
	private String to;
	private String subject;
	private boolean html;
}

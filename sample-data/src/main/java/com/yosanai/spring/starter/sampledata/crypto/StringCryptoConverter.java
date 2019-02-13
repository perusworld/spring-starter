package com.yosanai.spring.starter.sampledata.crypto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class StringCryptoConverter implements AttributeConverter<String, String> {

	@Autowired
	private Crypto<String> crypto;

	@Override
	public String convertToDatabaseColumn(String attribute) {
		return crypto.encrypt(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		return crypto.decrypt(dbData);
	}

}

package com.yosanai.spring.starter.samplefileparse.typehandler;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

public class AmountTypeHandler implements TypeHandler {

	@Override
	public Object parse(String text) throws TypeConversionException {
		return new BigDecimal(
				2 < text.length() ? new StringBuilder(text).insert(text.length() - 2, '.').toString() : text);
	}

	@Override
	public String format(Object value) {
		return ((BigDecimal) value).setScale(2, RoundingMode.CEILING).toString().replace(".", "");
	}

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}

}

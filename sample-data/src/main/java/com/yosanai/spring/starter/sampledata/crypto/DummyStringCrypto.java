package com.yosanai.spring.starter.sampledata.crypto;

import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DummyStringCrypto implements Crypto<String> {

	public DummyStringCrypto() {
		log.info("initialized");
	}

	@Override
	public String encrypt(String input) {
		return null == input ? null : Base64Utils.encodeToString(input.getBytes(Charset.defaultCharset()));
	}

	@Override
	public String decrypt(String input) {
		return null == input ? null : new String(Base64Utils.decodeFromString(input), Charset.defaultCharset());
	}
}

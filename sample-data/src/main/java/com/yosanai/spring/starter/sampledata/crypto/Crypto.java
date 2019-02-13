package com.yosanai.spring.starter.sampledata.crypto;

public interface Crypto<T> {

	public T encrypt(T input);

	public T decrypt(T input);
}

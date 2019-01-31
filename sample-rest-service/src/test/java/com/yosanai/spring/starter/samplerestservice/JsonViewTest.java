package com.yosanai.spring.starter.samplerestservice;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yosanai.spring.starter.sampledata.Views;
import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.model.Product;

import lombok.extern.java.Log;

@Log
public class JsonViewTest {

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	@Test
	public void checkSerialization() throws Exception {
		Customer customer = new Customer(rndStr(), rndStr(), rndStr());
		CustomerOrder order = new CustomerOrder(customer);
		order.addOrderItem(new OrderItem(new Product(rndStr(), rndStr(), rndInt()), rndInt()));
		order.addOrderItem(new OrderItem(new Product(rndStr(), rndStr(), rndInt()), rndInt()));
		order.addOrderItem(new OrderItem(new Product(rndStr(), rndStr(), rndInt()), rndInt()));
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writerWithView(Views.Public.class).writeValueAsString(order);
		log.info(result);
	}

}

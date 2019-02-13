package com.yosanai.spring.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.samplerestservice.controller.CustomerController;

import lombok.extern.java.Log;

@Log
public class CustomerControllerTest extends BaseControllerTest<CustomerController, Customer> {

	public CustomerControllerTest() {
		super(CustomerController.class, Customer.class);
	}

	@Test
	public void testCreateCustomer() throws Exception {
		Customer customer = new Customer(rndStr(), rndStr(), rndStr(), rndStr());
		Customer savedCustomer = create(customer);
		assertNotNull(savedCustomer);
		assertNotNull(customer.getSampleIgnoreInPublic());
		assertNull(savedCustomer.getSampleIgnoreInPublic());
		log.info(savedCustomer.toString());
		assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
		Customer getCustomer = getById(savedCustomer.getId());
		assertNotNull(getCustomer);
		assertNull(savedCustomer.getSampleIgnoreInPublic());
		log.info(getCustomer.toString());
		assertEquals(getCustomer.getId(), savedCustomer.getId());
		assertEquals(getCustomer.getLastName(), savedCustomer.getLastName());
	}

	@Test
	public void testFindAll() throws Exception {
		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			Customer customer = create(new Customer(rndStr(), rndStr(), rndStr(), rndStr()));
			customerMap.put(customer.getId(), customer);
		}
		List<Customer> saved = list();
		assertNotNull(saved);
		assertTrue(INSERT_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerMap.get(obj.getId()).getLastName().equals(obj.getLastName()));
		});
		assertTrue(customerMap.size() <= count.get());
	}

	@Test
	public void testFindAllByLastName() throws Exception {
		String lastName = rndStr();
		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			Customer customer = create(new Customer(rndStr(), lastName, rndStr(), rndStr()));
			customerMap.put(customer.getId(), customer);
		}
		List<Customer> saved = findAllBy(user, "search", "findAllByLastName", lastName);
		assertNotNull(saved);
		assertTrue(INSERT_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerMap.get(obj.getId()).getLastName().equals(obj.getLastName()));
		});
		assertEquals(customerMap.size(), count.get());
	}
}

package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;

public class CustomerTest extends BaseTest {

	@Autowired
	CustomerRepository customerRepository;

	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(customerRepository);
	}

	@Test
	public void checkInsert() {
		Customer savedCustomer = randomDataGenerator.someCustomer();
		assertNotNull(savedCustomer);
		assertTrue(null != savedCustomer.getId());
		assertTrue(null != savedCustomer.getSampleIgnoreInPublic());
	}

	@Test
	public void checkFindByLastName() {
		String lastName = randomDataGenerator.rndStr();
		randomDataGenerator.someCustomers(lastName, BATCH_SIZE);

		List<Customer> customers = customerRepository.findAllByLastName(lastName);
		assertNotNull(customers);
		assertEquals(BATCH_SIZE, customers.size());
		for (Customer customer : customers) {
			assertEquals(lastName, customer.getLastName());
		}

	}

}

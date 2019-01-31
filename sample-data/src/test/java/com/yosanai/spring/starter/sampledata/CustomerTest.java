package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.yosanai.spring.starter.sampledata.model.Customer;

public class CustomerTest extends BaseTest {

	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(customerRepository);
	}

	@Test
	public void checkInsert() {
		Customer savedCustomer = someCustomer();
		assertNotNull(savedCustomer);
		assertTrue(null != savedCustomer.getId());
		assertTrue(null != savedCustomer.getSampleIgnoreInPublic());
	}

	@Test
	public void checkFindByLastName() {
		String lastName = rndStr();
		someCustomers(lastName, BATCH_SIZE);

		List<Customer> customers = customerRepository.findAllByLastName(lastName);
		assertNotNull(customers);
		assertEquals(BATCH_SIZE, customers.size());
		for (Customer customer : customers) {
			assertEquals(lastName, customer.getLastName());
		}

	}

}

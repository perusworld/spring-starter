package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.sampledata.repository.CustomerOrderRepository;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;
import com.yosanai.spring.starter.sampledata.repository.ProductRepository;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { TestConfig.class }) })
public class BaseTest {

	public static final int BATCH_SIZE = 5;
	public static final int PAGE_STEP_SIZE = 2;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CustomerOrderRepository customerOrderRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Test
	public void initCheck() {
		assertNotNull(customerRepository);
		assertNotNull(productRepository);
		assertNotNull(customerOrderRepository);
	}

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	public void flush() {
		entityManager.flush();
		entityManager.clear();
	}

	public void someCustomers(String lastName, int size) {
		for (int idx = 0; idx < size; idx++) {
			customerRepository.save(new Customer(rndStr(), lastName, rndStr(), rndStr()));
		}
	}

	public Customer someCustomer() {
		return customerRepository.save(new Customer(rndStr(), rndStr(), rndStr(), rndStr()));
	}

	public List<Product> someProducts(int size) {
		List<Product> ret = new ArrayList<>();
		for (int idx = 0; idx < size; idx++) {
			ret.add(someProduct());
		}
		return ret;
	}

	public Product someProduct() {
		return productRepository.save(new Product(rndStr(), rndStr(), rndInt()));
	}

	public CustomerOrder someCustomerOrder(Customer customer) {
		return customerOrderRepository.save(new CustomerOrder(customer));
	}

	public void someCustomerOrdersWithItems(Customer customer, int size,
			BiConsumer<List<Product>, List<CustomerOrder>> callback) {
		List<CustomerOrder> orders = new ArrayList<>();
		List<Product> products = someProducts(size);
		for (int idx = 0; idx < size; idx++) {
			CustomerOrder order = new CustomerOrder(customer);
			for (int pIdx = 0; pIdx < RandomUtils.nextInt(1, products.size()); pIdx++) {
				order.addOrderItem(someOrderItem(products.get(pIdx)));
			}
			orders.add(customerOrderRepository.save(order));
		}
		callback.accept(products, orders);
	}

	public OrderItem someOrderItem(Product product) {
		return new OrderItem(product, rndInt());
	}

}

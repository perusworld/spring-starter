package com.yosanai.spring.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.projection.OrderSummary;
import com.yosanai.spring.starter.samplerestservice.controller.CustomerOrderController;

import lombok.extern.java.Log;

@Log
public class CustomerOrderControllerTest extends BaseControllerTest<CustomerOrderController, CustomerOrder> {

	public CustomerOrderControllerTest() {
		super(CustomerOrderController.class, CustomerOrder.class);
	}

	@Test
	public void testCreateCustomerOrder() throws Exception {
		Customer customer = someCustomer();
		CustomerOrder savedCustomerOrder = create(new CustomerOrder(customer));
		assertNotNull(savedCustomerOrder);
		log.info(savedCustomerOrder.toString());
		assertEquals(customer.getId(), savedCustomerOrder.getCustomer().getId());
		CustomerOrder getCustomerOrder = getById(savedCustomerOrder.getId());
		assertNotNull(getCustomerOrder);
		log.info(getCustomerOrder.toString());
		assertEquals(getCustomerOrder.getId(), savedCustomerOrder.getId());
		assertEquals(customer.getId(), getCustomerOrder.getCustomer().getId());
	}

	@Test
	public void testFindAll() throws Exception {
		Customer customer = someCustomer();
		Map<Long, CustomerOrder> customerOrderMap = new HashMap<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			CustomerOrder customerOrder = create(new CustomerOrder(customer));
			customerOrderMap.put(customerOrder.getId(), customerOrder);
		}
		List<CustomerOrder> saved = list();
		assertNotNull(saved);
		assertTrue(INSERT_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerOrderMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerOrderMap.get(obj.getId()).getCustomer().getId().equals(obj.getCustomer().getId()));
		});
		assertTrue(customerOrderMap.size() <= count.get());
	}

	@Test
	public void checkInsertWithItems() {
		Customer savedCustomer = someCustomer();
		List<CustomerOrder> orders = someCustomerOrdersWithItems(savedCustomer, INSERT_SIZE);
		assertNotNull(orders);
		assertEquals(INSERT_SIZE, orders.size());
		List<CustomerOrder> savedOrders = findAllBy(user, "search", "findAllByCustomer",
				savedCustomer.getId().toString());
		assertNotNull(savedOrders);
		assertTrue(INSERT_SIZE <= savedOrders.size());
		assertNotNull(savedOrders);
		assertEquals(INSERT_SIZE, savedOrders.size());
		savedOrders.forEach(order -> {
			final AtomicInteger total = new AtomicInteger(0);
			order.getOrderItems().forEach(item -> {
				assertNotNull(item.getProduct());
				total.addAndGet(item.getQuantity() * item.getProduct().getCost());
			});
			assertEquals(order.getTotalCost(), total.get());
		});
	}

	@Test
	public void checkOrderSummary() {
		Customer savedCustomer = someCustomer();
		List<CustomerOrder> orders = someCustomerOrdersWithItems(savedCustomer, INSERT_SIZE);
		assertNotNull(orders);
		assertEquals(INSERT_SIZE, orders.size());
		List<OrderSummary> summaries = findAllBy(CustomerOrderController.class, OrderSummary.class, user,
				savedCustomer.getId().toString(), "order-summary");
		assertNotNull(summaries);
		summaries.forEach(summary -> {
			log.info(String.format("%s, %d, %d", summary.getOrderDate(), summary.getSalesAmount(),
					summary.getSalesCount()));
			assertTrue(0 < summary.getSalesCount());
			assertTrue(0 < summary.getSalesAmount());
		});
	}
}

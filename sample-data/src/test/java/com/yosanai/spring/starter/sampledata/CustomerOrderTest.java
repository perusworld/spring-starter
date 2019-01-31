package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.sampledata.projection.OrderSummary;

import lombok.extern.java.Log;

@Log
public class CustomerOrderTest extends BaseTest {
	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(customerOrderRepository);
	}

	@Test
	public void checkInsert() {
		Customer savedCustomer = someCustomer();
		CustomerOrder savedOrder = someCustomerOrder(savedCustomer);
		assertNotNull(savedOrder);
		assertTrue(null != savedOrder.getId());
	}

	@Test
	public void checkInsertWithItems() {
		Customer savedCustomer = someCustomer();
		someCustomerOrdersWithItems(savedCustomer, BATCH_SIZE, (products, orders) -> {
			assertNotNull(orders);
			assertEquals(BATCH_SIZE, orders.size());
			flush();
			List<CustomerOrder> savedOrders = customerOrderRepository.findAllByCustomer(savedCustomer);
			assertNotNull(savedOrders);
			assertEquals(BATCH_SIZE, savedOrders.size());
			savedOrders.forEach(order -> {
				final AtomicInteger total = new AtomicInteger(0);
				order.getOrderItems().forEach(item -> {
					assertNotNull(item.getProduct());
					total.addAndGet(item.getQuantity() * item.getProduct().getCost());
				});
				assertEquals(order.getTotalCost(), total.get());
			});
		});
	}

	@Test
	public void checkInsertUpdateWithItems() {
		Customer savedCustomer = someCustomer();
		someCustomerOrdersWithItems(savedCustomer, BATCH_SIZE, (products, orders) -> {
			assertNotNull(orders);
			assertEquals(BATCH_SIZE, orders.size());
			flush();
			List<CustomerOrder> savedOrders = customerOrderRepository.findAllByCustomer(savedCustomer);
			assertNotNull(savedOrders);
			assertEquals(BATCH_SIZE, savedOrders.size());
			savedOrders.forEach(order -> {
				final AtomicInteger total = new AtomicInteger(0);
				order.getOrderItems().forEach(item -> {
					assertNotNull(item.getProduct());
					total.addAndGet(item.getQuantity() * item.getProduct().getCost());
				});
				assertEquals(order.getTotalCost(), total.get());
			});
			orders.forEach(order -> {
				Product product = someProduct();
				order.addOrderItem(someOrderItem(product));
				customerOrderRepository.save(order);
			});
			flush();
			savedOrders = customerOrderRepository.findAllByCustomer(savedCustomer);
			assertNotNull(savedOrders);
			assertEquals(BATCH_SIZE, savedOrders.size());
			savedOrders.forEach(order -> {
				final AtomicInteger total = new AtomicInteger(0);
				order.getOrderItems().forEach(item -> {
					assertNotNull(item.getProduct());
					total.addAndGet(item.getQuantity() * item.getProduct().getCost());
				});
				assertEquals(order.getTotalCost(), total.get());
			});
		});
	}

	@Test
	public void checkOrderSummary() {
		Customer savedCustomer = someCustomer();
		someCustomerOrdersWithItems(savedCustomer, BATCH_SIZE, (products, orders) -> {
			assertNotNull(orders);
			assertEquals(BATCH_SIZE, orders.size());
			flush();
			Iterable<OrderSummary> summaries = customerOrderRepository.summaryByDayForCustomer(savedCustomer);
			assertNotNull(summaries);
			summaries.forEach(summary -> {
				log.info(String.format("%s, %d, %d", summary.getOrderDate(), summary.getSalesAmount(),
						summary.getSalesCount()));
				assertTrue(0 < summary.getSalesCount());
				assertTrue(0 < summary.getSalesAmount());
			});

			summaries = customerOrderRepository.summaryByDayForCustomerId(savedCustomer.getId());
			assertNotNull(summaries);
			summaries.forEach(summary -> {
				log.info(String.format("%s, %d, %d", summary.getOrderDate(), summary.getSalesAmount(),
						summary.getSalesCount()));
				assertTrue(0 < summary.getSalesCount());
				assertTrue(0 < summary.getSalesAmount());
			});
		});
	}
}

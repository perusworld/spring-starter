package com.yosanai.spring.starter.sampledata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.sampledata.repository.CustomerOrderRepository;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;
import com.yosanai.spring.starter.sampledata.repository.ProductRepository;

@Component
public class RandomDataGenerator {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CustomerOrderRepository customerOrderRepository;

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
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

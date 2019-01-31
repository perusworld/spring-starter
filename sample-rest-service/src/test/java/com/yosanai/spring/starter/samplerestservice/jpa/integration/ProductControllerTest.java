package com.yosanai.spring.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.samplerestservice.controller.ProductController;

import lombok.extern.java.Log;

@Log
public class ProductControllerTest extends BaseControllerTest<ProductController, Product> {

	public ProductControllerTest() {
		super(ProductController.class, Product.class);
	}

	@Test
	public void testCreateProduct() throws Exception {
		Product product = new Product(rndStr(), rndStr(), rndInt());
		Product savedProduct = create(product);
		assertNotNull(savedProduct);
		log.info(savedProduct.toString());
		assertEquals(product.getName(), savedProduct.getName());
		Product getProduct = getById(savedProduct.getId());
		assertNotNull(getProduct);
		log.info(getProduct.toString());
		assertEquals(getProduct.getId(), savedProduct.getId());
		assertEquals(getProduct.getDescription(), savedProduct.getDescription());
	}

	@Test
	public void testFindAll() throws Exception {
		Map<Long, Product> productMap = new HashMap<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			Product product = create(new Product(rndStr(), rndStr(), rndInt()));
			productMap.put(product.getId(), product);
		}
		List<Product> saved = list();
		assertNotNull(saved);
		assertTrue(INSERT_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> productMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(productMap.get(obj.getId()).getName().equals(obj.getName()));
		});
		assertTrue(productMap.size() <= count.get());
	}

	@Test
	public void testFindByName() throws Exception {
		String lastName = rndStr();
		List<Product> products = new ArrayList<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			products.add(create(new Product(rndStr(), lastName, rndInt())));
		}
		Product getProduct = findBy("search", "findByName", products.get(0).getName());
		assertNotNull(getProduct);
		log.info(getProduct.toString());
		assertEquals(getProduct.getId(), products.get(0).getId());
		assertEquals(getProduct.getDescription(), products.get(0).getDescription());
	}
}

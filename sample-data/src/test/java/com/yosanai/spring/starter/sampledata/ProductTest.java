package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yosanai.spring.starter.sampledata.model.Product;

public class ProductTest extends BaseTest {

	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(productRepository);
	}

	@Test
	public void checkInsert() {
		Product savedProduct = someProduct();
		assertNotNull(savedProduct);
		assertTrue(null != savedProduct.getId());
	}

	@Test
	public void checkFindAllPaged() {
		List<Product> saved = someProducts(2 * BATCH_SIZE);
		Map<Long, Product> savedMap = saved.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
		flush();
		PageRequest page = PageRequest.of(0, PAGE_STEP_SIZE);
		boolean done = false;
		final AtomicInteger count = new AtomicInteger(0);
		do {
			Page<Product> results = productRepository.findAll(page);
			done = !results.hasContent();
			page = PageRequest.of(page.getPageNumber() + 1, PAGE_STEP_SIZE);
			results.forEach(product -> {
				if (savedMap.containsKey(product.getId())) {
					count.incrementAndGet();
					assertEquals(product.getName(), savedMap.get(product.getId()).getName());
					assertEquals(product.getDescription(), savedMap.get(product.getId()).getDescription());
					assertEquals(product.getCost(), savedMap.get(product.getId()).getCost());
				}
			});
		} while (!done);
	}

	@Test
	public void checkFindByName() {
		List<Product> saved = someProducts(BATCH_SIZE);
		Product product = productRepository.findByName(saved.get(0).getName());
		assertNotNull(product);
		assertEquals(product.getId(), saved.get(0).getId());
	}
}

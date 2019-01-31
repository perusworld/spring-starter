package com.yosanai.spring.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.samplerestservice.PagedResponse;
import com.yosanai.spring.starter.samplerestservice.controller.CustomerController;
import com.yosanai.spring.starter.samplerestservice.controller.CustomerOrderController;
import com.yosanai.spring.starter.samplerestservice.controller.ProductController;

import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
@ActiveProfiles("test")
@Log
@DirtiesContext
public abstract class BaseControllerTest<C, E> {

	public static final String DIR = "dir";

	public static final String DESC = "desc";

	public static final String ASC = "asc";

	public static final String SORT = "sort";

	public static final int INSERT_SIZE = 10;

	public static final int PAGE_SIZE = 5;

	public static final String SIZE = "size";

	public static final String PAGE = "page";

	@Value("#{'http://localhost:'+${local.server.port}+'%s/%s'}")
	private String localURL;

	@Value("#{'http://localhost:'+${local.management.port}+'/actuator/health'}")
	private String healthCheckURL;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	protected ObjectMapper objectMapper;

	protected Class<C> controllerDef;

	protected Class<E> entityClass;

	String adminToken;

	String user;

	public BaseControllerTest(Class<C> controllerDef, Class<E> entityClass) {
		super();
		this.controllerDef = controllerDef;
		this.entityClass = entityClass;
	}

	protected String getURL(Class<?> classDef, String... path) {
		return String.format(localURL, classDef.getAnnotation(RequestMapping.class).value()[0],
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	protected String getPath(Class<?> classDef, String... path) {
		return String.format(localURL, classDef.getAnnotation(RequestMapping.class).value()[0],
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	protected String encodeAsParams(Object... params) {
		StringBuilder ret = new StringBuilder();
		if (null != params) {
			for (int idx = 0; idx < params.length; idx = idx + 2) {
				if (0 < ret.length()) {
					ret.append("&");
				}
				try {
					ret.append(params[idx]).append("=")
							.append(URLEncoder.encode(params[idx + 1].toString(), StandardCharsets.UTF_8.toString()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		}
		return ret.toString();
	}

	protected String getURLWithParam(String url, Object... params) {
		return StringUtils.joinWith("?", url, encodeAsParams(params));
	}

	public double rndDouble() {
		return RandomUtils.nextDouble(0.1d, 100d);
	}

	public int rndPrice() {
		return RandomUtils.nextInt(10, 100);
	}

	protected long rndNum() {
		return RandomUtils.nextInt(1, 100000);
	}

	protected int rndInt() {
		return RandomUtils.nextInt(1, 100000);
	}

	protected int rndUnit() {
		return RandomUtils.nextInt(1, 25);
	}

	protected String rndStr() {
		return rndStr(10);
	}

	protected String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	public <T> T post(Class<?> classDef, Object obj, Class<T> response, String token) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, ""), HttpMethod.POST,
				new HttpEntity<>(obj, headers), response);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNotNull(resp.getBody());
		Long id = null;
		try {
			id = (Long) MethodUtils.invokeMethod(resp.getBody(), "getId");
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		assertNotNull(id);
		return resp.getBody();
	}

	public <T> T postTo(Class<?> classDef, Object obj, TypeReference<T> response, String token, boolean verifyBody,
			String... path) {
		T ret = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<String> resp = restTemplate.exchange(getURL(classDef, path), HttpMethod.POST,
				new HttpEntity<>(obj, headers), String.class);
		if (verifyBody) {
			assertNotNull(resp);
			assertNotNull(resp.getBody());
		}
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		if (!StringUtils.isBlank(resp.getBody())) {
			try {
				ret = objectMapper.readValue(resp.getBody(), response);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
		return ret;
	}

	public <T> T postTo(Class<?> classDef, Object obj, Class<T> response, String token, String... path) {
		T ret = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, path), HttpMethod.POST,
				new HttpEntity<>(obj, headers), response);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		ret = resp.getBody();
		return ret;
	}

	public <T, ID> T put(Class<?> classDef, Object obj, Class<T> response, String token, String... path) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, path), HttpMethod.PUT,
				new HttpEntity<>(obj, headers), response);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNotNull(resp.getBody());
		Long id = null;
		try {
			id = (Long) MethodUtils.invokeMethod(resp.getBody(), "getId");
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		assertNotNull(id);
		return resp.getBody();
	}

	public <T, ID> T put(Class<?> classDef, Object obj, Class<T> response, String token) {
		return put(classDef, obj, response, token, "");
	}

	public <T, ID> T delete(Class<?> classDef, Object obj, Class<T> response, String token, String... path) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<T> resp = restTemplate.exchange(getURL(classDef, path), HttpMethod.DELETE,
				new HttpEntity<>(obj, headers), response);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNotNull(resp.getBody());
		Long id = null;
		try {
			id = (Long) MethodUtils.invokeMethod(resp.getBody(), "getId");
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		assertNotNull(id);
		return resp.getBody();
	}

	public <T, ID> T delete(Class<?> classDef, Object obj, Class<T> response, String token) {
		return delete(classDef, obj, response, token, "");
	}

	public <T> boolean postAuthFail(Class<?> classDef, T obj, String token) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		try {
			ResponseEntity<String> resp = restTemplate.exchange(getURL(classDef, ""), HttpMethod.POST,
					new HttpEntity<>(obj, headers), String.class);
			if (401 == resp.getStatusCodeValue()) {
				return true;
			}
		} catch (Exception e) {
			return true;
		}
		fail("Should fail");
		return false;
	}

	public <T> boolean getAuthFail(Class<?> classDef, String path, String token) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		try {
			ResponseEntity<String> resp = restTemplate.exchange(getURL(classDef, path), HttpMethod.GET,
					new HttpEntity<>(headers), String.class);
			System.out.println(resp.getBody());
		} catch (Exception e) {
			return true;
		}
		fail("Should fail");
		return false;
	}

	public <T> T findBy(Class<?> classDef, Class<T> response, String token, String... args) {
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		ResponseEntity<T> resp = restTemplate.exchange(getPath(classDef, args), HttpMethod.GET,
				new HttpEntity<>(headers), response);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		return resp.getBody();
	}

	public <T> List<T> findAllBy(Class<?> classDef, Class<T> response, String token, String... args) {
		return exchangeAsList(getPath(classDef, args), token, response);
	}

	public <T> PagedResponse<T> findAllByAsPage(Class<?> classDef, Class<T> response, String token, String... args) {
		return exchangeAsPagedResponse(getPath(classDef, args), token, response);
	}

	public <T> List<T> exchangeAsList(String url, String token, Class<T> response) {
		List<T> ret = null;
		ResponseEntity<String> resp = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		try {
			resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		try {
			ret = objectMapper.readValue(resp.getBody(),
					objectMapper.getTypeFactory().constructCollectionType(List.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> PagedResponse<T> exchangeAsPagedResponse(String url, String token, Class<T> response) {
		PagedResponse<T> ret = null;
		ResponseEntity<String> resp = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.isNotEmpty(token)) {
			headers.add("Authorization", "Bearer " + token);
		}
		try {
			resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		try {
			ret = objectMapper.readValue(resp.getBody(),
					objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> PagedResponse<T> pagedResponse(Class<?> classDef, Class<T> response, String token, Object... args) {
		return exchangeAsPagedResponse(getURLWithParam(getPath(classDef, ""), args), token, response);
	}

	public <T> PagedResponse<T> pagedResponseWithPath(Class<?> classDef, Class<T> response, String token, String path,
			Object... args) {
		return exchangeAsPagedResponse(getURLWithParam(getPath(classDef, path), args), token, response);
	}

	// Spring is not liking the url encoding of comma in sort
	public <T> PagedResponse<T> pagedResponseWithSort(Class<?> classDef, Class<T> response, String token,
			String sortValue, Object... args) {
		return exchangeAsPagedResponse(getURLWithParam(getPath(classDef, ""), args)
				+ (null == sortValue ? "" : ("&" + SORT + "=" + sortValue)), token, response);
	}

	public String tokenFor(String user) {
		return null;
	}

	public E create(Object obj) {
		return post(controllerDef, obj, entityClass, adminToken);
	}

	public E postTo(Object obj, String... path) {
		return postTo(controllerDef, obj, entityClass, adminToken, path);
	}

	public <ID> E updateBy(Object obj, String user, String... path) {
		return put(controllerDef, obj, entityClass, tokenFor(user), path);
	}

	public <ID> E deleteBy(Object obj, String user, String... path) {
		return delete(controllerDef, obj, entityClass, tokenFor(user), path);
	}

	public boolean createAuthFail(E obj) throws Exception {
		return postAuthFail(controllerDef, obj, null);
	}

	public boolean getAuthFail(String path) throws Exception {
		return getAuthFail(controllerDef, path, null);
	}

	public E getById(Long id) {
		return getById(controllerDef, id, entityClass, adminToken);
	}

	public E getById(Long id, String user) {
		return getById(controllerDef, id, entityClass, tokenFor(user));
	}

	public <T> T getById(Class<?> classDef, Long id, Class<T> response, String token) {
		return findBy(classDef, response, token, id.toString());
	}

	public E findBy(String... args) {
		return findBy(controllerDef, entityClass, adminToken, args);
	}

	public <T> T findBy(Class<T> response, String... args) {
		return findBy(controllerDef, response, adminToken, args);
	}

	public List<E> findAllBy(String user, String... args) {
		return findAllBy(controllerDef, entityClass, tokenFor(user), args);
	}

	public PagedResponse<E> findAllByAsPage(String user, String... args) {
		return findAllByAsPage(controllerDef, entityClass, tokenFor(user), args);
	}

	public PagedResponse<E> findAllByAsPageWithPath(String user, String path, Object... args) {
		return pagedResponseWithPath(controllerDef, entityClass, tokenFor(user), path, args);
	}

	public List<E> list() {
		return list(0, Integer.MAX_VALUE).getList();
	}

	public List<E> filterBy(String field, Object value, String sortField, boolean ascending) {
		return pagedResponseWithSort(controllerDef, entityClass, adminToken,
				null == sortField ? null : String.join(",", sortField, ascending ? ASC : DESC), PAGE, 0, SIZE,
				Integer.MAX_VALUE, field, null == value ? "" : value.toString()).getList();
	}

	public <T> List<T> filterBy(Class<?> classDef, Class<T> response, String user, String field, Object value,
			String sortField, boolean ascending) {
		return pagedResponseWithSort(classDef, response, tokenFor(user),
				null == sortField ? null : String.join(",", sortField, ascending ? ASC : DESC), PAGE, 0, SIZE,
				Integer.MAX_VALUE, field, null == value ? "" : value.toString()).getList();
	}

	public List<E> filterBy(String user, String field, Object value, String sortField, boolean ascending) {
		return pagedResponseWithSort(controllerDef, entityClass, tokenFor(user),
				null == sortField ? null : String.join(",", sortField, ascending ? ASC : DESC), PAGE, 0, SIZE,
				Integer.MAX_VALUE, field, null == value ? "" : value.toString()).getList();
	}

	public List<E> filterBy(String user, String fieldA, Object valueA, String fieldB, Object valueB, String sortField,
			boolean ascending) {
		return pagedResponseWithSort(controllerDef, entityClass, tokenFor(user),
				null == sortField ? null : String.join(",", sortField, ascending ? ASC : DESC), PAGE, 0, SIZE,
				Integer.MAX_VALUE, fieldA, null == valueA ? "" : valueA.toString(), fieldB,
				null == valueB ? "" : valueB.toString()).getList();
	}

	public List<E> filterBy(String user, String sortField, boolean ascending, Object... args) {
		List<Object> params = new ArrayList<>(Arrays.asList(args));
		params.addAll(Arrays.asList(PAGE, 0, SIZE, Integer.MAX_VALUE));
		return pagedResponseWithSort(controllerDef, entityClass, tokenFor(user),
				String.join(",", sortField, ascending ? ASC : DESC), params.stream().toArray(Object[]::new)).getList();
	}

	public List<E> list(String user) {
		return pagedResponse(controllerDef, entityClass, tokenFor(user), PAGE, 0, SIZE, Integer.MAX_VALUE).getList();
	}

	public PagedResponse<E> list(int page, int size) {
		return pagedResponse(controllerDef, entityClass, adminToken, PAGE, page, SIZE, size);
	}

	public PagedResponse<E> list(int page, int size, String user) {
		return pagedResponse(controllerDef, entityClass, tokenFor(user), PAGE, page, SIZE, size);
	}

	public Customer someCustomer() {
		return post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()), Customer.class, adminToken);
	}

	public CustomerOrder someCustomerOrder(Customer customer) {
		return post(CustomerOrderController.class, new CustomerOrder(customer), CustomerOrder.class, adminToken);
	}

	public CustomerOrder addOrderItem(CustomerOrder order, OrderItem item) {
		return postTo(CustomerOrderController.class, item, CustomerOrder.class, adminToken, order.getId().toString(),
				"order-item");
	}

	public List<Product> someProducts(int size) {
		List<Product> ret = new ArrayList<>();
		for (int idx = 0; idx < INSERT_SIZE; idx++) {
			ret.add(post(ProductController.class, new Product(rndStr(), rndStr(), rndPrice()), Product.class,
					adminToken));
		}
		return ret;
	}

	public List<CustomerOrder> someCustomerOrdersWithItems(Customer customer, int size) {
		List<CustomerOrder> ret = new ArrayList<>();
		List<Product> products = someProducts(size);
		for (int idx = 0; idx < size; idx++) {
			CustomerOrder order = someCustomerOrder(customer);
			for (int pIdx = 0; pIdx < RandomUtils.nextInt(1, products.size()); pIdx++) {
				order = addOrderItem(order, new OrderItem(products.get(pIdx), rndUnit()));
			}
			ret.add(order);
		}
		return ret;
	}

	@Test
	public void testContextLoads() throws Exception {
		assertNotNull(restTemplate);
	}

	@Test
	public void testHealthCheck() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity(healthCheckURL, Map.class);
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
		log.info(entity.toString());
	}
}

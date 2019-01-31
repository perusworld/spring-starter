package com.yosanai.spring.starter.samplerestservice.docs;

import static org.junit.Assert.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.List;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.samplerestservice.controller.CustomerController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
@ActiveProfiles("test")
@DirtiesContext
public class DocGenTest extends BaseAsciiDocTest {

	private RequestFieldsSnippet customerRequestFieldDescriptors = requestFields(
			fieldDescriptorsFor("firstName", "Customer's first name", "lastName", "Customer's last name",
					"sampleIgnoreInPublic", "Some data that will be ignored while in public view"));

	private Snippet getPathParameters = pathParameters("id", "Customer ID to get");

	private Snippet lastNamePathParameters = pathParameters("lastName", "Last name to search by");

	private Snippet pageRequestParameters = requestParameters(PAGE, "Page number to get", SIZE,
			"Number of results per page");

	private ResponseFieldsSnippet customerResponseFieldDescriptors = responseFields(
			fieldDescriptorsFor("firstName", "Customer's first name", "lastName", "Customer's last name", "created",
					"Created timestamp", "updated", "Updated timestamp", "id", "ID"));

	private ResponseFieldsSnippet customerPagedResponseFieldDescriptors = responseFields(fieldDescriptorsFor("list[]",
			"List of customers", "list[].firstName", "Customer's first name", "list[].lastName", "Customer's last name",
			"list[].created", "Created timestamp", "list[].updated", "Updated timestamp", "list[].id", "ID",
			"currentPage", "Current page number", "totalPages", "Total number of pages", "totalSize",
			"Number of results", "first", "Is it first page", "last", "Is it last page"));

	private ResponseFieldsSnippet customerResponseArrayFieldDescriptors = responseFields(
			fieldDescriptorsFor("[].firstName", "Customer's first name", "[].lastName", "Customer's last name",
					"[].created", "Created timestamp", "[].updated", "Updated timestamp", "[].id", "ID"));

	public Customer someCustomer(int index) {
		return new Customer("firstName" + index, "lastName" + index, "sampleIgnoreInPublic" + index);
	}

	@Test
	public void jpaCustomerCreate() {
		setupDocument(customerResponseFieldDescriptors, customerRequestFieldDescriptors);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), true);
		assertNotNull(obj);
	}

	@Test
	public void jpaCustomerGet() {
		setupDocument(customerResponseFieldDescriptors, getPathParameters);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), false);
		assertNotNull(obj);
		obj = findBy(CustomerController.class, Customer.class, true, "{id}", obj.getId());
		assertNotNull(obj);
	}

	@Test
	public void jpaCustomerList() {
		setupDocument(customerPagedResponseFieldDescriptors, pageRequestParameters);
		IntStream.range(0, INSERT_SIZE).forEach(idx -> {
			Customer obj = create(CustomerController.class, Customer.class, someCustomer(idx), false);
			assertNotNull(obj);
		});
		List<Customer> objs = pagedResponse(CustomerController.class, Customer.class, true, PAGE, 0, SIZE,
				Integer.MAX_VALUE).getList();
		assertNotNull(objs);
		assertFalse(objs.isEmpty());
	}

	@Test
	public void jpaCustomersByLastName() {
		setupDocument(customerResponseArrayFieldDescriptors, lastNamePathParameters);
		Customer obj = create(CustomerController.class, Customer.class, someCustomer(1), false);
		assertNotNull(obj);
		List<Customer> objs = findAllBy(CustomerController.class, Customer.class, true,
				"search/findAllByLastName/{lastName}", obj.getLastName());
		assertNotNull(objs);
		assertFalse(objs.isEmpty());
	}

}

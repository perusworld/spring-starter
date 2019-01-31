package com.yosanai.spring.starter.samplerestservice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.querydsl.core.types.Predicate;
import com.yosanai.spring.starter.sampledata.Views;
import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;
import com.yosanai.spring.starter.samplerestservice.PagedResponse;
import com.yosanai.spring.starter.samplerestservice.ResourceException;

@RestController
@RequestMapping("/jpa/customers")
@RepositoryRestResource
public class CustomerController {

	@Autowired
	private CustomerRepository repository;

	@GetMapping
	@JsonView(Views.Public.class)
	public PagedResponse<Customer> filter(@QuerydslPredicate(root = Customer.class) Predicate predicate,
			@NotNull final Pageable pageable) {
		return new PagedResponse<>(repository.findAll(predicate, pageable));
	}

	/**
	 * Create or update customer
	 */
	@PostMapping
	@JsonView(Views.Public.class)
	public Customer save(@Valid @RequestBody Customer customer) {
		return repository.save(customer);
	}

	/**
	 * Get customer
	 * 
	 * @param id Id of the customer object
	 */
	@GetMapping("{id}")
	@JsonView(Views.Public.class)
	public Customer get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
	}

	/**
	 * Find all customers by last name
	 * 
	 * @param lastName last name to search by
	 */
	@GetMapping("search/findAllByLastName/{lastName}")
	@JsonView(Views.Public.class)
	public List<Customer> getByLastName(@PathVariable String lastName) {
		return repository.findAllByLastName(lastName);
	}

}

package com.yosanai.spring.starter.samplerestservice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.querydsl.core.types.Predicate;
import com.yosanai.spring.starter.sampledata.Views;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.projection.OrderSummary;
import com.yosanai.spring.starter.sampledata.repository.CustomerOrderRepository;
import com.yosanai.spring.starter.samplerestservice.PagedResponse;
import com.yosanai.spring.starter.samplerestservice.ResourceException;

@RestController
@RequestMapping("/jpa/customer-orders")
public class CustomerOrderController {

	@Autowired
	private CustomerOrderRepository repository;

	@GetMapping
	@JsonView(Views.Public.class)
	public PagedResponse<CustomerOrder> filter(@QuerydslPredicate(root = CustomerOrder.class) Predicate predicate,
			@NotNull final Pageable pageable) {
		return new PagedResponse<>(repository.findAll(predicate, pageable));
	}

	@PostMapping
	@JsonView(Views.Public.class)
	public CustomerOrder save(@Valid @RequestBody CustomerOrder customerOrder) {
		return repository.save(customerOrder);
	}

	@PostMapping("{id}/order-item")
	@JsonView(Views.Public.class)
	public CustomerOrder addOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItem orderItem) {
		CustomerOrder ret = repository.findById(id)
				.orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
		ret.addOrderItem(orderItem);
		return repository.save(ret);
	}

	@GetMapping("{id}")
	@JsonView(Views.Public.class)
	public CustomerOrder get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
	}

	@GetMapping("search/findAllByCustomer/{customerId}")
	@JsonView(Views.Public.class)
	public List<CustomerOrder> findByCustomer(@PathVariable Long customerId) {
		return repository.findAllByCustomerId(customerId);
	}

	@GetMapping("/{customerId}/order-summary")
	@JsonView(Views.Public.class)
	public List<OrderSummary> getOrderSummary(@NotNull @PathVariable Long customerId) {
		return repository.summaryByDayForCustomerId(customerId);
	}

}

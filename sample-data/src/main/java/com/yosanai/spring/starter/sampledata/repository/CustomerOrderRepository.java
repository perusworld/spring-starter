package com.yosanai.spring.starter.sampledata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestBody;

import com.querydsl.core.types.dsl.StringPath;
import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.QCustomerOrder;
import com.yosanai.spring.starter.sampledata.projection.OrderSummary;

@RepositoryRestResource(path = "customer-orders")
public interface CustomerOrderRepository
		extends PagingAndSortingRepository<CustomerOrder, Long>, QueryByExampleExecutor<CustomerOrder>,
		QuerydslPredicateExecutor<CustomerOrder>, QuerydslBinderCustomizer<QCustomerOrder> {

	@Override
	default public void customize(QuerydslBindings bindings, QCustomerOrder root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}

	public List<CustomerOrder> findAllByCustomer(@RequestBody Customer customer);

	public List<CustomerOrder> findAllByCustomerId(@Param("customerId") Long customerId);

	@Query("select new com.yosanai.spring.starter.sampledata.projection.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer = :customer"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public Iterable<OrderSummary> summaryByDayForCustomer(Customer customer);

	@Query("select new com.yosanai.spring.starter.sampledata.projection.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer.id = :customerId"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public List<OrderSummary> summaryByDayForCustomerId(@Param("customerId") Long customerId);

}

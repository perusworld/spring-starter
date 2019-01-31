package com.yosanai.spring.starter.sampledata.repository;

import java.util.List;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.querydsl.core.types.dsl.StringPath;
import com.yosanai.spring.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.starter.sampledata.model.OrderItem;
import com.yosanai.spring.starter.sampledata.model.QOrderItem;

@RepositoryRestResource(path = "order-items")
public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Long>,
		QueryByExampleExecutor<OrderItem>, QuerydslPredicateExecutor<OrderItem>, QuerydslBinderCustomizer<QOrderItem> {

	@Override
	default public void customize(QuerydslBindings bindings, QOrderItem root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}

	public List<OrderItem> findAllByCustomerOrder(CustomerOrder customerOrder);

	public List<OrderItem> findAllByCustomerOrderId(@Param("customerOrderId") Long customerOrderId);

}

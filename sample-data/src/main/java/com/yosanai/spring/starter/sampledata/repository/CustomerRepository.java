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

import com.querydsl.core.types.dsl.StringPath;
import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.model.QCustomer;

@RepositoryRestResource
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>,
		QueryByExampleExecutor<Customer>, QuerydslPredicateExecutor<Customer>, QuerydslBinderCustomizer<QCustomer> {

	@Override
	default public void customize(QuerydslBindings bindings, QCustomer root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}

	public List<Customer> findAllByLastName(@Param("lastName") String lastName);

	@Query("SELECT c.superSecretData FROM Customer c WHERE c.id = :id")
	public String getSuperSecretDataById(@Param("id") Long id);

}

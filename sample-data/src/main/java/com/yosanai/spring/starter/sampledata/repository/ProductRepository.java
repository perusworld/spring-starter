package com.yosanai.spring.starter.sampledata.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.querydsl.core.types.dsl.StringPath;
import com.yosanai.spring.starter.sampledata.model.Product;
import com.yosanai.spring.starter.sampledata.model.QProduct;

@RepositoryRestResource
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, QueryByExampleExecutor<Product>,
		QuerydslPredicateExecutor<Product>, QuerydslBinderCustomizer<QProduct> {

	@Override
	default public void customize(QuerydslBindings bindings, QProduct root) {
		bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
	}

	public Product findByName(@Param("name") String name);

}
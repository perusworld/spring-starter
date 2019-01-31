package com.yosanai.spring.starter.sampledata.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonView;
import com.yosanai.spring.starter.sampledata.Views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@JsonView(Views.Public.class)
/**
 * 
 * Product
 *
 */
public class Product extends Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	/**
	 * Id
	 */
	private Long id;
	@Column(unique = true)
	/**
	 * Product name
	 */
	private String name;
	/**
	 * Product description
	 */
	private String description;
	/**
	 * Product cost
	 */
	private int cost;

	public Product(String name, String description, int cost) {
		super();
		this.name = name;
		this.description = description;
		this.cost = cost;
	}

}

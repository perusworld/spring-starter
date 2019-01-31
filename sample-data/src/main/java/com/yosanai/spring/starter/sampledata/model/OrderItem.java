package com.yosanai.spring.starter.sampledata.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
 * Order Item
 *
 */
public class OrderItem implements Serializable {
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

	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonView(Views.Internal.class)
	/**
	 * Customer's order to which this order item is associated with
	 */
	private CustomerOrder customerOrder;

	@ManyToOne
	/**
	 * Product
	 */
	private Product product;

	/**
	 * Quantity
	 * 
	 */
	private int quantity;

	public OrderItem(Product product, int quantity) {
		super();
		this.product = product;
		this.quantity = quantity;
	}

	public OrderItem(CustomerOrder customerOrder, Product product, int quantity) {
		super();
		this.customerOrder = customerOrder;
		this.product = product;
		this.quantity = quantity;
	}

}

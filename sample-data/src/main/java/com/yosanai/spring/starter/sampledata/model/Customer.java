package com.yosanai.spring.starter.sampledata.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
 * Customer
 * 
 */
public class Customer extends Auditable {
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
	/**
	 * First name
	 */
	private String firstName;
	/**
	 * Last name
	 */
	private String lastName;
	/**
	 * Sample field that will be included only with internal view
	 */
	@JsonView(Views.Internal.class)
	private String sampleIgnoreInPublic;
	@OneToMany(mappedBy = "customer", orphanRemoval = true)
	@JsonView(Views.Internal.class)
	/**
	 * Orders by the customer
	 */
	private Set<CustomerOrder> orders;

	public void addItem(CustomerOrder order) {
		if (null == orders) {
			orders = new HashSet<>();
		}
		orders.add(order);
		order.setCustomer(this);
	}

	public void removeItem(CustomerOrder order) {
		if (null != orders) {
			orders.remove(order);
		}
		order.setCustomer(null);
	}

	public Customer(String firstName, String lastName, String sampleIgnoreInPublic) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.sampleIgnoreInPublic = sampleIgnoreInPublic;
	}

	public Customer(Long id) {
		super();
		this.id = id;
	}
	
	
	
}

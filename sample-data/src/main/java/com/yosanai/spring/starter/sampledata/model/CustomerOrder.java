package com.yosanai.spring.starter.sampledata.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.rest.core.annotation.RestResource;

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
@ToString(exclude = { "customer" })
@Entity
@JsonView(Views.Public.class)
/**
 * Customer Order
 *
 */
public class CustomerOrder extends Auditable {

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
	/**
	 * Customer to which the order is associated with
	 */
	@RestResource(exported = false)
	private Customer customer;

	@OneToMany(mappedBy = "customerOrder", orphanRemoval = true, cascade = CascadeType.ALL)
	/**
	 * Items in this order
	 */
	private Set<OrderItem> orderItems;

	/**
	 * Total cost of the order
	 */
	private int totalCost;

	public void addOrderItem(OrderItem item) {
		if (null == orderItems) {
			orderItems = new HashSet<>();
		}
		orderItems.add(item);
		item.setCustomerOrder(this);
		calculateTotal();
	}

	public void removeOrderItem(OrderItem item) {
		if (null != orderItems) {
			orderItems.remove(item);
		}
		item.setCustomerOrder(null);
		calculateTotal();
	}

	public CustomerOrder(Customer customer) {
		super();
		this.customer = customer;
	}

	@PrePersist
	@PreUpdate
	public void calculateTotal() {
		totalCost = 0;
		if (null != orderItems) {
			orderItems.forEach(item -> {
				if (null != item.getProduct()) {
					totalCost += item.getProduct().getCost() * item.getQuantity();
				}
			});
		}
	}
}

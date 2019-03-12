package com.yosanai.spring.starter.samplebatch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.yosanai.spring.starter.sampledata.model.CustomerOrder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerOrderProcessor implements ItemProcessor<CustomerOrder, CustomerOrder> {

	@Override
	public CustomerOrder process(CustomerOrder order) throws Exception {
		log.debug("Processing {}", order);
		return order;
	}

}

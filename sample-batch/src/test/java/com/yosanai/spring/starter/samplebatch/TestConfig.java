package com.yosanai.spring.starter.samplebatch;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.yosanai.spring.starter.sampledata.model.Auditable;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;

@EnableAutoConfiguration
@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackageClasses = { Auditable.class })
@EnableJpaRepositories(basePackageClasses = { CustomerRepository.class })
@ComponentScan(basePackages = { "com.yosanai" })
public class TestConfig {
	
	

}

package com.yosanai.spring.starter.samplerestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.yosanai.spring.starter.sampledata.model.Auditable;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackageClasses = { Auditable.class })
@EnableJpaRepositories(basePackageClasses = { CustomerRepository.class })
@ComponentScan(basePackages = { "com.yosanai" })
public class SampleRESTServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleRESTServiceApplication.class, args);
	}

}

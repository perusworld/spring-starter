package com.yosanai.spring.starter.sampledata;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableAutoConfiguration
@SpringBootConfiguration
@EnableJpaAuditing
@ComponentScan(basePackageClasses = TestConfig.class)
public class TestConfig {

}

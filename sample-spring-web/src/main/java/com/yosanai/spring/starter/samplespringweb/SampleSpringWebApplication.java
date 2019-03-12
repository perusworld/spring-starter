package com.yosanai.spring.starter.samplespringweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy;

@SpringBootApplication
@ComponentScan(basePackages = { "com.yosanai.spring.starter.samplespringweb" })
@EnableOAuth2Sso
public class SampleSpringWebApplication extends WebSecurityConfigurerAdapter {

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect(new GroupingStrategy());
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleSpringWebApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().antMatcher("/**").authorizeRequests()
				.antMatchers("/", "/login**", "/webjars/**", "/js/***", "/error**", "/sample-page", "/call-api",
						"/send-amqp-msg", "/actuator/***")
				.permitAll().anyRequest().authenticated().and().logout().logoutSuccessUrl("/").permitAll();
	}
}

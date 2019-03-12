package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { TestConfig.class }) })
public class BaseTest {

	public static final int BATCH_SIZE = 5;
	public static final int PAGE_STEP_SIZE = 2;

	@Autowired
	RandomDataGenerator randomDataGenerator;

	@PersistenceContext
	EntityManager entityManager;

	@Test
	public void initCheck() {
		assertNotNull(randomDataGenerator);
	}

	public void flush() {
		entityManager.flush();
		entityManager.clear();
	}

}

package com.yosanai.spring.starter.sampledata;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.yosanai.spring.starter.sampledata.model.Customer;
import com.yosanai.spring.starter.sampledata.repository.CustomerRepository;

public class CryptoTest extends BaseTest {

	@Autowired
	CustomerRepository customerRepository;

	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(customerRepository);
	}

	@Test
	public void checkCrypto() {
		Customer savedCustomer = randomDataGenerator.someCustomer();
		assertNotNull(savedCustomer);
		assertTrue(null != savedCustomer.getId());
		assertTrue(null != savedCustomer.getSampleIgnoreInPublic());
		String secretData = customerRepository.getSuperSecretDataById(savedCustomer.getId());
		assertEquals(savedCustomer.getSuperSecretData(), secretData);
		Session session = entityManager.unwrap(Session.class);
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement stmt = connection
						.prepareStatement("select super_secret_data from customer where id = ?");
				stmt.setLong(1, savedCustomer.getId());
				ResultSet results = stmt.executeQuery();
				assertTrue(results.next());
				assertEquals(savedCustomer.getSuperSecretData(),
						new String(Base64Utils.decodeFromString(results.getString(1)), Charset.defaultCharset()));
			}
		});
	}

}

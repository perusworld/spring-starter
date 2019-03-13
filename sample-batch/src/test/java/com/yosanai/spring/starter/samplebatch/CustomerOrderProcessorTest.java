package com.yosanai.spring.starter.samplebatch;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.starter.sampledata.RandomDataGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@ActiveProfiles("test")
@DirtiesContext
public class CustomerOrderProcessorTest {

	@Autowired
	@Qualifier("asyncJobLauncher")
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("exportCustomerOrder")
	Job job;

	@Autowired
	@Qualifier("jobDoneLatch")
	private CountDownLatch latch;

	@Autowired
	RandomDataGenerator randomDataGenerator;

	@Autowired
	ExportFilenamePathGenerator pathGenerator;

	@Before
	public void before() throws IOException {
		File parentDir = new File(pathGenerator.getNextPath(1)).getParentFile();
		parentDir.mkdirs();
		FileUtils.cleanDirectory(parentDir);
	}

	@Test
	public void txtCSVJobExport() throws Exception {
		int insertSize = 5;
		randomDataGenerator.someCustomerOrdersWithItems(randomDataGenerator.someCustomer(), insertSize,
				(products, orders) -> {
					try {
						String nextPath = pathGenerator.getNextPath(1);
						jobLauncher.run(job,
								new JobParametersBuilder()
										.addString(BatchConfiguration.EXPORT_CUSTOMER_ORDER_PATH_KEY, nextPath)
										.toJobParameters());
						latch.await();
						try (FileInputStream input = new FileInputStream(nextPath)) {
							assertEquals(insertSize, IOUtils.readLines(input, Charset.forName("UTF8")).size());
						}
					} catch (Exception e) {
						fail(e.getMessage());
					}
				});
	}

}

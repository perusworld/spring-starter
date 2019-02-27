package com.yosanai.spring.starter.samplesftpservice;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@ActiveProfiles("test")
public class SFTPSendReceiveTest {

	@Value("classpath:files/test-1.txt")
	private Resource testFileOne;

	@Value("classpath:files/test-2.txt")
	private Resource testFileTwo;

	@Autowired
	private TestSftpServer sftpServer;

	@Autowired
	@Qualifier("sampleIncomingFileListenerLatch")
	private CountDownLatch latch;

	@Autowired
	private SFTPSender sftpSender;

	@Before
	public void before() {
		sftpServer.start();
	}

	@After
	public void after() throws Exception {
		sftpServer.stop();
	}

	@Test
	public void checkReceiveFile() throws Exception {
		sftpSender.sendToSftp(testFileOne.getFile());
		sftpSender.sendToSftp(testFileTwo.getFile());
		latch.await();

	}

}

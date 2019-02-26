package com.yosanai.spring.starter.samplesftpservice;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@ActiveProfiles("test")
public class SFTPSendReceiveTest {

	@Value("classpath:files/test.txt")
	private Resource testFile;

	@Value("${samplesftp.remote.directory}")
	private String remoteDirectory;

	@Value("${samplesftp.local.directory}")
	private String localDirectory;

	@Autowired
	private TestSftpServer sftpServer;

	@Autowired
	@Qualifier("sftpSessionFactory")
	private SessionFactory<LsEntry> sessionFactory;

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
		FileUtils.forceDeleteOnExit(new File(localDirectory));
		sftpServer.stop();
	}

	@Test
	public void checkReceiveFile() throws Exception {
		sftpSender.sendToSftp(testFile.getFile());
		latch.await();

	}

}

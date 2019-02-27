package com.yosanai.spring.starter.samplesftpservice;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.util.SocketUtils;

@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan(basePackages = "com.yosanai.spring.starter.samplesftpservice")
public class TestConfig {

	@Value("${samplesftp.remote.directory}")
	private String initDir;

	@Value("${samplesftp.local.directory}")
	private String localDirectory;

	@Bean
	public TestSftpServer sftpServer(@Autowired DefaultSftpSessionFactory session) throws Exception {
		if (localDirectory.startsWith(FileUtils.getTempDirectoryPath())) {
			FileUtils.deleteDirectory(new File(localDirectory));
		}
		int port = SocketUtils.findAvailableTcpPort(2500);
		session.setPort(port);
		TestSftpServer ret = new TestSftpServer(port, initDir);
		return ret;
	}

}

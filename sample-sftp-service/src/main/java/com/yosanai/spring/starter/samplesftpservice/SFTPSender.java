package com.yosanai.spring.starter.samplesftpservice;

import java.io.File;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "toSftpChannel")
public interface SFTPSender {

	void sendToSftp(File file);

}
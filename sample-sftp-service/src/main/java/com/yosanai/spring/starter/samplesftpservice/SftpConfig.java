package com.yosanai.spring.starter.samplesftpservice;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.support.PeriodicTrigger;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@EnableAutoConfiguration
@SpringBootConfiguration
public class SftpConfig {
	@Value("${samplesftp.remote.host}")
	private String host;
	@Value("${samplesftp.remote.port}")
	private int port;
	@Value("${samplesftp.remote.user}")
	private String user;
	@Value("${samplesftp.remote.privateKey}")
	private Resource privateKey;
	@Value("${samplesftp.remote.password}")
	private String password;
	@Value("${samplesftp.remote.allowUnknowKeys}")
	private boolean allowUnknowKeys;
	@Value("${samplesftp.remote.testSession}")
	private boolean testSession;
	@Value("${samplesftp.remote.deleteRemoteFiles}")
	private boolean deleteRemoteFiles;
	@Value("${samplesftp.remote.directory}")
	private String remoteDirectory;
	@Value("${samplesftp.remote.filePattern}")
	private String filePattern;
	@Value("${samplesftp.local.directory}")
	private String localDirectory;
	@Value("${samplesftp.local.autoCreateDirectory}")
	private boolean autoCreateLocalDirectory;
	@Value("${samplesftp.inbound.maxFetchSize}")
	private int maxFetchSize;

	@Bean
	public CountDownLatch sampleIncomingFileListenerLatch(@Value("${samplesftp.latch}") int count) {
		return new CountDownLatch(count);
	}

	@Bean
	public DefaultSftpSessionFactory defaultSftpSessionFactory() {
		DefaultSftpSessionFactory ret = new DefaultSftpSessionFactory(true);
		ret.setHost(host);
		ret.setPort(port);
		ret.setUser(user);
		ret.setPrivateKey(privateKey);
		ret.setPassword(password);
		ret.setAllowUnknownKeys(allowUnknowKeys);
		return ret;
	}

	@Bean
	public CachingSessionFactory<LsEntry> sftpSessionFactory(
			@Autowired @Qualifier("defaultSftpSessionFactory") DefaultSftpSessionFactory sessionFactory) {
		CachingSessionFactory<LsEntry> ret = new CachingSessionFactory<>(sessionFactory);
		ret.setTestSession(testSession);
		return ret;
	}

	@Bean
	public SftpInboundFileSynchronizer sftpInboundFileSynchronizer(
			@Autowired @Qualifier("sftpSessionFactory") SessionFactory<LsEntry> sessionFactory,
			@Autowired @Qualifier("inboundFileFilter") FileListFilter<LsEntry> inboundFileFilter) {
		SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sessionFactory);
		fileSynchronizer.setDeleteRemoteFiles(deleteRemoteFiles);
		fileSynchronizer.setRemoteDirectory(remoteDirectory);
		fileSynchronizer.setFilter(inboundFileFilter);
		return fileSynchronizer;
	}

	@Bean
	public FileListFilter<LsEntry> inboundFileFilter() {
		CompositeFileListFilter<LsEntry> ret = new CompositeFileListFilter<>();
		ret.addFilter(new SftpSimplePatternFileListFilter(filePattern));
		ret.addFilter(new SftpPersistentAcceptOnceFileListFilter(new SimpleMetadataStore(), "prefix"));
		return ret;
	}

	@Bean
	public PeriodicTrigger inboundTrigger(@Value("${samplesftp.inbound.pollInterval}") long pollInterval,
			@Value("${samplesftp.inbound.initialDelay}") long initialDelay) {
		PeriodicTrigger ret = new PeriodicTrigger(pollInterval);
		ret.setInitialDelay(initialDelay);
		return ret;
	}

	@Bean
	@InboundChannelAdapter(value = "inboundChannel", poller = @Poller(trigger = "inboundTrigger"))
	public MessageSource<File> sftpMessageSource(@Autowired SftpInboundFileSynchronizer inboundFileSynchronizer) {
		SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(
				inboundFileSynchronizer);
		source.setLocalDirectory(new File(localDirectory));
		source.setAutoCreateLocalDirectory(autoCreateLocalDirectory);
		source.setLocalFilter(new AcceptOnceFileListFilter<File>());
		source.setMaxFetchSize(maxFetchSize);
		return source;
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	public MessageHandler handler(@Autowired @Qualifier("sftpSessionFactory") SessionFactory<LsEntry> sessionFactory) {
		SftpMessageHandler handler = new SftpMessageHandler(sessionFactory);
		handler.setRemoteDirectoryExpressionString("'" + remoteDirectory + "'");
		return handler;
	}

}

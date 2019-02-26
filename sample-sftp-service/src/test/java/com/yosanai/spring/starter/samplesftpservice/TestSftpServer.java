package com.yosanai.spring.starter.samplesftpservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSftpServer {

	public static final int PORT = 0;

	private final SshServer server = SshServer.setUpDefaultServer();

	@Value("${samplesftp.server.publicKeyFile}")
	private String publicKeyFile;

	@Value("${samplesftp.remote.directory}")
	private String remoteDirectory;

	private int port;

	private String initDir;

	@Value("${java.io.tmpdir}")
	private String tempDir;

	private boolean running;

	public TestSftpServer(int port, String initDir) {
		super();
		this.port = port;
		this.initDir = initDir;
	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		final PublicKey allowedKey = decodePublicKey();
		this.server.setPublickeyAuthenticator((username, key, session) -> key.equals(allowedKey));
		this.server.setPort(this.port);
		this.server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(tempDir, "hostkey.ser").toPath()));
		this.server.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
		final String pathname = tempDir + File.separator + "sftptest" + File.separator;
		new File(pathname, initDir).mkdirs();
		server.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(pathname)));
		FileUtils.forceDeleteOnExit(new File(pathname));
		log.debug("Using {}", pathname);
	}

	private PublicKey decodePublicKey() throws Exception {
		PublicKey ret = null;
		InputStream stream = new ClassPathResource(publicKeyFile).getInputStream();
		byte[] keyBytes = StreamUtils.copyToByteArray(stream);
		while (keyBytes[keyBytes.length - 1] == 0x0a || keyBytes[keyBytes.length - 1] == 0x0d) {
			keyBytes = Arrays.copyOf(keyBytes, keyBytes.length - 1);
		}
		byte[] decodeBuffer = Base64Utils.decode(keyBytes);
		ByteBuffer bb = ByteBuffer.wrap(decodeBuffer);
		int len = bb.getInt();
		byte[] type = new byte[len];
		bb.get(type);
		if ("ssh-rsa".equals(new String(type))) {
			BigInteger e = decodeBigInt(bb);
			BigInteger m = decodeBigInt(bb);
			RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
			ret = KeyFactory.getInstance("RSA").generatePublic(spec);
		} else {
			throw new IllegalArgumentException("Only supports RSA");
		}
		return ret;
	}

	private BigInteger decodeBigInt(ByteBuffer bb) {
		int len = bb.getInt();
		byte[] bytes = new byte[len];
		bb.get(bytes);
		return new BigInteger(bytes);
	}

	public void start() {
		try {
			this.server.start();
			this.running = true;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void stop() {
		if (this.running) {
			try {
				server.stop(true);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			} finally {
				this.running = false;
			}
		}
	}

}
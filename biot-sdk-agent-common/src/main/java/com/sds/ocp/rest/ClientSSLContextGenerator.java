package com.sds.ocp.rest;

import io.netty.handler.ssl.SslContext;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.util.StringUtil;

/**
 * SSL 인증 처리를 위한 클라이언트 용 SSL Context 생성 클래스.
 * 
 * @author 박성배
 * @since 2015. 4. 27.
 * 
 */
public class ClientSSLContextGenerator implements ISSLContextGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(ClientSSLContextGenerator.class);

	private static final String KEY_STORE_TYPE = "JKS";
	private static final String PROTOCOL = "TLS";
	private static final String DEFAULT_ALGORITHM = "SunX509";

	private SSLContext sslContext;

	private String algorithm = DEFAULT_ALGORITHM;
	protected String serverKeyStorePath = null;
	protected String serverKeyStorePassword = null;
	protected String caCertPath = null;

	public SSLContext getSSLContext() {
		return sslContext;
	}

	public SSLEngine getSSLEngine() {
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(true);
		return sslEngine;
	}
	
	public void init() {
		LOG.info("SSL CLIENT - HANDSHAKE");
		try {

			KeyManager[] keyManagers = null;

			TrustManager[] trustManagers = null;
			
			if (!StringUtil.isEmpty(serverKeyStorePath)) {
				LOG.debug("by key store");
				trustManagers = SSLUtil.createTrustManagerForClient(KEY_STORE_TYPE, serverKeyStorePath,
						serverKeyStorePassword, algorithm);
			} else if (!StringUtil.isEmpty(caCertPath)) {
				LOG.debug("by ca cert");
				trustManagers = SSLUtil.createTrustManagerForClient(caCertPath);
			} else {
				LOG.info("all cert.");
				trustManagers = SSLUtil.createTrustManagerAllCert(algorithm);
			}

			this.sslContext = SSLUtil.createSSLContext(PROTOCOL, keyManagers, trustManagers);

			LOG.info("SSL context created.");
		} catch (Throwable t) {
			LOG.error("Failed to create SSL context.", t);
			LOG.error("System will shut down!");
			System.exit(-1);
		}
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setServerKeyStorePath(String serverKeyStorePath) {
		this.serverKeyStorePath = serverKeyStorePath;
	}

	public void setServerKeyStorePassword(String serverKeyStorePassword) {
		this.serverKeyStorePassword = serverKeyStorePassword;
	}

	public void setCaCertPath(String caCertPath) {
		this.caCertPath = caCertPath;
	}

	public SslContext getNettySSlContext() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.sds.ocp.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.util.FileUtil;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * SSL Generator 클래스에서 공통적으로 사용하는 키 매니저 생성, 키 스토어 스트림 생성 로직 등을 유틸
 * 
 * @author 김성혜
 * @since 2015. 6. 18.
 */
public final class SSLUtil {
	private static final Logger	LOG	= LoggerFactory.getLogger(SSLUtil.class);

	public static SSLContext createSSLContext(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers)
			throws NoSuchAlgorithmException, KeyManagementException {

		SSLContext sslCtx = SSLContext.getInstance(protocol);
		sslCtx.init(keyManagers, trustManagers, new SecureRandom());

		return sslCtx;
	}

	public static SslContext createSSLContext(KeyManagerFactory kmf) throws NoSuchAlgorithmException, KeyManagementException,
			SSLException {
		final SslContextBuilder builder = SslContextBuilder.forServer(kmf);
		builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
		builder.sslProvider(SslProvider.OPENSSL);

		return builder.build();
	}

	public static SslContext createSSLContext(KeyManagerFactory kmf, TrustManagerFactory tmf) throws NoSuchAlgorithmException,
			KeyManagementException, SSLException {
		final SslContextBuilder builder = SslContextBuilder.forServer(kmf);
		builder.trustManager(tmf);
		builder.sslProvider(SslProvider.OPENSSL);
		builder.clientAuth(ClientAuth.REQUIRE);

		return builder.build();
	}

	public static KeyManager[] createKeyManagers(String keyStoreType, String keyStorePath, String keyStorePassword,
			String keyManagerPassword, String algorithm) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException {

		InputStream fin = null;
		try {
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			fin = createKeyStoreInputStream(keyStorePath);
			keyStore.load(fin, keyStorePassword.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(keyStore, keyManagerPassword.toCharArray());

			return kmf.getKeyManagers();
		} finally {
			// catch 를 하지는 않고 파일만 닫음
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				LOG.error(e.toString());
			}
		}
	}

	public static InputStream createKeyStoreInputStream(String keyStorePath) throws FileNotFoundException {
		// URL jksUrl = getClass().getClassLoader().getResource(keyStorePath);
		URL jksUrl = Thread.currentThread().getContextClassLoader().getResource(keyStorePath);

		if (jksUrl != null) {
			LOG.info("Starting with jks at {}, jks normal {}", jksUrl.toExternalForm(), jksUrl);
			// return getClass().getClassLoader().getResourceAsStream(keyStorePath);
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath);
		}
		LOG.info("jks not found in bundled resources, try on the filesystem");
		File jksFile = new File(keyStorePath);
		if (jksFile.exists()) {
			LOG.info("Using {} ", jksFile.getAbsolutePath());
			return new FileInputStream(jksFile);
		}
		LOG.warn("File {} doesn't exists", jksFile.getAbsolutePath());
		return null;
	}



	/**
	 * 서버를 인증하기 위한 클라이언트 용 TrustManager 목록 생성
	 * 
	 * @param keyStoreType
	 * @param serverKeyStorePath
	 * @param serverKeyStorePassword
	 * @param algorithm
	 * @return
	 */
	public static TrustManager[] createTrustManagerForClient(final String keyStoreType, final String serverKeyStorePath,
			final String serverKeyStorePassword, final String algorithm) {

		TrustManager[] trustManagers = new TrustManager[]{ new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				X509Certificate[] cert = null;
				/*
				 * nothing
				 */
				return cert;
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				InputStream in = null;
				try {
					LOG.debug("(checkServerTrusted) start checkServerTrusted.");
					KeyStore keyStore = KeyStore.getInstance(keyStoreType);
					in = createKeyStoreInputStream(serverKeyStorePath);
					keyStore.load(in, serverKeyStorePassword.toCharArray()); // Use default certification validation

					TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
					tmf.init(keyStore);
					TrustManager[] tms = tmf.getTrustManagers();
					for (TrustManager tm : tms) {
						((X509TrustManager) tm).checkServerTrusted(certs, authType);
					}
				} catch (CertificateException ce) {
					LOG.warn("checkServerTrusted failed.");
					throw ce;
				} catch (Throwable e) {
					LOG.warn("not implement client-certicate.");
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
						LOG.error(e.toString());
					}
				}
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				/*
				 * nothing
				 */
			}
		} };

		return trustManagers;
	}

	/**
	 * 서버를 인증하기 위한 클라이언트 용 TrustManager 목록 생성
	 * 
	 * @param caCertPath
	 *        (e.g. cacert.pem)
	 * @param algorithm
	 * @return
	 */
	public static TrustManager[] createTrustManagerForClient(final String caCertPath) {

		TrustManager[] trustManagers = new TrustManager[]{ new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				X509Certificate[] cert = null;
				/*
				 * nothing
				 */
				return cert;
			}


			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				InputStream in = null;
				try {
					LOG.debug("(checkServerTrusted) start checkServerTrusted.");
					in = FileUtil.getFileInputStream(caCertPath);
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					X509Certificate caCert = (X509Certificate) cf.generateCertificate(in);

					for (X509Certificate cert : certs) {
						if (!cert.equals(caCert)) {
							cert.verify(caCert.getPublicKey()); // check if it has been signed by your CA.
						}
						cert.checkValidity(); // check if it has expired.
					}
				} catch (CertificateException ce) {
					LOG.warn("checkServerTrusted failed.");
					throw ce;
				} catch (Throwable e) {
					LOG.warn("not implement client-certicate.");
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
						LOG.error(e.toString());
					}
				}
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				/*
				 * nothing
				 */
			}
		} };

		return trustManagers;
	}

	/**
	 * 서버를 인증하기 위한 클라이언트 용 TrustManager 목록 생성 (공인 인증서)
	 * 
	 * @param algorithm
	 * @return
	 */
	public static TrustManager[] createTrustManagerAllCert(final String algorithm) {

		TrustManager[] trustManagers = new TrustManager[]{ new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				X509Certificate[] cert = null;
				/*
				 * nothing
				 */
				LOG.debug("ALL CERT");
				return cert;
			}


			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				try {
					LOG.debug("(checkServerTrusted) start checkServerTrusted.");
					TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
					KeyStore keyStore = null;
					tmf.init(keyStore);
					TrustManager[] tms = tmf.getTrustManagers();
					for (TrustManager tm : tms) {
						((X509TrustManager) tm).checkServerTrusted(certs, authType);
					}
				} catch (CertificateException ce) {
					LOG.warn("checkServerTrusted failed.");
					throw ce;
				} catch (Throwable e) {
					LOG.warn("not implement client-certicate.");
				}
			}


			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
				/*
				 * nothing
				 */
				LOG.debug("ALL CERT");
			}
		} };

		return trustManagers;
	}
	
	public static KeyManagerFactory createKeyManagerFactory(String keyStoreType, String keyStorePath, String keyStorePassword,
			String keyManagerPassword, String algorithm) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException {

		InputStream fin = null;
		try {
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			fin = createKeyStoreInputStream(keyStorePath);
			keyStore.load(fin, keyStorePassword.toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(keyStore, keyManagerPassword.toCharArray());

			return kmf;
		} finally {
			// catch 를 하지는 않고 파일만 닫음
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				LOG.error(e.toString());
			}
		}
	}
	
	/**
	 * 서버를 인증하기 위한 클라이언트 용 TrustManager 목록 생성
	 * 
	 * @param keyStoreType
	 * @param clientKeyStorePath
	 * @param clientKeyStorePassword
	 * @param algorithm
	 * @return
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static TrustManagerFactory createTrustManagerFactoryForClient(final String keyStoreType,
			final String clientKeyStorePath, final String clientKeyStorePassword, final String algorithm) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		InputStream in = null;
		
		try {
			LOG.debug("(createTrustManagerFactoryForClient) start checkServerTrusted.");
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			in = createKeyStoreInputStream(clientKeyStorePath);
			keyStore.load(in, clientKeyStorePassword.toCharArray()); // Use default certification validation

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
			tmf.init(keyStore);
			
			return tmf;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				LOG.error(e.toString());
			}
		}
	}
}

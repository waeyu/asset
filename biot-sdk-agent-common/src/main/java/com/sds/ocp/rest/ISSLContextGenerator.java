
package com.sds.ocp.rest;

import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * SSL Context 를 생성하는 인터페이스
 * 
 * @author 김태호
 */
public interface ISSLContextGenerator {

	void init();

	SSLContext getSSLContext();

	SslContext getNettySSlContext();

	SSLEngine getSSLEngine();
}

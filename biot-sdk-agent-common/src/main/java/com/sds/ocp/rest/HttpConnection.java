package com.sds.ocp.rest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.OcpException;
import com.sds.ocp.StatusEnum;
import com.sds.ocp.util.FileUtil;

/**
 * HTTP Client 로써 HTTP Server 로 HTTP Request 를 송신하고 HTTP Response 를 수신하는 기능이 구현된 클래스.
 * 
 * @author 김태호
 * @author 김성혜
 * 
 */
public class HttpConnection {

	private static final Logger LOG = LoggerFactory.getLogger(HttpConnection.class);

	private ISSLContextGenerator sslContextGenerator = null;

	public void setSslContextGenerator(ISSLContextGenerator sslContextGenerator) {
		this.sslContextGenerator = sslContextGenerator;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public String get(String url) throws MalformedURLException, IOException {
		return get(url, null);
	}

	/**
	 * 
	 * @param url
	 * @param requestHeaders
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public String get(String url, Map<String, Object> requestHeaders) throws MalformedURLException, IOException {
		byte[] resultBytes = request("GET", new URL(url), null, 2000, 5000, requestHeaders);
		return new String(resultBytes, FileUtil.CHARSET_UTF8);
	}

	/**
	 * 
	 * @param url
	 * @param msgStr
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public String post(String url, String msgStr, Map<String, Object> requestHeaders) throws MalformedURLException, IOException {
		byte[] msgBytes = msgStr.getBytes(FileUtil.CHARSET_UTF8);
		byte[] resultBytes = post(url, msgBytes,requestHeaders);
		return new String(resultBytes, FileUtil.CHARSET_UTF8);
	}

	/**
	 * 
	 * @param url
	 * @param msgBytes
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public byte[] post(String url, byte[] msgBytes, Map<String, Object> requestHeaders) throws MalformedURLException, IOException {
		return request("POST", new URL(url), msgBytes, 2000, 10000, requestHeaders);
	}

	/**
	 * 
	 * @param url
	 * @param msgStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String put(String url, String msgStr) throws MalformedURLException, IOException {
		byte[] msgBytes = msgStr.getBytes(FileUtil.CHARSET_UTF8);
		byte[] resultBytes = request("PUT", new URL(url), msgBytes, 2000, 10000);
		return new String(resultBytes, FileUtil.CHARSET_UTF8);
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String delete(String url) throws MalformedURLException, IOException {
		byte[] resultBytes = request("DELETE", new URL(url), null, 2000, 5000, null);
		return new String(resultBytes, FileUtil.CHARSET_UTF8);
	}

	/**
	 * 
	 * @param method
	 * @param url
	 * @param msg
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws IOException
	 */
	public byte[] request(String method, URL url, byte[] msg, int connectTimeout, int readTimeout) throws IOException {
		return request(method, url, msg, connectTimeout, readTimeout, null);
	}

	/**
	 * 
	 * @param method
	 * @param url
	 * @param msg
	 * @param connectTimeout
	 * @param readTimeout
	 * @param requestHeaders
	 * @return
	 * @throws IOException
	 */
	public byte[] request(String method, URL url, byte[] msg, int connectTimeout, int readTimeout,
			Map<String, Object> requestHeaders) throws IOException {

		HttpURLConnection connection = null;

		try {

			connection = this.getConnection(method, url, connectTimeout, readTimeout);

			if (requestHeaders != null) {
				for (Entry<String, Object> e : requestHeaders.entrySet()) {
					connection.setRequestProperty(e.getKey(), e.getValue().toString());
					LOG.debug("{}", e.getKey());
				}
			}

			if ("POST".equals(method)) {
				if (msg == null || msg.length == 0) {
					throw new OcpException(StatusEnum.BAD_REQUEST, "message should be not null by POST method.");
				}
				httpSend(connection, msg);
			}

			// 이 메서드에서 실제로 socket connect 를 수행
			// read timed out 발생 시 IOException 을 던짐
			int resCode = connection.getResponseCode();

			if (!isResOk(resCode)) {
				StatusEnum errorCode = StatusEnum.fromString(String.valueOf(resCode));
				throw new OcpException(errorCode, "received error response [" + url.toString() + "] response code = [" + resCode + "]");
			}

			return getReturnValueByte(connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * 
	 * @param method
	 * @param url
	 * @param connectTimeout
	 *            an int that specifies the connect timeout value in milliseconds
	 * @param readTimeout
	 *            an int that specifies the connect timeout value in milliseconds
	 * @return
	 */
	private HttpURLConnection getConnection(String method, URL url, int connectTimeout, int readTimeout) {
		HttpURLConnection connection = null;

		try {
			if ("https".equals(url.getProtocol())) {
				LOG.info("HTTPS CONNECT: {} {}", method, url);
				connection = (HttpsURLConnection) url.openConnection();
				setSSL((HttpsURLConnection) connection);
			} else {
				LOG.info("HTTP CONNECT: {} {}", method, url);
				connection = (HttpURLConnection) url.openConnection();
			}

			connection.setDoOutput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);

			try {
				connection.setRequestMethod(method.toUpperCase(Locale.getDefault()));
			} catch (ProtocolException e) {
				throw new OcpException(StatusEnum.INTERNAL_SERVER_ERROR, e.toString(), e);
			}

		} catch (IOException e) {
			throw new OcpException(StatusEnum.BAD_GATEWAY, e.toString(), e);
		}

		return connection;
	}

	private void setSSL(HttpsURLConnection connection) {
		if (sslContextGenerator == null) {
			throw new IllegalStateException("No sslContextGenerator!!");
		}
		
		sslContextGenerator.init(); // init 이 안된 경우에 init 됨
		
		connection.setSSLSocketFactory(sslContextGenerator.getSSLContext().getSocketFactory());

		connection.setHostnameVerifier(new HostnameVerifier() {

			public boolean verify(String paramString, SSLSession paramSSLSession) {
				return true;
			}
		});
	}

	/**
	 * 
	 * @param method
	 * @param urlList
	 * @param msg
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public String httpMultiRequest(String method, List<URL> urlList, String msg, int connectTimeout, int readTimeout) {

		for (URL url : urlList) {
			try {
				byte[] msgBytes = null;
				if (msg != null) {
					msgBytes = msg.getBytes(FileUtil.CHARSET_UTF8);
				}

				byte[] resultBytes = this.request(method, url, msgBytes, connectTimeout, readTimeout);

				if (resultBytes != null && resultBytes.length != 0) {
					return new String(resultBytes, FileUtil.CHARSET_UTF8);
				}

				LOG.error("HTTP Request failed. {}", url.toString());
			} catch (Throwable e) {
				LOG.error("HTTP Request failed. {}", url.toString());
			}
		}

		return null;
	}

	private boolean isResOk(int resCode) {

		if (resCode == HttpURLConnection.HTTP_OK || resCode == HttpURLConnection.HTTP_CREATED
				|| resCode == HttpURLConnection.HTTP_ACCEPTED || resCode == HttpURLConnection.HTTP_NOT_AUTHORITATIVE
				|| resCode == HttpURLConnection.HTTP_NO_CONTENT || resCode == HttpURLConnection.HTTP_RESET
				|| resCode == HttpURLConnection.HTTP_PARTIAL) {
			return true;
		} else {
			return false;
		}
	}

	private byte[] getReturnValueByte(HttpURLConnection connection) throws IOException {
		InputStream is = null;
		try {
			is = connection.getInputStream();
		} catch (IOException e) {
			throw new OcpException(StatusEnum.BAD_GATEWAY, "get inputStream fail", e);
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			data = buffer.toByteArray();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch(IOException e) {
					LOG.info(e.toString());
				}
			}
			if(buffer != null) {
				try {
					buffer.close();
				} catch(IOException e) {
					LOG.info(e.toString());
				}
			}
		}
		
		return data;
	}

	private void httpSend(HttpURLConnection connection, byte[] content) {

		OutputStream os = null;
		try {
			os = connection.getOutputStream();
		} catch (IOException e) {
			//throw new OcpException(e.toString());			
			throw new OcpException(StatusEnum.INTERNAL_SERVER_ERROR, e.toString(), e);
			
		}

		DataOutputStream wr = new DataOutputStream(os);

		try {
			wr.write(content);
			wr.flush();
		} catch (UnsupportedEncodingException e) {
			throw new OcpException(StatusEnum.BAD_REQUEST, e.toString(), e);
		} catch (IOException e) {
			throw new OcpException(StatusEnum.CHANNEL_SEND_FAIL, e.toString(), e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					LOG.info(e.toString());
				}
			}
		}
	}
	
}

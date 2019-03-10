package com.sds.ocp;

import com.sds.ocp.StatusEnum;

/**
 */
@SuppressWarnings("serial")
public class OcpException extends RuntimeException implements IOcpException {

	private final StatusEnum errorCode;
	
	public OcpException(String message) {
		super(message);
		this.errorCode = StatusEnum.UNCLASSIFIED_FAIL;
	}

	public OcpException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = StatusEnum.UNCLASSIFIED_FAIL;
	}

	public OcpException(Throwable cause) {
		super(cause == null ? null : cause.toString(), cause);
		this.errorCode = StatusEnum.UNCLASSIFIED_FAIL;
	}
	
	public OcpException(StatusEnum errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public OcpException(StatusEnum errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public OcpException(StatusEnum errorCode, Throwable cause) {
		super(cause == null ? null : cause.toString(), cause);
		this.errorCode = errorCode;
	}

	public StatusEnum getErrorCode() {
		return this.errorCode;
	}
	
}

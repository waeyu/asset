
package com.sds.ocp;

/**
 * MomHeader / MomMessage 의 Status 값
 * 
 * @usage StatusEnum.DEFAULT_STATUS.code() : "000" <br/>
 *        StatusEnum.DEFAULT_STATUS.name() : "DEFAULT_STATUS"
 * @author 김태호
 */
public enum StatusEnum{

	/*
	 * == HTTP STATUS CODE ==
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	 */

	// Successful 2xx
	OK("200"), CREATED("201"), ACCEPTED("202"), NON_AUTHORITATIVE_INFORMATION("203"), NO_CONTENT("204"),

	// Client Error 4xx
	BAD_REQUEST("400"), UNAUTHORIZED("401"), PAYMENT_REQUIRED("402"), FORBIDDEN("403"), NOT_FOUND("404"), METHOD_NOT_ALLOWED("405"), NOT_ACCEPTABLE("406"), PROXY_AUTHENTICATION("407"), REQUEST_TIMEOUT(
			"408"), CONFLICT("409"), PRECONDITION_FAILED("412"), UNSUPPORTED_MEDIA_TYPE("415"),

	// Server Error 5xx
	INTERNAL_SERVER_ERROR("500"), NOT_IMPLEMENTED("501"), BAD_GATEWAY("502"), SERVICE_UNAVAILABLE("503"), GATEWAY_TIMEOUT("504"), HTTP_VERSION_NOT_SUPPORTED("505"),

	/* == STATUS CODE == */

	UNCLASSIFIED_FAIL("999"),

	//	CHANNEL_RECV_FAIL("910"),
	CHANNEL_SEND_FAIL("920"), PARSING_FAIL("930"),
	//	INTERNAL_SEND_FAIL("921"),
	;

	private String	cd;
	
	private static final char	SUCCESS_STATUS_START		= '2';
	private static final char	CLIENT_ERROR_STATUS_START	= '4';
	private static final char	SERVER_ERROR_STATUS_START	= '5';


	StatusEnum(String code) {
		this.cd = code;
	}


	/**
	 * @return 3자리 숫자로 된 상태 코드 (e.g. 200)
	 */
	public String code() {
		return this.cd;
	}

	public boolean isSuccess() {
		if (this.cd.charAt(0) == SUCCESS_STATUS_START) {
			return true;
		}
		return false;
	}

	public boolean isClientError() {
		if (this.cd.charAt(0) == CLIENT_ERROR_STATUS_START) {
			return true;
		}
		return false;
	}

	public boolean isServerError() {
		if (this.cd.charAt(0) == SERVER_ERROR_STATUS_START) {
			return true;
		}
		return false;
	}

	public boolean isError() {
		return !isSuccess(); // 반대
	}

	@Override
	public String toString() {
		return this.code();
	}

	public static StatusEnum fromString(String statusCode) {

		for (StatusEnum e : StatusEnum.values()) {
			if (e.code().equals(statusCode)) {
				return e;
			}
		}
		return UNCLASSIFIED_FAIL;
	}

}

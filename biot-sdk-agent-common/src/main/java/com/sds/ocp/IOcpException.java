package com.sds.ocp;

import com.sds.ocp.StatusEnum;

/**
 * StatusEnum 을 errorCode 로 가질 수 있는 AFI 익셉션 인터페이스
 * 
 * @author 김태호 <th71.kim@samsung.com>
 * @since 2011. 11. 18.
 *
 */
public interface IOcpException {
	StatusEnum getErrorCode();
}

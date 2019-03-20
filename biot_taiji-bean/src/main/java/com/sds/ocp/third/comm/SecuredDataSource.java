package com.sds.ocp.third.comm;

import java.util.Locale;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.common.constant.DatabaseInfo;
import com.sds.ocp.common.util.StringUtil;

/**
 * apache commons dbcp BasicDataSource 에 패스워드 단순 암복호화를 적용한 클래스. <br/>
 * 추가적으로 dbType 값을 설정하면, DatabaseInfo Enum 에 있는 dbType 인 경우, <br/>
 * driverClassName, validationQuery 도 자동 설정해주는 기능이 포함되어 있다. 
 *  
 * @author 김태호 <th71.kim@samsung.com>
 * @author 김성혜
 * @since 2011. 11. 12.
 *
 */
public class SecuredDataSource extends BasicDataSource {

	private static final Logger LOG = LoggerFactory.getLogger(SecuredDataSource.class);

	/**
	 * DBMS Type (e.g. ORACLE, MYSQL, TIBERO ..) 을 설정하면 <br/>
	 * driverClassName, validationQuery 이 자동으로 설정되는 메서드. <br/>
	 * 단, DatabaseInfo enum 클래스에 지정된 타입만 처리 가능
	 * 
	 * @param dbType
	 */
	@Deprecated
	public void setDbType(String dbType) {
		if (StringUtil.isEmpty(dbType)) {
			LOG.debug("dbType is empty");
			return;
		}
		
		DatabaseInfo dbInfo = DatabaseInfo.valueOf(dbType.toUpperCase(Locale.getDefault()));
		
		this.driverClassName = dbInfo.driverClassName;
		this.validationQuery = dbInfo.validationQuery;

		LOG.info(dbType + " : " + this.driverClassName);
	}
	
	@Override
	public void setValidationQuery(String validationQuery) {
		if (!StringUtil.isEmpty(validationQuery)) {
			LOG.info("validationQuery=[" + validationQuery + "]");
			super.setValidationQuery(validationQuery);
		}
	}
	
	public void setPassword(String password) {
		if (StringUtil.isEmpty(password)) {
			LOG.debug("password is empty");
			return;
		}
		//log.debug(password);

		String decryptPasswd = SecuredDataSourceEncrypt.decrypt(password);
		this.password = decryptPasswd;
	}
}

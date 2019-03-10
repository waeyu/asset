package com.sds.afi.dao.paging;


import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.sds.afi.dao.DatabaseInfo;
import com.sds.ocp.util.StringUtil;

public final class PagingSQLGeneratorFactory {

	private static final Logger LOG = LoggerFactory.getLogger(PagingSQLGeneratorFactory.class);
	
	private PagingSQLGeneratorFactory() {
		throw new AssertionError();
	}

	public static IPagingSQLGenerator createPagingSQLGenerator(DataSource dataSource) {
		if (dataSource == null) {
			LOG.debug("datasource is null.");
			return null;
		}

		String driverClassName = getDriverClassNameByDataSource(dataSource);
		
		// create pagingSqlGenerator by driverClassName
		if (driverClassName != null) {
			//LOG.debug("driverClassName : {}", driverClassName);
			return createPagingSQLGenerator(driverClassName);
		}
		
		LOG.warn("cannot create PagingSQLGenerator. dataSource type={}", dataSource.getClass().getCanonicalName());
		return null;
	}
	
	private static String getDriverClassNameByDataSource(DataSource dataSource) {
		if (dataSource instanceof BasicDataSource) {
			return ((BasicDataSource) dataSource).getDriverClassName();
		} else if (dataSource instanceof SimpleDriverDataSource) {
			return ((SimpleDriverDataSource) dataSource).getDriver().getClass().getCanonicalName();
		}
		// === c3p0 dataSource === 
		// dataSource.getClass().getSimpleName().equals(ComboPooledDataSource)
		// return invokeGetterMethod(dataSource, "getDricerClass", String.class);
		String driverClassName = invokeGetterMethodNoException(dataSource, "getDriverClass", String.class);
		if (driverClassName != null) {
			return driverClassName;
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	private static <T> T invokeGetterMethodNoException(Object vo, String getterMethodName, Class<T> returnType) {
		try {
			Method getter = vo.getClass().getMethod(getterMethodName);
			if (!returnType.isAssignableFrom(getter.getReturnType())) {
				if (returnType == String.class) {
					Object valueObject = getter.invoke(vo);
					return (T) String.valueOf(valueObject);
				} else {
					LOG.debug("Invalid returnType : " + getter.getReturnType().getName() + " != " + returnType.getName());
					return null;
				}
			} else {
				return (T) getter.invoke(vo);
			}
		} catch (Exception e) {
			LOG.debug(e.toString());
		}
		return null;
	}

	private static IPagingSQLGenerator createPagingSQLGenerator(String driverClassName) {
		if (StringUtil.isEmpty(driverClassName)) {
			LOG.warn("driverClassName is empty");
			return null;
		}
		
		DatabaseInfo dbinfo = DatabaseInfo.fromDriverClassName(driverClassName);
		if (dbinfo == null) {
			// 데이터소스의 내용을 설정하지 않은 경우 dbinfo 가 null 일 수 있음.
			LOG.warn("cannot find DBMS info by driverClassName={}", driverClassName);
			return null;
		}

		return createPagingSQLGenerator(dbinfo);
	}

	private static IPagingSQLGenerator createPagingSQLGenerator(DatabaseInfo dbinfo) {
		IPagingSQLGenerator pagingSqlGenerator = null;
		switch (dbinfo) {
		case ORACLE:
			pagingSqlGenerator = new OraclePagingSQLGenerator();
			break;
		case MSSQL:
			pagingSqlGenerator = new SQLServerPagingSQLGenerator();
			break;
		case DB2:
			pagingSqlGenerator = new DB2PagingSQLGenerator();
			break;
		case MARIA:
			pagingSqlGenerator = new MySQLPagingSQLGenerator();
			break;
		case POSTGRESQL:
			pagingSqlGenerator = new PostgreSQLPagingSQLGenerator();
			break;
		case HSQLDB:
		case HSQLDB1:
			pagingSqlGenerator = new HSQLPagingSQLGenerator();
			break;
		default:
			LOG.warn("No PagingSqlGenerator Class for this DBMS Type. [" + dbinfo.name() + "]");
			break;
		}

		return pagingSqlGenerator;
	}
}

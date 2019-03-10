package com.sds.afi.dao;

import java.util.Locale;

/**
 * DBMS 별로 JDBC driver class name, Connection string, Validation query 가 정의된 enum 클래스 
 * 
 * @author 김성혜
 *
 */
public enum DatabaseInfo {

	ORACLE		("oracle.jdbc.driver.OracleDriver", 
				 "SELECT 1 FROM DUAL",
				 "jdbc:oracle:thin:@%s:%s:%s"), 
	
	MSSQL		("com.microsoft.sqlserver.jdbc.SQLServerDriver",
				 "SELECT 1", 
				 "jdbc:sqlserver://%s:%s;databaseName=%s"), 
	
	DB2			("com.ibm.db2.jcc.DB2Driver", // COM.ibm.db2.jdbc.app.DB2Driver ??
				 "SELECT 1 FROM SYSIBM.SYSTABLES",
				 "jdbc:db2://%s:%s/%s"), 
	
	TIBERO		("com.tmax.tibero.jdbc.TbDriver",
				 "SELECT 1 FROM DUAL",
				 "jdbc:tibero:thin:@%s:%s:%s"),
			
	POSTGRESQL	("org.postgresql.Driver",
				 "SELECT 1",
				 "jdbc:postgresql://%s:%s/%s"),
			
	MARIA		("org.mariadb.jdbc.Driver",
				 "SELECT 1",
				 "jdbc:mariadb://%s:%s/%s"),


	// HSQL 1.8
	HSQLDB1     ("org.hsqldb.jdbcDriver", 
			     "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS", 
			     "jdbc:hsqldb:hsql://%s:%s/%s"),

    // 2.3
	HSQLDB		("org.hsqldb.jdbc.JDBCDriver", 
				 "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS", 
				 "jdbc:hsqldb:hsql://%s:%s/%s"),
				 
//	HSQLDB_FILE	("org.hsqldb.jdbc.JDBCDriver", 
//				 "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS", 
//				 "jdbc:hsqldb:res:%s"), // filepath

	VOLT		("org.voltdb.jdbc.Driver", 
			     "SELECT 1", 
			     "jdbc:voltdb://%s:%s"), 
	
	;
	
	public final String driverClassName;
	public final String validationQuery;
	private final String urlFormat;

	DatabaseInfo(String driverClassName, String validationQuery, String urlFormat) {
		this.driverClassName = driverClassName;
		this.validationQuery = validationQuery;
		this.urlFormat = urlFormat;
	}
	
	public static String getDriverClassName(String dbType) {
		try {
			return DatabaseInfo.valueOf(dbType.toUpperCase(Locale.getDefault())).driverClassName;
		} catch (Throwable e) {
			throw new IllegalArgumentException("Unknown DBMS Type [" + dbType + "]", e);
		}
	}
	
	public static String getValidationQuery(String dbType) {
		try {
			return DatabaseInfo.valueOf(dbType.toUpperCase(Locale.getDefault())).validationQuery;
		} catch (Throwable e) {
			throw new IllegalArgumentException("Unknown DBMS Type [" + dbType + "]", e);
		}
	}

	public static String getUrl(String dbType, String host, String port, String inst) {
		try {
			DatabaseInfo dbInfo = DatabaseInfo.valueOf(dbType.toUpperCase(Locale.getDefault()));
			return String.format(dbInfo.urlFormat, host, port, inst);
		} catch(Throwable e) {
			throw new IllegalArgumentException("Unknown DBMS Type [" + dbType + "]", e);
		}
	}

	/**
	 * Driver Class Name 으로부터 enum 값 리턴
	 * 
	 * @param driverClassName
	 * @return
	 */

	public static DatabaseInfo fromDriverClassName(String driverClassName) {
		for (DatabaseInfo e : DatabaseInfo.values()) {
			if (e.driverClassName.equals(driverClassName)) {
				return e;
			}
		}
		return null;
	}

}

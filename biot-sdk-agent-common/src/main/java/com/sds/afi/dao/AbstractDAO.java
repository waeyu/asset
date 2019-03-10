
package com.sds.afi.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import com.sds.ocp.OcpException;
import com.sds.ocp.util.VelocityUtil;

@SuppressWarnings("restriction")
public abstract class AbstractDAO {

	private static final Logger				LOGGER			= LoggerFactory.getLogger(AbstractDAO.class);

	/**
	 */
	protected long							minSelectTime	= 2000L;

	protected NamedParameterJdbcTemplate	jdbcTemplate	= null;
	private DataSource						dataSource		= null;
	private DatabaseInfo					databaseInfo	= null;

	static final int						SIZE_ONE		= 1;

	public void setDataSource(DataSource dataSource) {
		JdbcTemplate classicJdbcTemplate = new JdbcTemplate(dataSource);
		setExceptionTranslator(classicJdbcTemplate);
		this.jdbcTemplate = new NamedParameterJdbcTemplate(classicJdbcTemplate);
		this.dataSource = dataSource;
		this.databaseInfo = initDatabaseInfo(dataSource);
	}

	public DatabaseInfo getDatabaseInfo() {
		return databaseInfo;
	}

	private DatabaseInfo initDatabaseInfo(DataSource dataSource) {
		if (dataSource == null) {
			LOGGER.debug("datasource is null.");
			return null;
		}
		String driverClassName = getDriverClassNameByDataSource(dataSource);
		if (driverClassName != null) {
			DatabaseInfo dbinfo = DatabaseInfo.fromDriverClassName(driverClassName);
			if (dbinfo == null) {
				// �뜲�씠�꽣�냼�뒪�쓽 �궡�슜�쓣 �꽕�젙�븯吏� �븡�� 寃쎌슦 dbinfo 媛� null �씪 �닔 �엳�쓬.
				LOGGER.warn("cannot find DBMS info by driverClassName={}", driverClassName);
			}
			return dbinfo;
		}
		LOGGER.warn("cannot init DBMS info. dataSource type={}", dataSource.getClass().getCanonicalName());
		return null;
	}

	/**
	 * @param dataSource
	 * @param jdbcTemplate
	 */
	protected void setExceptionTranslator(JdbcTemplate classicJdbcTemplate) {
		if (classicJdbcTemplate.getDataSource() instanceof BasicDataSource) {
			String jdbcDriver = ((BasicDataSource) classicJdbcTemplate.getDataSource()).getDriverClassName();
			// LOGGER.debug("JDBC Driver Class : " + jdbcDriver);
			if (DatabaseInfo.MSSQL.driverClassName.equals(jdbcDriver)) {
				LOGGER.debug("[SqlServer] set ExceptionTranslator");
				classicJdbcTemplate.setExceptionTranslator(new DuplExceptionTranslatorForSqlServer());
			} else if (DatabaseInfo.DB2.driverClassName.equals(jdbcDriver)) {
				LOGGER.debug("[DB2] set ExceptionTranslator");
				classicJdbcTemplate.setExceptionTranslator(new DuplExceptionTranslatorForDB2());
			}
		}
	}

	/**
	 */
	static class DuplExceptionTranslatorForSqlServer extends SQLErrorCodeSQLExceptionTranslator {

		@Override
		public DataAccessException translate(String task, String sql, SQLException sqlEx) {
			LOGGER.debug("errorCode=" + sqlEx.getErrorCode() + ", sqlState=" + sqlEx.getSQLState());
			// LOGGER.debug("message=" + ex.getMessage());

			if (sqlEx.getErrorCode() == 2627 && sqlEx.getMessage().contains("Violation of PRIMARY KEY constraint")) {
				LOGGER.debug("change excpetion type : " + sqlEx.getClass().getName()
						+ "==> java.sql.SQLIntegrityConstraintViolationException");
				SQLException newEx = new java.sql.SQLIntegrityConstraintViolationException(sqlEx.getMessage(), sqlEx.getSQLState(),
						sqlEx.getErrorCode(), sqlEx);
				return getFallbackTranslator().translate(task, sql, newEx);
			}

			return super.translate(task, sql, sqlEx);
		}
	}

	/**
	 */
	static class DuplExceptionTranslatorForDB2 extends SQLErrorCodeSQLExceptionTranslator {

		/*
		 * com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: DB2 SQL Error: SQLCODE=-803, SQLSTATE=23505,
		 * SQLERRMC=1;
		 */
		@Override
		public DataAccessException translate(String task, String sql, SQLException sqlEx) {
			LOGGER.debug("errorCode=" + sqlEx.getErrorCode() + ", sqlState=" + sqlEx.getSQLState());
			// LOGGER.debug("message =" + sqlEx.getMessage());
			// LOGGER.debug("error class =" + sqlEx.getClass().getName());

			if ("com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException".equals(sqlEx.getClass().getName())) {
				LOGGER.debug("change excpetion type : " + sqlEx.getClass().getName()
						+ "==> java.sql.SQLIntegrityConstraintViolationException");
				SQLException newEx = new java.sql.SQLIntegrityConstraintViolationException(sqlEx.getMessage(), sqlEx.getSQLState(),
						sqlEx.getErrorCode(), sqlEx);
				return getFallbackTranslator().translate(task, sql, newEx);
			}

			// batchUpdateException�떆 �떎�젣 �삤瑜� �궡�슜�쓣 �븣湲� �쐞�븳 濡쒓렇 異붽�
			if ("com.ibm.db2.jcc.am.BatchUpdateException".equals(sqlEx.getClass().getName())) {
				LOGGER.warn("BatchUpdateException");
				LOGGER.warn("sql=[" + sql + "]");
				SQLException childEx = sqlEx.getNextException();
				while (childEx != null) {
					LOGGER.warn("  -->  " + childEx.toString());
					childEx = childEx.getNextException();
				}
			}
			return super.translate(task, sql, sqlEx);
		}
	}

	/* ========================================================================= */

	protected void executeSqlScript(EncodedResource resource, boolean continueOnError, boolean ignoreFailedDrops)
			throws ScriptException, SQLException {

		LOGGER.info("SQL File [" + resource.getResource().getFilename() + "]");
		
		try {
		ScriptUtils.executeSqlScript(dataSource.getConnection(), resource, continueOnError, ignoreFailedDrops,
				ScriptUtils.DEFAULT_COMMENT_PREFIX, ScriptUtils.DEFAULT_STATEMENT_SEPARATOR,
				ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
		
		} catch (RuntimeException e) {
			LOGGER.error(e.toString());
			LOGGER.error("* ERROR SQL File=[" + resource.getResource().getFilename() + "]");
			throw e;
		}
	}

	/**
	 * @param procedureName
	 * @param param
	 */
	protected Map<String, Object> executeProcedure(String procedureName, Object param) {
		Map<String, Object> paramMap = null;
		SqlParameterSource paramSrc = null;
		if (param != null) {
			paramMap = getMapFromVO(param);
			paramSrc = createParameterSource(paramMap);
		}

		LOGGER.info("executeProcedure.Procedure [" + procedureName + "]");

		try {

			SimpleJdbcCall procedure = new SimpleJdbcCall(dataSource).withProcedureName(procedureName);
			Map<String, Object> result = procedure.execute(paramSrc);

			LOGGER.debug("executeProcedure.Procedure [" + procedureName + "] called. result size : " + result.size());
			return result;
		} catch (RuntimeException e) {
			LOGGER.error("executeProcedure.Procedure [" + procedureName + "]");
			LOGGER.error(e.toString());
			throw e;
		}
	}

	/**
	 * @param procedureName
	 * @param args
	 *        optional array containing the in parameter values to be used in the call. Parameter values must be
	 *        provided in the same order as the parameters are defined for the stored procedure.
	 */
	protected Map<String, Object> executeProcedureByArgArray(String procedureName, Object... args) {
		LOGGER.info("executeProcedureByArgArray.Procedure [" + procedureName + "]");

		try {
			SimpleJdbcCall procedure = new SimpleJdbcCall(dataSource).withProcedureName(procedureName);
			Map<String, Object> result = procedure.execute(args);

			LOGGER.debug("executeProcedureByArgArray.Procedure [" + procedureName + "] called. result size : " + result.size());
			return result;
		} catch (RuntimeException e) {
			LOGGER.error(e.toString());
			LOGGER.error("executeProcedureByArgArray.Procedure [" + procedureName + "]");
			throw e;
		}
	}

	protected Map<String, Object> executeFunctionName(String functionName, Object... param) {
		LOGGER.info("executeFunctionName. Function [" + functionName + "]");

		try {
			SimpleJdbcCall function = new SimpleJdbcCall(dataSource).withFunctionName(functionName);
			Map<String, Object> result = function.execute(param);

			LOGGER.debug("executeFunctionName.Function [" + functionName + "] called. result size : " + result.size());
			return result;
		} catch (RuntimeException e) {
			LOGGER.error("executeFunctionName.Function [" + functionName + "]");
			LOGGER.error(e.toString());
			throw e;
		}
	}

	protected static String makeCallSql(String procedureName, Object... args) {
		StringBuilder sb = new StringBuilder("{call ");
		sb.append(procedureName).append("(");
		for (int i = 0; i < args.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("?");
		}
		sb.append(")}");
		return sb.toString();
	}

	protected static void setArgs(CallableStatement proc, Object... args) throws SQLException {
		int i = 1;
		for (Object arg : args) {
			if (arg instanceof String) {
				proc.setString(i++, (String) arg);
			} else if (arg instanceof Timestamp) {
				proc.setTimestamp(i++, (Timestamp) arg);
			} else if (arg instanceof Integer) {
				proc.setInt(i++, (Integer) arg);
			} else if (arg instanceof Long) {
				proc.setLong(i++, (Long) arg);
			} else {
				LOGGER.error("unsupported type : {}", arg.getClass().getSimpleName());
			}
		}
	}

	protected static void finallyProcess(Connection conn, CallableStatement proc, ResultSet results) {
		try {
			if (results != null) {
				results.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.toString());
		}
		try {
			if (proc != null) {
				proc.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.toString());
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.toString());
		}
	}

	/**
	 * @param procedureName
	 * @param args
	 *        optional array containing the in parameter values to be used in the call. Parameter values must be
	 *        provided in the same order as the parameters are defined for the stored procedure.
	 */
	protected ResultSet executeProcedureNoSpringjdbc(String procedureName, Object... args) {
		LOGGER.info("executeProcedureNoSpringjdbc.Procedure [" + procedureName + "]");

		String sql = makeCallSql(procedureName, args);

		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet results = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall(sql);

			setArgs(stmt, args);

			results = stmt.executeQuery();

			LOGGER.debug("executeProcedureNoSpringjdbc.Procedure [" + procedureName + "] called.");
			return results;
		} catch (RuntimeException e) {
			LOGGER.error(e.toString());
			LOGGER.error("Procedure [" + procedureName + "]");
			throw e;
		} catch (SQLException e) {
			LOGGER.error("Procedure [" + procedureName + "]", e);
			throw new UncategorizedSQLException("procedure call", procedureName, e);
		} finally {
			finallyProcess(conn, stmt, results);
		}
	}

	//	/**
	//	 * 
	//	 * @param procedureName
	//	 * @param argsList
	//	 */
	//	protected int[] executeBatchProcedureNoSpringjdbc(String procedureName, List<Object[]> argsList) {
	//		LOG.debug("Procedure [" + procedureName + "] : {}", argsList.size());
	//
	//		String sql = makeCallSql(procedureName, argsList.get(0));
	//
	//		boolean autoCommit = true;
	//		Connection conn = null;
	//		CallableStatement stmt = null;
	//		int[] results = null;
	//		try {
	//			conn = dataSource.getConnection();
	//			autoCommit = conn.getAutoCommit();
	//			conn.setAutoCommit(false);
	//			
	//			stmt = conn.prepareCall(sql);
	//
	//			for (Object[] args : argsList) {
	//				setArgs(stmt, args);
	//				stmt.addBatch();
	//			}
	//
	//			results = stmt.executeBatch();
	//
	//			LOG.debug("Procedure [" + procedureName + "] called.");
	//			return results;
	//		} catch (RuntimeException e) {
	//			LOG.error(e.toString());
	//			LOG.error("* ERROR PROCEDURE=[" + procedureName + "]");
	//			throw e;
	//		} catch (SQLException e) {
	//			LOG.error("* ERROR PROCEDURE=[" + procedureName + "]", e);
	//			throw new UncategorizedSQLException("procedure call", procedureName, e);
	//		} finally {
	//			finallyProcess(conn, stmt, null, autoCommit);
	//		}
	//	}

	/* ------------------------------------------------------------------------- */

	/**
	 * @param updateSql
	 * @param paramMap
	 *        : Map< String, Object>
	 * @return the number of rows affected
	 */
	protected int executeUpdateQuery(String updateSql, Map<String, Object> paramMap) {
		return executeUpdateQuery(updateSql, (Object) paramMap);
	}

	/**
	 * @param updateSql
	 * @param param
	 *        : Map< String, Object> �삉�뒗 VO. <br/>
	 *        VO �씤 寃쎌슦, sql 臾몄옄�뿴 �븞�뿉 :abc 濡� 蹂��닔瑜� �띁�떎硫�, paramVO �뿉 getAbc() 硫붿꽌�뱶媛� �엳�뼱�빞 �븿
	 * @return the number of rows affected
	 */
	protected int executeUpdateQuery(String updateSql, Object param) {

		Map<String, Object> paramMap = null;
		SqlParameterSource paramSrc = null;
		if (isDynamicQuery(updateSql) && param != null) {
			paramMap = getMapFromVO(param);
			paramSrc = createParameterSource(paramMap);
		} else if (param != null) {
			paramSrc = createParameterSource(param);
		}

		String updateSql2 = makeQueryFromVelocityString(updateSql, paramMap);

		sqlLogWithParameter(updateSql2, paramSrc);

		try {
			return this.jdbcTemplate.update(updateSql2, paramSrc);
		} catch (RuntimeException e) {
			sqlErrorLog(e, updateSql2, param);
			throw e;
		}
	}

	/**
	 * update 1嫄� �닔�뻾 <br/>
	 * �떒, 1嫄대룄 �뾽�뜲�씠�듃 �릺吏� �븡�쑝硫� insert 瑜� �븯�뒗 硫붿꽌�뱶
	 * 
	 * @param updateSql
	 * @param insertSql
	 * @param param
	 */
	protected void executeUpdateOrInsertQuery(String updateSql, String insertSql, Object param) {
		int cnt = this.executeUpdateQuery(updateSql, param);
		if (cnt == 0) {
			LOGGER.debug("there is no row for update. insert");
			this.executeUpdateQuery(insertSql, param);
			return;
		} else if (cnt != 1) {
			LOGGER.warn("{} rows updated!!", cnt);
			throw new IllegalStateException("Invalid update. " + cnt + " rows updated.");
		}
	}

	/**
	 * update 1嫄� �닔�뻾 <br/>
	 * �떒, 1嫄대룄 �뾽�뜲�씠�듃 �릺吏� �븡�쑝硫� insert 瑜� �븯�뒗 硫붿꽌�뱶
	 * 
	 * @param updateSql
	 * @param insertSql
	 * @param param
	 * @note �듃�옖�옲�뀡�쓣 �궗�슜�븯�뒗 寃쎌슦 �궗�슜 遺덇�
	 */
	protected void executeUpdateOrInsertQueryDupIgnore(String updateSql, String insertSql, Object param) {
		int cnt = this.executeUpdateQuery(updateSql, param);
		if (cnt == 0) {
			LOGGER.debug("there is no row for update. insert");
			try {
				this.executeUpdateQuery(insertSql, param);
			} catch (DataIntegrityViolationException dive) {
				LOGGER.info("dup ignore : {}", dive.toString());
				cnt = this.executeUpdateQuery(updateSql, param);
				if (cnt == 0) {
					LOGGER.warn("there is no row for update.");
				} else if (cnt != 1) {
					// LOG.warn("{} rows updated!!", cnt);
					LOGGER.error("Invalid update. {} rows updated.", cnt);
					// throw new IllegalStateException("Invalid update. "+ cnt +" rows updated.");
				}
			}
			return;
		} else if (cnt != 1) {
			LOGGER.warn("{} rows updated!!", cnt);
			throw new IllegalStateException("Invalid update. " + cnt + " rows updated.");
		}
	}

	/**
	 * insert �닔�뻾 <br/>
	 * �떒, Duplicated Key �삁�쇅媛� 諛쒖깮�븯硫� delete �썑 insert 瑜� �븯�뒗 硫붿꽌�뱶 <br/>
	 * 
	 * @param insertSql
	 * @param deleteSql
	 * @param param
	 * @note �듃�옖�옲�뀡�쓣 �궗�슜�븯�뒗 寃쎌슦 �궗�슜 遺덇�
	 */
	protected void executeInsertQueryDupIgnore(String insertSql, String deleteSql, Object param) {
		try {
			this.executeUpdateQuery(insertSql, param);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("executeUpdateQueryDupIgnore");
			this.executeUpdateQuery(deleteSql, param);

			try {
				this.executeUpdateQuery(insertSql, param);
			} catch (DataIntegrityViolationException e2) {
				LOGGER.warn("executeUpdateQueryDupIgnore(2)");
			}
		}
	}

//	/**
//	 * @param insertSql
//	 * @param updateSql
//	 * @param param
//	 * @note �듃�옖�옲�뀡�쓣 �궗�슜�븯�뒗 寃쎌슦 �궗�슜 遺덇�
//	 */
	//	@Deprecated
	//	protected void executeInsertOrUpdateQuery(String insertSql, String updateSql, Object param) {
	//		try {
	//			this.executeUpdateQuery(insertSql, param);
	//		} catch (DataIntegrityViolationException e) {
	//			LOG.warn("executeInsertOrUpdateQuery");
	//			this.executeUpdateQuery(updateSql, param);
	//		}
	//	}

	/**
	 * @param updateSql
	 * @param params
	 *        : List< Map < String, Object > > �삉�뒗 List< VO >
	 * @return [李멸퀬] 由ы꽩 媛믪씠 -2 �씤 寃껋�, <br/>
	 *         Oracle JDBC �뿉�꽌 batchUpdate �맂 寃곌낵 rows 媛쒖닔瑜� �븣 �닔 �뾾�뒗 寃쎌슦�엫.
	 */
	protected <T> int[] executeBatchUpdateQuery(String updateSql, List<T> params) {
		if (isDynamicQuery(updateSql)) {
			// dynamic query �씤 寃쎌슦, batchUpdate 遺덇�. executeUpdateBatch for臾� �룎由ш린
			int ret[] = new int[params.size()];
			int i = 0;
			for (T param : params) {
				ret[i++] = executeUpdateQuery(updateSql, param);
			}
			return ret;
		} else {
			LOGGER.debug("* batch Update");
			SqlParameterSource[] paramSrc = new SqlParameterSource[params.size()];
			int i = 0;
			for (T param : params) {
				paramSrc[i++] = param == null ? null : createParameterSource(param);
			}
			sqlLogWithParameter(updateSql, null);
			try {
				// BATCH UPDATE
				return this.jdbcTemplate.batchUpdate(updateSql, paramSrc);
			} catch (RuntimeException e) {
				sqlErrorLog(e, updateSql, params);
				throw e;
			}
		}
	}

	/**
	 * batchUpdate �닔�뻾 �썑, 寃곌낵瑜� �솗�씤�븯�뿬 update �릺吏� �븡�� 嫄댁� insert �븿. <br/>
	 * (二쇱쓽: Oracle �쓽 寃쎌슦 batchUpdate 嫄댁닔媛� �젣��濡� �굹�삤吏� �븡�쑝誘�濡� batchUpdate 媛� �븘�땶 嫄대퀎 �뾽�뜲�씠�듃媛� �맆 �닔 �엳�쓬)
	 * 
	 * @param updateSql
	 * @param insertSql
	 * @param params
	 */
	protected void executeBatchUpdateOrInsertQuery(String updateSql, String insertSql, List<?> params) {

		int[] resultArr = executeBatchUpdateQuery(updateSql, params);

		// 寃곌낵 泥댄겕 �썑 update count 媛� 0 �씤 寃껋� insert 泥섎━
		for (int i = 0; i < resultArr.length; i++) {
			int result = resultArr[i];
			if (result == 0) {
				LOGGER.debug("there is no row for update. insert");
				result = this.executeUpdateQuery(insertSql, params.get(i));
			}
			if (result != 1) {
				LOGGER.warn("{} rows updated!!", result);
				throw new IllegalStateException("Invalid update. " + result + " rows updated.");
			}
		}

		LOGGER.debug("executeBatchUpdateOrInsertQuery success.");
	}

	//	/**
	//	 * batchUpdate �닔�뻾 �썑, 寃곌낵瑜� �솗�씤�븯�뿬 update �릺吏� �븡�� 嫄댁� insert �븿. <br/>
	//	 * (二쇱쓽: Oracle �쓽 寃쎌슦 batchUpdate 嫄댁닔媛� �젣��濡� �굹�삤吏� �븡�쑝誘�濡� batchUpdate 媛� �븘�땶 嫄대퀎 �뾽�뜲�씠�듃媛� �맆 �닔 �엳�쓬) <br/>
	//	 * �떒, insert �떆�뿉 DataIntegrityViolationException �씡�뀎�뀡 諛쒖깮�� 臾댁떆�븿. (以묐났 insert �씤 寃쎌슦 �씡�뀎�뀡 臾댁떆) <br/>
	//	 * (二쇱쓽: postgresql �뿉�꽌�뒗 �씡�뀎�뀡 臾댁떆 �븯�뜑�씪�룄 �듃�옖�옲�뀡 濡ㅻ갚�맖)
	//	 * 
	//	 * @param updateSql
	//	 * @param insertSql
	//	 * @param params
	//	 * @return
	//	 * @note �듃�옖�옲�뀡�쓣 �궗�슜�븯�뒗 寃쎌슦 �궗�슜 遺덇�
	//	 */
	//	@Deprecated
	//	protected void executeBatchUpdateOrInsertQueryDupIgnore(String updateSql, String insertSql, List<?> params) {
	//		
	//		int[] resultArr = executeBatchUpdateQuery(updateSql, params);
	//		
	//		// 寃곌낵 泥댄겕 �썑 update count 媛� 0 �씤 寃껋� insert 泥섎━
	//		for (int i = 0; i < resultArr.length; i++) {
	//			int result = resultArr[i];
	//			if (result == 0) {
	//				LOG.debug("there is no row for update. insert");
	//				
	//				try { 
	//					result = this.executeUpdateQuery(insertSql, params.get(i));
	//				} catch (DataIntegrityViolationException dive) {
	//					LOG.info("dup ignore : {}", dive.toString());
	//					result = 1;
	//				}
	//			}
	//			if (result != 1) {
	//				LOG.warn("{} rows updated!!", result);
	//				throw new IllegalStateException("Invalid update. "+ result +" rows updated.");
	//			}
	//		}
	//		
	//		LOG.debug("executeBatchUpdateOrInsertQuery success.");
	//	}

	//	/**
	//	 * batch insert �닔�뻾 <br/>
	//	 * �떒, Duplicated Key �삁�쇅媛� 諛쒖깮�븯硫� delete �썑 insert 瑜� �븯�뒗 硫붿꽌�뱶 <br/>
	//	 * (二쇱쓽: postgresql �뿉�꽌�뒗 �씡�뀎�뀡 臾댁떆 �븯�뜑�씪�룄 �듃�옖�옲�뀡 濡ㅻ갚�맖)
	//	 * 
	//	 * @param insertSql
	//	 * @param deleteSql
	//	 * @param params
	//	 * @note �듃�옖�옲�뀡�쓣 �궗�슜�븯�뒗 寃쎌슦 �궗�슜 遺덇�
	//	 */
	//	@Deprecated
	//	protected <T> void executeBatchUpdateQueryDupIgnore(String insertSql, String deleteSql, List<T> params) {
	//		try {
	//			this.executeBatchUpdateQuery(insertSql, params);
	//		} catch (DataIntegrityViolationException e) {
	//			LOG.warn(e.toString());
	//			this.executeBatchUpdateQuery(deleteSql, params);
	//			this.executeBatchUpdateQuery(insertSql, params);
	//		}
	//	}

	/* ------------------------------------------------------------------------- */

	/**
	 * @brief
	 * @param selectQuery
	 * @param param
	 *        : Map �삉�뒗 VO 媛��뒫. null 媛��뒫
	 * @param rowMapper
	 * @return 寃곌낵 List �뒗 null �씠 �븘�떂. �떒 isEmpty() �씪 �닔 �엳�쓬.
	 */
	protected <T, R> List<R> executeSelectQuery(String selectQuery, T param, final RowMapper<R> rowMapper) {

		// create parameter map
		Map<String, Object> paramMap = createParamMap(selectQuery, param);

		// create select query
		String selectQuery2 = makeQueryFromVelocityString(selectQuery, paramMap);

		// create parameter source
		SqlParameterSource paramSrc = createParamSrc(paramMap, param);

		// sqlLogWithParameter(selectQuery2, paramSrc);

		try {
			long t1 = System.currentTimeMillis();

			// c.f. result cannot be null.
			List<R> result = this.jdbcTemplate.query(selectQuery2, paramSrc, rowMapper);

			long t2 = System.currentTimeMillis();
			long selectTime = t2 - t1;
			if (selectTime > minSelectTime) {
				LOGGER.warn("select: " + result.size() + " records, " + selectTime + " ms. ** warning **\n" + selectQuery);
			} else {
				LOGGER.debug("select: " + result.size() + " records, " + selectTime + " ms.");
			}

			return result;

		} catch (RuntimeException e) {
			sqlErrorLog(e, selectQuery2, param);
			throw e;
		}
	}

	/**
	 * @brief ColumnMapRowMapper 瑜� �궗�슜�븯�뒗 硫붿꽌�뱶 <br/>
	 *        queryForList �� �룞�씪
	 * @param selectQuery
	 * @param param
	 *        : Map �삉�뒗 VO 媛��뒫. null 媛��뒫
	 * @return 寃곌낵 List �뒗 null �씠 �븘�떂. �떒 isEmpty() �씪 �닔 �엳�쓬.
	 */
	protected List<Map<String, Object>> executeSelectQuery(String selectQuery, Map<String, Object> param) {
		RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();
		return executeSelectQuery(selectQuery, param, rowMapper);
	}

	/**
	 * @brief BeanPropertyRowMapper 瑜� �궗�슜�븯�뒗 硫붿꽌�뱶<br/>
	 *        parameter �겢�옒�뒪�� result �겢�옒�뒪媛� �룞�씪�븳 寃쎌슦 �궗�슜
	 * @note sql 臾몄옄�뿴 �븞�뿉 :abc 濡� 蹂��닔瑜� �띁�떎硫�, paramVo �겢�옒�뒪�뿉 getAbc() 硫붿꽌�뱶媛� �엳�뼱�빞 �븿. <br/>
	 *       �삉�븳 荑쇰━ 寃곌낵�뿉 XY_Z �씪�뒗 而щ읆�씠 �엳�떎硫� paramVo �겢�옒�뒪�뿉 setXyZ() 硫붿꽌�뱶媛� �엳�뼱�빞 �븿
	 * @param selectQuery
	 * @param paramVo
	 *        : VO �삎�깭 媛��뒫. null 遺덇��뒫
	 * @return 寃곌낵 List �뒗 null �씠 �븘�떂. �떒 isEmpty() �씪 �닔 �엳�쓬.
	 */
	protected <T> List<T> executeSelectQuery(String selectQuery, T paramVo) {
		@SuppressWarnings("unchecked")
		Class<T> resultVoClass = (Class<T>) paramVo.getClass();
		if (isSingleColumnType(resultVoClass)) {
			throw new IllegalArgumentException("Invalid paramVo type : " + resultVoClass.getName());
		}
		RowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(resultVoClass);
		return executeSelectQuery(selectQuery, paramVo, rowMapper);
	}

	/**
	 * @brief BeanPropertyRowMapper 瑜� �궗�슜�븯�뒗 硫붿꽌�뱶 <br/>
	 *        parameter �겢�옒�뒪�� result �겢�옒�뒪媛� �떎瑜� 寃쎌슦 �궗�슜
	 * @note sql 臾몄옄�뿴 �븞�뿉 :abc 濡� 蹂��닔瑜� �띁�떎硫�, param �겢�옒�뒪�뿉 getAbc() 硫붿꽌�뱶媛� �엳�뼱�빞 �븿. <br/>
	 *       �삉�븳 荑쇰━ 寃곌낵�뿉 XY_Z �씪�뒗 而щ읆�씠 �엳�떎硫� resultClass �뿉 setXyZ() 硫붿꽌�뱶媛� �엳�뼱�빞 �븿
	 * @param selectQuery
	 * @param param
	 *        : Map �삉�뒗 VO �삎�깭 媛��뒫. null 媛��뒫
	 * @param resultClass
	 *        : VO �겢�옒�뒪. <br/>
	 *        �삉�뒗 single column �씤 寃쎌슦, String.class, Integer.class, Long.class, Timestamp.class 媛��뒫
	 * @return 寃곌낵 List �뒗 null �씠 �븘�떂. �떒 isEmpty() �씪 �닔 �엳�쓬.
	 */
	protected <T, R> List<R> executeSelectQuery(String selectQuery, T param, Class<R> resultClass) {
		RowMapper<R> rowMapper = null;
		if (isSingleColumnType(resultClass)) {
			rowMapper = new SingleColumnRowMapper<R>(resultClass);
		} else {
			rowMapper = new BeanPropertyRowMapper<R>(resultClass);
		}
		return executeSelectQuery(selectQuery, param, rowMapper);
	}

	/* ------------------------------------------------------------------------- */

	/**
	 * @brief Row �븯�굹�뿉 �빐�떦�븯�뒗 �뜲�씠�꽣瑜� 由ы꽩
	 * @param selectQuery
	 * @param param
	 *        : Map �삉�뒗 VO 媛��뒫. null 媛��뒫
	 * @param rowMapper
	 * @return 議고쉶 寃곌낵媛� �뾾�뒗 寃쎌슦 null 由ы꽩
	 */
	protected <T, R> R executeSelectOneQuery(String selectQuery, T param, RowMapper<R> rowMapper) {
		List<R> list = executeSelectQuery(selectQuery, param, rowMapper);
		if (list.isEmpty()) {
			return null;
		}

		if (list.size() > SIZE_ONE) {
			LOGGER.warn("result list size is more than one. actual={}", list.size());
		}
		return list.get(0);
	}

	/**
	 * @brief Row �븯�굹�뿉 �빐�떦�븯�뒗 �뜲�씠�꽣瑜� Map �쑝濡� 由ы꽩 <br/>
	 *        (ColumnMapRowMapper 瑜� �궗�슜�븯�뒗 硫붿꽌�뱶)
	 * @param selectQuery
	 * @param paramMap
	 *        : null 媛��뒫
	 * @return 議고쉶 寃곌낵媛� �뾾�뒗 寃쎌슦 null 由ы꽩
	 */
	protected Map<String, Object> executeSelectOneQuery(String selectQuery, Map<String, Object> paramMap) {
		return executeSelectOneQuery(selectQuery, paramMap, new ColumnMapRowMapper());
	}

	/**
	 * @brief Row �븯�굹�뿉 �빐�떦�븯�뒗 �뜲�씠�꽣瑜� VO濡� 由ы꽩 <br/>
	 *        (BeanPropertyRowMapper 瑜� �궗�슜�븯�뒗 硫붿꽌�뱶)
	 * @param selectQuery
	 * @param paramVo
	 *        : VO 媛��뒫. null 遺덇��뒫
	 * @return 議고쉶 寃곌낵媛� �뾾�뒗 寃쎌슦 null 由ы꽩
	 */
	protected <T> T executeSelectOneQuery(String selectQuery, T paramVo) {
		@SuppressWarnings("unchecked")
		Class<T> resultVoClass = (Class<T>) paramVo.getClass();
		if (isSingleColumnType(resultVoClass)) {
			throw new IllegalArgumentException("Invalid paramVo type : " + resultVoClass.getName());
		}
		RowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(resultVoClass);
		return executeSelectOneQuery(selectQuery, paramVo, rowMapper);
	}

	/**
	 * @brief Row �븯�굹�뿉 �빐�떦�븯�뒗 �뜲�씠�꽣瑜� 由ы꽩
	 * @param selectQuery
	 * @param paramV
	 *        : Map �삉�뒗 VO 媛��뒫. null 媛��뒫
	 * @param resultClass
	 *        : VO �겢�옒�뒪 <br/>
	 *        �삉�뒗 single column �씤 寃쎌슦, String.class, Integer.class, Long.class, Timestamp.class 媛��뒫
	 * @return 議고쉶 寃곌낵媛� �뾾�뒗 寃쎌슦 null 由ы꽩
	 */
	protected <T, R> R executeSelectOneQuery(String selectQuery, T param, Class<R> resultClass) {
		RowMapper<R> rowMapper = null;
		if (isSingleColumnType(resultClass)) {
			rowMapper = new SingleColumnRowMapper<R>(resultClass);
		} else {
			rowMapper = new BeanPropertyRowMapper<R>(resultClass);
		}
		return executeSelectOneQuery(selectQuery, param, rowMapper);
	}

	/* ========================================================================= */

	@SuppressWarnings("unchecked")
	protected Map<String, Object> createParamMap(String selectQuery, Object param) {
		/*
		 * paramMap �깮�꽦 議곌굔 : �떎�씠�굹誘� 荑쇰━ �씠嫄곕굹, �썝�옒 Map �씠嫄곕굹
		 */
		if (param == null) {
			return null;
		} else if (isDynamicQuery(selectQuery)) {
			return getMapFromVO(param);
		} else if (param instanceof Map) {
			return (Map<String, Object>) param;
		}
		return null;
	}

	protected SqlParameterSource createParamSrc(Map<String, Object> paramMap, Object param) {
		SqlParameterSource paramSrc = null;
		if (paramMap != null) {
			paramSrc = createParameterSource(paramMap);
		} else if (param != null) {
			paramSrc = createParameterSource(param);
		}
		return paramSrc;
	}

	protected SqlParameterSource createParameterSource(Object param) {
		if (param instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> paramMap = (Map<String, Object>) param;
			return new MapSqlParameterSource(paramMap);
		} else {
			return new BeanPropertySqlParameterSource(param);
		}
	}

	protected boolean isSingleColumnType(Class<?> type) {
		if (String.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	protected boolean isDynamicQuery(String orgQuery) {
		// # �씠�굹 $ 臾몄옄媛� �룷�븿�맂 寃쎌슦 �떎�씠�굹誘� 荑쇰━濡� �씤�떇
		if (orgQuery.indexOf('#') != -1 || orgQuery.indexOf('$') != -1) {
			return true;
		}
		return false;
	}

	protected String makeQueryFromVelocityString(String orgQuery, Map<String, Object> paramMap) {
		if (paramMap == null || !isDynamicQuery(orgQuery)) {
			return orgQuery;
		}
		try {
			String resultQuery = VelocityUtil.make(orgQuery, paramMap);

			if ("".equals(resultQuery.trim())) {
				throw new OcpException("Result Query String is empty.");
			}
			return resultQuery;
		} catch (Exception e) {
			sqlErrorLog(e, orgQuery, paramMap);
			throw new OcpException("Dynamic Query Error", e);
		}
	}

	protected Map<String, Object> getMapFromSqlParameterSource(SqlParameterSource paramSrc) {
		if (paramSrc instanceof MapSqlParameterSource) {
			return ((MapSqlParameterSource) paramSrc).getValues();
		}
		if (paramSrc instanceof BeanPropertySqlParameterSource) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			for (String propName : ((BeanPropertySqlParameterSource) paramSrc).getReadablePropertyNames()) {
				if (paramSrc.hasValue(propName)) {
					paramMap.put(propName, paramSrc.getValue(propName));
				}
			}
			return paramMap;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getMapFromVO(Object vo) {
		if (vo instanceof Map) {
			return (Map<String, Object>) vo;
		}
		// VO -> Map
		Map<String, Object> map = new HashMap<String, Object>();
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(vo);
		PropertyDescriptor[] props = beanWrapper.getPropertyDescriptors();
		for (PropertyDescriptor pd : props) {
			String name = pd.getName();
			if (beanWrapper.isReadableProperty(name)) {
				Object value = beanWrapper.getPropertyValue(name);
				if (value != null) {
					map.put(name, value);
				}
			}
		}
		return map;
	}

	/**
	 * 荑쇰━ 濡쒓렇 <br/>
	 * TRACE �젅踰⑥씪�븣�뒗 �뙆�씪誘명꽣�� �븿猿�, DEBUG �젅踰⑥씪�뵲�뒗 �뙆�씪誘명꽣�뒗 �젣�쇅�븿.
	 * 
	 * @param query
	 * @param paramSrc
	 */
	protected void sqlLogWithParameter(String query, SqlParameterSource paramSrc) {
		if (!LOGGER.isTraceEnabled() && !LOGGER.isDebugEnabled()) {
			return;
		}

		String logStr = null;
		Map<String, Object> paramMap = null;

		if (paramSrc != null && LOGGER.isTraceEnabled()) {
			paramMap = getMapFromSqlParameterSource(paramSrc);
		}

		if (paramMap != null && !paramMap.isEmpty()) {
			// 荑쇰━媛� 湲� 寃쎌슦�뿉�뒗 format,. 湲몄� �븡�쑝硫� compact.
			if (query.length() > 120 || query.contains("AND") || query.contains("JOIN")) {
				logStr = "* Query with parameter:\n\n" + SimpleSqlFormatter.format(query, paramMap) + "\n";
			} else {
				logStr = "* Query with parameter=[" + SimpleSqlFormatter.compact(query, paramMap) + "]";
			}
		} else if (LOGGER.isDebugEnabled()) {
			logStr = "* Query=[" + SimpleSqlFormatter.compact(query) + "]";
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(logStr);
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(logStr);
		}
	}

	protected void sqlErrorLog(Exception e, String query, Object params) {
		LOGGER.error(e.toString());
		LOGGER.error("* ERROR Query=\n" + SimpleSqlFormatter.format(query));
		if (LOGGER.isDebugEnabled() && params != null) {
			if (params instanceof List) {
				for (Object param : (List<?>) params) {
					LOGGER.debug("* Parameter=[" + param.toString() + "]");
				}
			} else {
				LOGGER.debug("* Parameter=[" + params.toString() + "]");
			}
		}
	}

	private static String getDriverClassNameByDataSource(DataSource dataSource) {
		if (dataSource instanceof BasicDataSource) {
			return ((BasicDataSource) dataSource).getDriverClassName();
		} else if (dataSource instanceof SimpleDriverDataSource) {
			return ((SimpleDriverDataSource) dataSource).getDriver().getClass().getCanonicalName();
		}
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
					LOGGER.debug("Invalid returnType : " + getter.getReturnType().getName() + " != " + returnType.getName());
					return null;
				}
			} else {
				return (T) getter.invoke(vo);
			}
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		}
		return null;
	}
	/* ========================================================================= */

}

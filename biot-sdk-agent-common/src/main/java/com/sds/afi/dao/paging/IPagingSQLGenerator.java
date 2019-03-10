package com.sds.afi.dao.paging;

import java.util.Map;

public interface IPagingSQLGenerator {

	/**
	 * 
	 * @param originalSql
	 * @param paramMap
	 * @param pageIndex : 1-based
	 * @param pageSize : max row count (= limit)
	 * @return
	 */
	String getPaginationSQL(String originalSql, Map<String, Object> paramMap, long pageIndex, long pageSize);

	/**
	 * 
	 * @param originalSql
	 * @return
	 */
	String getCountSQL(String originalSql);
	
	/**
	 * 
	 * @param originalSql
	 * @param paramMap
	 * @param offset : 0-based
	 * @param limit : max row count (= pageSize)
	 * @return
	 */
	String getOffsetLimitSQL(String originalSql, Map<String, Object> paramMap, long offset, long limit);

}
package com.sds.afi.dao.paging;

import java.util.Map;

public class MySQLPagingSQLGenerator extends AbstractPagingSQLGenerator {
	
	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {

		paramMap.put("offset", offset);
		paramMap.put("limit", limit);
		
		StringBuilder sql = new StringBuilder(originalSql);
		sql.append(" LIMIT :offset, :limit");

		return sql.toString();
	}


}
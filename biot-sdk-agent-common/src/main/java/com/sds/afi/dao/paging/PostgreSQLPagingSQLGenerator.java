package com.sds.afi.dao.paging;

import java.util.Map;

public class PostgreSQLPagingSQLGenerator extends AbstractPagingSQLGenerator {

	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {

		paramMap.put("offset", offset);
		paramMap.put("limit", limit);

		StringBuilder sql = new StringBuilder(originalSql);
		sql.append(" LIMIT :limit OFFSET :offset ");

		return sql.toString();

	}

}
package com.sds.afi.dao.paging;

import java.util.Locale;
import java.util.Map;

public class HSQLPagingSQLGenerator extends AbstractPagingSQLGenerator {

	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {

		paramMap.put("offset", offset);
		paramMap.put("limit", limit);
		
		StringBuilder sql = new StringBuilder();
		sql.append(originalSql);
		sql.insert(originalSql.toUpperCase(Locale.getDefault()).indexOf("SELECT") + 6, " LIMIT :offset :limit ");

		return sql.toString();

	}


}
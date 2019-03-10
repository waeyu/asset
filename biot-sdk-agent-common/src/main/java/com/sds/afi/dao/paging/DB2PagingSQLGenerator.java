package com.sds.afi.dao.paging;

import java.util.Locale;
import java.util.Map;

public class DB2PagingSQLGenerator extends AbstractPagingSQLGenerator {
	
	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {
		String sql = originalSql.trim();
		
		paramMap.put("offset", offset);
		paramMap.put("endOffset", endOffset);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ( ");
		sb.append("SELECT rownumber() over() AS PAGING_ROW_SEQ, ");

		if (hasDistinct(sql)) {
			sb.append("row_.* FROM (");
			sb.append(sql);
			sb.append(") AS row_ ");
			sb.append(") AS INNER_TABLE WHERE PAGING_ROW_SEQ BETWEEN :offset AND :endOffset ");

			return sb.toString();
		}
		if (hasOrderByClause(sql) || isAll(sql)) {
			sb.append("INNER_TABLE.* FROM (");
			sb.append(sql);
			sb.append(") AS INNER_TABLE ) WHERE PAGING_ROW_SEQ BETWEEN :offset AND :endOffset ");

			return sb.toString();
		}

		sb.append(sql.substring(6));
		sb.append(") AS INNER_TABLE WHERE PAGING_ROW_SEQ BETWEEN :offset AND :endOffset ");

		return sb.toString();
		
	}

	protected boolean hasDistinct(String sql) {
		String others = sql.substring(6);
		return others.toUpperCase(Locale.getDefault()).trim().startsWith("DISTINCT");
	}

	protected boolean isAll(String sql) {
		String others = sql.substring(6);
		return others.trim().startsWith("*");
	}

}
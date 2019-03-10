package com.sds.afi.dao.paging;

import java.util.Map;

public class SQLServerPagingSQLGenerator extends AbstractPagingSQLGenerator {

	/*
	 * // SQL Server 2012 paramMap.put("pageValue1", Long.valueOf(pageIndex * pageSize)); paramMap.put("pageValue2",
	 * Long.valueOf(pageSize));
	 * 
	 * StringBuilder sql = new StringBuilder(); sql.append(originalSql); if (!isOrderBy(originalSql)) {
	 * //log.error("'order by' is needed!"); } sql.append(" OFFSET :pageValue1 FETCH NEXT :pageValue2 ROW ONLY "); //
	 * SQL Server 2012 }
	 */

	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {
		// -- sample query
		// SELECT X.* FROM (
		// SELECT ROW_NUMBER() OVER (ORDER BY name, database_id) AS PAGING_ROW_SEQ, * FROM sys.databases
		// ) X WHERE X.PAGING_ROW_SEQ BETWEEN 1 AND 10

		String orderByClause = extractOrderByClause(originalSql);
		if (orderByClause == null) {
			throw new IllegalArgumentException("'ORDER BY' clause is needed!");
		}

		paramMap.put("offset", offset + 1);
		paramMap.put("endOffset", endOffset);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT X.* FROM ( SELECT ROW_NUMBER() OVER (");
		sql.append(orderByClause); // ORDER BY 구문이 여기에 들어감 
		sql.append(") AS PAGING_ROW_SEQ, INNER_TABLE.* FROM ( \n");
		sql.append(removeOrderByClause(originalSql)); // 서브쿼리 안에 ORDER BY 가 있으면 안됨
		sql.append("\n ) INNER_TABLE ) X WHERE X.PAGING_ROW_SEQ BETWEEN :offset AND :endOffset ");

		return sql.toString();
	}

}

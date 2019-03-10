package com.sds.afi.dao.paging;

import java.util.Map;

public class OraclePagingSQLGenerator extends AbstractPagingSQLGenerator {

	@Override
	protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit) {

		paramMap.put("offset", offset+1);
		paramMap.put("endOffset", endOffset+1);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT X.* FROM ( SELECT INNER_TABLE.*, ROWNUM AS PAGING_ROW_SEQ FROM ( \n");
		sql.append(originalSql);
		sql.append("\n ) INNER_TABLE WHERE ROWNUM < :endOffset ) X WHERE X.PAGING_ROW_SEQ BETWEEN :offset AND :endOffset ");

		return sql.toString();
	}

}
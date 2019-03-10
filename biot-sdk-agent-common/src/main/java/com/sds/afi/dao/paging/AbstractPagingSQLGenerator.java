package com.sds.afi.dao.paging;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPagingSQLGenerator implements IPagingSQLGenerator {

	private static final Pattern ORDER_BY_PATTERN = Pattern.compile("(ORDER|order)\\s+(BY|by)");
	private static final Pattern ALIAS_PATTERN = Pattern.compile("\\S+\\.");

	public String getPaginationSQL(String originalSql, Map<String, Object> paramMap, long pageIndex, long pageSize) {
		
		long offset = (pageIndex - 1) * pageSize + 1; // same with : endOffset - limit + pageIndex - 1 
		long endOffset = pageIndex * pageSize; // same with : offset + pageSize 

		return getSQL(originalSql, paramMap, offset, endOffset, pageSize);
	}

	public String getOffsetLimitSQL(String originalSql, Map<String, Object> paramMap, long offset, long limit) {
		
		long endOffset = offset + limit; 
		
		return getSQL(originalSql, paramMap, offset, endOffset, limit);
	}
	
	abstract protected String getSQL(String originalSql, Map<String, Object> paramMap, long offset, long endOffset, long limit);	

	public String getCountSQL(String originalSql) {
		StringBuilder sql = new StringBuilder("SELECT count(*) FROM ( ");
		sql.append(removeOrderByClause(originalSql));
		sql.append(" ) CNT_INNER_TB "); // MySQL �뿉�꽌�뒗 �븯�쐞 �뀒�씠釉붿뿉 諛섎뱶�떆 alias媛� �엳�뼱�빞 �븿
		return sql.toString();
	}

	protected static boolean hasOrderByClause(String sql) {
		return ORDER_BY_PATTERN.matcher(sql).find();
	}

	protected static String removeOrderByClause(String sql) {
		// SQLServer �뿉�꽌�뒗 �꽌釉뚯옘由� �븞�뿉 ORDER BY 媛� �엳�쑝硫� �븞�맖
		Matcher matcher = ORDER_BY_PATTERN.matcher(sql);
		if (matcher.find()) {
			int orderbyIdx = matcher.start();
			return sql.substring(0, orderbyIdx);
		}
		return sql;
	}

	protected static String extractOrderByClause(String sql) {
		// SQLServer �뿉�꽌�뒗 ROW_NUMBER OVER()�뿉 ORDER BY 援щЦ�씠 �븘�슂�븿
		Matcher matcher = ORDER_BY_PATTERN.matcher(sql);
		if (matcher.find()) {
			int orderbyIdx = matcher.start();
			String orderby = sql.substring(orderbyIdx);
			// orderby 援щЦ�뿉�꽌 ALIAS �젣嫄�
			Matcher matcher2 = ALIAS_PATTERN.matcher(orderby);
			if (matcher2.find()) {
				return matcher2.replaceAll("");
			}
			return orderby;
		}
		return null;
	}

}
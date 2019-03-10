package com.sds.afi.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.sds.ocp.util.StringUtil;

/**
 * 디버깅 로그 용도로 SQL 포매팅 해주는 유틸
 *  
 * @author 김성혜
 *
 */
public final class SimpleSqlFormatter {

	private static final String WHITESPACE = " \n\r\f\t";
	private static final String SPECIALTOKEN = "'()";

	private static final Set<String> NEWLINE_TOKEN = new HashSet<String>();
	
	private SimpleSqlFormatter() {
		throw new AssertionError();
	}

	static {
		NEWLINE_TOKEN.add("select");
		NEWLINE_TOKEN.add("insert");
		NEWLINE_TOKEN.add("delete");
		NEWLINE_TOKEN.add("from");
		NEWLINE_TOKEN.add("where");
		NEWLINE_TOKEN.add("and");
		NEWLINE_TOKEN.add("order");
		NEWLINE_TOKEN.add("values");
	}

	/**
	 * @brief 특정 키워드에서 줄바꿈을 하고, 그 외 공백은 단순한 1글자 공백으로 변경
	 * @note 작은 따옴표 안의 내용은 그대로 보존함. 단, 작은 따옴표 안에 작은 따옴표 문자를 넣은 경우 처리 불가
	 * @param sql
	 * @return
	 * @code <pre>
	 * if (log.isDebugEnabled()) {
	 * 	String str = SimpleSqlFormatter.format(query);
	 * 	log.debug(str);
	 * }
	 * </pre>
	 */
	public static String format(String sql) {
		return format(sql, false);
	}

	public static String format(String sql, Map<String, Object> param) {
		String s = format(sql);
		return replaceParams(s, param);
	}

	/**
	 * @brief 모든 공백을 단순한 1글자 공백으로 변경
	 * @note 작은 따옴표 안의 내용은 그대로 보존함. 단, 작은 따옴표 안에 작은 따옴표 문자를 넣은 경우 처리 불가
	 * @param sql
	 * @return
	 */
	public static String compact(String sql) {
		return format(sql, true);
	}

	public static String compact(String sql, Map<String, Object> param) {
		String s = compact(sql);
		return replaceParams(s, param);
	}
	
	private static String replaceParams(String sql, Map<String, Object> param) {
		if (param == null || param.isEmpty()) {
			return sql;
		}
		
		// ** 역순으로 정렬하는 이유 :
		// $aaa : 'X' 와 $aaab : 'Y' 항목이 있는 경우,
		// $aaab 가 'Y' 로 바뀌는 게 아닌, 'X'b 로 바뀌는 경우가 있을 수 있기 때문
		List<String> keys = new ArrayList<String>(param.keySet());
		Collections.sort(keys, Collections.reverseOrder());

		String s = sql;
		for (String key : keys) {
			Object val = param.get(key);

			if (val instanceof String) {
				// value에 줄바꿈이 있는 경우, value 를 ... 로 변환함
				String sVal = (String) val;
				int idx = sVal.indexOf('\n');
				if (idx != -1) {
					val = "...";
				}
			}

			s = StringUtil.replace(s, ":" + key, "'" + val + "'");
		}
		return s;
	}

	private static String format(String sql, boolean isCompact) {

		StringTokenizer tokens = new StringTokenizer(sql.trim(), WHITESPACE + SPECIALTOKEN, true);
		StringBuilder sb = new StringBuilder();
		int indentLevel = 0;

		boolean prevWhitespace = false;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();

			if ("'".equals(token)) {
				prevWhitespace = false;
				sb.append(token);
				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if ("'".equals(token)) {
						break;
					} else {
						sb.append(token);
					}
				}
			}

			if (WHITESPACE.contains(token)) {
				if (!prevWhitespace) {
					sb.append(" ");
					prevWhitespace = true;
				}

			} else {

				if (isCompact) {
					sb.append(token);
				} else {
					if ("(".equals(token)) {
						indentLevel++;
						//newLineWithIndent(sb, indentLevel);
						sb.append(token);
					} else if (")".equals(token)) {
						//newLineWithIndent(sb, indentLevel);
						sb.append(token);
						indentLevel--;
					} else if (NEWLINE_TOKEN.contains(token.toLowerCase(Locale.getDefault()))) {
						newLineWithIndent(sb, indentLevel);
						sb.append(String.format("%-6s", token));
					} else {
						sb.append(token);
					}
				}

				prevWhitespace = false;
			}
		}

		return sb.toString();
	}

	private static void newLineWithIndent(StringBuilder sb, int indentLevel) {
		if (sb.length() != 0) {
			sb.append("\n");
			for (int i = 0; i < indentLevel; i++) {
				sb.append("\t");
			}
		}
	}
}

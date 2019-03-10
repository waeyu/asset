package com.sds.afi.dao;

import java.util.Locale;

import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.sds.ocp.util.StringUtil;

public class CamelCaseColumnMapRowMapper extends ColumnMapRowMapper {
	
	public static final CamelCaseColumnMapRowMapper INSTANCE = new CamelCaseColumnMapRowMapper();

	@Override
	protected String getColumnKey(String columnName) {
		return StringUtil.convCamelCaseString(columnName.toLowerCase(Locale.getDefault()));
	}
}

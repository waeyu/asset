package com.sds.ocp.third.svc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.third.svc.vo.VisitorInfo;

@Repository
public class VisitorInfoDAO extends AbstractDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "ecc_dataSource")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public VisitorInfo selectVisitorInfo(String orderCode) {
		
		logger.debug("selectVisitorInfo begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT * " );
		query.append("  FROM smartid_visitor_info ");
		query.append(" WHERE OrderCode = :orderCode " );
		
		Map<String, Object> param = new HashMap<>();
		param.put("orderCode", orderCode );
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectOneQuery( query.toString() , param ,  new VisitorInfoRowMapper());
		
	}
	
	private static class VisitorInfoRowMapper implements RowMapper<VisitorInfo> {
		
		@Override
		public VisitorInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			VisitorInfo visitorInfo = new VisitorInfo();

			visitorInfo.setInnerId(rs.getString("InnerId"));
			visitorInfo.setInnerName(rs.getString("InnerName"));
			visitorInfo.setOrderCode(rs.getString("orderCode"));
			visitorInfo.setVisitorName(rs.getString("VisitorName"));

			return visitorInfo;
		}

	}
	
	
}

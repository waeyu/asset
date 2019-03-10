package com.sds.ocp.svc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.svc.vo.VisitorInfo;

@Repository
public class VisitorInfoDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<VisitorInfo> list(Timestamp from , Timestamp to ) {
		
		logger.debug("list begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM smartid_visitor_info " ); 
		query.append("  WHERE OperDt > :from AND OperDt <= :to ");
		query.append(" ORDER BY OperDt ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new VisitorInfoRowMapper());
		
	}
	
	private static class VisitorInfoRowMapper implements RowMapper<VisitorInfo> {
		
		@Override
		public VisitorInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			VisitorInfo visitorInfo = new VisitorInfo();

			visitorInfo.setInnerId(rs.getString("InnerId"));
			visitorInfo.setInnerName(rs.getString("InnerName"));
			visitorInfo.setVisitorName(rs.getString("VisitorName"));
			visitorInfo.setOrderCode(rs.getString("OrderCode"));
			visitorInfo.setVisitorDate(rs.getTimestamp("VisitorDate"));
			visitorInfo.setOperDt(rs.getTimestamp("OperDt"));

			return visitorInfo;
		}

	}
	

}

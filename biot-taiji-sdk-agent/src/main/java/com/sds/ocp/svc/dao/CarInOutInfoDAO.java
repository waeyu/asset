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
import com.sds.ocp.svc.vo.CarInOutInfo;

@Repository
public class CarInOutInfoDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<CarInOutInfo> carInOutList(Timestamp from , Timestamp to ) {
		
		logger.debug("carInOutList begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM park_tc_usercrdtm " ); 
		query.append("  WHERE Crdtm > :from AND Crdtm <= :to ");
		query.append(" ORDER BY Crdtm ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new CarInOutRowMapper());
		
	}
	
	private static class CarInOutRowMapper implements RowMapper<CarInOutInfo> {
		
		@Override
		public CarInOutInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CarInOutInfo carInOut = new CarInOutInfo();
			
			carInOut.setCarCode(rs.getString("CarCode"));
			carInOut.setCrdtm(rs.getTimestamp("Crdtm"));
			carInOut.setInOut(rs.getString("InOrOut"));

			return carInOut;
		}

	}
	

}

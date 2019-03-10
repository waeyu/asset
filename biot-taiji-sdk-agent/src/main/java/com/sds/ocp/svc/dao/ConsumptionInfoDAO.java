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
import com.sds.ocp.svc.vo.ConsumptionInfo;
import com.sds.ocp.svc.vo.ConsumptionTerminal;

@Repository
public class ConsumptionInfoDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<ConsumptionTerminal> listConsumptionTerminal() {
		
		logger.debug("list begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM smartid_Consumption_Terminal " ); 
		query.append("  WHERE LEN(ComId) > 1 ");
		
		Map<String, Object> param = new HashMap<>();
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new ConsumptionTerminalRowMapper());
		
	}
	
	private static class ConsumptionTerminalRowMapper implements RowMapper<ConsumptionTerminal> {
		
		@Override
		public ConsumptionTerminal mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ConsumptionTerminal consumptionTerminal = new ConsumptionTerminal();

			consumptionTerminal.setConTerminalInnerId(rs.getInt("ConTerminalInnerId"));
			consumptionTerminal.setConTerminalName(rs.getString("ConTerminalName"));
			consumptionTerminal.setComId(rs.getString("ComId"));
			consumptionTerminal.setComSerials(rs.getString("ComSerials"));

			return consumptionTerminal;
		}

	}
	
	public List<ConsumptionInfo> list(Timestamp from , Timestamp to ) {
		
		logger.debug("list begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT a.* , b.ConTerminalInnerId ");
		query.append("   FROM smartid_conlog_user a , smartid_Consumption_Terminal b " ); 
		query.append("  WHERE a.ConDatetime > :from AND a.ConDatetime <= :to ");
		query.append("    AND a.ConTerminalName = b.ConTerminalName ");
		query.append(" ORDER BY a.ConDatetime ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new ConsumptionInfoRowMapper());
		
	}
	
	private static class ConsumptionInfoRowMapper implements RowMapper<ConsumptionInfo> {
		
		@Override
		public ConsumptionInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ConsumptionInfo consumptionInfo = new ConsumptionInfo();
			
			consumptionInfo.setMarkId(rs.getString("MarkId"));
			consumptionInfo.setConLogInnerId(rs.getInt("ConLogInnerId"));
			consumptionInfo.setMoney(rs.getInt("Money"));
			consumptionInfo.setDiscountMoney(rs.getInt("DiscountMoney"));
			consumptionInfo.setConDatetime(rs.getTimestamp("ConDatetime"));
			consumptionInfo.setConTerminalInnerId(rs.getInt("ConTerminalInnerId"));

			return consumptionInfo;
		}

	}

}

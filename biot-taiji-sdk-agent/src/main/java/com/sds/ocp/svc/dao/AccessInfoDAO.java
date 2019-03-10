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
import com.sds.ocp.svc.vo.AccessAlarm;
import com.sds.ocp.svc.vo.AccessCard;
import com.sds.ocp.svc.vo.AccessDoor;
import com.sds.ocp.util.DateUtil;

@Repository
public class AccessInfoDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<AccessDoor> listAccessDoor() {
		
		logger.debug("listAccessDoor begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM icms_acs_door " ); 
		query.append("  WHERE alarmtime > :from ");
		
		Map<String, Object> param = new HashMap<>();
		long from = DateUtil.getTime("1900-01-01 00:00:00.000");
		param.put("from", new Timestamp(from));
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new AccessDoorRowMapper());
		
	}
	
	public List<AccessDoor> listAccessDoorAfter(Timestamp from) {
		
		logger.debug("listAccessDoorAfter begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM icms_acs_door " ); 
		query.append("  WHERE alarmtime > :from ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from );
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new AccessDoorRowMapper());
		
	}
	
	private static class AccessDoorRowMapper implements RowMapper<AccessDoor> {
		
		@Override
		public AccessDoor mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			AccessDoor accessDoor = new AccessDoor();
			
			accessDoor.setDoorId(rs.getInt("doorid"));
			accessDoor.setDoorName(rs.getString("doorname"));
			accessDoor.setLockStatus(rs.getInt("lockstatus"));
			accessDoor.setAlarmTime(rs.getTimestamp("alarmtime"));

			return accessDoor;
			
		}

	}
	
	public List<AccessAlarm> listAccessAlarm(Timestamp from , Timestamp to ) {
		
		logger.debug("listAccessAlarm begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM icms_acs_alarmevents " ); 
		query.append("  WHERE alarmtime > :from AND alarmtime <= :to ");
		query.append(" ORDER BY alarmtime ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new AccessAlarmRowMapper());
		
	}
	
	private static class AccessAlarmRowMapper implements RowMapper<AccessAlarm> {
		
		@Override
		public AccessAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			AccessAlarm accessAlarm = new AccessAlarm();
			
			accessAlarm.setAlarmEventTime(rs.getTimestamp("alarmtime"));
			accessAlarm.setAlarmIndex(rs.getInt("alarmndx"));
			accessAlarm.setDoorId(rs.getInt("doorid"));
			accessAlarm.setEventNote(rs.getString("eventnote"));

			return accessAlarm;
		}

	}
	
	public List<AccessCard> listAccessCard(Timestamp from , Timestamp to ) {
		
		logger.debug("listAccessCard begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT *  ");
		query.append("   FROM icms_acs_cardevents " ); 
		query.append("  WHERE ftime > :from AND ftime <= :to ");
		query.append(" ORDER BY ftime ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new AccessCardRowMapper());
		
	}
	
	private static class AccessCardRowMapper implements RowMapper<AccessCard> {
		
		@Override
		public AccessCard mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			AccessCard accessCard = new AccessCard();
			
			accessCard.setDoorId(rs.getInt("doorid"));
			accessCard.setCardEventTime(rs.getTimestamp("ftime"));
			accessCard.setCardIndex(rs.getInt("sn"));
			accessCard.setCardId(rs.getString("cardid"));
			accessCard.setInOut(rs.getInt("indoor"));

			return accessCard;
		}

	}

}

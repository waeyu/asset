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
import com.sds.ocp.svc.vo.LeakDetection;
import com.sds.ocp.svc.vo.PowerMeter;
import com.sds.ocp.svc.vo.TaijiDevice;

@Repository
public class ComputerRoomEnvDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<TaijiDevice> listTaijiDevice() {
		
		logger.debug("listTaijiDevice begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT a.DeviceCN , a.DEviceCode " );
		query.append("  FROM dcms_TDevices a , ");
		query.append("       ( SELECT distinct(HisFTag) from dcms_TDataHistory WHERE LEN(HisValue) > 0 ) b " ); 
		query.append(" WHERE a.DeviceCode = b.HisFTag " );
		
		Map<String, Object> param = new HashMap<>();
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new TDeviceRowMapper());
		
	}
	
	private static class TDeviceRowMapper implements RowMapper<TaijiDevice> {
		
		@Override
		public TaijiDevice mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			TaijiDevice taijiDevice = new TaijiDevice();

			taijiDevice.setDeviceCN(rs.getString("DeviceCN"));
			taijiDevice.setDeviceCode(rs.getString("DeviceCode"));

			return taijiDevice;
		}

	}
	
	public List<PowerMeter> powerMeterDataList(Timestamp from , Timestamp to) {
		
		logger.debug("buildingEnvDataList begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT HisFTag , HisDate , ");
		query.append("   SUM ( CASE WHEN HisTag = 'temperature' THEN  HisValue END ) AS \"temperature\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'humidity' THEN  HisValue END ) AS \"humidity\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'F' THEN  HisValue END ) AS \"f\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'UA' THEN  HisValue END ) AS \"ua\" , " ); 		
		query.append("   SUM ( CASE WHEN HisTag = 'UB' THEN  HisValue END ) AS \"ub\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'UC' THEN  HisValue END ) AS \"uc\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'IA' THEN  HisValue END ) AS \"ia\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'IB' THEN  HisValue END ) AS \"ib\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'IC' THEN  HisValue END ) AS \"ic\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'IN' THEN  HisValue END ) AS \"in\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'COSA' THEN  HisValue END ) AS \"cosa\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'COSB' THEN  HisValue END ) AS \"cosb\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'COSC' THEN  HisValue END ) AS \"cosc\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'COST' THEN  HisValue END ) AS \"cost\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'PA' THEN  HisValue END ) AS \"pa\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'PB' THEN  HisValue END ) AS \"pb\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'PC' THEN  HisValue END ) AS \"pc\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'PT' THEN  HisValue END ) AS \"pt\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'QA' THEN  HisValue END ) AS \"qa\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'QB' THEN  HisValue END ) AS \"qb\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'QC' THEN  HisValue END ) AS \"qc\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'QT' THEN  HisValue END ) AS \"qt\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'SA' THEN  HisValue END ) AS \"sa\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'SB' THEN  HisValue END ) AS \"sb\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'SC' THEN  HisValue END ) AS \"sc\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'ST' THEN  HisValue END ) AS \"st\" , " ); 		
		query.append("   SUM ( CASE WHEN HisTag = 'PEPT' THEN  HisValue END ) AS \"pept\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'NEPT' THEN  HisValue END ) AS \"nept\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'PEQT' THEN  HisValue END ) AS \"peqt\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'NEQT' THEN  HisValue END ) AS \"neqt\" " ); 	
		query.append("  FROM ( SELECT HisFTag , HisDate , HisTag , CONVERT ( float , HisValue ) HisValue ");
		query.append("           FROM dcms_TDataHistory ");
		query.append("          WHERE LEN(HisValue) > 0  ");
		query.append("          AND   HisFTag LIKE 'elect%'  ");
		query.append("          AND   HisDate > :from AND HisDate <= :to ) as t ");
		query.append(" GROUP BY HisFTag , HisDate ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new PowerMeterRowMapper());
		
	}
	
	private static class PowerMeterRowMapper implements RowMapper<PowerMeter> {
		
		@Override
		public PowerMeter mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			PowerMeter powerMeter = new PowerMeter();
			
			powerMeter.setDeviceCode(rs.getString("HisFTag"));
			powerMeter.setHisDate(rs.getTimestamp("HisDate"));
			
			powerMeter.setF(rs.getDouble("f"));
			powerMeter.setUa(rs.getDouble("ua"));
			powerMeter.setUb(rs.getDouble("ub"));
			powerMeter.setUc(rs.getDouble("uc"));
			powerMeter.setIa(rs.getDouble("ia"));
			powerMeter.setIb(rs.getDouble("ib"));
			powerMeter.setIc(rs.getDouble("ic"));		
			powerMeter.setIn(rs.getDouble("in"));
			powerMeter.setCosa(rs.getDouble("cosa"));			
			powerMeter.setCosb(rs.getDouble("cosb"));		
			powerMeter.setCosc(rs.getDouble("cosc"));		
			powerMeter.setCost(rs.getDouble("cost"));		
			powerMeter.setPa(rs.getDouble("pa"));			
			powerMeter.setPb(rs.getDouble("pb"));	
			powerMeter.setPc(rs.getDouble("pc"));	
			powerMeter.setPt(rs.getDouble("pt"));	
			powerMeter.setQa(rs.getDouble("qa"));			
			powerMeter.setQb(rs.getDouble("qb"));	
			powerMeter.setQc(rs.getDouble("qc"));	
			powerMeter.setQt(rs.getDouble("qt"));	
			powerMeter.setSa(rs.getDouble("sa"));			
			powerMeter.setSb(rs.getDouble("sb"));	
			powerMeter.setSc(rs.getDouble("sc"));	
			powerMeter.setSt(rs.getDouble("st"));				
			powerMeter.setPept(rs.getDouble("pept"));			
			powerMeter.setNept(rs.getDouble("nept"));	
			powerMeter.setPeqt(rs.getDouble("peqt"));	
			powerMeter.setNeqt(rs.getDouble("neqt"));	
			
			return powerMeter;
		}

	}
	
	public List<LeakDetection> leakDetectionDataList(Timestamp from , Timestamp to) {
		
		logger.debug("leakDetectionDataList begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT HisFTag , HisDate , ");
		query.append("   SUM ( CASE WHEN HisTag = 'leakpos' THEN  HisValue END ) AS \"leakPos\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'posohm' THEN  HisValue END ) AS \"posOhm\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'detectohm' THEN  HisValue END ) AS \"detectOhm\" , " ); 		
		query.append("   SUM ( CASE WHEN HisTag = 'detectcurrent' THEN  HisValue END ) AS \"detectCurrent\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'rhohm' THEN  HisValue END ) AS \"rhOhm\" " ); 	
		query.append("  FROM ( SELECT HisFTag , HisDate , HisTag , CONVERT ( float , HisValue ) HisValue ");
		query.append("           FROM dcms_TDataHistory ");
		query.append("          WHERE LEN(HisValue) > 0  ");
		query.append("          AND   HisFTag LIKE 'leakdect%'  ");
		query.append("          AND   HisDate > :from AND HisDate <= :to ) as t ");
		query.append(" GROUP BY HisFTag , HisDate ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new LeakDetectionRowMapper());
		
	}
	
	private static class LeakDetectionRowMapper implements RowMapper<LeakDetection> {
		
		@Override
		public LeakDetection mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			LeakDetection leakDetection = new LeakDetection();
			
			leakDetection.setDeviceCode(rs.getString("HisFTag"));
			leakDetection.setHisDate(rs.getTimestamp("HisDate"));
			
			leakDetection.setLeakPos(rs.getDouble("leakPos"));
			leakDetection.setPosOhm(rs.getDouble("posOhm"));
			leakDetection.setDetectOhm(rs.getDouble("detectOhm"));
			leakDetection.setDetectCurrent(rs.getDouble("detectCurrent"));
			leakDetection.setRhOhm(rs.getDouble("rhOhm"));
			
			return leakDetection;
		}

	}

}

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
import com.sds.ocp.svc.vo.BuildingEnvData;
import com.sds.ocp.svc.vo.TaijiDevice;

@Repository
public class BuildingEnvDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<TaijiDevice> listTaijiDevice() {
		
		logger.debug("listTaijiDevice begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT a.DeviceCN , a.DEviceCode " );
		query.append("  FROM ibms_TDevices a , ");
		query.append("       ( SELECT distinct(HisFTag) from ibms_TDataHistory WHERE LEN(HisValue) > 0 ) b " ); 
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
	
	public List<BuildingEnvData> buildingEnvDataList(Timestamp from , Timestamp to) {
		
		logger.debug("list begin.");
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT HisFTag , HisDate , ");
		query.append("   SUM ( CASE WHEN HisTag = 'temperature' THEN  HisValue END ) AS \"temperature\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'humidity' THEN  HisValue END ) AS \"humidity\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'co' THEN  HisValue END ) AS \"co\" , " ); 
		query.append("   SUM ( CASE WHEN HisTag = 'co2' THEN  HisValue END ) AS \"co2\" , " ); 		
		query.append("   SUM ( CASE WHEN HisTag = 'voc' THEN  HisValue END ) AS \"voc\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'PM25' THEN  HisValue END ) AS \"pm25\" , " ); 	
		query.append("   SUM ( CASE WHEN HisTag = 'noise' THEN  HisValue END ) AS \"noise\" " ); 	
		query.append("  FROM ( SELECT HisFTag , HisDate , HisTag , max( CONVERT ( float , HisValue ) ) HisValue ");
		query.append("           FROM ibms_TDataHistory ");
		query.append("          WHERE LEN(HisValue) > 0  ");
		query.append("          AND   HisDate > :from  ");
		query.append("          AND   HisDate <= :to  ");
		query.append("          GROUP BY HisFTag , HisDate , HisTag ) as t ");
		query.append(" GROUP BY HisFTag , HisDate ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("from", from);
		param.put("to", to);
		
		logger.debug("query [{}] " , query.toString() );
		return executeSelectQuery( query.toString() , param ,  new BuildingEnvDataRowMapper());
		
	}
	
	private static class BuildingEnvDataRowMapper implements RowMapper<BuildingEnvData> {
		
		@Override
		public BuildingEnvData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			BuildingEnvData buildingEnvData = new BuildingEnvData();
			
			buildingEnvData.setDeviceCode(rs.getString("HisFTag"));
			buildingEnvData.setHisDate(rs.getTimestamp("HisDate"));
			buildingEnvData.setTemperature(rs.getDouble("temperature"));
			buildingEnvData.setHumidity(rs.getDouble("humidity"));
			buildingEnvData.setCo(rs.getDouble("co"));
			buildingEnvData.setCo2(rs.getDouble("co2"));
			buildingEnvData.setVoc(rs.getDouble("voc"));
			buildingEnvData.setPm25(rs.getDouble("pm25"));			
			buildingEnvData.setNoise(rs.getDouble("noise"));			
			
			return buildingEnvData;
		}

	}

}

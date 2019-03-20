package com.sds.ocp.third.svc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.common.util.JsonUtil;
import com.sds.ocp.third.svc.vo.ThingMsgDataIn;
import com.sds.ocp.third.svc.vo.ThingMsgDataOut;

@Repository
public class ThingMessageDataDAO extends AbstractDAO {
	
	private int limit ;
	
	@Value("#{taijiRestProperties['limit'] != null ? taijiRestProperties['limit'] : 1000 }") 
	public void setLimit(int limit) {
		this.limit = limit;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "ocp_dataSource_log")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public List<ThingMsgDataOut> listThingMsgData(ThingMsgDataIn thingMsgDataIn , String siteId , List<String> thingNameList){
		
		if (logger.isDebugEnabled()) {
			logger.debug("listThingMsgData begin.");
		}
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT * " );
		query.append("  FROM stg_thing_msg_occu_dc ");
		query.append(" WHERE sid = :siteId " ); 
		query.append("   AND mcdtm > :timeFrom ");
		query.append("   AND mcdtm <= :timeTo ");
		query.append("   AND tnm in ( :thingNameList ) ");
		query.append("   AND sdrvtcd = 'RECEIVE' ");
		query.append("   AND mcd = :mcd ");
		query.append("   AND dsz > 0  ");
		query.append(" ORDER BY mcdtm desc  ");		
		query.append("   LIMIT :limit ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("siteId", siteId );
		param.put("timeFrom",thingMsgDataIn.getFromDate());
		param.put("timeTo",thingMsgDataIn.getToDate());
		param.put("thingNameList", thingNameList);
		param.put("mcd", thingMsgDataIn.getUserMessageCode());
		param.put("limit", limit );
		
		if (logger.isDebugEnabled()) {
			logger.debug("query [{}] " , query.toString() );
		}
		return executeSelectQuery( query.toString() , param ,  new ThingMsgDataMapper());				

	}
	
	public List<ThingMsgDataOut> listThingMsgDataRegister(ThingMsgDataIn thingMsgDataIn , String siteId , List<String> thingNameList){
		
		if (logger.isDebugEnabled()) {
			logger.debug("listThingMsgData begin.");
		}
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT * " );
		query.append("  FROM stg_thing_msg_occu_dc ");
		query.append(" WHERE sid = :siteId " ); 
		query.append("   AND register_datetime > :timeFrom ");
		query.append("   AND register_datetime <= :timeTo ");
		query.append("   AND tnm in ( :thingNameList ) ");
		query.append("   AND sdrvtcd = 'RECEIVE' ");
		query.append("   AND mcd = :mcd ");
		query.append("   AND dsz > 0  ");
		query.append(" ORDER BY mcdtm desc  ");		
		query.append("   LIMIT :limit ");
		
		Map<String, Object> param = new HashMap<>();
		param.put("siteId", siteId );
		param.put("timeFrom",thingMsgDataIn.getFromDate());
		param.put("timeTo",thingMsgDataIn.getToDate());
		param.put("thingNameList", thingNameList);
		param.put("mcd", thingMsgDataIn.getUserMessageCode());
		param.put("limit", limit );
		
		if (logger.isDebugEnabled()) {
			logger.debug("query [{}] " , query.toString() );
		}
		return executeSelectQuery( query.toString() , param ,  new ThingMsgDataMapper());				

	}
	
	private static class ThingMsgDataMapper implements RowMapper<ThingMsgDataOut> {
		
		@Override
		public ThingMsgDataOut mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ThingMsgDataOut thingMsgDataOut = new ThingMsgDataOut();
			
			thingMsgDataOut.setThingName(rs.getString("tnm"));
			thingMsgDataOut.setMessageId(rs.getString("mid"));
			thingMsgDataOut.setMsgCreateDatetime(rs.getTimestamp("mcdtm").getTime());
			thingMsgDataOut.setMsgRegisterDatetime(rs.getTimestamp("register_datetime").getTime());
			byte[] msgBody = rs.getBytes("mcn");
			if( msgBody!=null && msgBody.length > 0) {
				thingMsgDataOut.setMessage(JsonUtil.fromJsonToMap(msgBody));
			}

			return thingMsgDataOut;
		}

	}

}

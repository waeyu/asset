package com.sds.ocp.third.svc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;

@Repository
public class ThingMDataDAO extends AbstractDAO {
		
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "ocp_dataSource_spt")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public String getSiteId(String siteCode) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("getSiteId begin.");
		}
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT site_id " );
		query.append("  FROM spt_customer_site_bs ");
		query.append(" WHERE site_name = :siteName " );
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("siteName", siteCode );
		
		if (logger.isDebugEnabled()) {
			logger.debug("query [{}] " , query.toString() );
		}
		
		return executeSelectOneQuery( query.toString() , param ,  String.class );
	}
	
	public List<String> listThingName(String thingModelName, String siteId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("listThing begin.");
		}
		
		StringBuffer query = new StringBuffer();
		query.append(" SELECT a.thing_name " );
		query.append("  FROM spt_thing_bs a , spt_thing_model_bs b ");
		query.append(" WHERE a.site_id = :siteId AND a.thing_model_id = b.thing_model_id AND b.thing_model_name = :thingModelName " );
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("thingModelName", thingModelName);
		param.put("siteId", siteId);
		
		if (logger.isDebugEnabled()) {
			logger.debug("query [{}] " , query.toString() );
		}
		
		return executeSelectQuery( query.toString() , param ,  String.class );
		
	}

}

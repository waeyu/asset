package com.sds.ocp.third.svc.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.third.svc.vo.EventNotifyData;

@Repository
public class EventNotifyDataDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int limit ;
	
	@Value("#{taijiRestProperties['limit'] != null ? taijiRestProperties['limit'] : 1000 }") 
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Resource(name = "ocp_dataSource_spt")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public List<EventNotifyData> listEventNotifyData(Date from, Date to) {

		if (logger.isDebugEnabled()) {
			logger.debug("listEventNotifyData begin.");
		}

		StringBuffer query = new StringBuffer();
		query.append(" SELECT * ");
		query.append("  FROM THIRD_EVENT_NOTIFY ");
		query.append(" WHERE register_datetime > :timeFrom ");
		query.append("   AND register_datetime <= :timeTo ");
		query.append("   LIMIT :limit ");

		Map<String, Object> param = new HashMap<>();
		param.put("timeFrom", from);
		param.put("timeTo", to);
		param.put("limit", limit);

		if (logger.isDebugEnabled()) {
			logger.debug("query [{}] ", query.toString());
		}
		return executeSelectQuery( query.toString(), param, EventNotifyData.class );

	}

}

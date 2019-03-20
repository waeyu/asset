package com.sds.ocp.third.svc.dao;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.sds.afi.dao.AbstractDAO;
import com.sds.ocp.third.svc.vo.EventNotify;

@Repository
public class EventNotifyDAO extends AbstractDAO {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "ocp_dataSource_spt")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	public int insertNotify(EventNotify eventNotify) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("### EventNotifyDAO - insertNotify() : {}", eventNotify.toString());
		}
		
		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO THIRD_EVENT_NOTIFY ( EVENT_KEY , TARGET_ID , TARGET_NAME , EVENT_TIME , EVENT_MESSAGE , REGISTER_DATETIME ) " );
		query.append("  VALUES ( :eventKey , :targetId , :targetName , :eventTime , :eventMessage , CURRENT_TIMESTAMP ) ");

		try {
			return executeUpdateQuery( query.toString() , eventNotify );
		}
		catch(Exception e) {
			logger.info("insertNotify fail." , e);
		}
		return 0;
		
	}

}

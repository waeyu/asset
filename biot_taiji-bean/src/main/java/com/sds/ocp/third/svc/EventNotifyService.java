package com.sds.ocp.third.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sds.ocp.common.exception.OcpException;
import com.sds.ocp.third.svc.dao.EventNotifyDAO;
import com.sds.ocp.third.svc.vo.EventNotify;

@Service
@Transactional(value = "ocp_txManager_spt", rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
public class EventNotifyService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EventNotifyDAO visitorInfoNotifyDAO;

	@Autowired
	public void setVisitorInfoNotifyDAO(EventNotifyDAO visitorInfoNotifyDAO) {
		this.visitorInfoNotifyDAO = visitorInfoNotifyDAO;
	}
	
	@Transactional(value = "ocp_txManager_spt", noRollbackFor = { OcpException.class }, propagation = Propagation.REQUIRED)
	public void addEventNotfify(EventNotify eventNotify) {

		if (logger.isDebugEnabled()) {
			logger.debug("### VisitorInfoNotifyService - addEventNotfify() : {}", eventNotify.toString());
		}
		
		int count = visitorInfoNotifyDAO.insertNotify(eventNotify);		
		if(count==0) {
			logger.warn("insert notify fail. {}" , eventNotify.toString());
			throw new OcpException("insert Notify fail.");
		}

	}

}

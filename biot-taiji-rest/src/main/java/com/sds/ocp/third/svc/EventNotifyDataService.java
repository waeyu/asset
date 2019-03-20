package com.sds.ocp.third.svc;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sds.ocp.third.svc.dao.EventNotifyDataDAO;
import com.sds.ocp.third.svc.vo.EventNotifyData;

@Service
public class EventNotifyDataService {
	
	private EventNotifyDataDAO eventNotifyDataDAO;
	
	@Autowired
	public void setEventNotifyDataDAO(EventNotifyDataDAO eventNotifyDataDAO) {
		this.eventNotifyDataDAO = eventNotifyDataDAO;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public List<EventNotifyData> getEventNotifyData(Date from ,Date to){
		
		if (logger.isDebugEnabled()) {
			logger.debug("getEventNotifyData begin.");
		}
		
		return eventNotifyDataDAO.listEventNotifyData(from, to);

	}

}

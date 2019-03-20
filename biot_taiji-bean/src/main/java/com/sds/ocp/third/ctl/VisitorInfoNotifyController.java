package com.sds.ocp.third.ctl;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sds.ocp.asset.annotation.Asset;
import com.sds.ocp.asset.annotation.BeanController;
import com.sds.ocp.common.exception.OcpException;
import com.sds.ocp.third.ctl.vo.VisitorSensorInfo;
import com.sds.ocp.third.svc.EventNotifyService;
import com.sds.ocp.third.svc.VisitorInfoService;
import com.sds.ocp.third.svc.vo.EventNotify;
import com.sds.ocp.third.svc.vo.VisitorInfo;

@BeanController
public class VisitorInfoNotifyController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EventNotifyService eventNotifyService;	
	private VisitorInfoService visitorInfoService;
	
	@Autowired
	public void setEventNotifyService(EventNotifyService eventNotifyService) {
		this.eventNotifyService = eventNotifyService;
	}	
	
	@Autowired
	public void setVisitorInfoService(VisitorInfoService visitorInfoService) {
		this.visitorInfoService = visitorInfoService;
	}

	@Asset
	public void addEventNotfify(VisitorSensorInfo visitorSensor) {
		
		logger.debug("addEventNotfify called.");
		
		VisitorInfo visitorInfo = visitorInfoService.getVisitorInfo(visitorSensor.getOrderCode());
		if(visitorInfo == null) {
			logger.info("fail to get visitor info {}" , visitorSensor.getOrderCode());
			throw new OcpException("orderCode is abnormal.");
		}
		
		EventNotify eventNotify = new EventNotify();
		eventNotify.setEventKey(visitorSensor.getOrderCode());
		eventNotify.setTargetId(visitorInfo.getInnerId());
		eventNotify.setTargetName(visitorInfo.getInnerName());
		
		Long operDt = visitorSensor.getOperDtLong();
		if(operDt==null) {
			operDt = System.currentTimeMillis();
		}
		eventNotify.setEventTime(new Timestamp(operDt));
		String eventMessage = String.format("[%s] 访问了.", visitorInfo.getVisitorName() );
		eventNotify.setEventMessage(eventMessage);
		
		eventNotifyService.addEventNotfify(eventNotify);
		
		logger.info("### VisitorInfoNotifyController - addEventNotfify() success : {}", visitorSensor.toString());

	}

}

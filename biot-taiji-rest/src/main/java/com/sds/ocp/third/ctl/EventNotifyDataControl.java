package com.sds.ocp.third.ctl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sds.ocp.asset.annotation.Asset;
import com.sds.ocp.asset.annotation.AssetMapping;
import com.sds.ocp.asset.annotation.AssetParam;
import com.sds.ocp.common.exception.OcpException;
import com.sds.ocp.common.msg.StatusEnum;
import com.sds.ocp.common.util.DateUtil;
import com.sds.ocp.third.ctl.vo.EventNotifyInterfaceData;
import com.sds.ocp.third.svc.EventNotifyDataService;
import com.sds.ocp.third.svc.vo.EventNotifyData;

@RestController
public class EventNotifyDataControl {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EventNotifyDataService eventNotifyDataService;

	@Autowired
	public void setEventNotifyDataService(EventNotifyDataService eventNotifyDataService) {
		this.eventNotifyDataService = eventNotifyDataService;
	}
	
	@AssetMapping(value = "/v0.9/eventNotifyData", method = RequestMethod.GET)
	@Asset(moduleId="rest_service", reqMsgCode = "MSGTHD000011", resMsgCode = "MSGTHD000012", tag = "Event Data")
	public List<EventNotifyInterfaceData> getEventNotifyDataBefore(
			@RequestParam(required = false) @AssetParam("timeZoneId ( e.g. GMT-08:00 , GMT+08:00 , GMT+00:00 )") String timeZoneId ,
			@RequestParam(required = false) @AssetParam("before ( e.g. 10(10second) , 3(3second) )") Integer before  ) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("### getEventNotifyDataBefore");
		}
		
		Date fromDate = null;
		Date toDate = null;
		TimeZone timeZone = null;
		
		try {	
			timeZone = TimeZone.getTimeZone(timeZoneId);
		}
		catch ( Exception e) {
			throw new OcpException(StatusEnum.BAD_REQUEST, "timeZone is bad request." , e );
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("timeZone is {} ", timeZone.toString() );
		}
		
		try {	
			long currentTime = System.currentTimeMillis();
			toDate = new Date(currentTime);
			fromDate = new Date(currentTime - ( before * 1000) );
		}
		catch ( Exception e) {
			throw new OcpException(StatusEnum.BAD_REQUEST,
					"timeFrom or timeTo is bad request. ");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("from Date is {} , toDate is {} ", fromDate.toString() , toDate.toString() );
		}
		
		List<EventNotifyData> eventNotifyDataList = eventNotifyDataService.getEventNotifyData(fromDate, toDate);
		if(eventNotifyDataList.size()==0) {
			throw new OcpException(StatusEnum.NOT_FOUND, "no data exist.");
		}
		
		return convertThingMsgData(eventNotifyDataList , timeZone );
	}
	
	private List<EventNotifyInterfaceData> convertThingMsgData( List<EventNotifyData> eventNotifyDataList , TimeZone timeZone ) {
		
		List<EventNotifyInterfaceData> eventNotifyInterfaceDataList = new ArrayList<EventNotifyInterfaceData>();
		
		for( EventNotifyData eventNotifyData : eventNotifyDataList) {
			
			EventNotifyInterfaceData eventNotifyInterfaceData = new EventNotifyInterfaceData();
			
			eventNotifyInterfaceData.setEventKey(eventNotifyData.getEventKey());
			eventNotifyInterfaceData.setEventMessage(eventNotifyData.getEventMessage());
			eventNotifyInterfaceData.setTargetId(eventNotifyData.getTargetId());
			eventNotifyInterfaceData.setTargetName(eventNotifyData.getTargetName());
			
			eventNotifyInterfaceData.setRegisterDatetime(DateUtil.format(ThingMessageDataControl.DATE_PATTERN_FORMAT, 
					eventNotifyData.getRegisterDatetime().getTime() , timeZone ) );
			eventNotifyInterfaceData.setEventTime(DateUtil.format(ThingMessageDataControl.DATE_PATTERN_FORMAT, 
					eventNotifyData.getEventTime().getTime() , timeZone ) );
			
			eventNotifyInterfaceDataList.add(eventNotifyInterfaceData);			
			
		}
		return eventNotifyInterfaceDataList;

	}

}

package com.sds.ocp.third.ctl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sds.ocp.asset.annotation.Asset;
import com.sds.ocp.asset.annotation.AssetMapping;
import com.sds.ocp.asset.annotation.AssetParam;
import com.sds.ocp.common.exception.OcpException;
import com.sds.ocp.common.msg.StatusEnum;
import com.sds.ocp.common.util.DateParseUtil;
import com.sds.ocp.common.util.DateUtil;
import com.sds.ocp.common.util.StringUtil;
import com.sds.ocp.third.ctl.vo.ThingMsgData;
import com.sds.ocp.third.svc.ThingMessageDataService;
import com.sds.ocp.third.svc.vo.ThingMsgDataIn;
import com.sds.ocp.third.svc.vo.ThingMsgDataOut;

@RestController
public class ThingMessageDataControl {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public final static String DATE_PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private ThingMessageDataService thingMessageDataService;
	
	@Autowired
	public void setThingMessageDataService(ThingMessageDataService thingMessageDataService) {
		this.thingMessageDataService = thingMessageDataService;
	}

	@AssetMapping(value = "/v0.9/thingMsgDataFromTo/thingModelNames/{thingModelName}/userMessages/{userMessageCode}", method = RequestMethod.GET)
	@Asset(moduleId="rest_service", reqMsgCode = "MSGTHD000001", resMsgCode = "MSGTHD000002", tag = "Thing Message Data")
	public List<ThingMsgData> getThingMsgDataFromTo(
			@PathVariable @AssetParam("thingModelName (e.g. Ensensor , DoorSensor )") String thingModelName,
			@PathVariable @AssetParam("userMessageCode (e.g. Basic-AttrGroup)") String userMessageCode,
			@RequestParam(required = true) @AssetParam("siteCode ( e.g. ECC , SDSC )") String siteCode ,
			@RequestParam(required = true) @AssetParam("thingName ( e.g. Ensensor.ensensor-f1104 )") String thingName ,
			@RequestParam(required = true) @AssetParam("timeZoneId ( e.g. GMT-08:00 , GMT+08:00 , GMT+00:00 )") String timeZoneId ,
			@RequestParam(required = true) @AssetParam("start time for search, \"yyyy-MM-dd HH:mm:ss\"") String timeFrom,
			@RequestParam(required = true) @AssetParam("end time for search, \"yyyy-MM-dd HH:mm:ss\"") String timeTo  ) {

		if (logger.isDebugEnabled()) {
			logger.debug("### getThingMsgDataFromTo begin.");
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
			fromDate = DateParseUtil.parse( timeFrom, DATE_PATTERN_FORMAT , timeZone , Locale.getDefault() );
			toDate = DateParseUtil.parse( timeTo, DATE_PATTERN_FORMAT , timeZone , Locale.getDefault() );
		}
		catch ( Exception e) {
			throw new OcpException(StatusEnum.BAD_REQUEST,
					"timeFrom or timeTo is bad request. ");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("from Date is {} , toDate is {} ", fromDate.toString() , toDate.toString() );
		}
		
		ThingMsgDataIn thingMsgDataIn = new ThingMsgDataIn();
		thingMsgDataIn.setThingModelName(thingModelName);
		thingMsgDataIn.setUserMessageCode(userMessageCode);
		thingMsgDataIn.setSiteCode(siteCode);
		if(!StringUtil.isEmpty(thingName)) {
			thingMsgDataIn.setThingName(thingName);
		}
		thingMsgDataIn.setFromDate(fromDate);
		thingMsgDataIn.setToDate(toDate);
		
		List<ThingMsgDataOut> thingMsgDataOutList = thingMessageDataService.getThingMsgData(thingMsgDataIn,false);

		if (logger.isDebugEnabled()) {
			logger.debug("thingMsgDataOutList count is {} ", thingMsgDataOutList.size() );
		}
		
		return convertThingMsgData(thingMsgDataIn, thingMsgDataOutList, timeZone );
		
	}
	
	@AssetMapping(value = "/v0.9/thingMsgDataBefore/thingModelNames/{thingModelName}/userMessages/{userMessageCode}", method = RequestMethod.GET)
	@Asset(moduleId="rest_service", reqMsgCode = "MSGTHD000003", resMsgCode = "MSGTHD000004", tag = "Thing Message Data")
	public List<ThingMsgData> getThingMsgDataBefore(
			@PathVariable @AssetParam("thingModelName (e.g. Ensensor , DoorSensor )") String thingModelName,
			@PathVariable @AssetParam("userMessageCode (e.g. Basic-AttrGroup)") String userMessageCode,
			@RequestParam(required = true) @AssetParam("siteCode ( e.g. ECC , SDSC )") String siteCode ,
			@RequestParam(required = true) @AssetParam("thingName ( e.g. Ensensor.ensensor-f1104 )") String thingName ,
			@RequestParam(required = false) @AssetParam("timeZoneId ( e.g. GMT-08:00 , GMT+08:00 , GMT+00:00 )") String timeZoneId ,
			@RequestParam(required = false) @AssetParam("before ( e.g. 10(10second) , 3(3second) )") Integer before  ) {

		if (logger.isDebugEnabled()) {
			logger.debug("### getThingMsgDataBefore");
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
		
		ThingMsgDataIn thingMsgDataIn = new ThingMsgDataIn();
		thingMsgDataIn.setThingModelName(thingModelName);
		thingMsgDataIn.setUserMessageCode(userMessageCode);
		thingMsgDataIn.setSiteCode(siteCode);
		if(!StringUtil.isEmpty(thingName)) {
			thingMsgDataIn.setThingName(thingName);
		}
		thingMsgDataIn.setFromDate(fromDate);
		thingMsgDataIn.setToDate(toDate);
		
		List<ThingMsgDataOut> thingMsgDataOutList = thingMessageDataService.getThingMsgData(thingMsgDataIn,true);		
		
		if (logger.isDebugEnabled()) {
			logger.debug("thingMsgDataOutList count is {} ", thingMsgDataOutList.size() );
		}

		return convertThingMsgData(thingMsgDataIn, thingMsgDataOutList , timeZone);
	}
	
	private List<ThingMsgData> convertThingMsgData(ThingMsgDataIn thingMsgDataIn, List<ThingMsgDataOut> thingMsgDataOutList, TimeZone timeZone ) {
		
		List<ThingMsgData> thingMsgDataList = new ArrayList<ThingMsgData>();
		for(ThingMsgDataOut thingMsgDataOut : thingMsgDataOutList) {
			
			ThingMsgData thingMsgData = new ThingMsgData();
			thingMsgData.setMessageId(thingMsgDataOut.getMessageId());
			thingMsgData.setThingName(thingMsgDataOut.getThingName());
			thingMsgData.setMessage(thingMsgDataOut.getMessage());
			
			thingMsgData.setMsgCreateDatetime(DateUtil.format(DATE_PATTERN_FORMAT, 
					thingMsgDataOut.getMsgCreateDatetime() , timeZone ) );
			thingMsgData.setMsgRegisterDatetime(DateUtil.format(DATE_PATTERN_FORMAT, 
					thingMsgDataOut.getMsgRegisterDatetime() , timeZone ) );
			
			thingMsgDataList.add(thingMsgData);
		}
		
		logger.info("thingModelName {} , userMessageCode {} , thingMsgDataList count is {} ", 
				thingMsgDataIn.getThingModelName() , thingMsgDataIn.getUserMessageCode() , thingMsgDataList.size() );
		
		return thingMsgDataList; 
	}

}

package com.sds.ocp.svc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sds.ocp.OcpException;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.svc.vo.TagInfo;
import com.sds.ocp.svc.vo.TagInfoResponse;
import com.sds.ocp.svc.vo.TagPosition;
import com.sds.ocp.svc.vo.TagPositionResponse;
import com.sds.ocp.svc.vo.Zone;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class TagInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "qpe-tag-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "QuuppaTag";
	
	private int currentThingCount;
	
	private String quuppaBaseUrl ;
	
	@Value("#{comProperties['qpe.baseurl'] != null ? comProperties['qpe.baseurl'] : 'http://ecc.quuppa.io' }") 
	public void setQuuppaBaseUrl(String quuppaBaseUrl) {
		this.quuppaBaseUrl = quuppaBaseUrl;
	}
	
	private int connectionTimeout;
	
	@Value("#{comProperties['qpe.connectionTimeout'] != null ? comProperties['qpe.connectionTimeout'] : 2000 }") 
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	private int readTimeout;
	
	@Value("#{comProperties['qpe.readTimeout'] != null ? comProperties['qpe.readTimeout'] : 5000 }") 

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Value("#{comProperties['tag.rootThingName'] != null ? comProperties['tag.rootThingName'] : 'QuuppaSystem.TagRoot' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}	

	@Override
	public void action() {
		
		logger.debug("action begin.");
		
		List<TagInfo> tagInfoList = getTagInfoList();
		logger.info("getTagInfoList count : [{}]" , tagInfoList.size() );
		if( this.currentThingCount < tagInfoList.size() ) {
			addEdgeThings();
		}
		for (TagInfo tagInfo : tagInfoList ) {
			sendTagInfoMessage(tagInfo);
		}
		
		List<TagPosition> tagPositionList = getTagPositionList();
		logger.info("getTagPositionList count : [{}]" , tagPositionList.size() );
		for(TagPosition tagPosition : tagPositionList) {
			sendTagPositionMessage (tagPosition);
		}		
		
	}
	
	private List<TagInfo> getTagInfoList(){
		
		String url = this.quuppaBaseUrl + "/qpe/getTagInfo?version=2";		
		try {
			byte[] jsonBytes = httpConnection.request("GET", new URL(url), null, this.connectionTimeout , this.readTimeout, null );
			TagInfoResponse tagInfoResponse = JsonUtil.fromJson(jsonBytes, TagInfoResponse.class);
			if( tagInfoResponse.getCode() == 0)
				return tagInfoResponse.getTags() ;
			else {
				logger.warn("getTagInfo call fail. code[{}] message[{}]" , tagInfoResponse.getCode() , tagInfoResponse.getMessage());
				throw new OcpException("getTagInfo call fail. " + tagInfoResponse.getMessage() );
			}
		} catch ( OcpException e) {
			throw e;	
		} catch (MalformedURLException e) {
			logger.error("url information is abnormal.",e);
			throw new OcpException(e);
		} catch (IOException e) {
			logger.error("request io exception." , e);
			throw new OcpException(e);
		}

	}
	
	private List<TagPosition> getTagPositionList(){
		String url = this.quuppaBaseUrl + "/qpe/getTagPosition?version=2";		
		try {
			byte[] jsonBytes = httpConnection.request("GET", new URL(url), null, this.connectionTimeout , this.readTimeout, null );
			TagPositionResponse tagPositionResponse = JsonUtil.fromJson(jsonBytes, TagPositionResponse.class);
			if( tagPositionResponse.getCode() == 0)
				return tagPositionResponse.getTags() ;
			else {
				logger.warn("getTagPosition call fail. code[{}] message[{}]" , tagPositionResponse.getCode() , tagPositionResponse.getMessage());
				throw new OcpException("getTagPosition call fail. " + tagPositionResponse.getMessage() );
			}
		} catch ( OcpException e) {
			throw e;	
		} catch (MalformedURLException e) {
			logger.error("url information is abnormal.",e);
			throw new OcpException(e);
		} catch (IOException e) {
			logger.error("request io exception." , e);
			throw new OcpException(e);
		}
	}
	
	private void sendTagInfoMessage (TagInfo tagInfo) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("color", tagInfo.getColor() );
	    message.addProperty("deviceType", tagInfo.getDeviceType() );
	    message.addProperty("group", tagInfo.getGroup() );
	    message.addProperty("lastPacketTS", tagInfo.getLastPacketTS() );
	    
	    JsonArray jsonArray = null;
	    if(tagInfo.getAcceleration()!=null) {
	    	jsonArray = new JsonArray();
		    for(Double d : tagInfo.getAcceleration()) {
		    	jsonArray.add(d);
		    }
		    message.add("acceleration", jsonArray);
	    }
	    
	    message.addProperty("accelerationTS", tagInfo.getAccelerationTS() );
	    message.addProperty("batteryVoltage", tagInfo.getBatteryVoltage() );
	    message.addProperty("batteryVoltageTS", tagInfo.getBatteryVoltageTS() );
	    message.addProperty("batteryAlarm", tagInfo.getBatteryAlarm() );
	    message.addProperty("batteryAlarmTS", tagInfo.getBatteryAlarmTS() );
	    message.addProperty("buttonState", tagInfo.getButtonState() );
	    message.addProperty("buttonStateTS", tagInfo.getButtonStateTS() );
	    message.addProperty("lastButtonPressTS", tagInfo.getLastButtonPressTS() );
	    message.addProperty("lastButton2PressTS", tagInfo.getLastButton2PressTS() );
	    message.addProperty("tagState", tagInfo.getTagState() );
	    message.addProperty("tagStateTS", tagInfo.getTagStateTS() );
	    message.addProperty("tagStateTransitionStatus", tagInfo.getTagStateTransitionStatus() );
	    message.addProperty("tagStateTransitionStatusTS", tagInfo.getTagStateTransitionStatusTS() );
	    message.addProperty("triggerCount", tagInfo.getTriggerCount() );
	    message.addProperty("triggerCountTS", tagInfo.getTriggerCountTS() );
	    
	    if(tagInfo.getIoStates() != null) {
		    jsonArray = new JsonArray();
		    for(String s : tagInfo.getIoStates()) {
		    	jsonArray.add(s);
		    }
		    message.add("ioStates", jsonArray);
	    }

	    message.addProperty("rssi", tagInfo.getRssi() );
	    message.addProperty("rssiLocator", tagInfo.getRssiLocator() );

	    if(tagInfo.getRssiLocatorCoords()!=null) {
		    jsonArray = new JsonArray();
		    for(Double d : tagInfo.getRssiLocatorCoords()) {
		    	jsonArray.add(d);
		    }
		    message.add("rssiLocatorCoords", jsonArray);
	    }

	    message.addProperty("rssiCoordinateSystemId", tagInfo.getRssiCoordinateSystemId() );
	    message.addProperty("rssiCoordinateSystemName", tagInfo.getRssiCoordinateSystemName() );
	    message.addProperty("rssiTS", tagInfo.getRssiTS() );
	    message.addProperty("txRate", tagInfo.getTxRate() );
	    message.addProperty("txRateTS", tagInfo.getTxRateTS() );
	    message.addProperty("txPower", tagInfo.getTxPower() );
	    message.addProperty("txPowerTS", tagInfo.getTxPowerTS() );

	    if(tagInfo.getZones()!=null) {
		    jsonArray = new JsonArray();
		    for(Zone zone : tagInfo.getZones()) {
		    	JsonObject z = new JsonObject();
		    	z.addProperty("id", zone.getId() );
		    	z.addProperty("name", zone.getName() );
		    	jsonArray.add(z);
		    }
		    message.add("zones", jsonArray);
	    }
	    
	    message.addProperty("coordinateSystemId", tagInfo.getCoordinateSystemId() );
	    message.addProperty("coordinateSystemName", tagInfo.getCoordinateSystemName() );
	    message.addProperty("lastAreaId", tagInfo.getLastAreaId() );
	    message.addProperty("lastAreaName", tagInfo.getLastAreaName() );
	    message.addProperty("lastAreaTS", tagInfo.getLastAreaTS() );
	    message.addProperty("configStatus", tagInfo.getConfigStatus() );
	    message.addProperty("configStatusTS", tagInfo.getConfigStatusTS() );	     
	    
	    sendEdgeUserMessage( getEdgeThingName( tagInfo.getId() ) , "tagInfo" , message , tagInfo.getLastPacketTS() );

	}
	
	private void sendTagPositionMessage (TagPosition tagPosition) throws OcpException {
				
	    JsonObject message = new JsonObject();
	    message.addProperty("positionTS", tagPosition.getPositionTS() );

	    JsonArray jsonArray = null;
	    if(tagPosition.getSmoothedPosition()!=null) {
	    	jsonArray = new JsonArray();
		    for(Double d : tagPosition.getSmoothedPosition() ) {
		    	jsonArray.add(d);
		    }
		    message.add("smoothedPosition", jsonArray);
	    }
	    
	    message.addProperty("smoothedPositionAccuracy", tagPosition.getSmoothedPositionAccuracy() );
	    message.addProperty("color", tagPosition.getColor() );

	    if(tagPosition.getZones()!=null) {
		    jsonArray = new JsonArray();
		    for(Zone zone : tagPosition.getZones()) {
		    	JsonObject z = new JsonObject();
		    	z.addProperty("id", zone.getId() );
		    	z.addProperty("name", zone.getName() );
		    	jsonArray.add(z);
		    }
		    message.add("zones", jsonArray);
	    }
	    
	    message.addProperty("coordinateSystemId", tagPosition.getCoordinateSystemId() );
	    message.addProperty("coordinateSystemName", tagPosition.getCoordinateSystemName() );
	    message.addProperty("areaId", tagPosition.getAreaId() );
	    message.addProperty("areaName", tagPosition.getAreaName() );

	    if(tagPosition.getPosition()!=null) {
		    jsonArray = new JsonArray();
		    for(Double d : tagPosition.getPosition() ) {
		    	jsonArray.add(d);
		    }
		    message.add("position", jsonArray);
	    }
	    
	    message.addProperty("positionAccuracy", tagPosition.getPositionAccuracy() );

	    if(tagPosition.getCovarianceMatrix()!=null) {
		    jsonArray = new JsonArray();
		    for(Double d : tagPosition.getCovarianceMatrix() ) {
		    	jsonArray.add(d);
		    }
		    message.add("covarianceMatrix", jsonArray);	    
	    }
	    
	    sendEdgeUserMessage( getEdgeThingName(tagPosition.getId()) , "tagPosition" , message , tagPosition.getPositionTS() );

	}
	
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		
		connectPlatform();		
		addEdgeThings();	
		
	}
	
	private void addEdgeThings() {
		
		logger.debug("addEdgeThings begin.");
		
		File file =  PropertiesUtil.getPropertiesFile( PROPERTIES_DIR , PROPERTIES_THINGLIST_FILE_NAME );
		
		String thingListStr = getPropertiesValue( file , PROPERTIES_THINGLIST_FILE_NAME , PROPERTIES_THINGLIST_KEY );
		List<Object> thingList = null;
		if(!StringUtil.isEmpty(thingListStr)) {
			thingList = JsonUtil.fromJsonToList(thingListStr.getBytes());
		}
		else {
			thingList = new ArrayList<Object>();
		}
		
		List<TagInfo> tagInfoList = getTagInfoList();
		if( thingList.size() < tagInfoList.size() ) {
			logger.debug("thingList size is small than tagInfoList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(TagInfo tagInfo : tagInfoList) {
				logger.debug(tagInfo.toString());
				if( !thingList.contains( tagInfo.getId() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(tagInfo.getId()));
					reqEdgeThing.setThingName(getEdgeThingName(tagInfo.getId()));
					reqEdgeThing.setThingNickName(tagInfo.getName());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(tagInfo.getId());
				}
			}
			
			devicedSendLoop(edgeThingList, addThingList, EDGE_MODEL_NAME.length()) ;
			
			thingList.addAll(addThingList);
			setPropertiesValue( file , PROPERTIES_THINGLIST_KEY , JsonUtil.toJson(thingList) );
			
		}
		this.currentThingCount = thingList.size();
		
	}

	private String getEdgeThingName(String id) {
		return EDGE_MODEL_NAME + "." + String.valueOf(id);
	}

}

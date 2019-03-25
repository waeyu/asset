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
import com.sds.ocp.svc.vo.Channel;
import com.sds.ocp.svc.vo.LocatorInfo;
import com.sds.ocp.svc.vo.LocatorInfoResponse;
import com.sds.ocp.svc.vo.ReqEdgeThing;
import com.sds.ocp.util.JsonUtil;
import com.sds.ocp.util.PropertiesUtil;
import com.sds.ocp.util.StringUtil;

@Service
public class LocatorInfoService extends AbstractAgentService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTIES_THINGLIST_FILE_NAME 	= "qpe-locator-thing.properties";
	private static final String PROPERTIES_THINGLIST_KEY 	    = "thingList";
	private static final String EDGE_MODEL_NAME 				= "QuuppaLocator";
	
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

	@Value("#{comProperties['locator.rootThingName'] != null ? comProperties['locator.rootThingName'] : 'QuuppaSystem.LocatorRoot' }") 
	public void setThingName(String thingName) {
		this.thingName = thingName;
	}	

	@Override
	public void action() {
		
		logger.debug("action begin.");
		
		List<LocatorInfo> locatorInfoList = getLocatorInfoList();
		if( this.currentThingCount < locatorInfoList.size() ) {
			addEdgeThings();
		}
		logger.info("getLocatorInfoList count : [{}]" , locatorInfoList.size() );
		for (LocatorInfo locatorInfo : locatorInfoList ) {
			sendLocatorInfoMessage(locatorInfo);
		}
		
	}
	
	private List<LocatorInfo> getLocatorInfoList(){
		
		String url = this.quuppaBaseUrl + "/qpe/getLocatorInfo";		
		try {
			byte[] jsonBytes = httpConnection.request("GET", new URL(url), null, this.connectionTimeout , this.readTimeout, null );
			LocatorInfoResponse locatorInfoResponse = JsonUtil.fromJson(jsonBytes, LocatorInfoResponse.class);
			if( locatorInfoResponse.getCode() == 0)
				return locatorInfoResponse.getLocators();
			else {
				logger.warn("getLocatorInfo call fail. code[{}] message[{}]" , locatorInfoResponse.getCode() , locatorInfoResponse.getMessage());
				throw new OcpException("getLocatorInfo call fail. " + locatorInfoResponse.getMessage() );
			}
		} catch ( OcpException e) {
			throw e;
		}
		catch (MalformedURLException e) {
			logger.error("url information is abnormal.",e);
			throw new OcpException(e);
		} catch (IOException e) {
			logger.error("request io exception." , e);
			throw new OcpException(e);
		}

	}
	
	private void sendLocatorInfoMessage (LocatorInfo locatorInfo) throws OcpException {
		
	    JsonObject message = new JsonObject();
	    message.addProperty("ethernetMac", locatorInfo.getEthernetMac() );
	    message.addProperty("ipAddress", locatorInfo.getIpAddress() );
	    message.addProperty("mainVersion", locatorInfo.getMainVersion() );
	    message.addProperty("radioVersion", locatorInfo.getRadioVersion() );
	    message.addProperty("ethernetVersion", locatorInfo.getEthernetVersion() );
	    message.addProperty("startup", locatorInfo.getStartup() );
	    message.addProperty("lastPacketTS", locatorInfo.getLastPacketTS() );
	    message.addProperty("lastGoodPacketTS", locatorInfo.getLastGoodPacketTS() );
	    message.addProperty("packetsPerSecond", locatorInfo.getPacketsPerSecond() );
	    message.addProperty("connection", locatorInfo.getConnection() );
	    message.addProperty("temperature", locatorInfo.getTemperature() );
	    message.addProperty("areaId", locatorInfo.getAreaId() );
	    message.addProperty("areaName", locatorInfo.getAreaName() );
	    message.addProperty("coordinateSystemId", locatorInfo.getCoordinateSystemId() );
	    message.addProperty("coordinateSystemName", locatorInfo.getCoordinateSystemName() );
	    message.addProperty("accelerometerElevation", locatorInfo.getAccelerometerElevation() );
	    message.addProperty("accelerometerRotation", locatorInfo.getAccelerometerRotation() );
	    message.addProperty("accelerometerElevationDelta", locatorInfo.getAccelerometerElevationDelta() );
	    message.addProperty("accelerometerRotationDelta", locatorInfo.getAccelerometerRotationDelta() );

	    if(locatorInfo.getReceiveChannels()!=null) {
		    JsonArray jsonArray = new JsonArray();
		    for(Channel channel : locatorInfo.getReceiveChannels()) {
		    	JsonObject c = new JsonObject();
		    	c.addProperty("ble", channel.getBle() );
		    	c.addProperty("frequency", channel.getFrequency() );
		    	jsonArray.add(c);
		    }
		    message.add("receiveChannels", jsonArray);
	    }
	    
	    sendEdgeAttrMessage( getEdgeThingName( locatorInfo.getId() ) , message , locatorInfo.getLastPacketTS() );

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
		
		List<LocatorInfo> locatorInfoList = getLocatorInfoList();
		if( thingList.size() < locatorInfoList.size() ) {
			logger.debug("thingList size is small than locatorInfoList size.");
			List<Object> addThingList = new ArrayList<Object>();
			List<ReqEdgeThing> edgeThingList = new ArrayList<ReqEdgeThing>();
			for(LocatorInfo locatorInfo : locatorInfoList) {
				logger.debug(locatorInfo.toString());
				if( !thingList.contains( locatorInfo.getId() ) ) {
					
					ReqEdgeThing reqEdgeThing = new ReqEdgeThing();
					reqEdgeThing.setModelName(EDGE_MODEL_NAME);
					reqEdgeThing.setParentThingName(this.thingName);
					reqEdgeThing.setUniqueNum(String.valueOf(locatorInfo.getId()));
					reqEdgeThing.setThingName(getEdgeThingName(locatorInfo.getId()));
					reqEdgeThing.setThingNickName(locatorInfo.getName());
					reqEdgeThing.setActivationStateCode("ACT");
					
					edgeThingList.add(reqEdgeThing);					
					addThingList.add(locatorInfo.getId());
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

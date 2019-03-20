package com.sds.ocp.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sds.ocp.sdk.IIotActionCallback;
import com.sds.ocp.sdk.IotConnectManager;
import com.sds.ocp.sdk.message.vo.MomMessageVO;

public class IotActionCallback implements IIotActionCallback{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String siteId;
	private String thingName;
	private long waitMiliSecond;
	
	private int tryReconnectCount;
	
	public IotActionCallback(String siteId, String thingName, long waitMiliSecond) {
		this.tryReconnectCount = 0 ;
		this.siteId = siteId;
		this.thingName = thingName;
		this.waitMiliSecond = waitMiliSecond;
	}

	public void onServerRequest(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onServerRequest {} " , arrivedMessage.getData().toString());				
	}

	public void onNotification(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onNotification {} " , arrivedMessage.getData().toString());				
	}

	public void onArrivedAnswer(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onArrivedAnswer {} " , arrivedMessage.getData().toString());					
	}

	public void onFirmwareUpdateAlarm(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onFirmwareUpdateAlarm {} " , arrivedMessage.getData().toString());		
	}

	public void onFirmwareRecentVersionResponse(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onFirmwareRecentVersionResponse {} " , arrivedMessage.getData().toString());		
	}

	public void onProvisioningMessage(IotConnectManager thing, MomMessageVO arrivedMessage) {
		if(logger.isDebugEnabled() && arrivedMessage!=null && arrivedMessage.getData() != null)
			logger.debug("onFirmwareRecentVersionResponse {} " , arrivedMessage.getData().toString());		
	}

	public void onConnectionLost(IotConnectManager connectManager) {
		try {
			Thread.sleep( tryReconnectCount * this.waitMiliSecond );
			connectManager.connectThing( this.siteId , this.thingName );
			this.tryReconnectCount = 0;
		} catch (Exception e) {
			this.tryReconnectCount++;
			logger.error("connect fail.", e);
		}
	}

	public void onArrivedEdgeThingName(IotConnectManager thing, MomMessageVO arrivedMessage, String edgeThingName) {
		logger.debug("onArrivedEdgeThingName {} " , arrivedMessage.getData().toString());		
	}

	public void onArrivedAccessKey(IotConnectManager thing, MomMessageVO arrivedMessage) {
		logger.debug("onArrivedAccessKey {} " , arrivedMessage.getData().toString());		
	}

}

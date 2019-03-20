package com.sds.ocp.third.ctl.vo;

public class EventNotifyInterfaceData {
	
	private String eventKey ;
	
	private String targetId;
	
	private String targetName;
	
	private String eventTime;
	
	private String eventMessage;
	
	private String registerDatetime;

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getRegisterDatetime() {
		return registerDatetime;
	}

	public void setRegisterDatetime(String registerDatetime) {
		this.registerDatetime = registerDatetime;
	}

	@Override
	public String toString() {
		return "EventNotifyData [eventKey=" + eventKey + ", targetId=" + targetId + ", targetName=" + targetName
				+ ", eventTime=" + eventTime + ", eventMessage=" + eventMessage + ", registerDatetime="
				+ registerDatetime + "]";
	}

}

package com.sds.ocp.third.svc.vo;

import java.sql.Timestamp;

public class EventNotifyData {
	
	private String eventKey ;
	
	private String targetId;
	
	private String targetName;
	
	private Timestamp eventTime;
	
	private String eventMessage;
	
	private Timestamp registerDatetime;

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

	public Timestamp getEventTime() {
		return eventTime;
	}

	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public Timestamp getRegisterDatetime() {
		return registerDatetime;
	}

	public void setRegisterDatetime(Timestamp registerDatetime) {
		this.registerDatetime = registerDatetime;
	}

	@Override
	public String toString() {
		return "EventNotifyData [eventKey=" + eventKey + ", targetId=" + targetId + ", targetName=" + targetName
				+ ", eventTime=" + eventTime + ", eventMessage=" + eventMessage + ", registerDatetime="
				+ registerDatetime + "]";
	}

}

package com.sds.ocp.third.ctl.vo;

import java.util.Map;

public class ThingMsgData {
	
	private String thingName;
	
	private String messageId;
		
	private String msgCreateDatetime;
	
	private String msgRegisterDatetime;
	
	private Map<String,Object> message;

	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMsgCreateDatetime() {
		return msgCreateDatetime;
	}

	public void setMsgCreateDatetime(String msgCreateDatetime) {
		this.msgCreateDatetime = msgCreateDatetime;
	}

	public String getMsgRegisterDatetime() {
		return msgRegisterDatetime;
	}

	public void setMsgRegisterDatetime(String msgRegisterDatetime) {
		this.msgRegisterDatetime = msgRegisterDatetime;
	}

	public Map<String, Object> getMessage() {
		return message;
	}

	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}

}

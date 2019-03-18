package com.sds.ocp.third.svc.vo;

import java.util.Map;

public class ThingMsgDataOut {
	
	private String thingName;
	
	private String messageId;
	
	private long msgCreateDatetime;
	
	private long msgRegisterDatetime;
	
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

	

	public long getMsgCreateDatetime() {
		return msgCreateDatetime;
	}

	public void setMsgCreateDatetime(long msgCreateDatetime) {
		this.msgCreateDatetime = msgCreateDatetime;
	}

	public long getMsgRegisterDatetime() {
		return msgRegisterDatetime;
	}

	public void setMsgRegisterDatetime(long msgRegisterDatetime) {
		this.msgRegisterDatetime = msgRegisterDatetime;
	}

	public Map<String, Object> getMessage() {
		return message;
	}

	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ThingMsgDataOut [thingName=" + thingName + ", messageId=" + messageId + ", msgCreateDatetime="
				+ msgCreateDatetime + ", msgRegisterDatetime=" + msgRegisterDatetime + ", message=" + message + "]";
	}

}

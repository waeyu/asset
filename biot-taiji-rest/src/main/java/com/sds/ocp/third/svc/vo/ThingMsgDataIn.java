package com.sds.ocp.third.svc.vo;

import java.util.Date;

public class ThingMsgDataIn {
	
	private String thingModelName = null;
	
	private String userMessageCode = null;
	
	private String siteCode = null;
	
	private String thingName = null;

	private Date fromDate = null;
	
	private Date toDate = null;

	public String getThingModelName() {
		return thingModelName;
	}

	public void setThingModelName(String thingModelName) {
		this.thingModelName = thingModelName;
	}

	public String getUserMessageCode() {
		return userMessageCode;
	}

	public void setUserMessageCode(String userMessageCode) {
		this.userMessageCode = userMessageCode;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	
	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "ThingMsgDataIn [thingModelName=" + thingModelName + ", userMessageCode=" + userMessageCode
				+ ", siteCode=" + siteCode + ", thingName=" + thingName + ", fromDate=" + fromDate + ", toDate="
				+ toDate + "]";
	}
	
}

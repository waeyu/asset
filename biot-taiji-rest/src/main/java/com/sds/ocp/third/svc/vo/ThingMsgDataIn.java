package com.sds.ocp.third.svc.vo;

import java.util.Date;

public class ThingMsgDataIn {
	
	private String thingModelName;
	
	private String userMessageCode;
	
	private String siteCode;

	private Date fromDate;
	
	private Date toDate;
	
	private Boolean latestFirst;

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

	public Boolean getLatestFirst() {
		return latestFirst;
	}

	public void setLatestFirst(Boolean latestFirst) {
		this.latestFirst = latestFirst;
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
				+ ", siteCode=" + siteCode + ", fromDate=" + fromDate + ", toDate=" + toDate + ", latestFirst="
				+ latestFirst + "]";
	}
	
}

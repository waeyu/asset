package com.sds.ocp.third.svc.vo;

public class VisitorInfo {
	
	private String orderCode;
	
	private String innerId;
	
	private String innerName;
	
	private String visitorName;

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getInnerId() {
		return innerId;
	}

	public void setInnerId(String innerId) {
		this.innerId = innerId;
	}

	public String getInnerName() {
		return innerName;
	}



	public void setInnerName(String innerName) {
		this.innerName = innerName;
	}



	public String getVisitorName() {
		return visitorName;
	}



	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}



	@Override
	public String toString() {
		return "VisitorInfo [orderCode=" + orderCode + ", innerId=" + innerId + ", innerName=" + innerName
				+ ", visitorName=" + visitorName + "]";
	}
	
	
	
	

}

package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class VisitorInfo {
	
	private String innerName ;
	
	private String innerId;
	
	private String visitorName;
	
	private String orderCode;

	private Timestamp visitorDate;
	
	private Timestamp OperDt;
	
	
	@Override
	public String toString() {
		return "VisitorInfo [innerName=" + innerName + ", innerId=" + innerId + ", visitorName=" + visitorName
				+ ", orderCode=" + orderCode + ", visitorDate=" + visitorDate + ", OperDt=" + OperDt + "]";
	}

	public String getInnerName() {
		return innerName;
	}

	public void setInnerName(String innerName) {
		this.innerName = innerName;
	}

	public String getInnerId() {
		return innerId;
	}

	public void setInnerId(String innerId) {
		this.innerId = innerId;
	}

	public String getVisitorName() {
		return visitorName;
	}

	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Timestamp getVisitorDate() {
		return visitorDate;
	}

	public void setVisitorDate(Timestamp visitorDate) {
		this.visitorDate = visitorDate;
	}

	public Timestamp getOperDt() {
		return OperDt;
	}

	public void setOperDt(Timestamp operDt) {
		OperDt = operDt;
	}

}

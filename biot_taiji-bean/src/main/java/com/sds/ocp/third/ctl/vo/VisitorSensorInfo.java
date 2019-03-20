package com.sds.ocp.third.ctl.vo;

public class VisitorSensorInfo {

	private String orderCode;
	
	private String operDt;
	
	private Long operDtLong;

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOperDt() {
		return operDt;
	}

	public void setOperDt(String operDt) {
		this.operDt = operDt;
	}

	public Long getOperDtLong() {
		return operDtLong;
	}

	public void setOperDtLong(Long operDtLong) {
		this.operDtLong = operDtLong;
	}

	@Override
	public String toString() {
		return "VisitorSensorInfo [orderCode=" + orderCode + ", operDt=" + operDt + "]";
	}

}

package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class CarInOutInfo {
	
	private String carCode;
	
	private String inOut;
	
	private Timestamp crdtm;

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getInOut() {
		return inOut;
	}

	public void setInOut(String inOut) {
		this.inOut = inOut;
	}

	public Timestamp getCrdtm() {
		return crdtm;
	}

	public void setCrdtm(Timestamp crdtm) {
		this.crdtm = crdtm;
	}

}

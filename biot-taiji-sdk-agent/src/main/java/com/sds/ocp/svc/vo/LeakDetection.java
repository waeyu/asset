package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class LeakDetection {
	
	private String deviceCode;
	
	private Timestamp hisDate;
	
	private Double leakPos;
	
	private Double posOhm;
	
	private Double detectOhm;
	
	private Double detectCurrent;
	
	private Double rhOhm;

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Timestamp getHisDate() {
		return hisDate;
	}

	public void setHisDate(Timestamp hisDate) {
		this.hisDate = hisDate;
	}

	public Double getLeakPos() {
		return leakPos;
	}

	public void setLeakPos(Double leakPos) {
		this.leakPos = leakPos;
	}

	public Double getPosOhm() {
		return posOhm;
	}

	public void setPosOhm(Double posOhm) {
		this.posOhm = posOhm;
	}

	public Double getDetectOhm() {
		return detectOhm;
	}

	public void setDetectOhm(Double detectOhm) {
		this.detectOhm = detectOhm;
	}

	public Double getDetectCurrent() {
		return detectCurrent;
	}

	public void setDetectCurrent(Double detectCurrent) {
		this.detectCurrent = detectCurrent;
	}

	public Double getRhOhm() {
		return rhOhm;
	}

	public void setRhOhm(Double rhOhm) {
		this.rhOhm = rhOhm;
	}

}

package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class BuildingEnvData {
	
	private String deviceCode;
	
	private Timestamp hisDate;
	
	private Double temperature;
	
	private Double humidity;
	
	private Double co;
	
	private Double co2;
	
	private Double voc;
	
	private Double pm25;
	
	private Double noise;

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

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}

	public Double getCo() {
		return co;
	}

	public void setCo(Double co) {
		this.co = co;
	}

	public Double getCo2() {
		return co2;
	}

	public void setCo2(Double co2) {
		this.co2 = co2;
	}

	public Double getVoc() {
		return voc;
	}

	public void setVoc(Double voc) {
		this.voc = voc;
	}

	public Double getPm25() {
		return pm25;
	}

	public void setPm25(Double pm25) {
		this.pm25 = pm25;
	}

	public Double getNoise() {
		return noise;
	}

	public void setNoise(Double noise) {
		this.noise = noise;
	}

}

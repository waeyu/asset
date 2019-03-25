package com.sds.ocp.svc.vo;

import java.util.List;

public class LocatorInfo {
	
	private String id;
	
	private String name;
	
	private String ethernetMac;
	
	private String ipAddress;
	
	private String mainVersion;
	
	private String radioVersion;
	
	private String ethernetVersion;
	
	private String startup;
	
	private Long lastPacketTS;
	
	private Long lastGoodPacketTS;
	
	private Double packetsPerSecond;
	
	private String connection;
	
	private Double temperature;
	
	private String areaId;
	
	private String areaName;
	
	private String coordinateSystemId;
	
	private String coordinateSystemName;
	
	private Double accelerometerElevation;
	
	private Double accelerometerRotation;
	
	private Double accelerometerElevationDelta;
	
	private Double accelerometerRotationDelta;
	
	private List<Channel> receiveChannels;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEthernetMac() {
		return ethernetMac;
	}

	public void setEthernetMac(String ethernetMac) {
		this.ethernetMac = ethernetMac;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMainVersion() {
		return mainVersion;
	}

	public void setMainVersion(String mainVersion) {
		this.mainVersion = mainVersion;
	}

	public String getRadioVersion() {
		return radioVersion;
	}

	public void setRadioVersion(String radioVersion) {
		this.radioVersion = radioVersion;
	}

	public String getEthernetVersion() {
		return ethernetVersion;
	}

	public void setEthernetVersion(String ethernetVersion) {
		this.ethernetVersion = ethernetVersion;
	}

	public String getStartup() {
		return startup;
	}

	public void setStartup(String startup) {
		this.startup = startup;
	}

	public Long getLastPacketTS() {
		return lastPacketTS;
	}

	public void setLastPacketTS(Long lastPacketTS) {
		this.lastPacketTS = lastPacketTS;
	}

	public Long getLastGoodPacketTS() {
		return lastGoodPacketTS;
	}

	public void setLastGoodPacketTS(Long lastGoodPacketTS) {
		this.lastGoodPacketTS = lastGoodPacketTS;
	}

	public Double getPacketsPerSecond() {
		return packetsPerSecond;
	}

	public void setPacketsPerSecond(Double packetsPerSecond) {
		this.packetsPerSecond = packetsPerSecond;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getCoordinateSystemId() {
		return coordinateSystemId;
	}

	public void setCoordinateSystemId(String coordinateSystemId) {
		this.coordinateSystemId = coordinateSystemId;
	}

	public String getCoordinateSystemName() {
		return coordinateSystemName;
	}

	public void setCoordinateSystemName(String coordinateSystemName) {
		this.coordinateSystemName = coordinateSystemName;
	}

	public Double getAccelerometerElevation() {
		return accelerometerElevation;
	}

	public void setAccelerometerElevation(Double accelerometerElevation) {
		this.accelerometerElevation = accelerometerElevation;
	}

	public Double getAccelerometerRotation() {
		return accelerometerRotation;
	}

	public void setAccelerometerRotation(Double accelerometerRotation) {
		this.accelerometerRotation = accelerometerRotation;
	}

	public Double getAccelerometerElevationDelta() {
		return accelerometerElevationDelta;
	}

	public void setAccelerometerElevationDelta(Double accelerometerElevationDelta) {
		this.accelerometerElevationDelta = accelerometerElevationDelta;
	}

	public Double getAccelerometerRotationDelta() {
		return accelerometerRotationDelta;
	}

	public void setAccelerometerRotationDelta(Double accelerometerRotationDelta) {
		this.accelerometerRotationDelta = accelerometerRotationDelta;
	}

	public List<Channel> getReceiveChannels() {
		return receiveChannels;
	}

	public void setReceiveChannels(List<Channel> receiveChannels) {
		this.receiveChannels = receiveChannels;
	}

}

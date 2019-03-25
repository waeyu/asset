package com.sds.ocp.svc.vo;

import java.util.List;

public class TagInfo {
	
	private String id ;
	
	private String name;
	
	private String color;
	
	private String deviceType;
	
	private String group ;
	
	private Long lastPacketTS;
	
	private List<Double> acceleration;
	
	private Long accelerationTS;
	
	private Double batteryVoltage;
	
	private Long batteryVoltageTS;
	
	private String batteryAlarm;
	
	private Long batteryAlarmTS;
	
	private String buttonState;
	
	private Long buttonStateTS;
	
	private Long lastButtonPressTS;
	
	private Long lastButton2PressTS;
	
	private String tagState;
	
	private Long tagStateTS;
	
	private String tagStateTransitionStatus;
	
	private Long tagStateTransitionStatusTS;
	
	private Long triggerCount;
	
	private Long triggerCountTS;
	
	private List<String> ioStates;
	
	private Long ioStatesTS;
	
	private Double rssi;
	
	private String rssiLocator;
	
	private List<Double> rssiLocatorCoords;
	
	private String rssiCoordinateSystemId;
	
	private String rssiCoordinateSystemName;
	
	private Long rssiTS;
	
	private Double txRate;
	
	private Long txRateTS;
	
	private Double txPower;
	
	private Long txPowerTS;
	
	private List<Zone> zones;
	
	private String coordinateSystemId;
	
	private String coordinateSystemName;
	
	private String lastAreaId;
	
	private String lastAreaName;
	
	private Long lastAreaTS;
	
	private String configStatus;
	
	private Long configStatusTS;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getLastPacketTS() {
		return lastPacketTS;
	}

	public void setLastPacketTS(Long lastPacketTS) {
		this.lastPacketTS = lastPacketTS;
	}

	public List<Double> getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(List<Double> acceleration) {
		this.acceleration = acceleration;
	}

	public Long getAccelerationTS() {
		return accelerationTS;
	}

	public void setAccelerationTS(Long accelerationTS) {
		this.accelerationTS = accelerationTS;
	}

	public Double getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(Double batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public Long getBatteryVoltageTS() {
		return batteryVoltageTS;
	}

	public void setBatteryVoltageTS(Long batteryVoltageTS) {
		this.batteryVoltageTS = batteryVoltageTS;
	}

	public String getBatteryAlarm() {
		return batteryAlarm;
	}

	public void setBatteryAlarm(String batteryAlarm) {
		this.batteryAlarm = batteryAlarm;
	}

	public Long getBatteryAlarmTS() {
		return batteryAlarmTS;
	}

	public void setBatteryAlarmTS(Long batteryAlarmTS) {
		this.batteryAlarmTS = batteryAlarmTS;
	}

	public String getButtonState() {
		return buttonState;
	}

	public void setButtonState(String buttonState) {
		this.buttonState = buttonState;
	}

	public Long getButtonStateTS() {
		return buttonStateTS;
	}

	public void setButtonStateTS(Long buttonStateTS) {
		this.buttonStateTS = buttonStateTS;
	}

	public Long getLastButtonPressTS() {
		return lastButtonPressTS;
	}

	public void setLastButtonPressTS(Long lastButtonPressTS) {
		this.lastButtonPressTS = lastButtonPressTS;
	}

	public Long getLastButton2PressTS() {
		return lastButton2PressTS;
	}

	public void setLastButton2PressTS(Long lastButton2PressTS) {
		this.lastButton2PressTS = lastButton2PressTS;
	}

	public String getTagState() {
		return tagState;
	}

	public void setTagState(String tagState) {
		this.tagState = tagState;
	}

	public Long getTagStateTS() {
		return tagStateTS;
	}

	public void setTagStateTS(Long tagStateTS) {
		this.tagStateTS = tagStateTS;
	}

	public String getTagStateTransitionStatus() {
		return tagStateTransitionStatus;
	}

	public void setTagStateTransitionStatus(String tagStateTransitionStatus) {
		this.tagStateTransitionStatus = tagStateTransitionStatus;
	}

	public Long getTagStateTransitionStatusTS() {
		return tagStateTransitionStatusTS;
	}

	public void setTagStateTransitionStatusTS(Long tagStateTransitionStatusTS) {
		this.tagStateTransitionStatusTS = tagStateTransitionStatusTS;
	}

	public Long getTriggerCount() {
		return triggerCount;
	}

	public void setTriggerCount(Long triggerCount) {
		this.triggerCount = triggerCount;
	}

	public Long getTriggerCountTS() {
		return triggerCountTS;
	}

	public void setTriggerCountTS(Long triggerCountTS) {
		this.triggerCountTS = triggerCountTS;
	}

	public List<String> getIoStates() {
		return ioStates;
	}

	public void setIoStates(List<String> ioStates) {
		this.ioStates = ioStates;
	}

	public Long getIoStatesTS() {
		return ioStatesTS;
	}

	public void setIoStatesTS(Long ioStatesTS) {
		this.ioStatesTS = ioStatesTS;
	}

	public Double getRssi() {
		return rssi;
	}

	public void setRssi(Double rssi) {
		this.rssi = rssi;
	}

	public String getRssiLocator() {
		return rssiLocator;
	}

	public void setRssiLocator(String rssiLocator) {
		this.rssiLocator = rssiLocator;
	}

	public List<Double> getRssiLocatorCoords() {
		return rssiLocatorCoords;
	}

	public void setRssiLocatorCoords(List<Double> rssiLocatorCoords) {
		this.rssiLocatorCoords = rssiLocatorCoords;
	}

	public String getRssiCoordinateSystemId() {
		return rssiCoordinateSystemId;
	}

	public void setRssiCoordinateSystemId(String rssiCoordinateSystemId) {
		this.rssiCoordinateSystemId = rssiCoordinateSystemId;
	}

	public String getRssiCoordinateSystemName() {
		return rssiCoordinateSystemName;
	}

	public void setRssiCoordinateSystemName(String rssiCoordinateSystemName) {
		this.rssiCoordinateSystemName = rssiCoordinateSystemName;
	}

	public Long getRssiTS() {
		return rssiTS;
	}

	public void setRssiTS(Long rssiTS) {
		this.rssiTS = rssiTS;
	}

	public Double getTxRate() {
		return txRate;
	}

	public void setTxRate(Double txRate) {
		this.txRate = txRate;
	}

	public Long getTxRateTS() {
		return txRateTS;
	}

	public void setTxRateTS(Long txRateTS) {
		this.txRateTS = txRateTS;
	}

	public Double getTxPower() {
		return txPower;
	}

	public void setTxPower(Double txPower) {
		this.txPower = txPower;
	}

	public Long getTxPowerTS() {
		return txPowerTS;
	}

	public void setTxPowerTS(Long txPowerTS) {
		this.txPowerTS = txPowerTS;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
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

	public String getLastAreaId() {
		return lastAreaId;
	}

	public void setLastAreaId(String lastAreaId) {
		this.lastAreaId = lastAreaId;
	}

	public String getLastAreaName() {
		return lastAreaName;
	}

	public void setLastAreaName(String lastAreaName) {
		this.lastAreaName = lastAreaName;
	}

	public Long getLastAreaTS() {
		return lastAreaTS;
	}

	public void setLastAreaTS(Long lastAreaTS) {
		this.lastAreaTS = lastAreaTS;
	}

	public String getConfigStatus() {
		return configStatus;
	}

	public void setConfigStatus(String configStatus) {
		this.configStatus = configStatus;
	}

	public Long getConfigStatusTS() {
		return configStatusTS;
	}

	public void setConfigStatusTS(Long configStatusTS) {
		this.configStatusTS = configStatusTS;
	}

}

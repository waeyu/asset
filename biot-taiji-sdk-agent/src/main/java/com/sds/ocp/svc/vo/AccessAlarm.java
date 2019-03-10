package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class AccessAlarm {
	
	private Integer doorId;
	
	private String eventNote;
	
	private Integer alarmIndex;
	
	private Timestamp alarmEventTime;

	public Integer getDoorId() {
		return doorId;
	}

	public void setDoorId(Integer doorId) {
		this.doorId = doorId;
	}

	public String getEventNote() {
		return eventNote;
	}

	public void setEventNote(String eventNote) {
		this.eventNote = eventNote;
	}

	public Integer getAlarmIndex() {
		return alarmIndex;
	}

	public void setAlarmIndex(Integer alarmIndex) {
		this.alarmIndex = alarmIndex;
	}

	public Timestamp getAlarmEventTime() {
		return alarmEventTime;
	}

	public void setAlarmEventTime(Timestamp alarmEventTime) {
		this.alarmEventTime = alarmEventTime;
	}

}

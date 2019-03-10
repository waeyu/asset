package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class ConsumptionInfo {
	
	private String markId;
	
	private int money;
	
	private int discountMoney;
	
	private Timestamp conDatetime;
	
	private int conLogInnerId;
	
	private int conTerminalInnerId;

	public String getMarkId() {
		return markId;
	}

	public void setMarkId(String markId) {
		this.markId = markId;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(int discountMoney) {
		this.discountMoney = discountMoney;
	}

	public Timestamp getConDatetime() {
		return conDatetime;
	}

	public void setConDatetime(Timestamp conDatetime) {
		this.conDatetime = conDatetime;
	}

	public int getConLogInnerId() {
		return conLogInnerId;
	}

	public void setConLogInnerId(int conLogInnerId) {
		this.conLogInnerId = conLogInnerId;
	}

	public int getConTerminalInnerId() {
		return conTerminalInnerId;
	}

	public void setConTerminalInnerId(int conTerminalInnerId) {
		this.conTerminalInnerId = conTerminalInnerId;
	}

}

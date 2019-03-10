package com.sds.ocp.svc.vo;

import java.sql.Timestamp;

public class AccessCard {
	
	private Integer doorId;
	
	private Integer cardIndex;
	
	private Timestamp cardEventTime;
	
	private String cardId;
	
	private Integer inOut;

	public Integer getDoorId() {
		return doorId;
	}

	public void setDoorId(Integer doorId) {
		this.doorId = doorId;
	}

	public Integer getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(Integer cardIndex) {
		this.cardIndex = cardIndex;
	}

	public Timestamp getCardEventTime() {
		return cardEventTime;
	}

	public void setCardEventTime(Timestamp cardEventTime) {
		this.cardEventTime = cardEventTime;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Integer getInOut() {
		return inOut;
	}

	public void setInOut(Integer inOut) {
		this.inOut = inOut;
	}

}

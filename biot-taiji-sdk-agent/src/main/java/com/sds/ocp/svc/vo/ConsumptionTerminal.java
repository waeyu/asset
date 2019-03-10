package com.sds.ocp.svc.vo;

public class ConsumptionTerminal {

	private Integer conTerminalInnerId;
	
	private String ConTerminalName;
	
	private String comId;
	
	private String comSerials;

	public Integer getConTerminalInnerId() {
		return conTerminalInnerId;
	}

	public void setConTerminalInnerId(Integer conTerminalInnerId) {
		this.conTerminalInnerId = conTerminalInnerId;
	}

	public String getConTerminalName() {
		return ConTerminalName;
	}

	public void setConTerminalName(String conTerminalName) {
		ConTerminalName = conTerminalName;
	}

	public String getComId() {
		return comId;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public String getComSerials() {
		return comSerials;
	}

	public void setComSerials(String comSerials) {
		this.comSerials = comSerials;
	}

	@Override
	public String toString() {
		return "ConsumptionTerminal [conTerminalInnerId=" + conTerminalInnerId + ", ConTerminalName=" + ConTerminalName
				+ ", comId=" + comId + ", comSerials=" + comSerials + "]";
	}
	
}

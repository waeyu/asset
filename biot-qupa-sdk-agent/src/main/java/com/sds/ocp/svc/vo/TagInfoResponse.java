package com.sds.ocp.svc.vo;

import java.util.List;

public class TagInfoResponse {
	
	private Integer code;
	
	private String command;
	
	private String message;
	
	private Long responseTS;
	
	private String status;
	
	private String version;
	
	private List<TagInfo> tags;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getResponseTS() {
		return responseTS;
	}

	public void setResponseTS(Long responseTS) {
		this.responseTS = responseTS;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

}

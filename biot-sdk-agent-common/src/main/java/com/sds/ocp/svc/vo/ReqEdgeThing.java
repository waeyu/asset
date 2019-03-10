package com.sds.ocp.svc.vo;

import java.io.Serializable;

public class ReqEdgeThing implements Serializable {

    private static final long serialVersionUID = -1;

    private String parentThingName;

    private String modelName;

    private String uniqueNum;

    private String thingName;

	private String activationStateCode;
    
	private String				thingNickName;

    public String getParentThingName() {
        return parentThingName;
    }

    public void setParentThingName(String parentThingName) {
        this.parentThingName = parentThingName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getUniqueNum() {
        return uniqueNum;
    }

    public void setUniqueNum(String uniqueNum) {
        this.uniqueNum = uniqueNum;
    }

    public String getThingName() {
        return thingName;
    }

    public void setThingName(String thingName) {
        this.thingName = thingName;
	}

	public String getActivationStateCode() {
		return activationStateCode;
	}

	public void setActivationStateCode(String activationStateCode) {
		this.activationStateCode = activationStateCode;
    }

	
    public String getThingNickName() {
		return thingNickName;
	}

	public void setThingNickName(String thingNickName) {
		this.thingNickName = thingNickName;
	}

	@Override
	public String toString() {
		return "ReqEdgeThing [parentThingName=" + parentThingName + ", modelName=" + modelName + ", uniqueNum=" + uniqueNum
				+ ", thingName=" + thingName + ", activationStateCode=" + activationStateCode + ", thingNickName=" + thingNickName
				+ "]";
	}

}

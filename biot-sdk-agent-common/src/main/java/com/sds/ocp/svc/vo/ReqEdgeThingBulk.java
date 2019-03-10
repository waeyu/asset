package com.sds.ocp.svc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReqEdgeThingBulk implements Serializable {

    private static final long serialVersionUID = -1;

    private List<ReqEdgeThing> things = new ArrayList<ReqEdgeThing>();

    public List<ReqEdgeThing> getThings() {
        return things;
    }

    public void setThings(List<ReqEdgeThing> things) {
        this.things = things;
    }

    @Override
    public String toString() {
        return "ReqEdgeThingBulk{" +
                "things=" + things +
                '}';
    }
}

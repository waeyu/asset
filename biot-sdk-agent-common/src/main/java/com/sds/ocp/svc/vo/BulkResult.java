package com.sds.ocp.svc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BulkResult implements Serializable {

    private static final long serialVersionUID = -1;

    private int totalCount;
    private int successCount;
    private int failCount;
    private List<String> failThings = new ArrayList<String>();

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public List<String> getFailThings() {
        return failThings;
    }

    public void setFailThings(List<String> failThings) {
        this.failThings = failThings;
    }

    @Override
    public String toString() {
        return "BulkThingResponse{" +
                "totalCount=" + totalCount +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                ", failThings=" + failThings +
                "}";
    }
}

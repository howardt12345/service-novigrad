package com.uottawa.servicenovigrad.branch;

import java.util.ArrayList;
import java.util.Date;

public class ServiceRequest {
    private String id;
    private String customerId;
    private String branchId;

    private ArrayList<String> info;
    private Date scheduledTime;

    private boolean approved, responded;

    public ServiceRequest() {
        id = "";
        customerId = "";
        branchId = "";

        info = new ArrayList<>();
        scheduledTime = new Date();

        approved = false;
        responded = false;
    }

    public ServiceRequest(
            String id,
            String customerId,
            String branchId,
            ArrayList<String> info,
            Date scheduledTime,
            boolean approved,
            boolean responded
    ) {
        this.id = id;
        this.customerId = customerId;
        this.branchId = branchId;

        this.info = info;
        this.scheduledTime = scheduledTime;

        this.approved = approved;
        this.responded = responded;
    }

    public String getTitle() {
        return id;
    }

    public String getDesc() {
        return customerId;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public ArrayList<String> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<String> info) {
        this.info = info;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }
}

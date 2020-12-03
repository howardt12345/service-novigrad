package com.uottawa.servicenovigrad.branch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ServiceRequest implements Serializable {
    private String id;
    private String customerId;
    private String branchId;
    private String serviceId;

    private String customerName;
    private String branchName;
    private String serviceName;

    private ArrayList<String> info;
    private Date scheduledTime;

    private boolean approved, responded;

    public ServiceRequest() {
        id = "";
        customerId = "";
        branchId = "";
        serviceId = "";

        info = new ArrayList<>();
        scheduledTime = new Date();

        approved = false;
        responded = false;
    }

    public ServiceRequest(
            String id,
            String customerId,
            String branchId,
            String serviceId,
            String customerName,
            String branchName,
            String serviceName,
            ArrayList<String> info,
            Date scheduledTime,
            boolean approved,
            boolean responded
    ) {
        this.id = id;
        this.customerId = customerId;
        this.branchId = branchId;
        this.serviceId = serviceId;

        this.customerName = customerName;
        this.branchName = branchName;
        this.serviceName = serviceName;

        this.info = info;
        this.scheduledTime = scheduledTime;

        this.approved = approved;
        this.responded = responded;
    }

    public String getBranchSideTitle() {
        return "From " + customerName;
    }

    public String getBranchSideDesc() {
        return "Request by " + customerName + " for " + serviceName + " on " + scheduledTime.toString();
    }

    public String getCustomerSideTitle() {
        return "To " + branchName;
    }

    public String getCustomerSideDesc() {
        return "Request to " + branchName + " for " + serviceName + " on " + scheduledTime.toString();
    }

    public String getRequestInfo() {
        //New string builder
        StringBuilder sb = new StringBuilder();

        sb.append("Branch Name: " + branchName + "\n");
        sb.append("Customer Name: " + customerName + "\n");
        sb.append("For Service: " + serviceName + "\n");
        sb.append("Scheduled Time: " + scheduledTime.toString() + "\n");

        sb.append("\nInfo Provided: \n");

        //Add the documents to the string
        for(String i : info) {
            sb.append(" > " + i + "\n");
        }

        if(responded) {
            sb.append("\nApproved: " + (approved ? "yes" : "no"));
        } else {
            sb.append("\nResponded: " + (responded ? "yes" : "no"));
        }
        return sb.toString();
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

    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getServiceName() {
        return serviceName;
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

    public boolean isResponded() {
        return responded;
    }

}

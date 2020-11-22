package com.uottawa.servicenovigrad.branch;

import java.util.ArrayList;
import java.util.Date;

public class ServiceRequest {
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

    public String getTitle() {
        return "From " + customerName;
    }

    public String getDesc() {
        return "Request by " + customerName + " for " + serviceName + " on " + scheduledTime.toString();
    }

    public String getRequestInfo() {
        //New string builder
        StringBuilder sb = new StringBuilder();

        sb.append("Customer Name: " + customerName + "\n");
        sb.append("For Service: " + serviceName + "\n");
        sb.append("Scheduled Time: " + scheduledTime.toString() + "\n");

        sb.append("\nInfo Provided: \n");

        //Add the documents to the string
        for(String i : info) {
            sb.append(" > " + i + "\n");
        }

        if(responded) {
            sb.append("Approved: " + (approved ? "yes" : "no"));
        } else {
            sb.append("Responded: " + (responded ? "yes" : "no"));
        }
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getServiceId() {
        return serviceId;
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

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isResponded() {
        return responded;
    }

}

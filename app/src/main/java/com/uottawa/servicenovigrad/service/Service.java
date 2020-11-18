package com.uottawa.servicenovigrad.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Service implements Serializable {
    private String id;
    private String name;
    private String desc;
    private List<String> forms;
    private List<String> documents;
    private int price;

    public Service() {
        id = "";
        name = "";
        desc = "";
        price = 0;
        forms = new ArrayList<String>();
        documents = new ArrayList<String>();
    }

    public Service(String id, String name, String desc, List<String> forms, List<String> documents, int price) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.forms = forms;
        this.documents = documents;
        this.price = price;
    }

    //Getters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public List<String> getForms() {
        return forms;
    }
    public List<String> getDocuments() {
        return documents;
    }
    public int getPrice() {
        return price;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setForms(List<String> forms) {
        this.forms = forms;
    }
    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the info of the service in a format for a dialog.
     * @return The info on this service.
     */
    public String getInfo() {
        //New string builder
        StringBuilder sb = new StringBuilder();
        //Append the description of the service
        sb.append(desc + "\n");
        //Add forms header
        sb.append("\nInformation required: \n");
        //Add the forms to the string
        for(String form : forms) {
            sb.append(" > " + form + "\n");
        }
        //Add documents header
        sb.append("\nDocuments required: \n");
        //Add the documents to the string
        for(String doc : documents) {
            sb.append(" > " + doc + "\n");
        }
        sb.append("Price: " + price + "\n");
        //Return the string builder result
        return sb.toString();
    }
}

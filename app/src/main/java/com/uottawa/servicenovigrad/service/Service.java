package com.uottawa.servicenovigrad.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Service implements Serializable {
    private String id = "";
    private String name = "";
    private String desc = "";
    private List<String> forms;
    private List<String> documents;

    public Service() {
        forms = documents = new ArrayList<String>();
    }

    public Service(String id, String name, String desc, List<String> forms, List<String> documents) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.forms = forms;
        this.documents = documents;
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
}

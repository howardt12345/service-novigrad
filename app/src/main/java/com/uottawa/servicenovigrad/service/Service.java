package com.uottawa.servicenovigrad.service;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Service {
    private String id;
    private String name;
    private String desc;
    private List<String> forms;
    private List<String> documents;

    public Service() {}

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
}

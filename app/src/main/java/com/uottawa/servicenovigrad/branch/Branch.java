package com.uottawa.servicenovigrad.branch;

import com.uottawa.servicenovigrad.service.Service;

import java.util.List;

public class Branch {
    private String name;
    private String description;
    private String address;
    private String phoneNumber;

    private List<String> services;

    private List<String> openDays;
    private int openingHour, openingMinute;
    private int closingHour, closingMinute;

    private float rating;
}

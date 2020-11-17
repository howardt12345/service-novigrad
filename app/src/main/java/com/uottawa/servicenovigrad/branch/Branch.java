package com.uottawa.servicenovigrad.branch;

import com.uottawa.servicenovigrad.service.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Branch implements Serializable {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;

    private List<String> services;

    private List<String> openDays;
    private int openingHour, openingMinute;
    private int closingHour, closingMinute;

    private double rating;

    public Branch() {
        id = "";
        name = "";
        address = "";
        phoneNumber = "";

        services = new ArrayList<String>();
        openDays = new ArrayList<String>();
        openingHour = openingMinute = 0;
        closingHour = closingMinute = 0;

        rating = 0.0;
    }

    public Branch(
            String id,
            String name,
            String address,
            String phoneNumber
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;

        services = new ArrayList<String>();
        openDays = new ArrayList<String>();
        openingHour = openingMinute = 0;
        closingHour = closingMinute = 0;

        rating = 0.0f;
    }

    public Branch(
            String id,
            String name,
            String address,
            String phoneNumber,
            List<String> services,
            List<String> openDays,
            int openingHour,
            int openingMinute,
            int closingHour,
            int closingMinute,
            double rating
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;

        this.services = services;
        this.openDays = openDays;

        this.openingHour = openingHour;
        this.openingMinute = openingMinute;
        this.closingHour = closingHour;
        this.closingMinute = closingMinute;

        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getRating() {
        return rating;
    }
}

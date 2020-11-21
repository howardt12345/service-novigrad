package com.uottawa.servicenovigrad.branch;

import java.io.Serializable;
import java.util.ArrayList;

public class Branch implements Serializable {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;

    private ArrayList<String> services;

    private ArrayList<String> openDays;
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
        closingHour = 0;
        closingMinute = 1;

        rating = 0.0f;
    }

    public Branch(
            String id,
            String name,
            String address,
            String phoneNumber,
            ArrayList<String> services,
            ArrayList<String> openDays,
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

    public ArrayList<String> getServices() {
        return services;
    }

    public ArrayList<String> getOpenDays() {
        return openDays;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public int getOpeningMinute() {
        return openingMinute;
    }

    public int getClosingHour() {
        return closingHour;
    }

    public int getClosingMinute() {
        return closingMinute;
    }

    public double getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public void setOpenDays(ArrayList<String> openDays) {
        this.openDays = openDays;
    }

    public void setOpeningHour(int openingHour) {
        this.openingHour = openingHour;
    }

    public void setOpeningMinute(int openingMinute) {
        this.openingMinute = openingMinute;
    }

    public void setClosingHour(int closingHour) {
        this.closingHour = closingHour;
    }

    public void setClosingMinute(int closingMinute) {
        this.closingMinute = closingMinute;
    }
}

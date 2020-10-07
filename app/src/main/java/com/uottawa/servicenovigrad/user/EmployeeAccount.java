package com.uottawa.servicenovigrad.user;

public class EmployeeAccount extends UserAccount {

    public EmployeeAccount(String name, String email, String uid) {
        super(name, email, "employee", uid);
    }
}

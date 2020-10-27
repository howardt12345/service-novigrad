package com.uottawa.servicenovigrad.user;

public class EmployeeAccount extends UserAccount {

    String branch;

    public EmployeeAccount(String name, String email, String uid) {
        super(name, email, "employee", uid);
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String b) {
        this.branch = b;
    }

    @Override
    public String toString() {
        return null;
    }
}

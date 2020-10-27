package com.uottawa.servicenovigrad.user;

public class AdminAccount extends UserAccount {


    public AdminAccount(String uid) {
        super("admin", "admin", "admin", uid);
    }

    public UserAccount[] getEmployees() {
        return null;
    }

    public UserAccount[] getCustomers() {
        return null;
    }
}

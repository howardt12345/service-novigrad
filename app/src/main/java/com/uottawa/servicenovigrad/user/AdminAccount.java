package com.uottawa.servicenovigrad.user;

public class AdminAccount extends UserAccount {


    public AdminAccount(String uid) {
        super("admin", "admin", "admin", uid);
    }
}

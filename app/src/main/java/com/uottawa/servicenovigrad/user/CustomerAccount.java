package com.uottawa.servicenovigrad.user;

public class CustomerAccount extends UserAccount {

    public CustomerAccount(String name, String email, String uid) {
        super(name, email, "customer", uid);
    }

    @Override
    public String toString() {
        return "UID: " + getUID()
            + "\nName: " + getName()
            + "\nEmail: " + getEmail()
            + "\nRole: " + getRole();
    }
}

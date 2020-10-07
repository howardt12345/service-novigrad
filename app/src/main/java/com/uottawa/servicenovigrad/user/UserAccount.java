package com.uottawa.servicenovigrad.user;

public abstract class UserAccount {
    private String name;
    private String email;
    private String role;
    private String uid;

    public UserAccount() {}

    public UserAccount(String name, String email, String role, String uid) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.uid = uid;
    }

    public void setInfo(String name, String email, String role, String uid) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.uid = uid;
    }

    //Getters
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getRole() {
        return this.role;
    }
    public String getUID() { return this.uid; }
}

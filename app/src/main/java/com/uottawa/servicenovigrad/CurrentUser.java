package com.uottawa.servicenovigrad;

public class CurrentUser {
    private static String name;
    private static String email;
    private static String role;

    public static void addInfo(String n, String e, String r) {
        name = n;
        email = e;
        role = r;
    }

    public static void clearInfo() {
        name = email = role = null;
    }

    //Getters
    public static String getName() {
        return name;
    }
    public static String getEmail() {
        return email;
    }
    public static String getRole() {
        return role;
    }
}

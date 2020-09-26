package com.uottawa.servicenovigrad;

public class CurrentUser {
    private static String name;
    private static String email;
    private static String role;
    private static String uid;

    public static void addInfo(String n, String e, String r, String u) {
        name = n;
        email = e;
        role = r;
        uid = u;
    }

    public static void clearInfo() {
        name = email = role = uid = null;
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

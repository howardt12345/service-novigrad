package com.uottawa.servicenovigrad;

public class CurrentUser {
    private static String name;
    private static String email;
    private static String role;
    private static String uid;

    /**
     * Sets the info of the current user.
     * @param n The name of the new current user
     * @param e The email of the new current user
     * @param r The role of the new current user
     * @param u The uid of the new current user
     */
    public static void setInfo(String n, String e, String r, String u) {
        name = n;
        email = e;
        role = r;
        uid = u;
    }

    /**
     * Clears the info of the current user.
     */
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
    public static String getUID() { return uid; }
}

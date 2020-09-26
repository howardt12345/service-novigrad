package com.uottawa.servicenovigrad.utils;

public class Utils {

    /**
     * Checks if string is of valid email format
     *
     * @param email
     * @return boolean true if valid, false if invalid.
     */
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

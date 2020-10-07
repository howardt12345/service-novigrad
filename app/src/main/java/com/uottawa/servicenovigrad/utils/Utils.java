package com.uottawa.servicenovigrad.utils;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Utils {

    /**
     * Checks if string is of valid email format
     *
     * @param email
     * @return boolean true if valid, false if invalid.
     */
    public static boolean isEmailValid(String email) {
        return androidx.core.util.PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Shows snackbar with given message. The snackbar has a close button, which does nothing.
     * @param message the message to show.
     * @param view the view to show the snackbar on.
     */
    public static void showSnackbar(String message, View view) {
        //Create snackbar
        Snackbar snackbar = Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_SHORT);
        //Add close button that does nothing
        snackbar.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        //Shows the snackbar
        snackbar.show();
    }
}

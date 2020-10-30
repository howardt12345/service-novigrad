package com.uottawa.servicenovigrad.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

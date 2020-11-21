package com.uottawa.servicenovigrad.utils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class Utils {

    /**
     * Checks if string is of valid email format
     * @param email the email to verify
     * @return boolean true if valid, false if invalid.
     */
    public static boolean isEmailValid(String email) {
        return androidx.core.util.PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks if string is of valid name format
     * @param name the name to verify
     * @return whether the name is a valid format
     */
    public static boolean isNameValid(String name) {
        return name.matches("^[a-zA-Z\\s]*$");
    }

    public static boolean isPhoneNumberValid(String number) {
        return TextUtils.isDigitsOnly(number) && number.length() == 10;
    }

    public static String formatPhoneNumber(String number) {
        return number.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
    }

    public static String formatToUnformattedNumber(String number) {
        return number.replaceAll("[^\\d]", "");
    }

    public static String formatTime(int hour, int minute) {
        return hour + ":" + String.format("%02d", minute);
    }

    public static void selectDaysInPicker(MaterialDayPicker picker, List<String> days) {
        List<MaterialDayPicker.Weekday> selectedDays = new ArrayList<MaterialDayPicker.Weekday>();
        for(String day : days) {
            switch(day) {
                case "Monday":
                    selectedDays.add(MaterialDayPicker.Weekday.MONDAY);
                    break;
                case "Tuesday":
                    selectedDays.add(MaterialDayPicker.Weekday.TUESDAY);
                    break;
                case "Wednesday":
                    selectedDays.add(MaterialDayPicker.Weekday.WEDNESDAY);
                    break;
                case "Thursday":
                    selectedDays.add(MaterialDayPicker.Weekday.THURSDAY);
                    break;
                case "Friday":
                    selectedDays.add(MaterialDayPicker.Weekday.FRIDAY);
                    break;
                case "Saturday":
                    selectedDays.add(MaterialDayPicker.Weekday.SATURDAY);
                    break;
                case "Sunday":
                    selectedDays.add(MaterialDayPicker.Weekday.SUNDAY);
                    break;
            }
        }

        picker.setSelectedDays(selectedDays);
    }

    public static ArrayList<String> convertPickedDays(List<MaterialDayPicker.Weekday> selectedDays) {
        ArrayList<String> daysList = new ArrayList<String>();
        for(MaterialDayPicker.Weekday day : selectedDays) {
            switch(day) {
                case MONDAY:
                    daysList.add("Monday");
                    break;
                case TUESDAY:
                    daysList.add("Tuesday");
                    break;
                case WEDNESDAY:
                    daysList.add("Wednesday");
                    break;
                case THURSDAY:
                    daysList.add("Thursday");
                    break;
                case FRIDAY:
                    daysList.add("Friday");
                    break;
                case SATURDAY:
                    daysList.add("Saturday");
                    break;
                case SUNDAY:
                    daysList.add("Sunday");
                    break;
            }
        }
        return daysList;
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

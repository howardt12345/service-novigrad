package com.uottawa.servicenovigrad.activities.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.branch.BranchInfoFragment;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.utils.Function;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class SearchResultListAdapter extends ArrayAdapter<Branch> {
    ArrayList<Branch> allBranches;
    Function onSelect;
    String searchQuery;

    String day, service;
    int hour = -1, minute = -1;

    public SearchResultListAdapter(Context context, ArrayList<Branch> branches, Function onSelect) {
        super(context, 0, branches);
        allBranches = new ArrayList<>();
        this.onSelect = onSelect;
        searchQuery = "";

        day = "";
        service = "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Branch b = getItem(position);
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_branch_search_listitem, parent, false);

        TextView branchName =  convertView.findViewById(R.id.branch_name);
        TextView branchPnNo = convertView.findViewById(R.id.branch_phone_number);
        TextView branchAddr = convertView.findViewById(R.id.branch_addr);
        TextView branchWkHrs = convertView.findViewById(R.id.branch_working_hours);
        TextView branchRating = convertView.findViewById(R.id.branch_rating);

        branchName.setText(b.getName());
        branchPnNo.setText("Phone Number: " + Utils.formatPhoneNumber(b.getPhoneNumber()));
        branchAddr.setText(b.getAddress());
        branchWkHrs.setText("Working Hours: " + Utils.formatTime(b.getOpeningHour(), b.getOpeningMinute()) + " - " + Utils.formatTime(b.getClosingHour(), b.getClosingMinute()));
        branchRating.setText("Rating: " + b.getRating());

        ImageButton selectBranch = convertView.findViewById(R.id.select_branch);
        selectBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect.f(b);
            }
        });

        MaterialDayPicker openDaysPicker = convertView.findViewById(R.id.branch_days_open);
        Utils.selectDaysInPicker(openDaysPicker, b.getOpenDays());
        openDaysPicker.disableAllDays();

        return convertView;
    }

    public void notifyDataSetChanged(Branch b) {
        notifyDataSetChanged();
        if (!allBranches.contains(b)) allBranches.add(b);
    }

    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        searchQuery = query;
        clear();
        if (query.length() == 0) addAll(allBranches);
        else {
            for (Branch b : allBranches) {
                if (b.getName().toLowerCase(Locale.getDefault()).contains(query)
                    || b.getAddress().toLowerCase(Locale.getDefault()).contains(query)) {
                    add(b);
                }
            }
        }
        if(!TextUtils.isEmpty(service)) {
            filterService();
        } else {
            if(!TextUtils.isEmpty(day)) {
                filterDayOfWeek();
            }
            if(hour != -1 || minute != -1) {
                filterTime();
            }
        }
        notifyDataSetChanged();
    }

    private void filterTime() {
        CopyOnWriteArrayList<Branch> branches = new CopyOnWriteArrayList<>();
        for (int i = 0; i < getCount(); ++i) {
            branches.add(getItem(i));
        }
        for (Branch b : branches) {
            if (!b.isOpenAt(hour, minute)) {
                branches.remove(b);
            }
        }
        clear();
        addAll(branches);
        notifyDataSetChanged();
    }

    private void filterDayOfWeek() {
        CopyOnWriteArrayList<Branch> branches = new CopyOnWriteArrayList<>();
        for (int i = 0; i < getCount(); ++i) {
            branches.add(getItem(i));
        }
        for (Branch b : branches) {
            ArrayList<String> openDays = b.getOpenDays();
            if (!openDays.contains(day)) {
                branches.remove(b);
            }
        }
        clear();
        addAll(branches);
        notifyDataSetChanged();
    }

    private void filterService() {
        CopyOnWriteArrayList<Branch> branches = new CopyOnWriteArrayList<>();
        for (int i = 0; i < getCount(); ++i) {
            branches.add(getItem(i));
        }
        for (Branch b : branches) {
            if (!b.getServices().contains(service)) {
                branches.remove(b);
            }
        }
        clear();
        addAll(branches);
        notifyDataSetChanged();
    }

    public void setDayFilter(String day) {
        this.day = day;
        filter(searchQuery);
    }

    public void setTimeFilter(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        filter(searchQuery);
    }

    public void setServiceFilter(String service) {
        this.service = service;
        filter(searchQuery);
    }

    public void resetFilter() {
        day = "";
        service = "";
        hour = -1;
        minute = -1;
        filter(searchQuery);
    }
}
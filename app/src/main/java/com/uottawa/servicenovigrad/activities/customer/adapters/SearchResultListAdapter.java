package com.uottawa.servicenovigrad.activities.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.Branch;

import java.util.ArrayList;

public class SearchResultListAdapter extends ArrayAdapter<Branch> {
    public SearchResultListAdapter(Context context, ArrayList<Branch> branches) {
        super(context, 0, branches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Branch b = getItem(position);
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_branch_search_listitem, parent, false);

        TextView branchName = (TextView) convertView.findViewById(R.id.branch_name);
        TextView branchAddr = (TextView) convertView.findViewById(R.id.branch_addr);
        TextView branchRating = (TextView) convertView.findViewById(R.id.branch_rating);

        branchName.setText(b.getName());
        branchAddr.setText(b.getAddress());
        branchRating.setText("Rating: " + b.getRating());

        return convertView;
    }
}
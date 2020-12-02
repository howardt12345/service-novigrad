package com.uottawa.servicenovigrad.activities.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.utils.Function;

import java.util.ArrayList;
import java.util.Locale;

public class SearchResultListAdapter extends ArrayAdapter<Branch> {
    ArrayList<Branch> allBranches;
    Function onSelect;

    public SearchResultListAdapter(Context context, ArrayList<Branch> branches, Function onSelect) {
        super(context, 0, branches);
        allBranches = new ArrayList<>();
        this.onSelect = onSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Branch b = getItem(position);
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_branch_search_listitem, parent, false);

        TextView branchName =  convertView.findViewById(R.id.branch_name);
        TextView branchAddr = convertView.findViewById(R.id.branch_addr);
        TextView branchRating = convertView.findViewById(R.id.branch_rating);

        branchName.setText(b.getName());
        branchAddr.setText(b.getAddress());
        branchRating.setText("Rating: " + b.getRating());

        ImageButton selectBranch = convertView.findViewById(R.id.select_branch);
        selectBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect.f(b);
            }
        });

        return convertView;
    }

    public void notifyDataSetChanged(Branch b) {
        notifyDataSetChanged();
        if (!allBranches.contains(b)) allBranches.add(b);
    }

    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
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
        notifyDataSetChanged();
    }
}
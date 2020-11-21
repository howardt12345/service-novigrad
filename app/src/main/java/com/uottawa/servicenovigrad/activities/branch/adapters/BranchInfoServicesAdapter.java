package com.uottawa.servicenovigrad.activities.branch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.service.Service;

import java.util.List;

public class BranchInfoServicesAdapter extends ArrayAdapter<Service> {
    private LayoutInflater inflater;
    List<Service> services;

    public BranchInfoServicesAdapter(@NonNull Context context, @NonNull List<Service> services, @NonNull LayoutInflater inflater) {
        super(context, R.layout.layout_admin_services_listitem, services);
        this.inflater = inflater;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = inflater.inflate(R.layout.layout_admin_services_listitem, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.service_name);
        TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.service_desc);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.service_price);

        Service service = services.get(position);

        textViewName.setText(service.getName());
        textViewDesc.setText(service.getDesc());
        textViewPrice.setText("Price: $" + service.getPrice());

        //Hide edit button functions
        ImageButton editButton = (ImageButton) listViewItem.findViewById(R.id.edit_service);
        editButton.setVisibility(View.GONE);

        return listViewItem;
    }
}

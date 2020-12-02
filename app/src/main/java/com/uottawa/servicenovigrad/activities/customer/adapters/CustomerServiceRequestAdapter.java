package com.uottawa.servicenovigrad.activities.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.ServiceRequest;

import java.util.List;

public class CustomerServiceRequestAdapter extends ArrayAdapter<ServiceRequest> {
    private LayoutInflater inflater;
    List<ServiceRequest> requests;

    public CustomerServiceRequestAdapter(@NonNull Context context, @NonNull List<ServiceRequest> requests, @NonNull LayoutInflater inflater) {
        super(context, R.layout.layout_service_request_listitem, requests);
        this.inflater = inflater;
        this.requests = requests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = inflater.inflate(R.layout.layout_service_request_listitem, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.request_title);
        TextView textViewDesc = listViewItem.findViewById(R.id.request_desc);

        ServiceRequest request = requests.get(position);

        textViewName.setText(request.getCustomerSideTitle());
        textViewDesc.setText(request.getCustomerSideDesc());

        return listViewItem;
    }
}

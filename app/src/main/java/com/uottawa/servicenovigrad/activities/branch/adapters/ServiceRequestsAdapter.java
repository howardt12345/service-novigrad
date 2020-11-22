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
import com.uottawa.servicenovigrad.branch.ServiceRequest;

import java.util.List;

public class ServiceRequestsAdapter extends ArrayAdapter<ServiceRequest> {
    private LayoutInflater inflater;
    List<ServiceRequest> requests;

    public ServiceRequestsAdapter(@NonNull Context context, @NonNull List<ServiceRequest> requests, @NonNull LayoutInflater inflater) {
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

        textViewName.setText(request.getTitle());
        textViewDesc.setText(request.getDesc());

        return listViewItem;
    }
}

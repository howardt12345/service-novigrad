package com.uottawa.servicenovigrad.activities.admin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.utils.Function;

import java.util.List;

public class AdminServicesEditListAdapter extends ArrayAdapter<String> {
    private Activity context;
    List<String> objects;
    Function edit, delete;

    public AdminServicesEditListAdapter(@NonNull Activity context, @NonNull List<String> objects, Function edit, Function delete) {
        super(context, R.layout.layout_admin_services_listitem, objects);
        this.context = context;
        this.objects = objects;
        this.edit = edit;
        this.delete = delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_admin_services_edit_listitem, null, true);

        TextView name = (TextView) listViewItem.findViewById(R.id.service_edit_listitem);
        name.setText(objects.get(position));

        ImageButton editButton = (ImageButton) listViewItem.findViewById(R.id.edit_service_listitem);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.f(position);
            }
        });

        ImageButton deleteButton = (ImageButton) listViewItem.findViewById(R.id.delete_service_listitem);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.f(position);
            }
        });

        return listViewItem;
    }
}

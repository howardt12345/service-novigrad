package com.uottawa.servicenovigrad.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import org.w3c.dom.Text;

public class AdminServicesEdit extends AppCompatActivity {

    Service service;
    ArrayAdapter<String> formsAdapter, documentsAdapter;

    ListView formsList, documentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_services_edit);
        getSupportActionBar().hide();

        if(getIntent().getExtras() != null) {
            service = (Service) getIntent().getSerializableExtra("service");
        }

        TextView title = (TextView) findViewById(R.id.services_edit_title);
        if(service == null) {
            title.setText("Add Service");
            service = new Service();
        }

        formsList = (ListView) findViewById(R.id.services_edit_formsList);
        documentsList = (ListView) findViewById(R.id.services_edit_documentsList);

        formsAdapter = new ArrayAdapter<String>(this, R.layout.layout_admin_services_edit_listitem, R.id.service_edit_listitem, service.getForms());
        documentsAdapter = new ArrayAdapter<String>(this, R.layout.layout_admin_services_edit_listitem, R.id.service_edit_listitem, service.getDocuments());

        formsList.setAdapter(formsAdapter);
        documentsList.setAdapter(documentsAdapter);

        Utils.setListViewHeightBasedOnChildren(formsList);
        Utils.setListViewHeightBasedOnChildren(documentsList);

        EditText name = (EditText) findViewById(R.id.services_edit_editName);
        EditText desc = (EditText) findViewById(R.id.services_edit_editDesc);

        name.setText(service.getName());
        desc.setText(service.getDesc());

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                service.setName(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                service.setDesc(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        cancelEdit(this.getCurrentFocus());
    }

    public void cancelEdit(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminServicesEdit.this);
        alertDialogBuilder
        .setTitle("Discard Changes?")
        .setMessage("Are you sure you want to discard your changes?")
        .setCancelable(true)
        .setPositiveButton(
            "YES",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                }
            }
        )
        .setNegativeButton(
            "NO",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }
        );
        //Show AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void confirmEdit(View view) {
        Intent intent = new Intent();
        intent.putExtra("service", service);
        setResult(0, intent);
        finish();
    }
}
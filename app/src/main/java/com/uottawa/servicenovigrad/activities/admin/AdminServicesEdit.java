package com.uottawa.servicenovigrad.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Function;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.List;

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

        formsAdapter = new AdminServicesEditListAdapter(this, service.getForms(), editFromList(service.getForms()), deleteFromList(service.getForms()));

        documentsAdapter = new AdminServicesEditListAdapter(this, service.getDocuments(), editFromList(service.getDocuments()), deleteFromList(service.getDocuments()));

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

    public void addToForms(View view) {
        addToList(service.getForms());
    }

    public void addToDocuments(View view) {
        addToList(service.getDocuments());
    }

    private void addToList(final List<String> list) {
        View v = LayoutInflater.from(this).inflate(R.layout.layout_admin_services_edit_dialog, null);
        final EditText input = (EditText) v.findViewById(R.id.service_edit_dialog_text);

        AlertDialog dialog = new AlertDialog.Builder(this)
        .setMessage("Add Field")
        .setView(v)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String editTextInput = input.getText().toString();
                list.add(editTextInput);
                updateLists();
            }
        })
        .setNegativeButton("Cancel", null)
        .create();
        dialog.show();
    }

    private Function editFromList(final List<String> list) {
        return new Function() {
            @Override
            public void f(final Object... params) {
                View v = LayoutInflater.from(AdminServicesEdit.this).inflate(R.layout.layout_admin_services_edit_dialog, null);
                final EditText input = (EditText) v.findViewById(R.id.service_edit_dialog_text);
                input.setText(list.get((int) params[0]));

                AlertDialog dialog = new AlertDialog.Builder(AdminServicesEdit.this)
                .setMessage("Edit Field")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String editTextInput = input.getText().toString();
                        list.set((int) params[0], editTextInput);
                        updateLists();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
                dialog.show();
            }
        };
    }

    private Function deleteFromList(final List<String> list) {
        return new Function() {
            @Override
            public void f(Object... params) {
                list.remove((int) params[0]);
                updateLists();
            }
        };
    }

    private void updateLists() {
        formsAdapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(formsList);

        documentsAdapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(documentsList);
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
        saveEdit();
    }

    private void saveEdit() {
        Intent intent = new Intent();
        intent.putExtra("service", service);
        setResult(0, intent);
        finish();
    }
}
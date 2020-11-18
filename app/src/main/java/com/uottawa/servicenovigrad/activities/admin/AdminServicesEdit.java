package com.uottawa.servicenovigrad.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.adapters.AdminServicesEditListAdapter;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Function;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.List;

public class AdminServicesEdit extends AppCompatActivity {

    Service service;
    ArrayAdapter<String> formsAdapter, documentsAdapter;

    ListView formsList, documentsList;
    EditText name, desc, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_services_edit);
        getSupportActionBar().hide();

        //If there is data passed through to this activity
        if(getIntent().getExtras() != null) {
            //Get the service from the data
            service = (Service) getIntent().getSerializableExtra("service");
        }

        //Get the title
        TextView title = (TextView) findViewById(R.id.services_edit_title);
        //If there was no data passed through to this activity
        if(service == null) {
            //Change title to reflect current function
            title.setText("Add Service");
            //Create new service
            service = new Service();
        }

        //Initialize the fields in this activity
        initializeFields();
    }

    /**
     * Initializes all the fields in this activity.
     */
    private void initializeFields() {
        //Get the listviews from the id
        formsList = (ListView) findViewById(R.id.services_edit_formsList);
        documentsList = (ListView) findViewById(R.id.services_edit_documentsList);
        //Create new adapters for the listviews
        formsAdapter = new AdminServicesEditListAdapter(
            this,
            service.getForms(),
            editFromList(service.getForms(), "Information"),
            deleteFromList(service.getForms())
        );
        documentsAdapter = new AdminServicesEditListAdapter(
            this,
            service.getDocuments(),
            editFromList(service.getDocuments(), "Document"),
            deleteFromList(service.getDocuments())
        );
        //Set the adapter to the respective listviews
        formsList.setAdapter(formsAdapter);
        documentsList.setAdapter(documentsAdapter);
        //Change the height of the listview to be the size of the children
        //This is so that the listviews can fit in the linearlayout
        Utils.setListViewHeightBasedOnChildren(formsList);
        Utils.setListViewHeightBasedOnChildren(documentsList);

        //Get the name and description fields
        name = (EditText) findViewById(R.id.services_edit_editName);
        desc = (EditText) findViewById(R.id.services_edit_editDesc);
        price = (EditText) findViewById(R.id.services_edit_editPrice);
        //Set the text to the service's name and description
        name.setText(service.getName());
        desc.setText(service.getDesc());
        price.setText(service.getPrice() + "");

        //Add text change listener to the name field
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set the name of the service to the name field content
                service.setName(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Add text change listener to the description field
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set the description of the service to the description field content
                service.setDesc(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //TODO: Add price parameter initialization
        //Add text change listener to the name field
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set the price of the service to the price field content
                if(s.toString().trim().isEmpty()) {
                    service.setPrice(0);
                } else {
                    try {
                        service.setPrice(Integer.parseInt(s.toString().trim()));
                    } catch (Exception e) {
                        service.setPrice(0);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Opens the dialog to add a field to the forms list
     * @param view
     */
    public void addToForms(View view) {
        addToList(service.getForms(), "Information", view);
    }

    /**
     * Opens the dialog to add a field to the documents list
     * @param view
     */
    public void addToDocuments(View view) {
        addToList(service.getDocuments(), "Document", view);
    }

    /**
     * Opens a dialog to add a string to the given list of strings
     * @param list The list to add a string to.
     * @param name the name of the list.
     */
    private void addToList(final List<String> list, String name, final View view) {
        View v = LayoutInflater.from(this).inflate(R.layout.layout_admin_services_edit_dialog, null);
        final EditText input = (EditText) v.findViewById(R.id.service_edit_dialog_text);

        AlertDialog dialog = new AlertDialog.Builder(this)
        .setMessage("Add " + name + " Field")
        .setView(v)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String editTextInput = input.getText().toString().trim();
                //If the text is not empty
                if(!editTextInput.isEmpty()) {
                    //Add the text to the list
                    list.add(editTextInput);
                    //Update the list components
                    updateLists();
                } else {
                    Utils.showSnackbar("Field cannot be empty.", view);
                }
            }
        })
        .setNegativeButton("Cancel", null)
        .create();
        dialog.show();
    }

    /**
     * Returns a function that opens a dialog to edit a string from the given list of strings
     * @param list The list to edit a string in.
     * @param name the name of the list.
     * @return the function to open the dialog.
     */
    private Function editFromList(final List<String> list, final String name) {
        return new Function() {
            @Override
            public void f(final Object... params) {
                //Gets the index from the parameters
                final int index = (int) params[0];
                //Sets up the view to put in the dialog
                final View v = LayoutInflater.from(AdminServicesEdit.this).inflate(R.layout.layout_admin_services_edit_dialog, null);
                final EditText input = (EditText) v.findViewById(R.id.service_edit_dialog_text);
                //Set the text of the input to the string at the index
                input.setText(list.get((index)));
                //Set up the dialog
                AlertDialog dialog = new AlertDialog.Builder(AdminServicesEdit.this)
                .setMessage("Edit " + name + " Field")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get the string from the input
                        String editTextInput = input.getText().toString().trim();
                        //If the text is not empty
                        if(!editTextInput.isEmpty()) {
                            //Set the string at the index
                            list.set(index, editTextInput);
                            //Update the list components
                            updateLists();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
                dialog.show();
            }
        };
    }

    /**
     * Returns a function to delete a string from a list of strings
     * @param list The list to delete a string from.
     * @return the function to delete the string.
     */
    private Function deleteFromList(final List<String> list) {
        return new Function() {
            @Override
            public void f(Object... params) {
                //Delete the item from the list at the index
                list.remove((int) params[0]);
                //Update the list components
                updateLists();
            }
        };
    }

    /**
     * Updates both the forms list and the documents list
     */
    private void updateLists() {
        //Notify the forms adapters that data has changed
        formsAdapter.notifyDataSetChanged();
        documentsAdapter.notifyDataSetChanged();

        //Change the height of the listview to be the size of the children
        //This is so that the listviews can fit in the linearlayout
        Utils.setListViewHeightBasedOnChildren(formsList);
        Utils.setListViewHeightBasedOnChildren(documentsList);
    }

    @Override
    public void onBackPressed() {
        //Cancel the edit when back button is pressed
        cancelEdit(this.getCurrentFocus());
    }

    /**
     * Display a dialog to confirm if user wants to cancel the edit
     * @param view the current view.
     */
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
                    //Close the activity with result code not ok
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
                    //Close the dialog without closing the activity
                    dialog.cancel();
                }
            }
        );
        //Show AlertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Applies form validation, and saves the edit if the forms are good
     * @param view the current view
     */
    public void confirmEdit(View view) {
        //Trims all the fields within the service.
        trimService(service);
        //If the service contents are valid
        boolean valid = validateEdit(service, view);

        if(valid) {
            saveEdit();
        }
    }

    /**
     * Trims all the fields within the service.
     * @param service the service to trim the fields in
     */
    private void trimService(Service service) {
        service.setName(service.getName().trim());
        service.setDesc(service.getDesc().trim());

        for(int i = 0; i < service.getForms().size(); i++) {
            service.getForms().set(i, service.getForms().get(i).trim());
        }
        for(int i = 0; i < service.getDocuments().size(); i++) {
            service.getDocuments().set(i, service.getDocuments().get(i).trim());
        }
    }

    /**
     * Validates the edit done to the service.
     * @param service the service to validate.
     * @param view the current view
     */
    private boolean validateEdit(Service service, View view) {
        //Verify if any fields are empty
        if(TextUtils.isEmpty(service.getName()) || TextUtils.isEmpty(service.getDesc())
                || service.getForms().isEmpty() || service.getDocuments().isEmpty()) {
            Utils.showSnackbar("One or more required fields are empty.", view);
            return false;
        }
        if(service.getPrice() <= 0) {
            Utils.showSnackbar("Price must be a positive number.", view);
            return false;
        }
        return true;
    }

    /**
     * Saves the edit.
     */
    private void saveEdit() {
        Intent intent = new Intent();
        //Add the service to the intent data
        intent.putExtra("service", service);
        setResult(0, intent);
        finish();
    }
}
package com.uottawa.servicenovigrad.activities.employee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesEdit;
import com.uottawa.servicenovigrad.activities.branch.adapters.BranchInfoServicesAdapter;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class EmployeeEditActivity extends AppCompatActivity {

    private CollectionReference servicesReference = FirebaseFirestore.getInstance().collection("services");

    Branch branch;
    private boolean newBranch = false;

    List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);
        getSupportActionBar().hide();

        //If there is data passed through to this activity
        if(getIntent().getExtras() != null) {
            //Get the service from the data
            branch = (Branch) getIntent().getSerializableExtra("branch");
        } else {
            //Change title to reflect current function
            TextView title = (TextView) findViewById(R.id.employee_edit_title);
            title.setText("Complete Branch Profile");
            title.setTextSize(24.0f);

            //Hide cancel button
            ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button_employee);
            cancelButton.setVisibility(View.GONE);

            newBranch = true;
            branch = new Branch();
        }
        initializeFields();
    }

    private void initializeFields() {
        EditText nameField = (EditText) findViewById(R.id.branch_edit_name);
        EditText phoneNumberField = (EditText) findViewById(R.id.branch_edit_phone_number);

        nameField.setText(branch.getName());
        phoneNumberField.setText(branch.getPhoneNumber());

        Button addressButton = (Button) findViewById(R.id.branch_edit_address_button);
        Button openingTimeButton = (Button) findViewById(R.id.branch_edit_opening_time);
        Button closingTimeButton = (Button) findViewById(R.id.branch_edit_closing_time);

        addressButton.setText(branch.getAddress());
        openingTimeButton.setText(branch.getOpeningHour() + ":" + branch.getOpeningMinute());
        closingTimeButton.setText(branch.getClosingHour() + ":" + branch.getClosingMinute());

        MaterialDayPicker openDaysPicker = (MaterialDayPicker) findViewById(R.id.branch_edit_days_open_picker);
        Utils.selectDaysInPicker(openDaysPicker, branch.getOpenDays());

        services = new ArrayList<>();

        final LinearLayout servicesList = (LinearLayout) findViewById(R.id.branch_edit_services_list);
        //Add listener to service reference
        servicesReference.orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ServiceActivity", "Listen failed.", error);
                    return;
                }
                //Clear the list and components
                services.clear();
                servicesList.removeAllViews();
                //Iterate through each document in collection
                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        //Get the information of the service
                        String id = doc.getId();
                        if(branch.getServices().contains(id)) {
                            String name = doc.getString("name");
                            String desc = doc.getString("desc");
                            List<String> forms = (List<String>) doc.get("forms");
                            List<String> documents = (List<String>) doc.get("documents");
                            int price = doc.getLong("price").intValue();

                            //Create a new service object
                            Service service = new Service(id, name, desc, forms, documents, price);
                            //Add service to list
                            services.add(service);
                        }
                    }
                }
                //Set up the services list
                setUpServicesList(services, servicesList, EmployeeEditActivity.this, EmployeeEditActivity.this.getLayoutInflater());
            }
        });
    }

    /**
     * Sets up the list in the UI
     * @param services the list of services to set up the list with
     * @param listView the LinearLayout in which the list items will go in
     */
    private void setUpServicesList(final List<Service> services, LinearLayout listView, Context context, LayoutInflater inflater) {
        //Create a list adapter
        BranchInfoServicesAdapter adapter = new BranchInfoServicesAdapter(context, services, inflater);
        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serviceInfo(services.get(finalI));
                }
            });

            //Set delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_service);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void serviceInfo(Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
                .setTitle(service.getName()) //Set the title of the dialog to the service name
                .setMessage(service.getInfo()) //Set the message of the dialog to the service info
                .setCancelable(true)
                .setPositiveButton(
                        "CLOSE",
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

    @Override
    public void onBackPressed() {
        if(newBranch) {
            //Stop the user from going back if the user is creating a new branch
            preventExitDialog();
        } else {
            //Prompt the user to cancel edit when back button is pressed
            cancelEditPrompt(getCurrentFocus());
        }
    }

    private void preventExitDialog() {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
        .setTitle("Cannot cancel edit.")
        .setMessage("You MUST complete your branch profile in order to proceed.")
        .setCancelable(true)
        .setPositiveButton(
                "OK",
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

    /**
     * Display a dialog to confirm if user wants to cancel the edit
     * @param view the current view.
     */
    public void cancelEditPrompt(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
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

    public void confirmEdit(View view) {

    }
}
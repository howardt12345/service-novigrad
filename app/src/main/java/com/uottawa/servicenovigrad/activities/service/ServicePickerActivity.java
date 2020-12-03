package com.uottawa.servicenovigrad.activities.service;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesActivity;
import com.uottawa.servicenovigrad.activities.admin.adapters.AdminServicesListAdapter;
import com.uottawa.servicenovigrad.activities.branch.adapters.BranchInfoServicesAdapter;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicePickerActivity extends AppCompatActivity {

    private CollectionReference servicesReference = FirebaseFirestore.getInstance().collection("services");

    List<Service> services;
    LinearLayout servicesList;

    Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_picker);
        getSupportActionBar().hide();

        //Set up services list
        services = new ArrayList<>();
        servicesList = findViewById(R.id.service_picker_list);

        if(getIntent().getExtras() != null) {
            branch = (Branch) getIntent().getSerializableExtra("branch");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Add listener to service reference
        servicesReference.orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ServicePickerActivity", "Listen failed.", error);
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
                        String name = doc.getString("name");
                        String desc = doc.getString("desc");
                        List<String> forms = (List<String>) doc.get("forms");
                        List<String> documents = (List<String>) doc.get("documents");
                        int price = doc.getLong("price").intValue();
                        //Create a new service object
                        Service service = new Service(id, name, desc, forms, documents, price);
                        //Add service to list
                        if(branch != null) {
                            if(branch.getServices().contains(id)) {
                                services.add(service);
                            }
                        } else {
                            services.add(service);
                        }
                    }
                }
                //Set up the services list
                setUpList(services, servicesList);
            }
        });
    }

    /**
     * Sets up the list in the UI
     * @param services the list of services to set up the list with
     * @param listView the LinearLayout in which the list items will go in
     */
    private void setUpList(final List<Service> services, LinearLayout listView) {
        //Create a list adapter
        BranchInfoServicesAdapter adapter = new BranchInfoServicesAdapter(ServicePickerActivity.this, services, getLayoutInflater());
        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickService(services.get(finalI));
                }
            });

            //Hide delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_service);
            deleteButton.setVisibility(View.GONE);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    public void exit(View view) {
        finish();
    }

    private void pickService(final Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServicePickerActivity.this);
        alertDialogBuilder
        .setTitle(service.getName()) //Set the title of the dialog to the service name
        .setMessage(service.getInfo()) //Set the message of the dialog to the service info
        .setCancelable(true)
        .setPositiveButton(
                "SELECT",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnService(service);
                    }
                }
        )
        .setNegativeButton(
            "CANCEL",
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

    private void returnService(Service service) {
        Intent intent = new Intent();
        intent.putExtra("service", service);
        setResult(RESULT_OK, intent);
        finish();
    }
}
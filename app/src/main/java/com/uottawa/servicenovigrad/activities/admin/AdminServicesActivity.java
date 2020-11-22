package com.uottawa.servicenovigrad.activities.admin;

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
import com.uottawa.servicenovigrad.activities.admin.adapters.AdminServicesListAdapter;
import com.uottawa.servicenovigrad.service.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminServicesActivity extends AppCompatActivity {

    private int ADD_SERVICE = 0, EDIT_SERVICE = 1;

    List<Service> services;
    LinearLayout servicesList;

    private FirebaseFirestore firestore;
    private CollectionReference servicesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_services);
        getSupportActionBar().hide();

        //Set up firestore
        firestore = FirebaseFirestore.getInstance();
        servicesReference = firestore.collection("services");

        //Set up services list
        services = new ArrayList<>();
        servicesList = (LinearLayout) findViewById(R.id.services_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        AdminServicesListAdapter adapter = new AdminServicesListAdapter(AdminServicesActivity.this, services);
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
            //Set edit button functions
            ImageButton editButton = (ImageButton) view.findViewById(R.id.edit_service);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editService(services.get(finalI));
                }
            });
            //Set delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_service);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteServiceDialog(services.get(finalI));
                }
            });
            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void serviceInfo(Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminServicesActivity.this);
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

    /**
     * Opens the Edit Services page to add a service.
     * @param view the current view
     */
    public void addService(View view) {
        //Set up intent
        Intent intent = new Intent(AdminServicesActivity.this, AdminServicesEdit.class);
        startActivityForResult(intent, ADD_SERVICE);
    }

    /**
     * Edits the selected service.
     * @param service the service to edit
     */
    private void editService(Service service) {
        //Set up intent
        Intent intent = new Intent(AdminServicesActivity.this, AdminServicesEdit.class);
        //Add service to intent data to be edited
        intent.putExtra("service", service);
        startActivityForResult(intent, EDIT_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If result is ok
        if(resultCode == RESULT_OK) {
            //Get the service
            Service service = (Service) data.getSerializableExtra("service");
            //Create a map with the data to write to cloud firestore
            Map<String, Object> serviceInfo = new HashMap<>();
            serviceInfo.put("name", service.getName());
            serviceInfo.put("desc", service.getDesc());
            serviceInfo.put("price", service.getPrice());
            serviceInfo.put("forms", service.getForms());
            serviceInfo.put("documents", service.getDocuments());

            //If the request was to add a service
            if(requestCode == ADD_SERVICE) {
                //Add the service to the database
                servicesReference.add(serviceInfo);
            } else if (requestCode == EDIT_SERVICE) { //If the request was to edit a service
                //Get the service id, then override the data in the database
                servicesReference.document(service.getId()).set(serviceInfo);
            } else {
                //Do nothing
            }
        }
    }

    /**
     * Opens a dialog to confirm if user wants to delete given service, and deletes the service when confirmed.
     * @param service The service to delete.
     */
    private void deleteServiceDialog(final Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminServicesActivity.this);
        alertDialogBuilder
        .setTitle("Delete " + service.getName() + "?")
        .setMessage("Are you sure you want to delete this service? This service will be permanently deleted.")
        .setCancelable(true)
        .setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete the service
                        deleteService(service);
                        dialog.cancel();
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

    private void deleteService(Service service) {
        //Delete the service from the database
        servicesReference.document(service.getId()).delete();
    }

    public void back(View view) {
        //Close this activity
        this.finish();
    }
}
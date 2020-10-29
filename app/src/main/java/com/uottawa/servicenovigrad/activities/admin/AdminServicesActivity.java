package com.uottawa.servicenovigrad.activities.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.user.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AdminServicesActivity extends AppCompatActivity {

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

        services = new ArrayList<>();
        servicesList = (LinearLayout) findViewById(R.id.services_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        servicesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ServiceActivity", "Listen failed.", error);
                    return;
                }
                services.clear();
                servicesList.removeAllViews();

                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String desc = doc.getString("desc");
                        List<String> forms = (List<String>) doc.get("forms");
                        List<String> documents = (List<String>) doc.get("documents");

                        Service service = new Service(id, name, desc, forms, documents);

                        services.add(service);
                    }
                }
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
            //Set delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_service);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteService(services.get(finalI));
                }
            });
            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void serviceInfo(Service service) {

    }

    private void deleteService(Service service) {

    }

    public void back(View view) {
        this.finish();
    }
}
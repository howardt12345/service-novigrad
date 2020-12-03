package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.auth.LoginActivity;
import com.uottawa.servicenovigrad.activities.branch.adapters.ServiceRequestsAdapter;
import com.uottawa.servicenovigrad.activities.customer.adapters.CustomerServiceRequestAdapter;
import com.uottawa.servicenovigrad.activities.employee.EmployeeMainActivity;
import com.uottawa.servicenovigrad.branch.ServiceRequest;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMainActivity extends AppCompatActivity {

    private int NEW_REQUEST = 0;

    private CollectionReference requestsReference = FirebaseFirestore.getInstance().collection("requests");

    List<ServiceRequest> serviceRequests;
    LinearLayout requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        getSupportActionBar().hide();
        serviceRequests = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        requestsList = findViewById(R.id.customer_service_requests);

        requestsReference
                .whereEqualTo("customer", UserController.getInstance().getUserAccount().getUID())
                .whereGreaterThan("scheduledTime", new Date())
                .orderBy("scheduledTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("CustomerMainActivity", "Listen failed.", error);
                            return;
                        }

                        serviceRequests.clear();

                        for(QueryDocumentSnapshot doc : value) {
                            if(doc.exists()) {
                                //Get the information of the service
                                String id = doc.getId();
                                String customerId = doc.getString("customer");
                                String branchId = doc.getString("branch");
                                String serviceId = doc.getString("service");

                                String customerName = doc.getString("customerName");
                                String branchName = doc.getString("branchName");
                                String serviceName = doc.getString("serviceName");

                                ArrayList<String> info = (ArrayList<String>) doc.get("info");

                                Timestamp scheduledTime = doc.getTimestamp("scheduledTime");

                                boolean approved = doc.getBoolean("approved");
                                boolean responded = doc.getBoolean("responded");

                                ServiceRequest request = new ServiceRequest(id, customerId, branchId, serviceId, customerName, branchName, serviceName, info, scheduledTime.toDate(), approved, responded);

                                if (customerName != null && branchName != null && serviceName != null)
                                    serviceRequests.add(request);
                            }
                        }

                        setUpRequestsList(serviceRequests, requestsList, getApplicationContext(), getLayoutInflater());
                    }
                });
    }

    private void setUpRequestsList(final List<ServiceRequest> requests, LinearLayout listView, final Context context, LayoutInflater inflater) {

        requestsList.removeAllViews();

        CustomerServiceRequestAdapter adapter = new CustomerServiceRequestAdapter(context, requests, inflater);

        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestInfo(requests.get(finalI));
                }
            });

            ImageButton approveButton = view.findViewById(R.id.approve_request);
            if(!(requests.get(finalI).isApproved() && requests.get(finalI).isResponded())) {
                approveButton.setVisibility(View.GONE);
            }
            ImageButton rejectButton = view.findViewById(R.id.reject_request);
            if(!(!requests.get(finalI).isApproved() && requests.get(finalI).isResponded())) {
                rejectButton.setVisibility(View.GONE);
            }

            ImageButton undoButton = view.findViewById(R.id.undo_reject_request);
            undoButton.setVisibility(View.GONE);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void requestInfo(ServiceRequest request) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerMainActivity.this);
        alertDialogBuilder
                .setTitle(request.getCustomerSideTitle()) //Set the title of the dialog to the service name
                .setMessage(request.getRequestInfo()) //Set the message of the dialog to the service info
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == NEW_REQUEST) {
                ServiceRequest request = (ServiceRequest) data.getSerializableExtra("request");
                Map<String, Object> requestInfo = new HashMap<>();

                requestInfo.put("branch", request.getBranchId());
                requestInfo.put("customer", UserController.getInstance().getUserAccount().getUID());
                requestInfo.put("service", request.getServiceId());
                requestInfo.put("scheduledTime", request.getScheduledTime());
                requestInfo.put("info", request.getInfo());
                requestInfo.put("approved", false);
                requestInfo.put("responded", false);

                requestsReference.add(requestInfo);
            }
        }
    }

    public void newRequest(View view) {
        Intent intent = new Intent(CustomerMainActivity.this, CustomerNewRequestActivity.class);
        startActivityForResult(intent, NEW_REQUEST);
    }

    /**
     * Sign out of the app.
     * @param view The current view.
     */
    public void signOut(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerMainActivity.this);
        alertDialogBuilder
                .setTitle("Log out?")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(true)
                .setPositiveButton(
                        "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Sign out of Firebase
                                UserController.getInstance().signOut();
                                //Navigate back to Login Page
                                Intent intent = new Intent(CustomerMainActivity.this, LoginActivity.class);
                                //set the new task and clear flags, so that the user can't go back here
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
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
}
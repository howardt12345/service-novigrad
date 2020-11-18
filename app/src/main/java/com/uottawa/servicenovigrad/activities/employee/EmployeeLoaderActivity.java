package com.uottawa.servicenovigrad.activities.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesActivity;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesEdit;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;

import java.util.ArrayList;
import java.util.List;

public class EmployeeLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_loader);
        getSupportActionBar().hide();

        //Get the branch associated with the employee
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference branchesReference = firestore.collection("branches");
        final CollectionReference servicesReference = firestore.collection("services");

        UserAccount account = UserController.getInstance().getUserAccount();

        branchesReference.document(account.getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    //Get all the fields of the branch from the databse
                    String id = documentSnapshot.getId();
                    String name = documentSnapshot.getString("name");
                    String address = documentSnapshot.getString("address");
                    String phoneNumber = documentSnapshot.getString("phoneNumber");
                    ArrayList<String> servicesIds = (ArrayList<String>) documentSnapshot.get("services");

                    ArrayList<String> openDays = (ArrayList<String>) documentSnapshot.get("openDays");
                    int openingHour = documentSnapshot.getLong("openingHour").intValue();
                    int openingMinute = documentSnapshot.getLong("openingMinute").intValue();
                    int closingHour = documentSnapshot.getLong("closingHour").intValue();
                    int closingMinute = documentSnapshot.getLong("closingMinute").intValue();
                    double rating = documentSnapshot.getDouble("rating");

                    //Initialize the branch object.
                    Branch branch = new Branch(
                        id,
                        name,
                        address,
                        phoneNumber,
                        servicesIds,
                        openDays,
                        openingHour,
                        openingMinute,
                        closingHour,
                        closingMinute,
                        rating
                    );
                    //Set up intent
                    Intent intent = new Intent(EmployeeLoaderActivity.this, EmployeeMainActivity.class);
                    //Add branch to intent data to be edited
                    intent.putExtra("branch", branch);
                    //set the new task and clear flags, so that the user can't go back to this screen
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    //Set up intent
                    Intent intent = new Intent(EmployeeLoaderActivity.this, EmployeeMainActivity.class);
                    //set the new task and clear flags, so that the user can't go back to this screen
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
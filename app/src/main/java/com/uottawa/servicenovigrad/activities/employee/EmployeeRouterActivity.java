package com.uottawa.servicenovigrad.activities.employee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.auth.LoginActivity;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;

public class EmployeeRouterActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private CollectionReference branchesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_router);
        getSupportActionBar().hide();

        //Set up firestore
        firestore = FirebaseFirestore.getInstance();
        branchesReference = firestore.collection("branches");

        UserAccount account = UserController.getInstance().getUserAccount();
    }

    /**
     * Sign out of the app.
     * @param view The current view.
     */
    public void signOut(View view) {
        //Sign out of Firebase
        UserController.getInstance().signOut();
        //Navigate back to Login Page
        Intent intent = new Intent(EmployeeRouterActivity.this, LoginActivity.class);
        //set the new task and clear flags, so that the user can't go back here
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
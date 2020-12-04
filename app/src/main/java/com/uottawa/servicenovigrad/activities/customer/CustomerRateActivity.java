package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.Branch;

import java.util.ArrayList;

public class CustomerRateActivity extends AppCompatActivity {

    private int GET_BRANCH = 0;
    Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_rate);
        getSupportActionBar().hide();
    }

    public void selectBranch(View view) {
        Intent intent = new Intent(CustomerRateActivity.this, CustomerSearchActivity.class);
        startActivityForResult(intent, GET_BRANCH);
    }
}
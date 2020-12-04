package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.user.UserController;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerRateActivity extends AppCompatActivity {

    private int GET_BRANCH = 0;
    Branch branch;

    Button branchButton;
    RatingBar ratingBar;
    ProgressBar ratingProgress;
    EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_rate);
        getSupportActionBar().hide();

        branchButton = findViewById(R.id.rate_select_branch_section);
        ratingBar = findViewById(R.id.branch_rating_bar);
        comments = findViewById(R.id.branch_rating_comments);
        ratingProgress = findViewById(R.id.rating_progressbar);

        Intent intent = new Intent(CustomerRateActivity.this, CustomerSearchActivity.class);
        startActivityForResult(intent, GET_BRANCH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == GET_BRANCH) {
                branch = (Branch) data.getSerializableExtra("branch");
                branchButton.setText(branch.getName());
            }
        }
    }

    public void exit(View view) {
        finish();
    }

    public void submit(View view) {
        boolean valid = verify();
        if(valid) {
            submitRating();
        }
    }

    private boolean verify() {
        if(branch == null || ratingBar.getRating() <= 0 || comments.getText().toString().isEmpty()) {
            Utils.showSnackbar("One or more of the required fields are empty.", findViewById(R.id.rate_branch_view));
            return false;
        }
        return true;
    }

    private void submitRating() {
        Toast.makeText(getApplicationContext(), "Submitting review...", Toast.LENGTH_SHORT).show();
        ratingProgress.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
        .collection("ratings").document(branch.getId())
        .set(new HashMap<String, Object>() {{
            put(UserController.getInstance().getUserAccount().getUID(), ratingBar.getRating());
        }}, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Review has been submitted.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Review failed to submit.", Toast.LENGTH_SHORT).show();
                    task.getException().printStackTrace();
                }
                ratingProgress.setVisibility(View.GONE);
            }
        });
    }

    public void selectBranch(View view) {
        Intent intent = new Intent(CustomerRateActivity.this, CustomerSearchActivity.class);
        startActivityForResult(intent, GET_BRANCH);
    }
}
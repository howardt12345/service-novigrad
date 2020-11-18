package com.uottawa.servicenovigrad.activities.employee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.auth.LoginActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.Locale;

public class EmployeeMainActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private CollectionReference branchesReference;

    private Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main);
        getSupportActionBar().hide();

        //Set up firestore
        firestore = FirebaseFirestore.getInstance();
        branchesReference = firestore.collection("branches");

        //If there is data passed through to this activity
        if(getIntent().getExtras() != null) {
            branch = (Branch) getIntent().getSerializableExtra("branch");
            initializeFields();
        } else {
            //Launch edit services page
            Intent intent = new Intent(EmployeeMainActivity.this, EmployeeEditActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void initializeFields() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BranchInfoFragment infoFragment = BranchInfoFragment.newInstance(branch);
        ft.replace(R.id.employee_info_fragment_container, infoFragment);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserAccount account = UserController.getInstance().getUserAccount();

        branchesReference.document(account.getUID()).addSnapshotListener(EmployeeMainActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null) {
                    if(value.exists()) {

                    } else {

                    }
                } else {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Sign out of the app.
     * @param view The current view.
     */
    public void signOut(View view) {
        //Sign out of Firebase
        UserController.getInstance().signOut();
        //Navigate back to Login Page
        Intent intent = new Intent(EmployeeMainActivity.this, LoginActivity.class);
        //set the new task and clear flags, so that the user can't go back here
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
package com.uottawa.servicenovigrad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminMainActivity;
import com.uottawa.servicenovigrad.activities.auth.LoginActivity;
import com.uottawa.servicenovigrad.activities.customer.CustomerActivity;
import com.uottawa.servicenovigrad.activities.employee.EmployeeRouterActivity;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        UserAccount account = UserController.getInstance().getUserAccount();

        Intent intent;
        //Sets intent based on what role the user is
        switch(account.getRole()) {
            case "admin":
                intent = new Intent(MainActivity.this, AdminMainActivity.class);
                break;
            case "employee":
                intent = new Intent(MainActivity.this, EmployeeRouterActivity.class);
                break;
            case "customer":
                intent = new Intent(MainActivity.this, CustomerActivity.class);
                break;
            default:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                break;
        }
        //set the new task and clear flags, so that the user can't go back here
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
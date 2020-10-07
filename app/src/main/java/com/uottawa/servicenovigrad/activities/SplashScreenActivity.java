package com.uottawa.servicenovigrad.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.user.AdminAccount;
import com.uottawa.servicenovigrad.user.CustomerAccount;
import com.uottawa.servicenovigrad.user.EmployeeAccount;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;
import com.uottawa.servicenovigrad.utils.Function;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        //Check if firebase auth is already signed in
        boolean signedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

        if(signedIn) {
            try {
                //Try to get user data from shared preferences.
                SharedPreferences sharedPref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);

                String name = sharedPref.getString(getString(R.string.user_name_key), "");
                String email = sharedPref.getString(getString(R.string.user_email_key), "");
                String role = sharedPref.getString(getString(R.string.user_role_key), "");
                String uid = sharedPref.getString(getString(R.string.user_uid_key), "");

                //If there's actually data in shared preferences
                if(!name.isEmpty() && !email.isEmpty() && !role.isEmpty() && !uid.isEmpty()) {
                    UserAccount account = null;

                    switch(role) {
                        case "employee":
                            account = new EmployeeAccount(name, email, uid);
                            break;
                        case "customer":
                            account = new CustomerAccount(name, email, uid);
                            break;
                        case "admin":
                            account = new AdminAccount();
                            break;
                        default:
                            throw new Exception("Failed to get proper role. Aborting login attempt.");
                    }

                    UserController.initialize(account);

                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    //set the new task and clear flags, so that the user can't go back to splash screen
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    throw new Exception("Failed to get data from shared preferences. Aborting login attempt.");
                }
            } catch (Exception e) {
                Log.e("SPLASH SCREEN ERROR", e.toString());
                goToLoginPage();
            }
        } else {
            goToLoginPage();
        }
    }

    private void goToLoginPage() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        //set the new task and clear flags, so that the user can't go back to splash screen
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
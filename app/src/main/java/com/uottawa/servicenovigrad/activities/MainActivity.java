package com.uottawa.servicenovigrad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.user.AdminAccount;
import com.uottawa.servicenovigrad.user.UserAccount;
import com.uottawa.servicenovigrad.user.UserController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        TextView title = (TextView) findViewById(R.id.successful_login);
        TextView info = (TextView) findViewById(R.id.login_info);

        UserAccount account = UserController.getInstance().getUserAccount();

        //Write the name, role, and email of the current user.
        title.setText("Welcome " + account.getName() + "!");
        info.setText("You are logged in as a " + account.getRole() + "\nYour email is: " + account.getEmail());


        Intent intent;
        //Sets intent based on what role the user is
        switch(account.getRole()) {
            case "admin":
                intent = new Intent(MainActivity.this, AdminActivity.class);
                break;
            case "employee":
                intent = new Intent(MainActivity.this, EmployeeActivity.class);
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

    /**
     * Sign out of the app.
     * @param view The current view.
     */
    public void signOut(View view) {
        //Sign out of Firebase
        UserController.getInstance().signOut();
        //Navigate back to Login Page
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //set the new task and clear flags, so that the user can't go back here
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
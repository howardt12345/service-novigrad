package com.uottawa.servicenovigrad.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.auth.LoginActivity;
import com.uottawa.servicenovigrad.activities.employee.EmployeeEditActivity;
import com.uottawa.servicenovigrad.user.UserController;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        getSupportActionBar().hide();
    }

    public void onServicesCardClick(View view) {
        //Navigate to services page
        Intent intent = new Intent(AdminMainActivity.this, AdminServicesActivity.class);
        startActivity(intent);
    }

    public void onUsersCardClick(View view) {
        //Navigate to users page
        Intent intent = new Intent(AdminMainActivity.this, AdminUsersActivity.class);
        startActivity(intent);
    }

    /**
     * Sign out of the app.
     * @param view The current view.
     */
    public void signOut(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminMainActivity.this);
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
                                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
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
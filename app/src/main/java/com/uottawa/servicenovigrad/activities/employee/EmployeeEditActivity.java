package com.uottawa.servicenovigrad.activities.employee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesEdit;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;

public class EmployeeEditActivity extends AppCompatActivity {

    Branch branch;
    private boolean newBranch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);
        getSupportActionBar().hide();

        //If there is data passed through to this activity
        if(getIntent().getExtras() != null) {
            //Get the service from the data
            branch = (Branch) getIntent().getSerializableExtra("branch");
        } else {
            //Change title to reflect current function
            TextView title = (TextView) findViewById(R.id.employee_edit_title);
            title.setText("Complete Branch Profile");
            title.setTextSize(24.0f);

            //Hide cancel button
            ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button_employee);
            cancelButton.setVisibility(View.GONE);

            newBranch = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(newBranch) {
            //Stop the user from going back if the user is creating a new branch
            preventExitDialog();
        } else {
            //Prompt the user to cancel edit when back button is pressed
            cancelEditPrompt(getCurrentFocus());
        }
    }

    private void preventExitDialog() {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
        .setTitle("Cannot cancel edit.")
        .setMessage("You MUST complete your branch profile in order to proceed.")
        .setCancelable(true)
        .setPositiveButton(
                "OK",
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

    /**
     * Display a dialog to confirm if user wants to cancel the edit
     * @param view the current view.
     */
    public void cancelEditPrompt(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
        .setTitle("Discard Changes?")
        .setMessage("Are you sure you want to discard your changes?")
        .setCancelable(true)
        .setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //Close the activity with result code not ok
                        Intent intent = new Intent();
                        setResult(1, intent);
                        finish();
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
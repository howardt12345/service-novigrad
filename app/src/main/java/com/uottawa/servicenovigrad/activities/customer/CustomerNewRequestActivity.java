package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.service.ServicePickerActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.branch.ServiceRequest;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

public class CustomerNewRequestActivity extends AppCompatActivity {

    private int GET_BRANCH = 0, GET_SERVICE = 1;

    Branch branch;
    Service service;

    ServiceRequest request;

    Button branchButton, serviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_new_request);
        getSupportActionBar().hide();

        if(branch == null) {
            Intent intent = new Intent(CustomerNewRequestActivity.this, CustomerSearchActivity.class);
            startActivityForResult(intent, GET_BRANCH);
        }

        branchButton = findViewById(R.id.request_select_branch);
        serviceButton = findViewById(R.id.request_select_service);

        request = new ServiceRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == GET_BRANCH) {
                branch = (Branch) data.getSerializableExtra("branch");
                branchButton.setText(branch.getName());
                service = null;
                serviceButton.setText("Select Service");

                request.setBranchId(branch.getId());
                request.setServiceId("");
            } else if (requestCode == GET_SERVICE) {
                service = (Service) data.getSerializableExtra("service");

                serviceButton.setText(service.getName());
                request.setServiceId(service.getId());
            }
        }
    }

    private void initializeServiceInfo() {

    }

    public void selectService(View view) {
        if(branch != null) {
            Intent intent = new Intent(CustomerNewRequestActivity.this, ServicePickerActivity.class);
            intent.putExtra("branch", branch);
            startActivityForResult(intent, GET_SERVICE);
        } else {
            Utils.showSnackbar("Select a branch first.", getCurrentFocus());
        }
    }

    public void selectBranch(View view) {
        Intent intent = new Intent(CustomerNewRequestActivity.this, CustomerSearchActivity.class);
        startActivityForResult(intent, GET_BRANCH);
    }

    @Override
    public void onBackPressed() {
        //Cancel the edit when back button is pressed
        cancelEdit(this.getCurrentFocus());
    }

    /**
     * Display a dialog to confirm if user wants to cancel the edit
     * @param view the current view.
     */
    public void cancelEdit(View view) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerNewRequestActivity.this);
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

    public void confirmEdit(View view) {
        boolean valid = verifyEdit();
        if(valid) {
            sendRequest();
        }
    }

    private boolean verifyEdit() {
        if(branch == null || request.getBranchId().isEmpty()) {
            Utils.showSnackbar("A branch must be selected.", findViewById(R.id.new_request_view));
            return false;
        }
        if(service == null || request.getServiceId().isEmpty()) {
            Utils.showSnackbar("A service must be selected.", findViewById(R.id.new_request_view));
            return false;
        }

        return true;
    }

    /**
     * Saves the edit.
     */
    private void sendRequest() {
        Intent intent = new Intent();
        //Add the service to the intent data
        intent.putExtra("request", request);
        setResult(RESULT_OK, intent);
        finish();
    }
}
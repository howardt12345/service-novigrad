package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.service.ServicePickerActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.branch.ServiceRequest;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomerNewRequestActivity extends AppCompatActivity {

    private int GET_BRANCH = 0, GET_SERVICE = 1;

    Branch branch;
    Service service;

    ServiceRequest request;

    Button branchButton, serviceButton, dateButton, timeButton;
    LinearLayout infoLayout, docsLayout;

    List<View> infoFields;
    List<View> docsFields;

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
        dateButton = findViewById(R.id.request_select_date);
        timeButton = findViewById(R.id.request_select_time);

        infoLayout = findViewById(R.id.request_service_info);
        docsLayout = findViewById(R.id.request_service_docs);

        infoFields = new ArrayList<>();
        docsFields = new ArrayList<>();

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

                infoFields.clear();
                infoLayout.removeAllViews();
                docsFields.clear();
                docsLayout.removeAllViews();

                TextView textView = new TextView(this);
                textView.setText("Select a Service first.");
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                infoLayout.addView(textView);

                TextView textView1 = new TextView(this);
                textView1.setText("Select a Service first.");
                textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                docsLayout.addView(textView1);

            } else if (requestCode == GET_SERVICE) {
                service = (Service) data.getSerializableExtra("service");

                serviceButton.setText(service.getName());
                request.setServiceId(service.getId());
                initializeServiceFields();
            }
        }
    }

    private void initializeServiceFields() {
        infoFields.clear();
        infoLayout.removeAllViews();
        docsFields.clear();
        docsLayout.removeAllViews();

        for(String infoField : service.getForms()) {
            //If the info field requires a date
            if(infoField.contains("Date")) {
                final Button dateButton = new Button(this);

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(CustomerNewRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateButton.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
                            }
                        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                infoFields.add(dateButton);
                infoLayout.addView(dateButton);
            } else if (infoField.contains("Address")) { //If the info field requires an address

            } else {
                EditText field = new EditText(this);
                field.setHint(infoField);

                infoFields.add(field);
                infoLayout.addView(field);
            }
        }

        for(String docField : service.getDocuments()) {
            Button button = new Button(this);
            button.setText("Select " + docField);

            docsFields.add(button);
            docsLayout.addView(button);
        }
    }

    public void selectBranch(View view) {
        Intent intent = new Intent(CustomerNewRequestActivity.this, CustomerSearchActivity.class);
        startActivityForResult(intent, GET_BRANCH);
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

    public void selectDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(CustomerNewRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDate(year, month, dayOfMonth);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void setDate(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);

        if(!Utils.getDaysOfWeek(branch.getOpenDays()).contains(c.get(Calendar.DAY_OF_WEEK))) {
            Utils.showSnackbar("The branch is not open on the selected day of the week.", findViewById(R.id.new_request_view));
            dateButton.setText("Select Date");
        } else {
            dateButton.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
        }
    }

    public void selectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(CustomerNewRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay, minute);
            }
        }, branch.getOpeningHour(), branch.getOpeningMinute(), true);
        timePickerDialog.show();
    }

    private void setTime(int hourOfDay, int minute) {
        if(!branch.isOpenAt(hourOfDay, minute)) {
            Utils.showSnackbar("The branch is not open on the selected time.", findViewById(R.id.new_request_view));
            timeButton.setText("Select Time");
        } else {
            timeButton.setText(Utils.formatTime(hourOfDay, minute));
        }
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
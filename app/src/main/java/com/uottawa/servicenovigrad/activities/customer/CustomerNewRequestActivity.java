package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.service.ServicePickerActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.branch.ServiceRequest;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomerNewRequestActivity extends AppCompatActivity {

    private int GET_BRANCH = 0, GET_SERVICE = 1, GET_ADDRESS = 2;

    Branch branch;
    Service service;
    int year, month, day, hour, minute;
    boolean dateSet, timeSet;

    Button branchButton, serviceButton, dateButton, timeButton;
    LinearLayout infoLayout, docsLayout;

    List<View> infoFields;
    List<View> docsFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_new_request);
        getSupportActionBar().hide();

        // Initialize the SDK
        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCyu0x3W3kNBMvZPfEd9v1Lna52vfFvyp4");
        }

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

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
                initializeServiceFields();
            } else if (requestCode == GET_ADDRESS) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                for(View v : infoFields) {
                    if(v.getClass() == Button.class) {
                        if(((Button) v).getText().toString().contains("Address")) {
                            ((Button) v).setText(place.getAddress());
                        }
                    }
                }
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
                dateButton.setText(infoField);

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog = new DatePickerDialog(CustomerNewRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateButton.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
                            }
                        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        dialog.show();
                    }
                });

                infoFields.add(dateButton);
                infoLayout.addView(dateButton);
            } else if (infoField.contains("Address")) { //If the info field requires an address
                final Button addressButton = new Button(this);
                addressButton.setText(infoField);

                addressButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Set the fields to specify which types of place data to
                        // return after the user has made a selection.
                        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS);

                        // Start the autocomplete intent.
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("CA").setTypeFilter(TypeFilter.ADDRESS).build(CustomerNewRequestActivity.this);
                        startActivityForResult(intent, GET_ADDRESS);
                    }
                });

                infoFields.add(addressButton);
                infoLayout.addView(addressButton);
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
            dateSet = false;
        } else {
            dateButton.setText(year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth));
            this.year = year;
            this.month = month;
            this.day = dayOfMonth;
            dateSet = true;
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
            timeSet = false;
        } else {
            try {
                timeButton.setText(Utils.formatTime(hourOfDay, minute));
                this.hour = hourOfDay;
                this.minute = minute;
                timeSet = true;
            } catch (Exception e) {
                Utils.showSnackbar("Failed to select time.", findViewById(R.id.new_request_view));
                timeButton.setText("Select Time");
                timeSet = false;
            }
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
        if(branch == null) {
            Utils.showSnackbar("A branch must be selected.", findViewById(R.id.new_request_view));
            return false;
        }
        if(service == null) {
            Utils.showSnackbar("A service must be selected.", findViewById(R.id.new_request_view));
            return false;
        }
        if(!dateSet) {
            Utils.showSnackbar("A date must be selected.", findViewById(R.id.new_request_view));
            return false;
        }
        if(!timeSet) {
            Utils.showSnackbar("A time must be selected.", findViewById(R.id.new_request_view));
            return false;
        }
        for(int i = 0; i < service.getForms().size(); i++) {
            View v = infoFields.get(i);
            if(v.getClass() == Button.class) {
                if(((Button) v).getText().toString().equals(service.getForms().get(i))) {
                    Utils.showSnackbar("One or more of the required fields are empty.", findViewById(R.id.new_request_view));
                    return false;
                }
            } else if (v.getClass() == EditText.class) {
                if(((EditText) v).getText().toString().isEmpty()) {
                    Utils.showSnackbar("One or more of the required fields are empty.", findViewById(R.id.new_request_view));
                    return false;
                }
            }
        }

        return true;
    }

    private ArrayList<String> parseInfo() {
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < service.getForms().size(); i++) {
            View v = infoFields.get(i);
            if(v.getClass() == Button.class) {
                data.add(service.getForms().get(i) + ": " + ((Button) v).getText());
            } else if (v.getClass() == EditText.class) {
                data.add(service.getForms().get(i) + ": " + ((EditText) v).getText());
            }
        }
        return data;
    }

    /**
     * Saves the edit.
     */
    private void sendRequest() {
        ServiceRequest request = new ServiceRequest();
        request.setBranchId(branch.getId());
        request.setServiceId(service.getId());

        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, minute);
        request.setScheduledTime(date.getTime());

        request.setInfo(parseInfo());

        Intent intent = new Intent();
        //Add the service to the intent data
        intent.putExtra("request", request);
        setResult(RESULT_OK, intent);
        finish();
    }
}
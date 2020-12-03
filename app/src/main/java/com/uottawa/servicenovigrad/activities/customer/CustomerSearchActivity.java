package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.branch.BranchInfoFragment;
import com.uottawa.servicenovigrad.activities.customer.adapters.SearchResultListAdapter;
import com.uottawa.servicenovigrad.activities.service.ServicePickerActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Function;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class CustomerSearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int GET_SERVICE = 0;

    private FirebaseFirestore firestore;
    private CollectionReference branches;
    private CollectionReference services;

    private ArrayList<Branch> branchList;

    SearchResultListAdapter resultsListAdapter;
    ListView list;

    MaterialDayPicker daysPicker;
    LinearLayout dateAndTimeSelect;
    Button serviceSelectButton, timeButton;
    Spinner filterSpinner;

    SearchView searchView;

    String[] filters = {"None", "Date and Time", "Specific Service"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        getSupportActionBar().hide();

        // Connect to firestore
        firestore = FirebaseFirestore.getInstance();
        branches = firestore.collection("branches");
        services = firestore.collection("services");

        // Get branch data
        branchList = new ArrayList<>();
        getData();

        // Populate list
        list = findViewById(R.id.search_results);

        //Get days picker
        daysPicker = findViewById(R.id.customer_search_days_picker);
        daysPicker.setSelectionMode(SingleSelectionMode.create());
        daysPicker.setDaySelectionChangedListener(new MaterialDayPicker.DaySelectionChangedListener() {
            @Override
            public void onDaySelectionChanged(List<MaterialDayPicker.Weekday> list) {
                if(list.isEmpty()) {
                    resultsListAdapter.resetFilter();
                } else {
                    List<String> convertedDays = Utils.convertPickedDays(list);
                    resultsListAdapter.setDayFilter(convertedDays.get(0));
                }
            }
        });
        //Get opening and closing time buttons
        timeButton = findViewById(R.id.customer_search_select_time);

        //Get the date and time selection layout and hide it
        dateAndTimeSelect = findViewById(R.id.customer_search_date_and_time);
        dateAndTimeSelect.setVisibility(View.GONE);

        //Get the service selection layout and hide it
        serviceSelectButton = findViewById(R.id.customer_search_service_select);
        serviceSelectButton.setVisibility(View.GONE);

        //Set up the filter spinner
        filterSpinner = findViewById(R.id.customer_search_filter);
        filterSpinner.setOnItemSelectedListener(this);

        //Give the spinner values
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        filterSpinner.setAdapter(spinnerAdapter);

        // Create and bind adapter
        resultsListAdapter = new SearchResultListAdapter(this, branchList, new Function() {
            @Override
            public void f(final Object... params) {
                final Branch b = (Branch) params[0];
                //Create new AlertDialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerSearchActivity.this);
                alertDialogBuilder
                        .setTitle("Select " + b.getName() + "?") //Set the title of the dialog to the service name
                        .setMessage("Are you sure you want to select " + b.getName() + "?") //Set the message of the dialog to the service info
                        .setCancelable(true)
                        .setPositiveButton(
                                "SELECT",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.putExtra("branch", b);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                        )
                        .setNegativeButton(
                                "CANCEL",
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
        });
        list.setAdapter(resultsListAdapter);

        searchView = findViewById(R.id.branch_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText;
                resultsListAdapter.filter(newText);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == GET_SERVICE) {
                Service service = (Service) data.getSerializableExtra("service");
                serviceSelectButton.setText(service.getName());
                resultsListAdapter.setServiceFilter(service.getId());
            }
        }
    }

    public void getData() {
        branches.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("CustomerSearchActivity", "Listen failed.", error);
                    return;
                }

                branchList.clear();
                resultsListAdapter.clear();

                for (QueryDocumentSnapshot document : value) {
                    String id = document.getId();
                    String name = document.getString("name");
                    String address = document.getString("address");
                    String phoneNumber = document.getString("phoneNumber");
                    ArrayList<String> servicesIds = (ArrayList<String>) document.get("services");

                    ArrayList<String> openDays = (ArrayList<String>) document.get("openDays");
                    int openingHour = document.getLong("openingHour").intValue();
                    int openingMinute = document.getLong("openingMinute").intValue();
                    int closingHour = document.getLong("closingHour").intValue();
                    int closingMinute = document.getLong("closingMinute").intValue();
                    double rating = document.getDouble("rating");

                    //Initialize the branch object.
                    Branch b = new Branch(
                            id,
                            name,
                            address,
                            phoneNumber,
                            servicesIds,
                            openDays,
                            openingHour,
                            openingMinute,
                            closingHour,
                            closingMinute,
                            rating
                    );
                    branchList.add(b);
                    resultsListAdapter.notifyDataSetChanged(b);
                }
            }
        });
    }

    public void timeSelector(View view) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                resultsListAdapter.setTimeFilter(hourOfDay, minute);
                timeButton.setText(Utils.formatTime(hourOfDay, minute));
            }
        }, hour, min, true);
        picker.show();
    }

    public void selectService(View view) {
        Intent intent = new Intent(CustomerSearchActivity.this, ServicePickerActivity.class);
        startActivityForResult(intent, GET_SERVICE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Reset the filters
        dateAndTimeSelect.setVisibility(View.GONE);
        serviceSelectButton.setVisibility(View.GONE);
        daysPicker.clearSelection();
        timeButton.setText("Select Time");
        serviceSelectButton.setText("Select Service");

        resultsListAdapter.resetFilter();

        if (position == 1) {
            dateAndTimeSelect.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            serviceSelectButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        dateAndTimeSelect.setVisibility(View.GONE);
        serviceSelectButton.setVisibility(View.GONE);
    }
}
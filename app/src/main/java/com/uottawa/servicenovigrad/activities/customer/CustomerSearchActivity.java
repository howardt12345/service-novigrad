package com.uottawa.servicenovigrad.activities.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.customer.adapters.SearchResultListAdapter;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.utils.Function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CustomerSearchActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private CollectionReference branches;
    private CollectionReference services;

    private ArrayList<Branch> branchList;
    private ArrayList<String> serviceList;
    private ArrayList<String> serviceNameList;

    SearchResultListAdapter adapter;
    ListView list;

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
        serviceList = new ArrayList<>();
        serviceNameList = new ArrayList<>();
        getData();

        // Populate list
        list = findViewById(R.id.search_results);

        // Create and bind adapter
        adapter = new SearchResultListAdapter(this, branchList, new Function() {
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
        list.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.branch_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText;
                adapter.filter(newText);
                return false;
            }
        });
    }

    public void getData() {
        branches.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Branch b = new Branch(
                                document.getId(),
                                (String)document.getData().get("name"),
                                (String)document.getData().get("address"),
                                (String)document.getData().get("phoneNumber"),
                                (ArrayList<String>)document.getData().get("services"),
                                (ArrayList<String>)document.getData().get("openDays"),
                                ((Long)document.getData().get("openingHour")).intValue(),
                                ((Long)document.getData().get("openingMinute")).intValue(),
                                ((Long)document.getData().get("closingHour")).intValue(),
                                ((Long)document.getData().get("closingMinute")).intValue(),
                                (double)document.getData().get("rating")
                        );
                        branchList.add(b);
                        adapter.notifyDataSetChanged(b);
                    }
                } else {
                    Log.d("Search: ", "Error getting branch data from database");
                }
            }
        });

        services.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        serviceList.add(doc.getId());
                        serviceNameList.add(doc.getString("name"));
                    }
                }
            }
        });
    }


    public void openTimeSelector(View view) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                adapter.filterTimes(hourOfDay, minute, true);
            }
        }, hour, min, true);
        picker.show();
    }

    public void closingTimeSelector(View view) {
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                adapter.filterTimes(hourOfDay, minute, false);
            }
        }, hour, min, true);
        picker.show();
    }

    public void selectDaysOfWeek(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Select Days of Week");
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        boolean[] initialState = new boolean[7];
        final boolean[] selected = initialState;
        alertDialog.setMultiChoiceItems(days, initialState, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selected[which] = isChecked;
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.filterDayOfWeek(selected);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void selectServices(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Select Services");
        final String[] services = new String[serviceNameList.size()];
        serviceNameList.toArray(services);
        boolean[] initialState = new boolean[services.length];
        final ArrayList<String> selected = new ArrayList<>();
        alertDialog.setMultiChoiceItems(services, initialState, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selected.add(serviceList.get(which));
                } else {
                    if (selected.contains(serviceList.get(which))) selected.remove(serviceList.get(which));
                }
            }
        });

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.filterServices(selected);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
}
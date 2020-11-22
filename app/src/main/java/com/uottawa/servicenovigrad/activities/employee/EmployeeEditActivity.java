package com.uottawa.servicenovigrad.activities.employee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.AdminServicesEdit;
import com.uottawa.servicenovigrad.activities.branch.adapters.BranchInfoServicesAdapter;
import com.uottawa.servicenovigrad.activities.service.ServicePickerActivity;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class EmployeeEditActivity extends AppCompatActivity {

    private static String TAG = "EmployeeEditActivity";

    private static int SERVICE_REQUEST_CODE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE = 2;

    private CollectionReference servicesReference = FirebaseFirestore.getInstance().collection("services");

    Branch branch;
    private boolean newBranch = false;

    List<Service> services;
    LinearLayout servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);
        getSupportActionBar().hide();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyCyu0x3W3kNBMvZPfEd9v1Lna52vfFvyp4");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

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
            branch = new Branch();
        }
        initializeFields();
    }

    private void initializeFields() {
        EditText nameField = (EditText) findViewById(R.id.branch_edit_name);
        final EditText phoneNumberField = (EditText) findViewById(R.id.branch_edit_phone_number);

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                branch.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //Flag for if user is backspacing
            private boolean backspacingFlag = false;
            //Block the afterTextChanged method from being called again after replacing text
            private boolean editedFlag = false;
            //Mark cursor position to restore after replacing
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Store cursor relative to the end of the string in the EditText
                cursorComplement = s.length()-phoneNumberField.getSelectionStart();
                //Check if user is inputting or erasing character
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set phone number
                branch.setPhoneNumber(Utils.formatToUnformattedNumber(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                //Unformat the number in the string
                String phone = Utils.formatToUnformattedNumber(string);

                //if the text was just edited, this will be called another time
                //Otherwise, format the text
                if (!editedFlag) {
                    //Verify when there are more than 6 digits
                    if (phone.length() >= 6 && !backspacingFlag) {
                        editedFlag = true;
                        //Substring the raw digits and add the mask.
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3,6) + "-" + phone.substring(6);
                        phoneNumberField.setText(ans);
                        //Set the cursor to its original position relative to the end of the string.
                        phoneNumberField.setSelection(phoneNumberField.getText().length()-cursorComplement);

                        //Verify when only one character mask is needed
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" +phone.substring(0, 3) + ") " + phone.substring(3);
                        phoneNumberField.setText(ans);
                        phoneNumberField.setSelection(phoneNumberField.getText().length()-cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        nameField.setText(branch.getName());
        phoneNumberField.setText(branch.getPhoneNumber());

        Button addressButton = (Button) findViewById(R.id.branch_edit_address_button);

        addressButton.setText(TextUtils.isEmpty(branch.getAddress()) ? "Set Address" : branch.getAddress());

        setOpeningTime(branch.getOpeningHour(), branch.getOpeningMinute(), false);
        setClosingTime(branch.getClosingHour(), branch.getClosingMinute(), false);

        MaterialDayPicker openDaysPicker = (MaterialDayPicker) findViewById(R.id.branch_edit_days_open_picker);
        Utils.selectDaysInPicker(openDaysPicker, branch.getOpenDays());
        openDaysPicker.setDaySelectionChangedListener(new MaterialDayPicker.DaySelectionChangedListener() {
            @Override
            public void onDaySelectionChanged(List<MaterialDayPicker.Weekday> list) {
                branch.setOpenDays(Utils.convertPickedDays(list));
            }
        });

        services = new ArrayList<>();

        servicesList = (LinearLayout) findViewById(R.id.branch_edit_services_list);
        //Add listener to service reference
        servicesReference.orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                //Clear the list and components
                services.clear();
                //Iterate through each document in collection
                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        //Get the information of the service
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String desc = doc.getString("desc");
                        List<String> forms = (List<String>) doc.get("forms");
                        List<String> documents = (List<String>) doc.get("documents");
                        int price = doc.getLong("price").intValue();

                        //Create a new service object
                        Service service = new Service(id, name, desc, forms, documents, price);
                        //Add service to list
                        services.add(service);
                    }
                }
                //Set up the services list
                setUpServicesList(services, servicesList, EmployeeEditActivity.this, EmployeeEditActivity.this.getLayoutInflater());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If adding a service
        if(requestCode == SERVICE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //Get service from result and add to branch list
                Service service = (Service) data.getSerializableExtra("service");
                branch.getServices().add(service.getId());

                //Set up the services list
                setUpServicesList(services, servicesList, EmployeeEditActivity.this, EmployeeEditActivity.this.getLayoutInflater());
            }
        }
        //if setting address
        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                branch.setAddress(place.getAddress());

                Button addressButton = (Button) findViewById(R.id.branch_edit_address_button);
                addressButton.setText(TextUtils.isEmpty(branch.getAddress()) ? "Set Address" : branch.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
    }

    public void pickAddress(View view) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("CA").build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void pickOpeningTime(View view) {
        TimePickerDialog openingTimePicker = new TimePickerDialog(EmployeeEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setOpeningTime(hourOfDay, minute, true);
            }
        }, branch.getOpeningHour(), branch.getOpeningMinute(), true);
        openingTimePicker.show();
    }

    public void pickClosingTime(View view) {
        TimePickerDialog closingTimePicker = new TimePickerDialog(EmployeeEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setClosingTime(hourOfDay, minute, true);
            }
        }, branch.getClosingHour(), branch.getClosingMinute(), true);
        closingTimePicker.show();
    }

    private void setOpeningTime(int hour, int minute, boolean setBranch) {
        if(setBranch) {
            if(verifyOpeningTime(hour, minute)) {
                branch.setOpeningHour(hour);
                branch.setOpeningMinute(minute);
            } else {
                Utils.showSnackbar("Opening time cannot be after closing time.", findViewById(R.id.branch_edit_view));
            }
        }
        Button openingTimeButton = (Button) findViewById(R.id.branch_edit_opening_time);
        openingTimeButton.setText(Utils.formatTime(branch.getOpeningHour(), branch.getOpeningMinute()));
    }

    private void setClosingTime(int hour, int minute, boolean setBranch) {
        if(setBranch) {
            if(verifyClosingTime(hour, minute)) {
                branch.setClosingHour(hour);
                branch.setClosingMinute(minute);
            } else {
                Utils.showSnackbar("Closing time cannot be before opening time.", findViewById(R.id.branch_edit_view));
            }
        }
        Button closingTimeButton = (Button) findViewById(R.id.branch_edit_closing_time);
        closingTimeButton.setText(Utils.formatTime(branch.getClosingHour(), branch.getClosingMinute()));
    }

    public void pickService(View view) {
        Intent intent = new Intent(EmployeeEditActivity.this, ServicePickerActivity.class);
        startActivityForResult(intent, SERVICE_REQUEST_CODE);
    }

    /**
     * Sets up the list in the UI
     * @param services the list of services to set up the list with
     * @param listView the LinearLayout in which the list items will go in
     */
    private void setUpServicesList(final List<Service> services, LinearLayout listView, Context context, LayoutInflater inflater) {
        //Filter the service list
        final List<Service> filteredServices = new ArrayList<Service>();
        for(Service s : services) {
            if(branch.getServices().contains(s.getId())) {
                filteredServices.add(s);
            }
        }

        servicesList.removeAllViews();

        //Create a list adapter
        BranchInfoServicesAdapter adapter = new BranchInfoServicesAdapter(context, filteredServices, inflater);
        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serviceInfo(filteredServices.get(finalI));
                }
            });

            //Set delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_service);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeService(filteredServices.get(finalI));
                }
            });

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void serviceInfo(Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
                .setTitle(service.getName()) //Set the title of the dialog to the service name
                .setMessage(service.getInfo()) //Set the message of the dialog to the service info
                .setCancelable(true)
                .setPositiveButton(
                        "CLOSE",
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

    private void removeService(final Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeEditActivity.this);
        alertDialogBuilder
                .setTitle("Remove service?")
                .setMessage("Are you sure you want to remove " + service.getName() + " from your branch profile?")
                .setCancelable(true)
                .setPositiveButton(
                        "YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                branch.getServices().remove(service.getId());
                                //Refresh the services list
                                setUpServicesList(services, servicesList, EmployeeEditActivity.this, EmployeeEditActivity.this.getLayoutInflater());
                                dialog.cancel();
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

    public void confirmEdit(View view) {
        boolean valid = validateEdit(branch, view);
        if(valid) {
            saveEdit();
        }
    }

    private boolean validateEdit(Branch branch, View view) {
        if(TextUtils.isEmpty(branch.getName()) || TextUtils.isEmpty(branch.getPhoneNumber()) || TextUtils.isEmpty(branch.getAddress())
        || branch.getOpenDays().isEmpty() || branch.getServices().isEmpty()) {
            Utils.showSnackbar("One or more required fields are empty.", view);
            return false;
        }
        if(branch.getPhoneNumber().length() < 10) {
            Utils.showSnackbar("Phone number is too short.", view);
            return false;
        }
        if(!verifyOpeningTime(branch.getOpeningHour(), branch.getOpeningMinute())) {
            Utils.showSnackbar("Opening time cannot be after closing time.", findViewById(R.id.branch_edit_view));
        }
        if(!verifyClosingTime(branch.getClosingHour(), branch.getClosingMinute())) {
            Utils.showSnackbar("Closing time cannot be before opening time.", findViewById(R.id.branch_edit_view));
        }
        return true;
    }

    private boolean verifyOpeningTime(int hour, int minute) {
        if(hour < branch.getClosingHour()) {
            return true;
        } else if (hour == branch.getClosingHour()) {
            return minute < branch.getClosingMinute();
        } else {
            return false;
        }
    }

    private boolean verifyClosingTime(int hour, int minute) {
        if(hour > branch.getOpeningHour()) {
            return true;
        } else if (hour == branch.getOpeningHour()) {
            return minute > branch.getOpeningMinute();
        } else {
            return false;
        }
    }

    /**
     * Saves the edit.
     */
    private void saveEdit() {
        Intent intent = new Intent();
        //Add the service to the intent data
        intent.putExtra("branch", branch);
        setResult(0, intent);
        finish();
    }
}
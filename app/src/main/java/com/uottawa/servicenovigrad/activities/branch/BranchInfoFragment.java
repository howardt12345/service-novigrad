package com.uottawa.servicenovigrad.activities.branch;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.branch.adapters.BranchInfoServicesAdapter;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BranchInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchInfoFragment extends Fragment {

    private CollectionReference servicesReference = FirebaseFirestore.getInstance().collection("services");

    private static final String ARG_NAME = "name";
    private static final String ARG_PHONENUMBER = "phoneNumber";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_SERVICES = "services";
    private static final String ARG_OPENDAYS = "openDays";
    private static final String ARG_OPENINGHOUR = "openingHour";
    private static final String ARG_OPENINGMINUTE = "openingMinute";
    private static final String ARG_CLOSINGHOUR = "closingHour";
    private static final String ARG_CLOSINGMINUTE = "closingMinute";
    private static final String ARG_RATING = "rating";

    List<Service> services;
    LinearLayout servicesList;

    private String name;
    private String phoneNumber;
    private String address;

    private List<String> serviceIds;
    private List<String> openDays;

    private int openingHour, openingMinute, closingHour, closingMinute;
    private double rating;

    public BranchInfoFragment() {
        // Required empty public constructor
    }

    public static BranchInfoFragment newInstance(Branch branch) {
        BranchInfoFragment fragment = new BranchInfoFragment();
        Bundle args = new Bundle();

        args.putString(ARG_NAME, branch.getName());
        args.putString(ARG_PHONENUMBER, branch.getPhoneNumber());
        args.putString(ARG_ADDRESS, branch.getAddress());
        args.putStringArrayList(ARG_SERVICES, branch.getServices());
        args.putStringArrayList(ARG_OPENDAYS, branch.getOpenDays());
        args.putInt(ARG_OPENINGHOUR, branch.getOpeningHour());
        args.putInt(ARG_OPENINGMINUTE, branch.getOpeningMinute());
        args.putInt(ARG_CLOSINGHOUR, branch.getClosingHour());
        args.putInt(ARG_CLOSINGMINUTE, branch.getClosingMinute());
        args.putDouble(ARG_RATING, branch.getRating());

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            phoneNumber = getArguments().getString(ARG_PHONENUMBER);
            address = getArguments().getString(ARG_ADDRESS);
            serviceIds = getArguments().getStringArrayList(ARG_SERVICES);
            openDays = getArguments().getStringArrayList(ARG_OPENDAYS);
            openingHour = getArguments().getInt(ARG_OPENINGHOUR);
            openingMinute = getArguments().getInt(ARG_OPENINGMINUTE);
            closingHour = getArguments().getInt(ARG_CLOSINGHOUR);
            closingMinute = getArguments().getInt(ARG_CLOSINGMINUTE);
            rating = getArguments().getDouble(ARG_RATING);

            services = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_branch_info, container, false);

        TextView nameView = rootView.findViewById(R.id.employee_info_name);
        nameView.setText(name);

        TextView phoneView = rootView.findViewById(R.id.employee_info_phoneNumber);
        phoneView.setText("Phone Number: " + Utils.formatPhoneNumber(phoneNumber));

        TextView addressView = rootView.findViewById(R.id.employee_info_address);
        addressView.setText("Address: " + address);

        TextView workingHoursView = rootView.findViewById(R.id.employee_info_workinghours);
        workingHoursView.setText("Working Hours: " + Utils.formatTime(openingHour, openingMinute) + " - " + Utils.formatTime(closingHour, closingMinute));

        MaterialDayPicker openDaysPicker = rootView.findViewById(R.id.branch_days_open_picker);
        Utils.selectDaysInPicker(openDaysPicker, openDays);
        openDaysPicker.disableAllDays();

        servicesList = rootView.findViewById(R.id.employee_branch_services);
        //Add listener to service reference
        servicesReference.orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("BranchInfoFragment", "Listen failed.", error);
                    return;
                }
                //Clear the list and components
                services.clear();
                servicesList.removeAllViews();
                //Iterate through each document in collection
                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        //Get the information of the service
                        String id = doc.getId();
                        if(serviceIds.contains(id)) {
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
                }
                //Set up the services list
                setUpServicesList(services, servicesList, rootView.getContext(), inflater);
            }
        });

        return rootView;
    }

    /**
     * Sets up the list in the UI
     * @param services the list of services to set up the list with
     * @param listView the LinearLayout in which the list items will go in
     */
    private void setUpServicesList(final List<Service> services, LinearLayout listView, Context context, LayoutInflater inflater) {
        //Create a list adapter
        BranchInfoServicesAdapter adapter = new BranchInfoServicesAdapter(context, services, inflater);
        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serviceInfo(services.get(finalI));
                }
            });

            //Hide delete button functions
            ImageButton deleteButton = view.findViewById(R.id.delete_service);
            deleteButton.setVisibility(View.GONE);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void serviceInfo(Service service) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
}
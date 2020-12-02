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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.branch.adapters.ServiceRequestsAdapter;
import com.uottawa.servicenovigrad.branch.ServiceRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BranchServiceRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchServiceRequestsFragment extends Fragment {

    private CollectionReference requestsReference = FirebaseFirestore.getInstance().collection("requests");

    private Context context;
    private LayoutInflater inflater;

    private static final String ARG_BRANCHID = "branchId";

    private String branchId;

    List<ServiceRequest> serviceRequests;
    LinearLayout pendingRequestsList, approvedRequestsList, rejectedRequestsList;

    public BranchServiceRequestsFragment() {
        // Required empty public constructor
    }

    public static BranchServiceRequestsFragment newInstance(String branchId) {
        BranchServiceRequestsFragment fragment = new BranchServiceRequestsFragment();
        Bundle args = new Bundle();

        args.putString(ARG_BRANCHID, branchId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            branchId = getArguments().getString(ARG_BRANCHID);

            serviceRequests = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_branch_service_requests, container, false);

        context = rootView.getContext();
        this.inflater = inflater;

        pendingRequestsList = rootView.findViewById(R.id.branch_service_requests_pending);
        approvedRequestsList = rootView.findViewById(R.id.branch_service_requests_approved);
        rejectedRequestsList = rootView.findViewById(R.id.branch_service_requests_rejected);

        requestsReference
            .whereEqualTo("branch", branchId)
            .whereGreaterThan("scheduledTime", new Date())
            .orderBy("scheduledTime")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ServiceRequestsFragment", "Listen failed.", error);
                    return;
                }

                serviceRequests.clear();

                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        //Get the information of the service
                        String id = doc.getId();
                        String customerId = doc.getString("customer");
                        String serviceId = doc.getString("service");

                        String customerName = doc.getString("customerName");
                        String branchName = doc.getString("branchName");
                        String serviceName = doc.getString("serviceName");

                        ArrayList<String> info = (ArrayList<String>) doc.get("info");

                        Timestamp scheduledTime = doc.getTimestamp("scheduledTime");

                        boolean approved = doc.getBoolean("approved");
                        boolean responded = doc.getBoolean("responded");

                        ServiceRequest request = new ServiceRequest(id, customerId, branchId, serviceId, customerName, branchName, serviceName, info, scheduledTime.toDate(), approved, responded);

                        serviceRequests.add(request);
                    }
                }

                setUpPendingRequestsList(serviceRequests, pendingRequestsList, context, inflater);
                setUpApprovedRequestsList(serviceRequests, approvedRequestsList, context, inflater);
                setUpRejectedRequestsList(serviceRequests, rejectedRequestsList, context, inflater);
            }
        });

        return rootView;
    }


    private void setUpPendingRequestsList(final List<ServiceRequest> requests, LinearLayout listView, Context context, LayoutInflater inflater) {

        final List<ServiceRequest> filteredRequests = new ArrayList<ServiceRequest>();
        for(ServiceRequest r : requests) {
            if(r.isResponded() == false) {
                filteredRequests.add(r);
            }
        }

        pendingRequestsList.removeAllViews();

        ServiceRequestsAdapter adapter = new ServiceRequestsAdapter(context, filteredRequests, inflater);

        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestInfo(filteredRequests.get(finalI));
                }
            });

            ImageButton approveButton = view.findViewById(R.id.approve_request);
            approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveRequest(filteredRequests.get(finalI));
                }
            });

            ImageButton rejectButton = view.findViewById(R.id.reject_request);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectRequest(filteredRequests.get(finalI));
                }
            });

            ImageButton undoButton = view.findViewById(R.id.undo_reject_request);
            undoButton.setVisibility(View.GONE);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void setUpApprovedRequestsList(final List<ServiceRequest> requests, LinearLayout listView, Context context, LayoutInflater inflater) {

        final List<ServiceRequest> filteredRequests = new ArrayList<ServiceRequest>();
        for(ServiceRequest r : requests) {
            if(r.isResponded() == true && r.isApproved() == true) {
                filteredRequests.add(r);
            }
        }

        approvedRequestsList.removeAllViews();

        ServiceRequestsAdapter adapter = new ServiceRequestsAdapter(context, filteredRequests, inflater);

        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestInfo(filteredRequests.get(finalI));
                }
            });

            ImageButton approveButton = view.findViewById(R.id.approve_request);
            approveButton.setVisibility(View.GONE);

            ImageButton rejectButton = view.findViewById(R.id.reject_request);
            rejectButton.setVisibility(View.GONE);

            ImageButton undoButton = view.findViewById(R.id.undo_reject_request);
            undoButton.setVisibility(View.GONE);

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void setUpRejectedRequestsList(final List<ServiceRequest> requests, LinearLayout listView, Context context, LayoutInflater inflater) {

        final List<ServiceRequest> filteredRequests = new ArrayList<ServiceRequest>();
        for(ServiceRequest r : requests) {
            if(r.isResponded() == true && r.isApproved() == false) {
                filteredRequests.add(r);
            }
        }

        rejectedRequestsList.removeAllViews();

        ServiceRequestsAdapter adapter = new ServiceRequestsAdapter(context, filteredRequests, inflater);

        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestInfo(filteredRequests.get(finalI));
                }
            });

            ImageButton approveButton = view.findViewById(R.id.approve_request);
            approveButton.setVisibility(View.GONE);

            ImageButton rejectButton = view.findViewById(R.id.reject_request);
            rejectButton.setVisibility(View.GONE);

            ImageButton undoButton = view.findViewById(R.id.undo_reject_request);
            undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveRequest(filteredRequests.get(finalI));
                }
            });

            //Add the list item to the list view
            listView.addView(view);
        }
    }

    private void requestInfo(ServiceRequest request) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(request.getBranchSideTitle()) //Set the title of the dialog to the service name
                .setMessage(request.getRequestInfo()) //Set the message of the dialog to the service info
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

    private void approveRequest(final ServiceRequest request) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
        .setTitle("Approve Request?") //Set the title of the dialog to the service name
        .setMessage("Are you sure you want to approve this request? \nTHIS ACTION CANNOT BE UNDONE. \nThe customer will be notified immediately.") //Set the message of the dialog to the service info
        .setCancelable(true)
        .setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Create a map with the data to write to cloud firestore
                        Map<String, Object> requestInfo = new HashMap<>();
                        requestInfo.put("responded", true);
                        requestInfo.put("approved", true);
                        //Approve Request
                        requestsReference.document(request.getId()).update(requestInfo);
                        //Update lists
                        setUpPendingRequestsList(serviceRequests, pendingRequestsList, context, inflater);
                        setUpApprovedRequestsList(serviceRequests, approvedRequestsList, context, inflater);
                        setUpRejectedRequestsList(serviceRequests, rejectedRequestsList, context, inflater);

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

    private void rejectRequest(final ServiceRequest request) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
        .setTitle("Request Request?") //Set the title of the dialog to the service name
        .setMessage("Are you sure you want to reject this request? \nThe customer will be notified. \nThis can be undone until the date of the request.") //Set the message of the dialog to the service info
        .setCancelable(true)
        .setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Create a map with the data to write to cloud firestore
                    Map<String, Object> requestInfo = new HashMap<>();
                    requestInfo.put("responded", true);
                    requestInfo.put("approved", false);
                    //Reject Request
                    requestsReference.document(request.getId()).update(requestInfo);;
                    //Update lists
                    setUpPendingRequestsList(serviceRequests, pendingRequestsList, context, inflater);
                    setUpApprovedRequestsList(serviceRequests, approvedRequestsList, context, inflater);
                    setUpRejectedRequestsList(serviceRequests, rejectedRequestsList, context, inflater);

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
}
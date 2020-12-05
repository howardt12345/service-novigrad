package com.uottawa.servicenovigrad.activities.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.admin.adapters.AdminUsersListAdapter;
import com.uottawa.servicenovigrad.user.CustomerAccount;
import com.uottawa.servicenovigrad.user.EmployeeAccount;
import com.uottawa.servicenovigrad.user.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    List<UserAccount> employees, customers;
    LinearLayout employeesList, customersList;
    private FirebaseFirestore firestore;
    private CollectionReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        getSupportActionBar().hide();

        //Set up firestore
        firestore = FirebaseFirestore.getInstance();
        usersReference = firestore.collection("users");
        //Set up the employee and com.uottawa.servicenovigrad.customer lists
        employees = new ArrayList<>();
        customers = new ArrayList<>();
        //Set up the employee and com.uottawa.servicenovigrad.customer list in the layout
        employeesList = findViewById(R.id.employees_list);
        customersList = findViewById(R.id.customers_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Add a new listener to query employee accounts
        usersReference
        .whereEqualTo("role", "employee")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("UsersActivity", "Listen failed.", error);
                    return;
                }
                //Clear the list to prepare for loading of new data
                employees.clear();
                employeesList.removeAllViews();
                //Iterate through the documents read from firestore
                for(QueryDocumentSnapshot doc : value) {
                    //If the document exists
                    if(doc.exists()) {
                        //Get the basic user data from the document
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String uid = doc.getId();
                        //Create an EmployeeAccount Object
                        EmployeeAccount account = new EmployeeAccount(name, email, uid);
                        //If the employee has assigned themselves a branch (Deliverable 3)
                        if(doc.get("branch") != null) {
                            //Set the branch if there is any
                            account.setBranch(doc.getString("branch"));
                        }
                        //Add the account to the list
                        employees.add(account);
                    }
                }
                //Set up the list in the UI
                setUpList(employees, employeesList);
            }
        });
        //Add a new listener to query com.uottawa.servicenovigrad.customer accounts
        usersReference
        .whereEqualTo("role", "customer")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("UsersActivity", "Listen failed.", error);
                    return;
                }
                //Clear the list to prepare for loading of new data
                customers.clear();
                customersList.removeAllViews();
                //Iterate through the documents read from firestore
                for(QueryDocumentSnapshot doc : value) {
                    //If the document exists
                    if(doc.exists()) {
                        //Get the basic user data from the document
                        String name = (String) doc.get("name");
                        String email = (String) doc.get("email");
                        String uid = doc.getId();
                        //Create an CustomerAccount Object
                        CustomerAccount account = new CustomerAccount(name, email, uid);
                        //Add the account to the list
                        customers.add(account);
                    }
                }
                //Set up the list in the UI
                setUpList(customers, customersList);
            }
        });
    }

    /**
     * Sets up the list in the UI
     * @param accounts the list of accounts to set up the list with
     * @param listView the LinearLayout in which the list items will go in
     */
    private void setUpList(final List<UserAccount> accounts, LinearLayout listView) {
        //Create a list adapter
        AdminUsersListAdapter adapter = new AdminUsersListAdapter(AdminUsersActivity.this, accounts);
        for(int i = 0; i < adapter.getCount(); i++) {
            //Get final version of index
            final int finalI = i;
            //Get the list item from the adapter at the index
            View view = adapter.getView(i, null, listView);
            //Open the user info dialog when the list item is clicked
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInfoDialog(accounts.get(finalI));
                }
            });
            //Set delete button functions
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_user);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUserDialog(accounts.get(finalI));
                }
            });
            //Add the list item to the list view
            listView.addView(view);
        }
    }

    /**
     * Opens a dialog displaying the text format of the given user account
     * @param account the user account to display the info for
     */
    private void userInfoDialog(UserAccount account) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminUsersActivity.this);
        alertDialogBuilder
        .setTitle(account.getName()) //Set the title of the dialog to the account name
        .setMessage(account.toString()) //Set the message of the dialog to the account text
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

    /**
     * Prompts the admin if the admin truly wants to delete the given account
     * @param account the account to delete
     */
    private void deleteUserDialog(final UserAccount account) {
        //Create new AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminUsersActivity.this);
        alertDialogBuilder
        .setTitle("Delete user account for " + account.getName() + "?")
        .setMessage("Are you sure you want to delete this user account? Any data associated with this user will be permanently deleted.")
        .setCancelable(true)
        .setPositiveButton(
            "YES",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Delete the account
                    deleteUser(account);
                    dialog.cancel();
                }
            }
        )
        .setNegativeButton(
            "NO",
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
     * Deletes the given user from the database
     * @param account the account to delete
     */
    private void deleteUser(UserAccount account) {
        usersReference.document(account.getUID()).delete();
    }

    /**
     * Back button function
     * @param view the current view
     */
    public void back(View view) {
        //Go back to previous activity
        this.finish();
    }
}
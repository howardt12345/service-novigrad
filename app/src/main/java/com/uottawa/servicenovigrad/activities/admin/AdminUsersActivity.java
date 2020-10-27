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

        firestore = FirebaseFirestore.getInstance();
        usersReference = firestore.collection("users");

        employees = new ArrayList<>();
        customers = new ArrayList<>();

        employeesList = (LinearLayout) findViewById(R.id.employees_list);
        customersList = (LinearLayout) findViewById(R.id.customers_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        usersReference
        .whereEqualTo("role", "employee")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("UsersActivity", "Listen failed.", error);
                    return;
                }

                employees.clear();
                employeesList.removeAllViews();

                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String uid = doc.getId();

                        EmployeeAccount account = new EmployeeAccount(name, email, uid);

                        if(doc.get("branch") != null) {
                            account.setBranch(doc.getString("branch"));
                        }

                        employees.add(account);
                    }
                }

                setUpList(employees, employeesList);
            }
        });

        usersReference
        .whereEqualTo("role", "customer")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("UsersActivity", "Listen failed.", error);
                    return;
                }

                customers.clear();
                customersList.removeAllViews();

                for(QueryDocumentSnapshot doc : value) {
                    if(doc.exists()) {
                        String name = (String) doc.get("name");
                        String email = (String) doc.get("email");
                        String uid = doc.getId();

                        customers.add(new CustomerAccount(name, email, uid));
                    }
                }

                setUpList(customers, customersList);
            }
        });
    }

    private void setUpList(final List<UserAccount> accounts, LinearLayout listView) {

        AdminUsersListAdapter adapter = new AdminUsersListAdapter(AdminUsersActivity.this, accounts);
        for(int i = 0; i < adapter.getCount(); i++) {
            final int finalI = i;

            View view = adapter.getView(i, null, listView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userInfoDialog(accounts.get(finalI));
                }
            });
            //Set delete button
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_user);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUserDialog(accounts.get(finalI));
                }
            });

            listView.addView(view);
        }
    }

    private void userInfoDialog(UserAccount account) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminUsersActivity.this);
        alertDialogBuilder
        .setTitle(account.getName())
        .setMessage(account.toString())
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

    private void deleteUserDialog(final UserAccount account) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminUsersActivity.this);
        alertDialogBuilder
        .setTitle("Delete user?")
        .setMessage("Are you sure you want to delete this user? Any data associated with this user will be permanently deleted.")
        .setCancelable(true)
        .setPositiveButton(
            "YES",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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

    private void deleteUser(UserAccount account) {
        usersReference.document(account.getUID()).delete();
    }

    public void back(View view) {
        this.finish();
    }
}
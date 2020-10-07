package com.uottawa.servicenovigrad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.CurrentUser;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.errors.SignUpError;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Method to open the login activity.
     * @param view The current view.
     */
    public void logInInstead(View view) {
        //Navigate to Login Activity
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * The method that will be called when the sign up button is pressed. Signs user up for the app with the given inputs.
     * @param view The current view.
     */
    public void onSignUpButtonClicked(View view) {
        //stores the editText from sign up page in variables
        final EditText signUpNameEntry = (EditText) findViewById(R.id.signUp_nameEntry);
        final EditText signUpEmailEntry = (EditText) findViewById(R.id.signUp_emailEntry);
        final EditText signUpPasswordEntry = (EditText) findViewById(R.id.signUp_passwordEntry);
        final EditText signUpPasswordConfirm = (EditText) findViewById(R.id.signUp_passwordConfirm);
        //Get values of email and password variables
        final String name = signUpNameEntry.getText().toString();
        final String email = signUpEmailEntry.getText().toString();
        final String password = signUpPasswordEntry.getText().toString();
        final String passwordConfirm = signUpPasswordConfirm.getText().toString();
        //Gets the employee toggle.
        Switch signUp_asEmployee_switch = (Switch) findViewById(R.id.signUp_asEmployee);

        final String role = signUp_asEmployee_switch.isChecked() ? "employee" : "customer";

        //Validates input and gets error message
        final SignUpError signUpError = validateInput(name, email, password, passwordConfirm);

        //If there is an error
        if (signUpError != SignUpError.None) {
            //Show a snackbar with the error message
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.signup_page), errorMessage(signUpError), BaseTransientBottomBar.LENGTH_SHORT);
            //Add close button
            mySnackbar.setAction("CLOSE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Close button does nothing really
                }
            });
            //Clear text when snackbar is closed
            mySnackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    switch (signUpError) {
                        case FieldsEmpty:
                            break;
                        case NameWhiteSpace:
                            //Clears only the name entry
                            signUpNameEntry.getText().clear();
                            break;
                        case InvalidEmail:
                            //Clears only the email entry
                            signUpEmailEntry.getText().clear();
                            break;
                        case PasswordTooShort:
                        case PasswordsNoMatch:
                            //Clears both password entries
                            signUpPasswordEntry.getText().clear();
                            signUpPasswordConfirm.getText().clear();
                            break;
                    }
                }
            });
            //Show snackbar
            mySnackbar.show();
        } else {
            // Create user on Firebase auth
            auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sucessful signup

                        //Create a map with the data to write to cloud firestore
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("email", email);
                        userInfo.put("role", role);

                        //Writes the data to firestore
                        firestore.collection("users")
                        .document(auth.getCurrentUser().getUid())
                        .set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            //Only when firestore succeeds in writing user data to database does the app log the user in.
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Add info to CurrentUser
                                CurrentUser.setInfo(name, email, role, auth.getCurrentUser().getUid());

                                //Navigate to Main Activity when successful
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Show failed error
                                showSnackbar("Failed to add user to database!");
                                //Tries to delete current user so that user can try to create new account again.
                                auth.getCurrentUser().delete();
                            }
                        });
                    } else {
                        //Show failed error
                        showSnackbar("Failed to create user!");
                    }
                }
            });
        }
    }

    /**
     * Shows snackbar with given message. The snackbar has a close button, which does nothing.
     * @param message
     */
    private void showSnackbar(String message) {
        //Create snackbar
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), message, BaseTransientBottomBar.LENGTH_SHORT);
        //Add close button that does nothing
        snackbar.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        //Shows the snackbar
        snackbar.show();
    }

    /**
     * The method that will be called when the sign up as employee switch is toggled.
     * @param view The current view.
     */
    public void onSignUpAsEmployeeToggleClicked(View view) {
        final Switch signUp_asEmployee_switch = (Switch) findViewById(R.id.signUp_asEmployee);
        //If the switch was toggled on
        if(signUp_asEmployee_switch.isChecked()) {
            //Create confirmation AlertDialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
            alertDialogBuilder
                .setTitle("Sign Up as Employee?")
                .setMessage("Are you sure you want to sign up as an employee? Make sure you have the permission to do so.")
                .setCancelable(true)
                .setPositiveButton(
                    "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                            //
                            signUp_asEmployee_switch.toggle();
                        }
                    }
                );
            //Show AlertDialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    /**
     * Validates the inputs of the sign up page
     * @param name the name to validate
     * @param email the email to validate
     * @param password the password to validate
     * @param passwordConfirm the password confirmation
     * @return the SignUpError value for the given inputs.
     */
    private SignUpError validateInput(String name, String email, String password, String passwordConfirm) {
        //Checks if any field is empty
        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            return SignUpError.FieldsEmpty;
        }
        //Checks if name is only composed of whitespaces
        if(name.trim().length() == 0){
            return SignUpError.NameWhiteSpace;
        }
        //Validates Email
        boolean validEmail = Utils.isEmailValid(email);
        if(!validEmail) {
            return SignUpError.InvalidEmail;
        }
        //Checks if passwords match
        if(password.compareTo(passwordConfirm) != 0) {
            return SignUpError.PasswordsNoMatch;
        }
        //Checks if password is long enough
        if(password.length() < 6) {
            return SignUpError.PasswordTooShort;
        }
        //Returns no error if all inputs are valid.
        return SignUpError.None;
    }

    /**
     * Returns a string representation of the sign up error.
     * @param error the sign up error
     * @return the string representation of the sign up error.
     */
    private String errorMessage(SignUpError error) {
        switch(error) {
            case FieldsEmpty:
                return "One or more required fields are empty. ";
            case NameWhiteSpace:
                return "Name contains only whitespaces";
            case InvalidEmail:
                return "Email is invalid.";
            case PasswordTooShort:
                return "Password is too short.";
            case PasswordsNoMatch:
                return "Passwords do not match.";
            default:
                return "";
        }
    }
}
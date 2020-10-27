package com.uottawa.servicenovigrad.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.MainActivity;
import com.uottawa.servicenovigrad.errors.SignUpError;
import com.uottawa.servicenovigrad.user.UserController;
import com.uottawa.servicenovigrad.utils.Function;
import com.uottawa.servicenovigrad.utils.Utils;

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
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.signup_page), signUpError.toString(), BaseTransientBottomBar.LENGTH_SHORT);
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
                        case InvalidName:
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
            //Sign up using UserController
            UserController.getInstance().signUp(name, email, role, password, getCurrentFocus(), new Function() {
                @Override
                public void f(Object... params) {
                    writeToSharedPrefs(params);

                    //Navigate to Main Activity when successful
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    //set the new task and clear flags, so that the user can't go back here
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    private void writeToSharedPrefs(Object... params) {
        Log.d("LOGIN DEBUG", "Writing data to shared preferences...");
        //Writing data to shared preferences after everything has succeeded.
        //Get shared preferences
        SharedPreferences prefs = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        //Get the editor of the shared preferences
        SharedPreferences.Editor editor = prefs.edit();
        //Write login data to shared preferences
        editor.putString(getString(R.string.user_name_key), (String) params[0]);
        editor.putString(getString(R.string.user_email_key), (String) params[1]);
        editor.putString(getString(R.string.user_role_key), (String) params[2]);
        editor.putString(getString(R.string.user_uid_key), (String) params[3]);
        //Apply shared preferences changes
        editor.apply();
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
        if(name.trim().length() == 0 || !name.matches("^[a-zA-Z\\\\s]*$")){
            return SignUpError.InvalidName;
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
}
package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.uottawa.servicenovigrad.utils.Utils;

enum SignUpError {
    None,
    FieldsEmpty,
    InvalidEmail,
    PasswordTooShort,
    PasswordsNoMatch
}

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        Button signUp_login_button = (Button) findViewById(R.id.signUp_login);
        signUp_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Navigate to Login Activity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            }
        });

        Button signUp_button = (Button) findViewById(R.id.signUp_button);
        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores the editText from sign up page in variables
                final EditText signUpNameEntry = (EditText) findViewById(R.id.signUp_emailEntry);
                final EditText signUpEmailEntry = (EditText) findViewById(R.id.signUp_emailEntry);
                final EditText signUpPasswordEntry = (EditText) findViewById(R.id.signUp_passwordEntry);
                final EditText signUpPasswordConfirm = (EditText) findViewById(R.id.signUp_passwordConfirm);
                //Get values of email and password variables
                String name = signUpNameEntry.getText().toString();
                String email = signUpEmailEntry.getText().toString();
                String password = signUpPasswordEntry.getText().toString();
                String passwordConfirm = signUpPasswordConfirm.getText().toString();

                //Validates input and gets error message
                final SignUpError signUpError = validateInput(name, email, password, passwordConfirm);

                //If there is an error
                if(signUpError != SignUpError.None) {
                    //Show a snackbar with the error message
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.signup_page), errorMessage(signUpError), BaseTransientBottomBar.LENGTH_SHORT);
                    //Add close button
                    mySnackbar.setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    //Clear text when snackbar is closed
                    mySnackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                        switch(signUpError) {
                            case FieldsEmpty:
                                break;
                            case InvalidEmail:
                                signUpEmailEntry.getText().clear();
                                break;
                            case PasswordTooShort:
                            case PasswordsNoMatch:
                                signUpPasswordEntry.getText().clear();
                                signUpPasswordConfirm.getText().clear();
                                break;
                        }
                        }
                    });
                    //Show snackbar
                    mySnackbar.show();
                } else {
                    //TODO: Attempt to create an account on Firebase
                    //Navigate to Main Activity when successful
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
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
                return "One or more required fields are empty";
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
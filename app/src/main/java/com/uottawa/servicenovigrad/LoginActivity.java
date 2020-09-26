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

enum LoginError {
    None,
    FieldsEmpty,
    EmailInvalid,
    PasswordTooShort,
}

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        Button login_createNewAccount_button = (Button) findViewById(R.id.login_createNewAccount);
        login_createNewAccount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Navigate to Sign Up Activity
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            }
        });

        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores the editText from login page in variables
                final EditText login_emailEntry = (EditText) findViewById(R.id.login_emailEntry);
                final EditText login_passwordEntry = (EditText) findViewById(R.id.login_passwordEntry);
                //Get values of email and password variables
                String email = (login_emailEntry.getText().toString());
                String password = (login_passwordEntry.getText().toString());


                //Validates input and gets error message
                final LoginError loginError = validateInput(email, password);

                //If there is an error
                if(loginError != LoginError.None) {
                    //Show a snackbar with the error message
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.login_page), errorMessage(loginError), BaseTransientBottomBar.LENGTH_SHORT);
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
                        switch(loginError) {
                            case FieldsEmpty:
                                break;
                            case EmailInvalid:
                                login_emailEntry.getText().clear();
                                break;
                            case PasswordTooShort:
                                login_passwordEntry.getText().clear();
                                break;
                        }
                        }
                    });
                    //Show snackbar
                    mySnackbar.show();
                } else {
                    //TODO: Attempt to Log into Firebase
                    //Navigate to Main Activity when successful
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Validates the inputs of login page
     * @param email the email to validate
     * @param password the password to validate
     * @return the LoginError value for the given inputs.
     */
    private LoginError validateInput(String email, String password) {
        //Checks if any field is empty
        if(email.isEmpty() || password.isEmpty()) {
            return LoginError.FieldsEmpty;
        }
        //Validates Email
        boolean validEmail = Utils.isEmailValid(email);
        if(!validEmail) {
            return LoginError.EmailInvalid;
        }
        //Checks if password is long enough
        if(password.length() < 6) {
            return LoginError.PasswordTooShort;
        }
        //Returns no error message if inputs are valid.
        return LoginError.None;
    }

    /**
     * Returns a string representation of the login error
     * @param error the login error
     * @return the string representation of the login error.
     */
    private String errorMessage(LoginError error) {
        switch(error) {
            case FieldsEmpty:
                return "One or more required fields are empty";
            case EmailInvalid:
                return "Email is invalid.";
            case PasswordTooShort:
                return "Password is too short.";
            default:
                return "";
        }
    }
}
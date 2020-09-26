package com.uottawa.servicenovigrad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.utils.Utils;

import java.util.HashMap;
import java.util.Map;

enum SignUpError {
    None,
    FieldsEmpty,
    InvalidEmail,
    PasswordTooShort,
    PasswordsNoMatch
}

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

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
                final EditText signUpNameEntry = (EditText) findViewById(R.id.signUp_nameEntry);
                final EditText signUpEmailEntry = (EditText) findViewById(R.id.signUp_emailEntry);
                final EditText signUpPasswordEntry = (EditText) findViewById(R.id.signUp_passwordEntry);
                final EditText signUpPasswordConfirm = (EditText) findViewById(R.id.signUp_passwordConfirm);
                //Get values of email and password variables
                final String name = signUpNameEntry.getText().toString();
                final String email = signUpEmailEntry.getText().toString();
                final String password = signUpPasswordEntry.getText().toString();
                final String passwordConfirm = signUpPasswordConfirm.getText().toString();

                final String role = "customer";

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
                        }
                    });
                    //Clear text when snackbar is closed
                    mySnackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            switch (signUpError) {
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
                    // Create user on Firebase
                    auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sucessful signup

                                //Store user info to cloud firestore
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("name", name);
                                userInfo.put("email", email);
                                userInfo.put("role", role);

                                firestore.collection("users").document(auth.getCurrentUser().getUid()).set(userInfo);
                                CurrentUser.addInfo(name, email, role, auth.getCurrentUser().getUid());

                                //Navigate to Main Activity when successful
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                //Show failed error
                                Toast.makeText(SignUpActivity.this, "Failed to sign up.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
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
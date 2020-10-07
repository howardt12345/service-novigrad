package com.uottawa.servicenovigrad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.CurrentUser;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.errors.LoginError;
import com.uottawa.servicenovigrad.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBackPressed() {
        //Prevent user from going back because it could go back to the splash page
    }

    /**
     * Method to open the sign up activity.
     * @param view The current view.
     */
    public void createNewAccount(View view) {
        //Navigate to Sign Up Activity
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * The method that will be called when the login button is pressed. Logs user into the app.
     * @param view The current view.
     */
    public void onLoginButtonPressed(View view) {
        //stores the editText from login page in variables
        final EditText login_emailEntry = (EditText) findViewById(R.id.login_emailEntry);
        final EditText login_passwordEntry = (EditText) findViewById(R.id.login_passwordEntry);
        //Get values of email and password variables
        final String email = (login_emailEntry.getText().toString());
        final String password = (login_passwordEntry.getText().toString());

        //Checks if user is trying to log in as admin
        final boolean admin = email.compareTo("admin") == 0 && password.compareTo("admin") == 0;
        //If logging in as admin
        if(admin) {
            //Stores information in the CurrentUser static object.
            CurrentUser.setInfo("admin", "admin", "admin", "admin");
            //Show message to user to notify that they are logging into the admin account.
            Toast.makeText(LoginActivity.this, "Logging in as admin", Toast.LENGTH_SHORT).show();
            //Navigate to Main Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
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
                //Sign into firebase
                auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Successful login

                            //Retrieve user info from firestore
                            firestore.collection("users")
                            .document(auth.getCurrentUser().getUid()) //Gets the document with the user UID, where the data should be stored.
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //Gets the result of the firestore read
                                        DocumentSnapshot document = task.getResult();
                                        //Gets the name and role from the data
                                        String n = (String) document.getData().get("name");
                                        String r = (String) document.getData().get("role");
                                        //Add info to CurrentUser
                                        CurrentUser.setInfo(n, email, r, auth.getCurrentUser().getUid());

                                        //Navigate to Main Activity when successful
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        //Show failed error
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.login_page), "Failed to get user details from database!", BaseTransientBottomBar.LENGTH_SHORT);
                                        snackbar.setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {}
                                        });
                                        snackbar.addCallback(new Snackbar.Callback() {
                                            @Override
                                            public void onDismissed(Snackbar snackbar, int event) {
                                                return;
                                            }
                                        });
                                        snackbar.show();
                                    }
                                }
                            });
                        } else {
                            //Show failed error
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.signup_page), "Failed to login!", BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar.setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {}
                            });
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    return;
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
            }
        }
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
                return "One or more required fields are empty. ";
            case EmailInvalid:
                return "Email is invalid.";
            case PasswordTooShort:
                return "Password is too short.";
            default:
                return "";
        }
    }
}
package com.uottawa.servicenovigrad.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.activities.MainActivity;
import com.uottawa.servicenovigrad.errors.LoginError;
import com.uottawa.servicenovigrad.user.UserController;
import com.uottawa.servicenovigrad.utils.Function;
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
            //Show message to user to notify that they are logging into the admin account.
            Toast.makeText(LoginActivity.this, "Logging in as admin", Toast.LENGTH_SHORT).show();

            //Sign in as admin
            UserController.getInstance().signInAsAdmin(getCurrentFocus(), new Function() {
                @Override
                public void f(Object... params) {
                    writeToSharedPrefs("admin", "admin", "admin", "admin");

                    //Navigate to Main Activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            //Validates input and gets error message
            final LoginError loginError = validateInput(email, password);

            //If there is an error
            if(loginError != LoginError.None) {
                //Show a snackbar with the error message
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.login_page), loginError.toString(), BaseTransientBottomBar.LENGTH_SHORT);
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
                                //Clears only the email entry
                                login_emailEntry.getText().clear();
                                break;
                            case InvalidAdminLogin:
                            case PasswordTooShort:
                                //Clears only the password entry
                                login_passwordEntry.getText().clear();
                                break;
                        }
                    }
                });
                //Show snackbar
                mySnackbar.show();
            } else {
                //Sign in using UserController
                UserController.getInstance().signIn(email, password, getCurrentFocus(), new Function() {
                    @Override
                    public void f(Object... params) {
                        writeToSharedPrefs(params);

                        //Navigate to Main Activity when successful
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //set the new task and clear flags, so that the user can't go back here
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
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
        if(email.compareTo("admin") == 0) {
            //This should only happen when the admin password is wrong.
            return LoginError.InvalidAdminLogin;
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
}
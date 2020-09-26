package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Button login_button  = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores the editText from login page in variables
                final EditText login_emailEntry = (EditText) findViewById(R.id.login_emailEntry);
                final EditText login_passwordEntry = (EditText) findViewById(R.id.login_passwordEntry);
                //Get values of username and password variables
                String username = (login_emailEntry.getText().toString());
                String password = (login_passwordEntry.getText().toString());

                String errorMessage = "";

                //TODO: Check if input is valid, then sign into Firebase

                if(username.compareTo("admin") == 0) {
                    errorMessage = "no";
                }

                //If there is an error
                if(!errorMessage.isEmpty()) {
                    //Show a snackbar with the error message
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.login_page), errorMessage, BaseTransientBottomBar.LENGTH_SHORT);
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
                            login_emailEntry.getText().clear();
                            login_passwordEntry.getText().clear();
                        }
                    });
                    //Show snackbar
                    mySnackbar.show();
                } else {
                    //Navigate to Main Activity if login is valid
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
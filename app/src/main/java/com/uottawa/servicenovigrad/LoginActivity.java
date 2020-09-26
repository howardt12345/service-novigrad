package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                EditText login_emailEntry = (EditText) findViewById(R.id.login_emailEntry);
                EditText login_passwordEntry = (EditText) findViewById(R.id.login_passwordEntry);
                //Get values of username and password variables
                String username = (login_emailEntry.getText().toString());
                String password = (login_passwordEntry.getText().toString());

                //TODO: Check if input is valid

                //Navigate to Main Activity if login is valid
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
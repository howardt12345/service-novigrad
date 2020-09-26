package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_button  = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stores the editText from login page in variables
                EditText login_usernameEntry = (EditText) findViewById(R.id.login_usernameEntry);
                EditText login_passwordEntry = (EditText) findViewById(R.id.login_passwordEntry);
                //Get values of username and password variables
                String username = (login_usernameEntry.getText().toString());
                String password = (login_passwordEntry.getText().toString());
            }
        });
    }
}
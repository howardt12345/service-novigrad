package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        TextView title = (TextView) findViewById(R.id.successful_login);
        TextView info = (TextView) findViewById(R.id.login_info);

        title.setText("Welcome " + CurrentUser.getName() + "!");
        info.setText("You are logged in as a " + CurrentUser.getRole());

        Button signOut_button = (Button) findViewById(R.id.signOut_button);
        signOut_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO: Sign out of Firebase

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            }
        });
    }
}
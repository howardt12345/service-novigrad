package com.uottawa.servicenovigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CreateNewAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        getSupportActionBar().hide();
    }
}
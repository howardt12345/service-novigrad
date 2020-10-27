package com.uottawa.servicenovigrad.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.uottawa.servicenovigrad.R;

public class AdminUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        getSupportActionBar().hide();
    }

    public void back(View view) {
        this.finish();
    }
}
package com.uottawa.servicenovigrad.activities.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;

public class CustomerSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        getSupportActionBar().hide();

        TextView view = findViewById(R.id.search_text);
        view.setText(getIntent().getSerializableExtra("searchQuery").toString());
    }
}
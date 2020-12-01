package com.uottawa.servicenovigrad.activities.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;

public class CustomerSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        getSupportActionBar().hide();

        SearchView searchView = (SearchView) findViewById(R.id.branch_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        TextView view = findViewById(R.id.search_text);
        view.setText(getIntent().getSerializableExtra("searchQuery").toString());
    }
}
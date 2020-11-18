package com.uottawa.servicenovigrad.activities.employee;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.branch.Branch;
import com.uottawa.servicenovigrad.service.Service;

public class EmployeeEditActivity extends AppCompatActivity {

    Branch branch;
    private boolean newBranch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);
        getSupportActionBar().hide();

        //If there is data passed through to this activity
        if(getIntent().getExtras() != null) {
            //Get the service from the data
            branch = (Branch) getIntent().getSerializableExtra("branch");
        } else {
            //Change title to reflect current function
            TextView title = (TextView) findViewById(R.id.employee_edit_title);
            title.setText("Complete Branch Profile");
            title.setTextSize(24.0f);

            //Hide cancel button
            ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button_employee);
            cancelButton.setVisibility(View.GONE);

            newBranch = true;
        }
    }

    @Override
    public void onBackPressed() {
        //Cancel the edit when back button is pressed
        if(newBranch) {

        } else {
            
        }
    }
}
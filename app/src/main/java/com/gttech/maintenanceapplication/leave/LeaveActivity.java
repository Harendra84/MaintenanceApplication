package com.gttech.maintenanceapplication.leave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.hostel.HostelActivity;

public class LeaveActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        /*Buck Button*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add Button*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveActivity.this, AddLeaveActivity.class);
                startActivity(intent);
            }
        });


    }
}
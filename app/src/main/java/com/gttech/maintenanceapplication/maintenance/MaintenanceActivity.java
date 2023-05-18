package com.gttech.maintenanceapplication.maintenance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.attendance.HostelAttendanceActivity;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;

public class MaintenanceActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceActivity.this, AddMaintenanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
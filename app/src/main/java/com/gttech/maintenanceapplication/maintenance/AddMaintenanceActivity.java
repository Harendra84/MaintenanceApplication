package com.gttech.maintenanceapplication.maintenance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.internship.InternshipActivity;

public class AddMaintenanceActivity extends AppCompatActivity {

    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMaintenanceActivity.this, MaintenanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
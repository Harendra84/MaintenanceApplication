package com.gttech.maintenanceapplication.ambulance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gttech.maintenanceapplication.R;

public class AddAmbulanceActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etLicensePlate;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ambulance);

        etName = findViewById(R.id.et_name);
        etLicensePlate = findViewById(R.id.et_license_plate);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddAmbulanceActivity.this, AmbulanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
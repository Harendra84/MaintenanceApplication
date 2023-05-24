package com.gttech.maintenanceapplication.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gttech.maintenanceapplication.feedback.FeedbackActivity;
import com.gttech.maintenanceapplication.ambulance.AmbulanceActivity;
import com.gttech.maintenanceapplication.maintenance.MaintenanceActivity;
import com.gttech.maintenanceapplication.attendance.HostelAttendanceActivity;
import com.gttech.maintenanceapplication.hostel.HostelActivity;
import com.gttech.maintenanceapplication.internship.InternshipActivity;
import com.gttech.maintenanceapplication.leave.LeaveActivity;
import com.gttech.maintenanceapplication.user.MainActivity;
import com.gttech.maintenanceapplication.mess.MessActivity;
import com.gttech.maintenanceapplication.R;

public class HomeActivity extends AppCompatActivity{

    private TextView tvUsername;
    private Button btnLogout;
    private Button btnHostelAttendance;
    private Button btnMess;
    private Button btnMaintenance;
    private Button btnLeave;
    private Button btnInternship;
    private Button btnAmbulance;
    private Button btnFeedback;
    private Button btnHostel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUsername = findViewById(R.id.tv_username);
        btnLogout = findViewById(R.id.btn_logout);
        btnHostelAttendance = findViewById(R.id.btn_hostel_attendance);
        btnMess = findViewById(R.id.btn_mess);
        btnMaintenance = findViewById(R.id.btn_maintenance);
        btnLeave = findViewById(R.id.btn_leave);
        btnInternship = findViewById(R.id.btn_internship);
        btnAmbulance = findViewById(R.id.btn_ambulance);
        btnFeedback = findViewById(R.id.btn_feedback);
        btnHostel = findViewById(R.id.btn_hostel);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String userId = sharedPreferences.getString("sessionId", "");
        String roleType = sharedPreferences.getString("roleType", "");
        tvUsername.setText("Welcome, " + username + "!");

        /*Click listeners on the logout buttons*/
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear user data from SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnHostelAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HostelAttendanceActivity.class);
                startActivity(intent);
            }
        });

        btnMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MessActivity.class);
                startActivity(intent);
            }
        });

        btnMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MaintenanceActivity.class);
                startActivity(intent);
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LeaveActivity.class);
                startActivity(intent);
            }
        });

        btnInternship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InternshipActivity.class);
                startActivity(intent);
            }
        });

        btnAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AmbulanceActivity.class);
                startActivity(intent);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        btnHostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HostelActivity.class);
                startActivity(intent);
            }
        });
    }
}
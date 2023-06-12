package com.gttech.maintenanceapplication.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
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
    private CardView hostelAttendance, mess, maintenance, leave, internship, ambulance, feedback, hostel, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //tvUsername = findViewById(R.id.tv_username);
        logout = findViewById(R.id.logout);
        hostelAttendance = findViewById(R.id.hostel_attendance);
        maintenance = findViewById(R.id.maintenance);
        internship = findViewById(R.id.internship);
        ambulance = findViewById(R.id.ambulance);
        feedback = findViewById(R.id.feedback);
        leave = findViewById(R.id.leave);
        hostel = findViewById(R.id.hostel);
        mess = findViewById(R.id.mess);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");
        //tvUsername.setText("Welcome, " + username + "!");

        /*Click listeners on the logout buttons*/
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear user data from SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                showSnackbar("Logout successful", Toast.LENGTH_LONG);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        hostelAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HostelAttendanceActivity.class);
                startActivity(intent);
            }
        });

        maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MaintenanceActivity.class);
                startActivity(intent);
            }
        });

        internship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InternshipActivity.class);
                startActivity(intent);
            }
        });

        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AmbulanceActivity.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LeaveActivity.class);
                startActivity(intent);
            }
        });

        hostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HostelActivity.class);
                startActivity(intent);
            }
        });

        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MessActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showSnackbar(String message, int duration) {

        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, duration);

        // Set text color
        snackbar.setActionTextColor(Color.WHITE);
        // Set background color
        snackbar.getView().setBackgroundColor(Color.DKGRAY);
        // Set duration
        snackbar.setDuration(duration);
        // Set the Snackbar to be displayed at the top
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
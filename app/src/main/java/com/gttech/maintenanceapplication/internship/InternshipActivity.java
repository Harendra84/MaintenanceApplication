package com.gttech.maintenanceapplication.internship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.ambulance.AmbulanceActivity;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.mess.MessActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InternshipActivity extends AppCompatActivity implements InternshipAdapter.InternshipItemClickListener{

    private RecyclerView rvInternship;
    private List<Internship> internshipList;
    private InternshipAdapter internshipAdapter;
    private Toolbar toolbarBack;
    private FloatingActionButton floatingAdd;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship);

        progressBar = findViewById(R.id.progress_bar);
        // Initialize the handler
        handler = new Handler();

        toolbarBack = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbarBack);
        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingAdd = findViewById(R.id.floating_add);

        rvInternship = findViewById(R.id.rv_internship);
        rvInternship.setLayoutManager(new LinearLayoutManager(this));
        internshipList = new ArrayList<>();
        internshipAdapter = new InternshipAdapter(internshipList, this);
        rvInternship.setAdapter(internshipAdapter);

        // Make API call to fetch inter data
        fetchInternshipData();

        /*Add internship button click listener*/
        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddInternshipDialog();
            }
        });
    }

    /*List internship data*/
    private void fetchInternshipData() {

        showLoader(); // Show loader before making the API call

        // Set a timeout of 10 seconds for hiding the loader
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader(); // Hide loader after timeout
            }
        }, 10000);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.43.43:9090/internship/listOfInternships";

        RequestBody requestBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoader(); // Hide loader in case of API call failure
                        Toast.makeText(InternshipActivity.this, "Failed to fetch internship data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                hideLoader(); // Hide loader in case of API call failure
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Integer internshipId = jsonObject.getInt("internshipId");
                            String name = jsonObject.getString("name");
                            String registrationNumber = jsonObject.getString("registrationNumber");
                            String purpose = jsonObject.getString("purpose");
                            String phoneNo = jsonObject.getString("phoneNo");
                            String emailId = jsonObject.getString("emailId");
                            Integer days = jsonObject.getInt("noOfDays");

                            Internship internship = new Internship(internshipId, name, registrationNumber, purpose, phoneNo, emailId, days);
                            internshipList.add(internship);
                        }
                        // Update the RecyclerView with the new internship data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                internshipAdapter.notifyDataSetChanged();
                                hideLoader(); // Hide loader in case of API call failure
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoader(); // Hide loader in case of API call failure
                                Toast.makeText(InternshipActivity.this, "Failed to parse internship data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader(); // Hide loader in case of API call failure
                            Toast.makeText(InternshipActivity.this, "Failed to fetch internship data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /*Show loader*/
    private void showLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                rvInternship.setVisibility(View.GONE);
            }
        });
    }


    /*Show hide*/
    private void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                rvInternship.setVisibility(View.VISIBLE);
            }
        });
    }

    /*Internship alert dialog */
    private void showAddInternshipDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Internship");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_internship, null);
        final EditText etInternshipName = view.findViewById(R.id.et_name);
        final EditText etRegistrationNumber = view.findViewById(R.id.et_registration_number);
        final EditText etPurpose = view.findViewById(R.id.et_purpose);
        final EditText etEmailId = view.findViewById(R.id.et_email_id);
        final EditText etPhoneNumber = view.findViewById(R.id.et_phone_no);
        final EditText etDays = view.findViewById(R.id.et_no_of_days);

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etInternshipName.getText().toString();
                String registration = etRegistrationNumber.getText().toString();
                String emailId = etEmailId.getText().toString();
                String phoneNo = etPhoneNumber.getText().toString();
                String purpose = etPurpose.getText().toString();
                String days = etDays.getText().toString();

                if (!name.isEmpty() || !registration.isEmpty() || !phoneNo.isEmpty() || !purpose.isEmpty() || !emailId.isEmpty() || !days.isEmpty()) {
                    // Add the new mess item to the list
                    addInternship(name, registration, phoneNo, purpose, emailId, days);
                } else {
                    Toast.makeText(InternshipActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*Internship add method*/
    private void addInternship(String name, String registrationNumber, String phoneNo, String purpose, String emailId, String days) {

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");
        SharedPreferences hostel = getSharedPreferences("HostelData", MODE_PRIVATE);
        int hostelId = hostel.getInt("hostel_id", 0);
        SharedPreferences mess = getSharedPreferences("MessData", MODE_PRIVATE);
        int messId = mess.getInt("messId", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/internship/addOrEditInternship";

        RequestBody requestBody = new FormBody.Builder()
                .add("internshipId", "0")
                .add("name", name)
                .add("registrationNo", registrationNumber)
                .add("emailId", emailId)
                .add("phoneNo", phoneNo)
                .add("purpose", purpose)
                //.add("noOfDays", noOfDays)
                .add("hostelId", String.valueOf(hostelId))
                .add("messId", String.valueOf(messId))
                .add("userId", userId)
                .add("roleType", roleType)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InternshipActivity.this, "Failure to adding internship", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchInternshipData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InternshipActivity.this, "Internship added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InternshipActivity.this, "Failed to adding internship", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Implement adapter class method for update*/
    @Override
    public void onInternshipItemClick(Internship internship) {
        showUpdateInternshipDialog(internship);
    }

    /*Show Update Internship Dialog for update*/
    private void showUpdateInternshipDialog(Internship internship) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Internship");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_internship, null);
        final EditText etStudentName = view.findViewById(R.id.et_name);
        final EditText etRegistrationNumber = view.findViewById(R.id.et_registration_number);
        final EditText etEmailId = view.findViewById(R.id.et_email_id);
        final EditText etPhoneNo = view.findViewById(R.id.et_phone_no);
        final EditText etPurpose = view.findViewById(R.id.et_purpose);
        final EditText etDays = view.findViewById(R.id.et_no_of_days);

        etStudentName.setText(internship.getName());
        etRegistrationNumber.setText(internship.getRegistrationNumber());
        etEmailId.setText(internship.getEmailId());
        etPhoneNo.setText(internship.getPhoneNo());
        etPurpose.setText(internship.getPurpose());
        etDays.setText(internship.getNoOfDays());

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String studentName = etStudentName.getText().toString().trim();
                String registrationNumber = etRegistrationNumber.getText().toString().trim();
                String emailId = etEmailId.getText().toString().trim();
                String phoneNo = etPhoneNo.getText().toString().trim();
                String purpose = etPurpose.getText().toString().trim();
                String days = etDays.getText().toString().trim();

                if (!studentName.isEmpty() || !registrationNumber.isEmpty() || !emailId.isEmpty() || !phoneNo.isEmpty() || !purpose.isEmpty() || !days.isEmpty()) {
                    // Make API call to update the mess
                    updateInternship(internship, studentName, registrationNumber, emailId, phoneNo, purpose, days);
                } else {
                    Toast.makeText(InternshipActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*Update Method for Internship*/
    private void updateInternship(Internship internship, String studentName, String registrationNumber, String emailId, String phoneNo, String purpose, String days) {
    }
}
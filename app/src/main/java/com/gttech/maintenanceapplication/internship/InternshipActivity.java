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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class InternshipActivity extends AppCompatActivity {

    private RecyclerView rvInternship;
    private List<Internship> internshipList;
    private InternshipAdapter internshipAdapter;
    private Toolbar toolbar;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship);

        rvInternship = findViewById(R.id.rv_internship);
        toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd = findViewById(R.id.btn_add);

        rvInternship.setLayoutManager(new LinearLayoutManager(this));
        internshipList = new ArrayList<>();
        internshipAdapter = new InternshipAdapter(internshipList);
        rvInternship.setAdapter(internshipAdapter);

        // Make API call to fetch inter data
        fetchInternshipData();


        /*Add internship button click listener*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddInternshipDialog();
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

    /*List internship data*/
    private void fetchInternshipData() {

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/internship/listOfInternships";

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
                        Toast.makeText(InternshipActivity.this, "Failed to fetch internship data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String internshipId = jsonObject.getString("internshipId");
                            String name = jsonObject.getString("name");
                            String registrationNumber = jsonObject.getString("registrationNumber");
                            String purpose = jsonObject.getString("purpose");
                            String phoneNo = jsonObject.getString("phoneNo");
                            String emailId = jsonObject.getString("emailId");
                            //Integer noOfDays = jsonObject.getInt("noOfDays");

                            Internship internship = new Internship(internshipId, name, registrationNumber, purpose, phoneNo, emailId);
                            internshipList.add(internship);
                        }
                        // Update the RecyclerView with the new internship data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                internshipAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InternshipActivity.this, "Failed to parse internship data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InternshipActivity.this, "Failed to fetch internship data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
        //final EditText etNoOfDays = view.findViewById(R.id.et_no_of_days);

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etInternshipName.getText().toString();
                String registration = etRegistrationNumber.getText().toString();
                String emailId = etEmailId.getText().toString();
                String phoneNo = etPhoneNumber.getText().toString();
                String purpose = etPurpose.getText().toString();
                //String noOfDays = etNoOfDays.getText().toString();

                if (!name.isEmpty() || !registration.isEmpty() || !phoneNo.isEmpty() || !purpose.isEmpty() || !emailId.isEmpty()) {
                    // Add the new mess item to the list
                    addInternship(name, registration, phoneNo, purpose, emailId);
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
    private void addInternship(String name, String registrationNumber, String phoneNo, String purpose, String emailId) {

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
}
package com.gttech.maintenanceapplication.maintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.ambulance.AmbulanceActivity;
import com.gttech.maintenanceapplication.attendance.HostelAttendanceActivity;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.feedback.Feedback;
import com.gttech.maintenanceapplication.feedback.FeedbackActivity;
import com.gttech.maintenanceapplication.mess.Mess;
import com.gttech.maintenanceapplication.mess.MessActivity;
import com.gttech.maintenanceapplication.mess.MessAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MaintenanceActivity extends AppCompatActivity implements MaintenanceAdapter.MaintenanceItemClickListener{

    private RecyclerView rvMaintenance;
    private MaintenanceAdapter maintenanceAdapter;
    private List<Maintenance> maintenanceList;
    private Toolbar toolbarBack;
    private FloatingActionButton floatingAdd;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        progressBar = findViewById(R.id.progress_bar);
        // Initialize the handler
        handler = new Handler();

        toolbarBack = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbarBack);
        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingAdd = findViewById(R.id.floating_add);

        rvMaintenance = findViewById(R.id.rv_maintenance);
        rvMaintenance.setLayoutManager(new LinearLayoutManager(this));
        maintenanceList = new ArrayList<>();
        maintenanceAdapter = new MaintenanceAdapter(maintenanceList,this);
        rvMaintenance.setAdapter(maintenanceAdapter);

        // Make API call to fetch mess data
        fetchMaintenanceData();

        /*Add maintenance button click listener*/
        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMaintenanceDialog();
            }
        });
    }

    /*List maintenance data*/
    private void fetchMaintenanceData() {

        showLoader(); // Show loader before making the API call

        // Set a timeout of 10 seconds for hiding the loader
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader(); // Hide loader after timeout
            }
        }, 10000);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.43.43:9090/maintenance/listOfMaintenanceData";

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
                        Toast.makeText(MaintenanceActivity.this, "Failed to fetch maintenance data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                hideLoader(); // Hide loader in case of API call failure
                if (response.isSuccessful()){
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing mess list
                        maintenanceList.clear();

                        // Parse the JSON data and add it to the maintenance list
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String maintenanceId = jsonObject.getString("maintenanceId");
                            String maintenanceType = jsonObject.getString("maintenanceType");
                            String maintenanceStatus = jsonObject.getString("maintenanceStatus");
                            String maintenanceDescription = jsonObject.getString("description");
                            String date = jsonObject.getString("date");
                            Maintenance maintenance = new Maintenance(maintenanceId,maintenanceType,maintenanceStatus,maintenanceDescription,date);
                            maintenanceList.add(maintenance);
                        }

                        // Update the RecyclerView with the new mess data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                maintenanceAdapter.notifyDataSetChanged();
                                hideLoader(); // Hide loader in case of API call failure
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoader(); // Hide loader in case of API call failure
                                Toast.makeText(MaintenanceActivity.this, "Failed to parse maintenanceId data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader(); // Hide loader in case of API call failure
                            Toast.makeText(MaintenanceActivity.this, "Failed to fetch maintenanceId data", Toast.LENGTH_SHORT).show();
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
                rvMaintenance.setVisibility(View.GONE);
            }
        });
    }

    /*Show hide*/
    private void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                rvMaintenance.setVisibility(View.VISIBLE);
            }
        });
    }

    /*Maintenance alert dialog */
    private void showAddMaintenanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Maintenance");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_maintenance, null);
        final EditText etMaintenanceType = view.findViewById(R.id.et_maintenance_type);
        final EditText etDescription = view.findViewById(R.id.et_description);
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String MaintenanceType = etMaintenanceType.getText().toString();
                String Description = etDescription.getText().toString();

                if (!MaintenanceType.isEmpty() || !Description.isEmpty()){
                    // Add the new mess item to the list
                    addMaintenance(MaintenanceType, Description);
                }else{
                    Toast.makeText(MaintenanceActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Add maintenance method*/
    private void addMaintenance(String maintenanceType, String description) {

        // Retrieve user data from SharedPreferences
        SharedPreferences hostel = getSharedPreferences("HostelData", MODE_PRIVATE);
        int hostelId = hostel.getInt("hostel_id", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String username = sharedPreferences.getString("username", "");
        String roleType = sharedPreferences.getString("roleType", "");

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/maintenance/addOrEditMaintenanceDetails";

        RequestBody requestBody = new FormBody.Builder()
                .add("maintenanceId", "0")
                .add("maintenanceType", maintenanceType)
                .add("description", description)
                .add("hostelName", String.valueOf(hostelId))
                .add("userName", username)
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
                        Toast.makeText(MaintenanceActivity.this, "Failure to adding maintenance", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    fetchMaintenanceData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MaintenanceActivity.this, "Maintenance added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MaintenanceActivity.this, "Failed to adding maintenance", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Implement adapter class method for update*/
    @Override
    public void onMaintenanceItemClick(Maintenance maintenance) {
        showUpdateMaintenanceDialog(maintenance);
    }

    /*Show Update Maintenance Dialog for update*/
    private void showUpdateMaintenanceDialog(Maintenance maintenance) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Maintenance");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_maintenance, null);
        final EditText etMaintenanceType = view.findViewById(R.id.et_maintenance_type);
        final EditText etDescription = view.findViewById(R.id.et_description);
        etMaintenanceType.setText(maintenance.getMaintenanceType());
        etDescription.setText((maintenance.getDescription()));

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String maintenanceType = etMaintenanceType.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if (!maintenanceType.isEmpty() || !description.isEmpty()) {
                    // Make API call to update the mess
                    updateMaintenance(maintenance, maintenanceType, description);
                } else {
                    Toast.makeText(MaintenanceActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Update Method for Maintenance*/
    private void updateMaintenance(Maintenance maintenance, String maintenanceType, String description) {
    }

}
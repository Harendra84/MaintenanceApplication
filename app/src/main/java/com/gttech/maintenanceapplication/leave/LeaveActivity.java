package com.gttech.maintenanceapplication.leave;

import static android.content.ContentValues.TAG;

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
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.hostel.HostelActivity;
import com.gttech.maintenanceapplication.mess.Mess;
import com.gttech.maintenanceapplication.mess.MessActivity;
import com.gttech.maintenanceapplication.mess.MessAdapter;

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

public class LeaveActivity extends AppCompatActivity implements LeaveAdapter.LeaveItemClickListener{

    private RecyclerView rvLeave;
    private LeaveAdapter leaveAdapter;
    private List<Leave> leaveList;
    private Toolbar toolbarBack;
    private FloatingActionButton floatingAdd;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        progressBar = findViewById(R.id.progress_bar);
        // Initialize the handler
        handler = new Handler();

        toolbarBack = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbarBack);
        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingAdd = findViewById(R.id.floating_add);

        rvLeave = findViewById(R.id.rv_leave);
        rvLeave.setLayoutManager(new LinearLayoutManager(this));
        leaveList = new ArrayList<>();
        leaveAdapter = new LeaveAdapter(leaveList, this);
        rvLeave.setAdapter(leaveAdapter);

        // Make API call to fetch mess data
        fetchLeaveData();

        /*Add internship*/
        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLeaveDialog();
            }
        });
    }

    /*List leave data*/
    private void fetchLeaveData() {

        showLoader(); // Show loader before making the API call

        // Set a timeout of 10 seconds for hiding the loader
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader(); // Hide loader after timeout
            }
        }, 10000);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/leave/listOfLeavesByStudent";

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");

        RequestBody requestBody = new FormBody.Builder()
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
                        hideLoader(); // Hide loader in case of API call failure
                        Toast.makeText(LeaveActivity.this, "Failed to fetch leave data", Toast.LENGTH_SHORT).show();
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

                        // Clear the existing mess list
                        leaveList.clear();

                        // Parse the JSON data and add it to the mess list
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Integer leaveId = jsonObject.getInt("leaveId");
                            String leaveType = jsonObject.getString("leaveType");
                            String status = jsonObject.getString("status");
                            String reason = jsonObject.getString("reason");
                            String parentName = jsonObject.getString("parentName");
                            String parentNumber = jsonObject.getString("parentNumber");
                            Leave leave = new Leave(leaveId, leaveType, reason, status, parentName, parentNumber);
                            leaveList.add(leave);
                        }

                        // Update the RecyclerView with the new mess data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                leaveAdapter.notifyDataSetChanged();
                                hideLoader(); // Hide loader in case of API call failure
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoader(); // Hide loader in case of API call failure
                                Toast.makeText(LeaveActivity.this, "Failed to parse leave data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader(); // Hide loader in case of API call failure
                            Toast.makeText(LeaveActivity.this, "Failed to fetch leave data", Toast.LENGTH_SHORT).show();
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
                rvLeave.setVisibility(View.GONE);
            }
        });
    }

    /*Show hide*/
    private void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                rvLeave.setVisibility(View.VISIBLE);
            }
        });
    }

    /*Leave alert dialog */
    private void showAddLeaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Leave");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_leave, null);
        final EditText etLeaveType = view.findViewById(R.id.et_leave_type_id);
        final EditText etLeaveReason = view.findViewById(R.id.et_reason);
        final EditText etParentName = view.findViewById(R.id.et_parent_name);
        final EditText etParentNumber = view.findViewById(R.id.et_parent_number);
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String leaveType = etLeaveType.getText().toString();
                String leaveReason = etLeaveReason.getText().toString();
                String parentName = etParentName.getText().toString();
                String parentNumber = etParentNumber.getText().toString();

                if (!leaveType.isEmpty() || !leaveReason.isEmpty() || !parentName.isEmpty() || !parentNumber.isEmpty()) {
                    // Add the new mess item to the list
                    addLeave(leaveType, leaveReason, parentName, parentNumber);
                } else {
                    Toast.makeText(LeaveActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Add leave method*/
    private void addLeave(String leaveType, String leaveReason, String parentName, String parentNumber) {

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");
        SharedPreferences hostel = getSharedPreferences("HostelData", MODE_PRIVATE);
        int hostelId = hostel.getInt("hostel_id", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/leave/addOrEditLeave";

        RequestBody requestBody = new FormBody.Builder()
                .add("leaveId", "0")
                .add("leaveTypeId", leaveType)
                .add("reason", leaveReason)
                .add("parentName", parentName)
                .add("parentPhoneNo", parentNumber)
                .add("hostelId", String.valueOf(hostelId))
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
                        Toast.makeText(LeaveActivity.this, "Failure to adding leave", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchLeaveData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LeaveActivity.this, "Leave added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LeaveActivity.this, "Failed to adding leave", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Implement adapter class method for update*/
    @Override
    public void onLeaveItemClick(Leave leave) {
        showUpdateLeaveDialog(leave);
    }

    /*Show Update Leave Dialog for update*/
    private void showUpdateLeaveDialog(Leave leave) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Leave");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_leave, null);
        final EditText etLeaveType = view.findViewById(R.id.et_leave_type_id);
        final EditText etLeaveReason = view.findViewById(R.id.et_reason);
        final EditText etParentName = view.findViewById(R.id.et_parent_name);
        final EditText etParentNumber = view.findViewById(R.id.et_parent_number);

        etLeaveType.setText(leave.getLeaveType());
        etLeaveReason.setText(leave.getReason());
        etParentName.setText(leave.getParentName());
        etParentNumber.setText(leave.getParentNumber());

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String leaveType = etLeaveType.getText().toString().trim();
                String leaveReason = etLeaveReason.getText().toString().trim();
                String parentName = etParentName.getText().toString().trim();
                String parentNumber= etParentNumber.getText().toString().trim();

                if (!leaveType.isEmpty() || !leaveReason.isEmpty() || !parentName.isEmpty() || !parentNumber.isEmpty()) {
                    // Make API call to update the mess
                    updateLeave(leave, leaveType, leaveReason, parentName, parentNumber);
                } else {
                    Toast.makeText(LeaveActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Update Method for Leave*/
    private void updateLeave(Leave leave, String leaveType, String leaveReason, String parentName, String parentNumber) {

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");
        SharedPreferences hostel = getSharedPreferences("HostelData", MODE_PRIVATE);
        int hostelId = hostel.getInt("hostel_id", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/leave/addOrEditLeave";

        RequestBody requestBody = new FormBody.Builder()
                .add("leaveId", "0")
                .add("leaveTypeId", leaveType)
                .add("reason", leaveReason)
                .add("parentName", parentName)
                .add("parentPhoneNo", parentNumber)
                .add("hostelId", String.valueOf(hostelId))
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
                        Toast.makeText(LeaveActivity.this, "Failure to adding leave", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchLeaveData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LeaveActivity.this, "Leave added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LeaveActivity.this, "Failed to adding leave", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
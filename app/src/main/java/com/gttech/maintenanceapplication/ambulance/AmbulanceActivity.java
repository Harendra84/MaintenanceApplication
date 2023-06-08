package com.gttech.maintenanceapplication.ambulance;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.hostel.Hostel;
import com.gttech.maintenanceapplication.hostel.HostelActivity;
import com.gttech.maintenanceapplication.internship.InternshipActivity;
import com.gttech.maintenanceapplication.mess.MessActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AmbulanceActivity extends AppCompatActivity {

    private RecyclerView rvAmbulance;
    private List<Ambulance> ambulanceList;
    private AmbulanceAdapter ambulanceAdapter;
    private Toolbar toolbar;
    private Button btnAdd;
    private Button btnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);

        btnAdd = findViewById(R.id.btn_add);
        //btnStatus =  findViewById(R.id.btn_status);
        toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvAmbulance = findViewById(R.id.rv_ambulance);
        rvAmbulance.setLayoutManager(new LinearLayoutManager(this));
        rvAmbulance.setScrollbarFadingEnabled(false);
        rvAmbulance.setVerticalScrollBarEnabled(true);

        ambulanceList = new ArrayList<>();
        ambulanceAdapter = new AmbulanceAdapter(ambulanceList);
        rvAmbulance.setAdapter(ambulanceAdapter);

        // Make API call to fetch ambulance data
        fetchAmbulanceData();

        /*Add ambulance button click listener*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAmbulanceDialog();
            }
        });

        /*Change status of ambulance*//*
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });*/
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

    /*List ambulance data*/
    private void fetchAmbulanceData() {

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/ambulance/listAllAmbulances";

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
                        Toast.makeText(AmbulanceActivity.this, "Failed to fetch ambulance data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    int ambulanceId = 0;
                    String ambulanceName="";
                    String ambulanceStatus="";
                    String licensePlate="";
                    String lastMaintenanceDate="";
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing ambulance list
                        ambulanceList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ambulanceId = jsonObject.getInt("ambulance_id");
                            ambulanceName = jsonObject.getString("ambulanceName");
                            ambulanceStatus = jsonObject.getString("ambulanceStatus");
                            licensePlate = jsonObject.getString("licensePlate");
                            lastMaintenanceDate = jsonObject.getString("lastMaintenanceDate");
                            Ambulance ambulance = new Ambulance(ambulanceId, ambulanceName, ambulanceStatus, licensePlate, lastMaintenanceDate);
                            ambulanceList.add(ambulance);
                        }
                        // Update the RecyclerView with the new ambulance data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ambulanceAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AmbulanceActivity.this, "Failed to parse ambulance data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // Save user details in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("AmbulanceData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("ambulanceId", ambulanceId);
                    editor.apply();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AmbulanceActivity.this, "Failed to fetch ambulance data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Ambulance alert dialog */
    private void showAddAmbulanceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Ambulance");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ambulance, null);
        final EditText etAmbulanceName = view.findViewById(R.id.et_ambulance_name);
        final EditText etAmbulancePlate = view.findViewById(R.id.et_ambulance_plate);
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ambulanceName = etAmbulanceName.getText().toString();
                String ambulancePlate = etAmbulancePlate.getText().toString();

                if (!ambulanceName.isEmpty() || !ambulancePlate.isEmpty()) {
                    // Add the new mess item to the list
                    addAmbulance(ambulanceName, ambulancePlate);
                } else {
                    Toast.makeText(AmbulanceActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Ambulance add method*/
    private void addAmbulance(String ambulanceName, String licensePlate) {

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");

        OkHttpClient client = new OkHttpClient();

        String url = "http://192.168.29.43:9090/ambulance/add";


        // Create JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("ambulanceName", ambulanceName);
            requestBody.put("licensePlate", licensePlate);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(AmbulanceActivity.this, "Failed to create request body", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestJsonBody = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        // Add userId and roleType as a query parameter in the URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("userId", userId);
        urlBuilder.addQueryParameter("roleType", roleType);
        String updateUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(updateUrl)
                .post(requestJsonBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AmbulanceActivity.this, "Failure to adding ambulance", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchAmbulanceData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AmbulanceActivity.this, "Ambulance added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AmbulanceActivity.this, "Failed to adding ambulance", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void changeStatus() {

        // Retrieve user data from SharedPreferences
        SharedPreferences user = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences ambulanceData = getSharedPreferences("AmbulanceData", MODE_PRIVATE);

        String userId = user.getString("userId", "");
        String roleType = user.getString("roleType", "");
        int ambulance_id = ambulanceData.getInt("ambulanceId", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/ambulance/changeStatus";

        RequestBody requestBody = new FormBody.Builder()
                .add("ambulance_id", String.valueOf(ambulance_id))
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
                        Toast.makeText(AmbulanceActivity.this, "Failed to fetch ambulance data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing ambulance list
                        ambulanceList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int ambulanceId = jsonObject.getInt("ambulance_id");
                            String ambulanceName = jsonObject.getString("ambulanceName");
                            String ambulanceStatus = jsonObject.getString("ambulanceStatus");
                            String licensePlate = jsonObject.getString("licensePlate");
                            String lastMaintenanceDate = jsonObject.getString("lastMaintenanceDate");
                            Ambulance ambulance = new Ambulance(ambulanceId, ambulanceName, ambulanceStatus, licensePlate,lastMaintenanceDate);
                            ambulanceList.add(ambulance);
                        }
                        // Update the RecyclerView with the new ambulance data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ambulanceAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AmbulanceActivity.this, "Failed to parse ambulance data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    fetchAmbulanceData();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AmbulanceActivity.this, "Failed to fetch ambulance data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
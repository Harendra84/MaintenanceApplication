package com.gttech.maintenanceapplication.maintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.ambulance.AmbulanceActivity;
import com.gttech.maintenanceapplication.attendance.HostelAttendanceActivity;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
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

public class MaintenanceActivity extends AppCompatActivity {

    private RecyclerView rvMaintenance;
    private MaintenanceAdapter maintenanceAdapter;
    private List<Maintenance> maintenanceList;
    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        rvMaintenance = findViewById(R.id.rv_maintenance);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        rvMaintenance.setLayoutManager(new LinearLayoutManager(this));
        maintenanceList = new ArrayList<>();
        maintenanceAdapter = new MaintenanceAdapter(maintenanceList);
        rvMaintenance.setAdapter(maintenanceAdapter);

        // Make API call to fetch mess data
        fetchMaintenanceData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add maintenance button click listener*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMaintenanceDialog();
            }
        });
    }

    /*List maintenance data*/
    private void fetchMaintenanceData() {

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/maintenance/listOfMaintenanceData";

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
                        Toast.makeText(MaintenanceActivity.this, "Failed to fetch maintenance data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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
                            Maintenance maintenance = new Maintenance(maintenanceId,maintenanceType,maintenanceStatus,maintenanceDescription);
                            maintenanceList.add(maintenance);
                        }

                        // Update the RecyclerView with the new mess data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                maintenanceAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MaintenanceActivity.this, "Failed to parse maintenanceId data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MaintenanceActivity.this, "Failed to fetch maintenanceId data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/maintenance/addOrEditMaintenanceDetails";


        // Create JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("maintenanceType", maintenanceType);
            requestBody.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MaintenanceActivity.this, "Failed to create request body", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestJsonBody = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        // Add userId and roleType as a query parameter in the URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("userId",userId);
        urlBuilder.addQueryParameter("roleType",roleType);
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
                            Toast.makeText(MaintenanceActivity.this, "maintenance added successfully", Toast.LENGTH_SHORT).show();
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
}
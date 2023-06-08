package com.gttech.maintenanceapplication.hostel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HostelActivity extends AppCompatActivity {


    private RecyclerView rvHostel;
    private List<Hostel> hostelList;
    private HostelAdapter hostelAdapter;
    private Toolbar toolbar;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel);

        rvHostel = findViewById(R.id.rv_hostel);
        toolbar = findViewById(R.id.toolbars);
        toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvHostel.setLayoutManager(new LinearLayoutManager(this));
        hostelList = new ArrayList<>();
        hostelAdapter = new HostelAdapter(hostelList);
        rvHostel.setAdapter(hostelAdapter);

        // Make API call to fetch mess data
        fetchMessData();

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

    /*List hostel data*/
    private void fetchMessData() {

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/hostel/getAllHostels";

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
                        Toast.makeText(HostelActivity.this, "Failed to fetch hostel data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    int hostelId = 0;
                    String hostelName ="";
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing mess list
                        hostelList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            hostelId = jsonObject.getInt("hostel_id");
                            hostelName = jsonObject.getString("hostelName");
                            Hostel hostel = new Hostel(hostelId,hostelName);
                            hostelList.add(hostel);
                        }

                        // Update the RecyclerView with the new mess data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hostelAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HostelActivity.this, "Failed to parse hostel data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // Save feed id details in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("HostelData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("hostel_id", hostelId);
                    editor.apply();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HostelActivity.this, "Failed to featch hostel data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Dailog box*/
    private void showAddHostelDialog() {
    }
}
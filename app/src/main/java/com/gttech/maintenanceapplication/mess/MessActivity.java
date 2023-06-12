package com.gttech.maintenanceapplication.mess;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

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
import android.util.Log;
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
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.internship.InternshipActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessActivity extends AppCompatActivity {

    private RecyclerView rvMess;
    private MessAdapter messAdapter;
    private List<Mess> messList;
    private Toolbar toolbarBack;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        progressBar = findViewById(R.id.progress_bar);
        // Initialize the handler
        handler = new Handler();

        toolbarBack = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbarBack);
        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvMess = findViewById(R.id.rv_mess);
        rvMess.setLayoutManager(new LinearLayoutManager(this));
        messList = new ArrayList<>();
        messAdapter = new MessAdapter(messList);
        rvMess.setAdapter(messAdapter);

        // Make API call to fetch mess data
        fetchMessData();
    }

    /*List mess data*/
    private void fetchMessData() {

        showLoader(); // Show loader before making the API call

        // Set a timeout of 10 seconds for hiding the loader
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader(); // Hide loader after timeout
            }
        }, 10000);


        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.43.43:9090/mess/listOfAllMess";

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
                        Toast.makeText(MessActivity.this, "Failed to fetch mess data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                hideLoader(); // Hide loader in case of API call failure
                if (response.isSuccessful()){
                    Integer messId = 0;
                    String messName = "";
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing mess list
                        messList.clear();

                        // Parse the JSON data and add it to the mess list
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            messId = jsonObject.getInt("messId");
                            Log.d(TAG, "onResponse: "+messId);
                            messName = jsonObject.getString("messName");
                            Mess mess = new Mess(messId, messName);
                            messList.add(mess);
                        }

                        // Update the RecyclerView with the new mess data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messAdapter.notifyDataSetChanged();
                                hideLoader(); // Hide loader in case of API call failure
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoader(); // Hide loader in case of API call failure
                                Toast.makeText(MessActivity.this, "Failed to parse mess data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // Save mess id details in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MessData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("messId", messId);
                    editor.apply();

                } else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader(); // Hide loader in case of API call failure
                            Toast.makeText(MessActivity.this, "Failed to fetch mess data", Toast.LENGTH_SHORT).show();
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
                rvMess.setVisibility(View.GONE);
            }
        });
    }

    /*Hide loder*/
    private void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                rvMess.setVisibility(View.VISIBLE);
            }
        });
    }

}
package com.gttech.maintenanceapplication.mess;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button btnBack;
    //private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        rvMess = findViewById(R.id.rv_mess);
        btnBack = findViewById(R.id.btn_back);
        //btnAdd = findViewById(R.id.btn_add);

        rvMess.setLayoutManager(new LinearLayoutManager(this));
        messList = new ArrayList<>();
        messAdapter = new MessAdapter(messList);
        rvMess.setAdapter(messAdapter);

        // Make API call to fetch mess data
        fetchMessData();

        /*Back mess button click listener*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add mess button click listener*/
       /* btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMessDialog();
            }
        });*/
    }

    /*List mess data*/
    private void fetchMessData() {

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/mess/listOfAllMess";

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
                        Toast.makeText(MessActivity.this, "Failed to fetch mess data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    int messId = 0;
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
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
                            Toast.makeText(MessActivity.this, "Failed to fetch mess data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Mess alert dialog */
    /*private void showAddMessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Mess");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_mess, null);
        final EditText etMessName = view.findViewById(R.id.et_mess_name);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String messName = etMessName.getText().toString();

                if(!messName.isEmpty()){
                    // Add the new mess item to the list
                    addMess(messName);
                }else{
                    Toast.makeText(MessActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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
    }*/

   /*Mess add method*/
    /*private void addMess(String messName) {

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String sessionId = sharedPreferences.getString("sessionId", "");
        String roleType = sharedPreferences.getString("roleType", "");

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/mess/addMess";

        // Create JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("messName", messName);
            requestBody.put("userId", sessionId);
            requestBody.put("roleType", roleType);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MessActivity.this, "Failed to create request body", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestJsonBody = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(requestJsonBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(MessActivity.this, "Failed to adding mess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    fetchMessData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MessActivity.this, "Mess added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MessActivity.this, "Failed to add mess", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });*/
}
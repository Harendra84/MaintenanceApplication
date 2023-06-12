package com.gttech.maintenanceapplication.feedback;

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

public class FeedbackActivity extends AppCompatActivity implements FeedbackAdapter.FeedbackItemClickListener {

    private RecyclerView rvFeedback;
    private List<Feedback> feedbackList;
    private FeedbackAdapter feedbackAdapter;
    private Toolbar toolbarBack, toolbarAdd;
    private FloatingActionButton floatingAdd;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        progressBar = findViewById(R.id.progress_bar);
        // Initialize the handler
        handler = new Handler();

        toolbarBack = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbarBack);
        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingAdd = findViewById(R.id.floating_add);

        rvFeedback = findViewById(R.id.rv_feedback);
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList, this);
        rvFeedback.setAdapter(feedbackAdapter);

        // Make API call to fetch mess data
        fetchFeedbackData();

        /*Add Feedback*/
        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFeedbackDialog();
            }
        });
    }

    /*List feedback data*/
    private void fetchFeedbackData() {

        showLoader(); // Show loader before making the API call

        // Set a timeout of 10 seconds for hiding the loader
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader(); // Hide loader after timeout
            }
        }, 10000);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/feedback/listOfFeedbacks";

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
                        Toast.makeText(FeedbackActivity.this, "Failed to fetch feedback data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                hideLoader(); // Hide loader after receiving the API response
                if (response.isSuccessful()) {
                    int feedId = 0;
                    String feed = "";
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);

                        // Clear the existing feedback list
                        feedbackList.clear();

                        // Parse the JSON data and add it to the feed list
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            feedId = jsonObject.getInt("feedbackId");
                            feed = jsonObject.getString("feedback");
                            Feedback feedback = new Feedback(feedId, feed);
                            feedbackList.add(feedback);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                feedbackAdapter.notifyDataSetChanged();
                                hideLoader(); // Hide loader after receiving the API response
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideLoader(); // Hide loader after receiving the API response
                                Toast.makeText(FeedbackActivity.this, "Failed to parse feedback data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // Save feed id details in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("FeedbackData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("feedbackId", feedId);
                    editor.apply();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader(); // Hide loader after receiving the API response
                            Toast.makeText(FeedbackActivity.this, "Failed to fetch mess data", Toast.LENGTH_SHORT).show();
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
                rvFeedback.setVisibility(View.GONE);
            }
        });
    }


    /*Show hide*/
    private void hideLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                rvFeedback.setVisibility(View.VISIBLE);
            }
        });
    }


    /*Feedback alert dialog */
    private void showAddFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Feedback");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_feedback, null);
        final EditText etFeedback = view.findViewById(R.id.et_feedback);
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String feedback = etFeedback.getText().toString();

                if (!feedback.isEmpty()) {
                    // Add the new mess item to the list
                    addFeedback(feedback);
                } else {
                    Toast.makeText(FeedbackActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Feedback add method*/
    private void addFeedback(String feedback) {

        // Retrieve user data from SharedPreferences
        SharedPreferences feedbacks = getSharedPreferences("FeedbackData", MODE_PRIVATE);
        int feedbackId = feedbacks.getInt("feedbackId", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        SharedPreferences mess = getSharedPreferences("MessData", MODE_PRIVATE);
        int messId = mess.getInt("messId", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/feedback/addOrEditFeedback";

        RequestBody requestBody = new FormBody.Builder()
                .add("feedbackId", "0")
                .add("feedback", feedback)
                .add("messId", String.valueOf(messId))
                .add("userId", userId)
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
                        Toast.makeText(FeedbackActivity.this, "Failure to adding Feedback", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchFeedbackData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Feedback added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Failed to adding Feedback", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Implement adapter class method for update*/
    @Override
    public void onFeedbackItemClick(Feedback feedback) {
        showUpdateFeedbackDialog(feedback);
    }

    /*Implement adapter class method for delete*/
    @Override
    public void onFeedbackItemDeleteClick(Feedback feedback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Feedback");
        builder.setMessage("Are you sure you want to delete?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFeedback(feedback);
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

    /*Delete Method for Feedback*/
    private void deleteFeedback(Feedback feedback) {

        // Retrieve user data from SharedPreferences
        SharedPreferences feedbacks = getSharedPreferences("FeedbackData", MODE_PRIVATE);
        int feedbackId = feedbacks.getInt("feedbackId", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String roleType = sharedPreferences.getString("roleType", "");

        OkHttpClient client = new OkHttpClient();

        String url = "http://192.168.29.43:9090/feedback/deleteFeedback";

        // Create the form body with mess details
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("feedbackId", String.valueOf(feedbackId))
                .add("userId", userId)
                .add("roleType",roleType);

        // Create the request body
        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FeedbackActivity.this, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchFeedbackData(); // Refresh the mess list after deleting a mess
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Feedback deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /*Show Update Feedback Dialog for update*/
    private void showUpdateFeedbackDialog(Feedback feedback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Feedback");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_feedback, null);
        final EditText etFeedbackName = view.findViewById(R.id.et_feedback);
        etFeedbackName.setText(feedback.getFeedback());

        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String feedbackName = etFeedbackName.getText().toString().trim();

                if (!feedbackName.isEmpty()) {
                    // Make API call to update the mess
                    updateFeedback(feedback, feedbackName);
                } else {
                    Toast.makeText(FeedbackActivity.this, "Please enter valid details", Toast.LENGTH_SHORT).show();
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

    /*Update Method for Feedback*/
    private void updateFeedback(final Feedback feedback, String feedbackName) {

        // Retrieve user data from SharedPreferences
        SharedPreferences feedbacks = getSharedPreferences("FeedbackData", MODE_PRIVATE);
        int feedbackId = feedbacks.getInt("feedbackId", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        SharedPreferences mess = getSharedPreferences("MessData", MODE_PRIVATE);
        int messId = mess.getInt("messId", 0);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.29.43:9090/feedback/addOrEditFeedback";

        RequestBody requestBody = new FormBody.Builder()
                .add("feedbackId", String.valueOf(feedbackId))
                .add("feedback", feedbackName)
                .add("userId", userId)
                .add("messId", String.valueOf(messId))
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
                        Toast.makeText(FeedbackActivity.this, "Failure to update feedback", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    fetchFeedbackData(); // Refresh the mess list after updating a feedback
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Feedback updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FeedbackActivity.this, "Failed to update feedback", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
package com.gttech.maintenanceapplication.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        /*Click listeners on the login buttons*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient();
                String url = "http://192.168.43.43:9090/auth/login";

                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    showSnackbar("Please enter username!", Snackbar.LENGTH_SHORT);
                    return;
                }
                if (password.isEmpty()) {
                    showSnackbar("Please enter password!", Snackbar.LENGTH_SHORT);
                    return;
                }

                RequestBody requestBody = new FormBody.Builder()
                        .add("loginId", email)
                        .add("password", password)
                        .build();

                Request post = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(post).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {

                            // initialize the username variable
                            String username = "";
                            String sessionId = "";
                            String roleType = "";

                            try {
                                String responseBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseBody);

                                // retrieve the username from the response
                                username = jsonObject.getString("username");
                                sessionId = jsonObject.getString("sessionId");
                                roleType = jsonObject.getString("roleName");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSnackbar( "Failed to featch login", Toast.LENGTH_SHORT);
                                    }
                                });
                            }

                            // Save user details in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("userId", sessionId);
                            editor.putString("roleType", roleType);
                            editor.apply();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackbar("Login successful", Toast.LENGTH_LONG);
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   showSnackbar("Login failed", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void showSnackbar(String message, int duration) {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, duration);

        // Set text color
        snackbar.setActionTextColor(Color.WHITE);
        // Set background color
        snackbar.getView().setBackgroundColor(Color.DKGRAY);
        // Set duration
        snackbar.setDuration(duration);
        // Set the Snackbar to be displayed at the top
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        snackbar.show();
    }
}
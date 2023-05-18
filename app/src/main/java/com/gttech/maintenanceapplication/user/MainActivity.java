package com.gttech.maintenanceapplication.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

       /* login button*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient();

                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                RequestBody requestBody = new FormBody.Builder()
                        .add("email", email)
                        .add("password", password)
                        .build();

                /*POST*/
                Request post = new Request.Builder()
                        .url("http://192.168.29.43:8080/auth/login")
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
                            String responseBody = response.body().string();
                            String username = ""; // initialize the username variable
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                username = jsonObject.getString("username"); // retrieve the username from the response
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                           /* Intent Passing*/
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("username", username); // pass the username as an intent extra
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
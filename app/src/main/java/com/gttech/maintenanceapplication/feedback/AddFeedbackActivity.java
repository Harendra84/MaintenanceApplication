package com.gttech.maintenanceapplication.feedback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gttech.maintenanceapplication.R;

public class AddFeedbackActivity extends AppCompatActivity {

    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        btnAdd = findViewById(R.id.btn_add);

        /*Add Feedback*/
       btnAdd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(AddFeedbackActivity.this, FeedbackActivity.class);
               startActivity(intent);
           }
       });
    }
}
package com.gttech.maintenanceapplication.feedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;

import org.json.JSONArray;
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

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView rvFeedback;
    private List<Feedback> feedbackList;
    private FeedbackAdapter feedbackAdapter;
    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        rvFeedback = findViewById(R.id.rv_feedback);
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        rvFeedback.setAdapter(feedbackAdapter);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("userId", "1")
                .add("messId", "1")
                .add("roleType", "ADMIN")
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.29.43:8080/feedback/listOfFeedbacks")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String feed = jsonObject.getString("feedback");
                        Feedback  feedback =  new Feedback(feed);
                        feedbackList.add(feedback);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            feedbackAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        /*Back Button*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add Button*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, AddFeedbackActivity.class);
                startActivity(intent);
            }
        });
    }

    /*Model Class*/

    public class Feedback{

        private String feedback;

        public Feedback() {
        }

        public Feedback(String feedback) {
            this.feedback = feedback;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
    }

    /*Adapter Class*/
    public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackViewHolder>{

        private final Context context;
        private final List<Feedback> feedbackList;

        public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
            this.context = context;
            this.feedbackList = feedbackList;
        }

        @NonNull
        @Override
        public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
            return new FeedbackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
            Feedback feedback = feedbackList.get(position);
            holder.tvFeedback.setText(feedback.getFeedback());
        }

        @Override
        public int getItemCount() {
            return feedbackList.size();
        }
    }

    /*ViewHolder Class*/
    static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView tvFeedback;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedback = itemView.findViewById(R.id.tv_feedback);

        }
    }
}
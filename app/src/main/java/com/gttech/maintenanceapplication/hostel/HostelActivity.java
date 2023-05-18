package com.gttech.maintenanceapplication.hostel;

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
import com.gttech.maintenanceapplication.ambulance.AmbulanceActivity;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.feedback.AddFeedbackActivity;
import com.gttech.maintenanceapplication.feedback.FeedbackActivity;

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
    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel);

        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        rvHostel = findViewById(R.id.rv_hostel);
        rvHostel.setLayoutManager(new LinearLayoutManager(this));

        hostelList = new ArrayList<>();
        hostelAdapter = new HostelAdapter(this, hostelList);
        rvHostel.setAdapter(hostelAdapter);

        RequestBody requestBody = new FormBody.Builder()
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.29.43:8080/hostel/getAllHostels")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String hostelName = jsonObject.getString("hostelName");

                            Hostel hostel = new Hostel(hostelName);
                            hostelList.add(hostel);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hostelAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostelActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add Button*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostelActivity.this, AddHostelActivity.class);
                startActivity(intent);
            }
        });

    }

    /*Model Class*/
    public class Hostel{
        private String hostelName;

        public Hostel(String hostelName) {
            this.hostelName = hostelName;
        }

        public String getHostelName() {
            return hostelName;
        }

        public void setHostelName(String hostelName) {
            this.hostelName = hostelName;
        }
    }


    /*Adapter Class*/
    private static class HostelAdapter extends RecyclerView.Adapter<HostelViewHolder> {
        private final Context context;
        private final List<Hostel> hostelList;

        public HostelAdapter(Context context, List<Hostel> hostelList) {
            this.context = context;
            this.hostelList = hostelList;
        }

        @NonNull
        @Override
        public HostelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_hostel, parent, false);
            return new HostelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HostelViewHolder holder, int position) {
            Hostel hostel = hostelList.get(position);
            holder.tvHostelName.setText(hostel.getHostelName());
        }

        @Override
        public int getItemCount() {
            return hostelList.size();
        }
    }

    /*View Holder Class*/
    private static class HostelViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvHostelName;

        public HostelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHostelName = itemView.findViewById(R.id.tv_hostel_name);
        }
    }
}
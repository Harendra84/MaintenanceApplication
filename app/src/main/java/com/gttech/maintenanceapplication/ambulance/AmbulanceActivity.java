package com.gttech.maintenanceapplication.ambulance;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
import com.gttech.maintenanceapplication.hostel.HostelActivity;
import com.gttech.maintenanceapplication.internship.InternshipActivity;

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

public class AmbulanceActivity extends AppCompatActivity {

    private RecyclerView rvAmbulance;
    private List<Ambulance> ambulanceList;
    private AmbulanceAdapter ambulanceAdapter;
    private Button btnBack;
    private Button btnAdd;
    private Button Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);

        btnAdd = findViewById(R.id.btn_add);
        rvAmbulance = findViewById(R.id.rv_ambulance);
        rvAmbulance.setLayoutManager(new LinearLayoutManager(this));
        rvAmbulance.setScrollbarFadingEnabled(false);
        rvAmbulance.setVerticalScrollBarEnabled(true);

        ambulanceList = new ArrayList<>();
        ambulanceAdapter = new AmbulanceAdapter(this, ambulanceList);
        rvAmbulance.setAdapter(ambulanceAdapter);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("roleType", "UNDEFINED")
                .add("roleType", "STUDENT")
                .add("roleType", "MENTOR")
                .add("roleType", "WARDEN")
                .add("roleType", "ADMIN")
                .add("roleType", "MESSINCHARGE")
                .add("roleType", "CHIEFWARDEN")
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.29.43:8080/ambulance/listall")
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

                        Integer ambulanceId = jsonObject.getInt("ambulance_id");
                        String ambulanceName = jsonObject.getString("ambulanceName");
                        String licensePlate = jsonObject.getString("licensePlate");

                        Ambulance ambulance = new Ambulance(ambulanceId, ambulanceName, licensePlate);
                        ambulanceList.add(ambulance);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ambulanceAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*back button*/
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Intent Passing*/
                Intent intent = new Intent(AmbulanceActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*add*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Intent Passing*/
                Intent intent = new Intent(AmbulanceActivity.this, AddAmbulanceActivity.class);
                startActivity(intent);
            }
        });

    }

    /*Model class*/

    public class Ambulance {

        private Integer ambulance_id;
        private String ambulanceName;
        private String licensePlate;

        public Ambulance(Integer ambulance_id, String ambulanceName, String licensePlate) {
            this.ambulance_id = ambulance_id;
            this.ambulanceName = ambulanceName;
            this.licensePlate = licensePlate;
        }

        public Integer getAmbulance_id() {
            return ambulance_id;
        }

        public void setAmbulance_id(Integer ambulance_id) {
            this.ambulance_id = ambulance_id;
        }

        public String getAmbulanceName() {
            return ambulanceName;
        }

        public void setAmbulanceName(String ambulanceName) {
            this.ambulanceName = ambulanceName;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }
    }

    /*Adapter class*/

    private static class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceViewHolder> {

        private final Context context;
        private final List<Ambulance> ambulanceList;

        public AmbulanceAdapter(Context context, List<Ambulance> ambulanceList) {
            this.context = context;
            this.ambulanceList = ambulanceList;
        }

        @NonNull
        @Override
        public AmbulanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_ambulance, parent, false);
            return new AmbulanceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AmbulanceViewHolder holder, int position) {
            Ambulance ambulance = ambulanceList.get(position);

            holder.tvAmbulanceId.setInputType(ambulance.getAmbulance_id());
            holder.tvAmbulanceName.setText(ambulance.getAmbulanceName());
            holder.tvLicensePlate.setText(ambulance.getLicensePlate());
        }

        @Override
        public int getItemCount() {
            return ambulanceList.size();
        }
    }

    /*View Holder class*/
    private static class AmbulanceViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAmbulanceId;
        private final TextView tvAmbulanceName;
        private final TextView tvLicensePlate;

        public AmbulanceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmbulanceId = itemView.findViewById(R.id.tv_ambulance_id);
            tvAmbulanceName = itemView.findViewById(R.id.tv_ambulance_name);
            tvLicensePlate = itemView.findViewById(R.id.tv_license_plate);
        }

    }

}
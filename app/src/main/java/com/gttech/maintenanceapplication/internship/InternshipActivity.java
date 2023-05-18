package com.gttech.maintenanceapplication.internship;

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

public class InternshipActivity extends AppCompatActivity {

    private RecyclerView rvInternship;
    private List<Internship> internshipList;
    private InternshipAdapter internshipAdapter;
    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship);

        rvInternship = findViewById(R.id.rv_internship);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        rvInternship.setLayoutManager(new LinearLayoutManager(this));
        internshipList = new ArrayList<>();
        internshipAdapter = new InternshipAdapter(this,internshipList);
        rvInternship.setAdapter(internshipAdapter);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("hostelId", "1")
                .add("userId", "1")
                .add("roleType", "ADMIN")
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.29.43:8080/internship/listOfInternshipStudentsByHostel")
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

                        String name = jsonObject.getString("name");
                        String registrationNumber = jsonObject.getString("registrationNumber");
                        String campus = jsonObject.getString("campus");
                        String purpose = jsonObject.getString("purpose");
                        String phoneNo = jsonObject.getString("phoneNo");
                        String emailId = jsonObject.getString("emailId");
                        Integer noOfDays = jsonObject.getInt("noOfDays");

                        Internship internship = new Internship(name, registrationNumber, campus, purpose, phoneNo, emailId, noOfDays);
                        internshipList.add(internship);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            internshipAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        /*Back*/
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InternshipActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        /*Back*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InternshipActivity.this, AddInternshipActivity.class);
                startActivity(intent);
            }
        });
    }

    /*Model Class*/
    public class Internship {

        private String name;
        private String registrationNumber;
        private String campus;
        private String purpose;
        private String phoneNo;
        private String emailId;
        private Integer noOfDays;

        public Internship(String name, String registrationNumber, String campus, String purpose, String phoneNo, String emailId, Integer noOfDays) {
            this.name = name;
            this.registrationNumber = registrationNumber;
            this.campus = campus;
            this.purpose = purpose;
            this.phoneNo = phoneNo;
            this.emailId = emailId;
            this.noOfDays = noOfDays;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getCampus() {
            return campus;
        }

        public void setCampus(String campus) {
            this.campus = campus;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public Integer getNoOfDays() {
            return noOfDays;
        }

        public void setNoOfDays(Integer noOfDays) {
            this.noOfDays = noOfDays;
        }
    }

    /*Adapter Class*/

    private static class InternshipAdapter extends RecyclerView.Adapter<InternshipViewHolder> {

        private final Context context;
        private final List<InternshipActivity.Internship> InternshipList;

        public InternshipAdapter(Context context, List<InternshipActivity.Internship> InternshipList) {
            this.context = context;
            this.InternshipList = InternshipList;
        }

        @NonNull
        @Override
        public InternshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_internship, parent, false);
            return new InternshipViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InternshipViewHolder holder, int position) {
            Internship Internship = InternshipList.get(position);
            holder.tvName.setText(Internship.getName());
            holder.tvRegistrationNumber.setText(Internship.getRegistrationNumber());
            holder.tvCampus.setText(Internship.getCampus());
            holder.tvPurpose.setText(Internship.getPurpose());
            holder.tvPhoneNo.setText(Internship.getPhoneNo());
            holder.tvEmailId.setText(Internship.getEmailId());
            holder.tvNoOfDays.setInputType(Internship.getNoOfDays());
        }

        @Override
        public int getItemCount() {
            return InternshipList.size();
        }
    }

    /*View Holder class*/
    private static class InternshipViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvRegistrationNumber;
        private final TextView tvCampus;
        private final TextView tvPhoneNo;
        private final TextView tvEmailId;
        private final TextView tvPurpose;
        private final TextView tvNoOfDays;

        public InternshipViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvRegistrationNumber = itemView.findViewById(R.id.tv_registration_number);
            tvCampus = itemView.findViewById(R.id.tv_campus);
            tvPhoneNo  = itemView.findViewById(R.id.tv_phone_no);
            tvEmailId = itemView.findViewById(R.id.tv_email_id);
            tvPurpose = itemView.findViewById(R.id.tv_purpose);
            tvNoOfDays  = itemView.findViewById(R.id.tv_no_of_days);
        }

    }

}
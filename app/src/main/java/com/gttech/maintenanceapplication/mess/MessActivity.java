package com.gttech.maintenanceapplication.mess;

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
import android.widget.Toast;

import com.gttech.maintenanceapplication.R;
import com.gttech.maintenanceapplication.dashboard.HomeActivity;
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

public class MessActivity extends AppCompatActivity {

    private RecyclerView rvMess;
    private List<Mess> messList;
    private MessAdapter messAdapter;
    private Button btnBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        rvMess = findViewById(R.id.rv_mess);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);

        rvMess.setLayoutManager(new LinearLayoutManager(this));


        messList = new ArrayList<>();
        messAdapter = new MessAdapter(this, messList);
        rvMess.setAdapter(messAdapter);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.29.43:8080/mess/listOfAllMess")
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

                        Integer messId = jsonObject.getInt("messId");
                        String messName = jsonObject.getString("messName");

                        Mess mess = new Mess(messId,messName);
                        messList.add(mess);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(MessActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        /*Add Button*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessActivity.this, AddMessActivity.class);
                startActivity(intent);
            }
        });
    }

    /*Model Class*/
    public class Mess {

        private int messId;
        private String messName;

        public Mess(int messId, String messName) {
            this.messId = messId;
            this.messName = messName;
        }

        public int getMessId() {
            return messId;
        }

        public void setMessId(int messId) {
            this.messId = messId;
        }

        public String getMessName() {
            return messName;
        }

        public void setMessName(String messName) {
            this.messName = messName;
        }
    }


    /*MessAdapter*/
    public class MessAdapter extends RecyclerView.Adapter<MessViewHolder> {

        private final Context context;
        private final List<Mess> messList;

        public MessAdapter(Context context, List<Mess> messList) {
            this.context = context;
            this.messList = messList;
        }

        @NonNull
        @Override
        public MessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_mess, parent, false);
            return new MessViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessViewHolder holder, int position) {
            Mess mess = messList.get(position);
            holder.tvMessName.setText(mess.getMessName());

        }

        @Override
        public int getItemCount() {
            return messList.size();
        }
    }

    /*View Holder Class*/
    public class MessViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMessName;

        public MessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessName = itemView.findViewById(R.id.tv_mess_name);
        }
    }
}
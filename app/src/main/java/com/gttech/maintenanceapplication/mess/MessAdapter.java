package com.gttech.maintenanceapplication.mess;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gttech.maintenanceapplication.R;

import java.util.List;

public class MessAdapter extends RecyclerView.Adapter<MessAdapter.MessViewHolder> {

    private List<Mess> messList;

    public MessAdapter(List<Mess> messList) {
        this.messList = messList;
    }

    @NonNull
    @Override
    public MessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mess, parent, false);
        return new MessViewHolder(itemView);
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

    public static class MessViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessName;

        public MessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessName = itemView.findViewById(R.id.tv_mess_name);
        }
    }
}

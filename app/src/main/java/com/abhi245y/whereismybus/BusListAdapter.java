package com.abhi245y.whereismybus;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusViewHolder> {
    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class BusViewHolder extends RecyclerView.ViewHolder {
        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

package com.kent.university.privelt.ui.dashboard.card.data_metrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.CardItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataMetricsAdapter extends RecyclerView.Adapter<DataMetricsViewHolder> {

    private List<CardItem> dataMetrics;

    public DataMetricsAdapter() {
        this.dataMetrics = new ArrayList<>();
    }

    @NonNull
    @Override
    public DataMetricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_metrics, parent, false);
        return new DataMetricsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataMetricsViewHolder holder, int position) {
        holder.bind(dataMetrics.get(position));
    }

    @Override
    public int getItemCount() {
        return dataMetrics.size();
    }

    public void setDataMetrics(List<CardItem> dataMetrics) {
        this.dataMetrics = dataMetrics;
    }
}

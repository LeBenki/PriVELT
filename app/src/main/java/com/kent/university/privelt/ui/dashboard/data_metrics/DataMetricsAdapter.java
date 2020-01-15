package com.kent.university.privelt.ui.dashboard.data_metrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataMetricsAdapter extends RecyclerView.Adapter<DataMetricsViewHolder> {

    LinkedHashMap<String, Integer> dataMetrics;

    public DataMetricsAdapter() {
        this.dataMetrics = new LinkedHashMap<>();
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
        int i = 0;
        for (Map.Entry<String, Integer> entry : dataMetrics.entrySet()) {
            if (position == i)
                holder.bind(entry.getKey(), entry.getValue());
            i++;
        }
    }

    @Override
    public int getItemCount() {
        return dataMetrics.size();
    }

    public void setDataMetrics(LinkedHashMap<String, Integer> dataMetrics) {
        this.dataMetrics = dataMetrics;
    }
}

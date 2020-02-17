package com.kent.university.privelt.ui.dashboard.service.data_metrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceMetricsAdapter extends RecyclerView.Adapter<ServiceMetricsViewHolder> {

    private LinkedHashMap<Service, List<UserData>> dataMetrics;

    public ServiceMetricsAdapter() {
        this.dataMetrics = new LinkedHashMap<>();
    }

    @NonNull
    @Override
    public ServiceMetricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_metrics, parent, false);
        return new ServiceMetricsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceMetricsViewHolder holder, int position) {
        int i = 0;
        for (Map.Entry<Service, List<UserData>> entry : dataMetrics.entrySet()) {
            if (position == i)
                holder.bind(entry.getKey().getName(), entry.getValue().size());
            i++;
        }
    }

    @Override
    public int getItemCount() {
        return dataMetrics.size();
    }

    public void setDataMetrics(LinkedHashMap<Service, List<UserData>> dataMetrics) {
        this.dataMetrics = dataMetrics;
    }
}

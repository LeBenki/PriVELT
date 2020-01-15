package com.kent.university.privelt.ui.dashboard.data_metrics;

import android.view.View;
import android.widget.TextView;

import com.kent.university.privelt.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DataMetricsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.metrics)
    TextView metrics;

    DataMetricsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String key, Integer value) {
        metrics.setText(value + " different " + key + " found");
    }
}

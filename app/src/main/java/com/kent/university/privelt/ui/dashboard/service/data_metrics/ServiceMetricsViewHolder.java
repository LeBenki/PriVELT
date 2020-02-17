package com.kent.university.privelt.ui.dashboard.service.data_metrics;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceMetricsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.value_metrics)
    TextView metrics;

    @BindView(R.id.image_type)
    ImageView service;

    ServiceMetricsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String key, Integer value) {
        metrics.setText(value < 99 ? String.valueOf(value) : String.valueOf(99));
        PriVELT priVELT = (PriVELT) service.getContext().getApplicationContext();
        service.setImageResource(priVELT.getServiceHelper().getResIdWithName(key));
    }
}

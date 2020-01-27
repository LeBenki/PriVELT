package com.kent.university.privelt.ui.dashboard.service;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.LaunchDataEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.dashboard.service.data_metrics.DataMetricsAdapter;
import com.kent.university.privelt.utils.ParseUserData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class ServiceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_service)
    ImageView imageService;

    @BindView(R.id.title_service)
    TextView title;

    @BindView(R.id.settings)
    ImageView settings;

    @BindView(R.id.metric_rv)
    RecyclerView metrics;

    @BindView(R.id.total_metrics)
    TextView totalMetrics;

    @BindView(R.id.risk_progress)
    ProgressBar riskProgress;

    private DataMetricsAdapter dataMetricsAdapter;

    ServiceViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
        metrics.setLayoutManager(layoutManager);
        dataMetricsAdapter = new DataMetricsAdapter();
        metrics.setAdapter(dataMetricsAdapter);
    }

    void bind(Service service, List<UserData> userDatas) {
        title.setText(service.getName());
        PriVELT priVELT = (PriVELT) title.getContext().getApplicationContext();
        imageService.setImageResource(priVELT.getServiceHelper().getResIdWithName(service.getName()));
        settings.setOnClickListener(view -> EventBus.getDefault().post(new UpdateCredentialsEvent(service)));
        itemView.setOnClickListener(view -> EventBus.getDefault().post(new LaunchDataEvent(service)));

        if (userDatas != null && userDatas.size() != 0) {
            //TODO: 200 HARDCODED (MAX DATA)
            riskProgress.setProgress(userDatas.size() * 100 / 200);
            metrics.setVisibility(View.VISIBLE);
            totalMetrics.setVisibility(View.VISIBLE);
            totalMetrics.setText(String.format(itemView.getResources().getString(R.string.information_found_total), userDatas.size()));
            dataMetricsAdapter.setDataMetrics(ParseUserData.parseUserData(userDatas));
            dataMetricsAdapter.notifyDataSetChanged();
        }
        else {
            metrics.setVisibility(View.GONE);
            totalMetrics.setVisibility(View.GONE);
        }
    }
}

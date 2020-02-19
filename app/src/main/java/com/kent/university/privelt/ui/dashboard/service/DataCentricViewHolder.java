package com.kent.university.privelt.ui.dashboard.service;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.events.ChangeWatchListStatusEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.dashboard.service.data_metrics.ServiceMetricsAdapter;
import com.kent.university.privelt.ui.risk_value.RiskValueActivity;
import com.kent.university.privelt.utils.UserDataType;


import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.ui.risk_value.RiskValueActivity.PARAM_DATA;

class DataCentricViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_service)
    ImageView imageService;

    @BindView(R.id.title_service)
    TextView title;

    @BindView(R.id.settings)
    ImageView settings;

    @BindView(R.id.watch_icon)
    ImageView watchIcon;

    @BindView(R.id.metric_rv)
    RecyclerView metrics;

    @BindView(R.id.service_value)
    TextView totalMetrics;

    @BindView(R.id.risk_progress)
    ProgressBar riskProgress;

    final AtomicBoolean watch;

    private ServiceMetricsAdapter dataMetricsAdapter;

    DataCentricViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        metrics.setLayoutManager(layoutManager);
        dataMetricsAdapter = new ServiceMetricsAdapter();
        metrics.setAdapter(dataMetricsAdapter);

        watch = new AtomicBoolean(false);
    }

    void bind(String type, LinkedHashMap<Service, List<UserData>> map, boolean isWatched) {
        title.setText(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
        UserDataType userDataType = UserDataType.valueOf(type.toUpperCase());
        imageService.setImageResource(userDataType.getRes());
        watch.set(isWatched);
        watchIcon.setOnClickListener(view -> {
            EventBus.getDefault().post(new ChangeWatchListStatusEvent(type));
            watch.set(watch.get());
            watchIcon.setColorFilter(!watch.get() ? itemView.getContext().getResources().getColor(R.color.colorAccent) : itemView.getContext().getResources().getColor(android.R.color.black));
        });
        settings.setVisibility(View.GONE);
        //itemView.setOnClickListener(view -> EventBus.getDefault().post(new LaunchDataEvent(service)));

        watchIcon.setColorFilter(isWatched ? itemView.getContext().getResources().getColor(R.color.colorAccent) : itemView.getContext().getResources().getColor(android.R.color.black));

        //TODO: to double check
        int score = 0;

        for (Map.Entry<Service, List<UserData>> entry : map.entrySet())
            score += entry.getValue().size();

        if (score != 0) {
            //TODO: 200 HARDCODED (MAX DATA)
            riskProgress.setProgress(score * 100 / 200);
            riskProgress.setOnClickListener((v) -> {
                Intent intent = new Intent(riskProgress.getContext(), RiskValueActivity.class);
                intent.putExtra(PARAM_DATA, type);
                riskProgress.getContext().startActivity(intent);
            });
            metrics.setVisibility(View.VISIBLE);
            totalMetrics.setVisibility(View.VISIBLE);
            totalMetrics.setText(String.valueOf(score < 99 ? score : 99));
            dataMetricsAdapter.setDataMetrics(map);
            dataMetricsAdapter.notifyDataSetChanged();
        }
        else {
            riskProgress.setOnClickListener(null);
            metrics.setVisibility(View.GONE);
            totalMetrics.setVisibility(View.GONE);
        }
    }
}

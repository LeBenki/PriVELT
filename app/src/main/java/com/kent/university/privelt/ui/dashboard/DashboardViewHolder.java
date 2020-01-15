package com.kent.university.privelt.ui.dashboard;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.LaunchDataEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.dashboard.data_metrics.DataMetricsAdapter;
import com.kent.university.privelt.utils.ParseUserData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class DashboardViewHolder extends RecyclerView.ViewHolder {

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

    private DataMetricsAdapter dataMetricsAdapter;

    DashboardViewHolder(@NonNull View itemView) {
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

        if (userDatas != null) {
            totalMetrics.setText(userDatas.size() + " different information found");
            dataMetricsAdapter.setDataMetrics(ParseUserData.parseUserData(userDatas));
            dataMetricsAdapter.notifyDataSetChanged();
        }
    }
}

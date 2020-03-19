package com.kent.university.privelt.ui.dashboard.card;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.ChangeWatchListStatusEvent;
import com.kent.university.privelt.events.DetailedCardEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Card;
import com.kent.university.privelt.model.CardItem;
import com.kent.university.privelt.ui.dashboard.card.data_metrics.DataMetricsAdapter;
import com.kent.university.privelt.ui.risk_value.RiskValueActivity;
import com.university.kent.dataextractor.model.UserDataTypes;

import org.greenrobot.eventbus.EventBus;

import static com.kent.university.privelt.ui.risk_value.RiskValueActivity.PARAM_DATA;
import static com.kent.university.privelt.ui.risk_value.RiskValueActivity.PARAM_SERVICE;

class CardViewHolder extends RecyclerView.ViewHolder {

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

    private DataMetricsAdapter dataMetricsAdapter;

    CardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        metrics.setLayoutManager(layoutManager);
        dataMetricsAdapter = new DataMetricsAdapter();
        metrics.setAdapter(dataMetricsAdapter);
    }

    void bind(Card card) {
        title.setText(card.getTitle());

        if (card.isService()) {
            PriVELT priVELT = (PriVELT) title.getContext().getApplicationContext();
            imageService.setImageResource(priVELT.getServiceHelper().getResIdWithName(card.getTitle()));
        } else {
            UserDataTypes userDataType = UserDataTypes.valueOf(card.getTitle().toUpperCase());
            imageService.setImageResource(userDataType.getRes());
        }

        if (card.isService()) {
            settings.setVisibility(View.VISIBLE);
            settings.setOnClickListener(view -> EventBus.getDefault().post(new UpdateCredentialsEvent(card.getTitle())));
        } else {
            settings.setVisibility(View.GONE);
        }

        watchIcon.setOnClickListener(view -> {
            EventBus.getDefault().post(new ChangeWatchListStatusEvent(card.getTitle()));
            card.setWatched(!card.isWatched());
            watchIcon.setColorFilter(card.isWatched() ? itemView.getContext().getResources().getColor(R.color.colorAccent) : itemView.getContext().getResources().getColor(android.R.color.black));
        });

        itemView.setOnClickListener(view -> EventBus.getDefault().post(new DetailedCardEvent(card)));

        watchIcon.setColorFilter(card.isWatched() ? itemView.getContext().getResources().getColor(R.color.colorAccent) : itemView.getContext().getResources().getColor(android.R.color.black));

        int total = 0;
        for (CardItem item : card.getMetrics())
            total += item.getNumber();

        if (card.getMetrics().size() != 0) {
            //TODO: 200 HARDCODED (MAX DATA)
            riskProgress.setProgress(total * 100 / 200);
            riskProgress.setOnClickListener((v) -> {
                Intent intent = new Intent(riskProgress.getContext(), RiskValueActivity.class);
                if (card.isService())
                    intent.putExtra(PARAM_SERVICE, card.getTitle());
                else
                    intent.putExtra(PARAM_DATA, card.getTitle());
                riskProgress.getContext().startActivity(intent);
            });
            metrics.setVisibility(View.VISIBLE);
            totalMetrics.setVisibility(View.VISIBLE);
            totalMetrics.setText(String.valueOf(Math.min(total, 99)));
            dataMetricsAdapter.setDataMetrics(card.getMetrics(), !card.isService());
            dataMetricsAdapter.notifyDataSetChanged();
        }
        else {
            metrics.setVisibility(View.GONE);
            totalMetrics.setVisibility(View.GONE);
            riskProgress.setOnClickListener(null);
        }
    }
}

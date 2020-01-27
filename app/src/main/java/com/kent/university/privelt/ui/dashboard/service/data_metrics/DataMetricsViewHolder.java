package com.kent.university.privelt.ui.dashboard.service.data_metrics;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kent.university.privelt.R;
import com.kent.university.privelt.utils.sentence.SentenceAdapter;

public class DataMetricsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.metrics)
    TextView metrics;

    DataMetricsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String key, Integer value) {
        metrics.setText(SentenceAdapter.adapt(itemView.getResources().getString(R.string.information_found), value, key));
    }
}

package com.kent.university.privelt.ui.dashboard.service.data_metrics;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kent.university.privelt.R;
import com.kent.university.privelt.utils.UserDataType;

public class DataMetricsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.value_metrics)
    TextView metrics;

    @BindView(R.id.image_type)
    ImageView type;

    DataMetricsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String key, Integer value) {
        metrics.setText(value < 98 ? String.valueOf(value) : String.valueOf(99));
        UserDataType userDataType = UserDataType.valueOf(key.toUpperCase());
        type.setImageResource(userDataType.getRes());
    }
}

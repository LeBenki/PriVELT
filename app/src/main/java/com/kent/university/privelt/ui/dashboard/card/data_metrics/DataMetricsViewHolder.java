package com.kent.university.privelt.ui.dashboard.card.data_metrics;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.model.CardItem;
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

    public void bind(CardItem cardItem, boolean isService) {
        metrics.setText(cardItem.getNumber() < 98 ? String.valueOf(cardItem.getNumber()) : String.valueOf(99));
        if (!isService) {
            UserDataType userDataType = UserDataType.valueOf(cardItem.getName().toUpperCase());
            type.setImageResource(userDataType.getRes());
        }
        else {
            PriVELT priVELT = (PriVELT) type.getContext().getApplicationContext();
            type.setImageResource(priVELT.getServiceHelper().getResIdWithName(cardItem.getName()));
        }
    }
}

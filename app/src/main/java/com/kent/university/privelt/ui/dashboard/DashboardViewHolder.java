package com.kent.university.privelt.ui.dashboard;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.LaunchDataEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
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

    DashboardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bind(Service service) {
        title.setText(service.getName());
        PriVELT priVELT = (PriVELT) title.getContext().getApplicationContext();
        imageService.setImageResource(priVELT.getServiceHelper().getResIdWithName(service.getName()));
        settings.setOnClickListener(view -> EventBus.getDefault().post(new UpdateCredentialsEvent(service)));
        itemView.setOnClickListener(view -> EventBus.getDefault().post(new LaunchDataEvent(service)));
    }
}

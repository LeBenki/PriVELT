package com.kent.university.privelt.ui.detailed;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.events.LaunchListDataEvent;
import com.kent.university.privelt.model.CardItem;
import com.kent.university.privelt.utils.UserDataType;
import com.kent.university.privelt.utils.sentence.SentenceAdapter;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedCardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text1)
    TextView text;

    @BindView(R.id.icon)
    ImageView imageView;

    DetailedCardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(CardItem cardItem, boolean isService) {
        text.setText(SentenceAdapter.adapt(text.getContext().getResources().getString(R.string.data_found), cardItem.getNumber()));
        text.setOnClickListener(view -> EventBus.getDefault().post(new LaunchListDataEvent(cardItem.getName())));

        if (!isService) {
            UserDataType userDataType = UserDataType.valueOf(cardItem.getName().toUpperCase());
            imageView.setImageResource(userDataType.getRes());
        }
        else {
            PriVELT priVELT = (PriVELT) imageView.getContext().getApplicationContext();
            imageView.setImageResource(priVELT.getServiceHelper().getResIdWithName(cardItem.getName()));
        }
    }
}

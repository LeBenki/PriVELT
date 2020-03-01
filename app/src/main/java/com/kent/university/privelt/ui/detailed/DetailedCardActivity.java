package com.kent.university.privelt.ui.detailed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.events.LaunchListDataEvent;
import com.kent.university.privelt.model.Card;
import com.kent.university.privelt.ui.data.DataActivity;
import com.kent.university.privelt.utils.UserDataType;
import com.kent.university.privelt.utils.sentence.SentenceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.ui.data.DataActivity.PARAM_TYPE;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;

public class DetailedCardActivity extends BaseActivity {

    public static final String PARAM_CARD = "PARAM_CARD";
    private Card card;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.recycler_view_metrics)
    RecyclerView recyclerView;

    @BindView(R.id.image_logo)
    ImageView logo;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.overall_risk)
    TextView overallRisk;

    private DetailedCardAdapter detailedCardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_card);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            card = (Card) savedInstanceState.getSerializable(PARAM_CARD);
        } else if (getIntent() != null) {
            card = (Card) getIntent().getSerializableExtra(PARAM_CARD);
        }

        title.setText(card.getTitle());

        if (card.getMetrics().size() < 20)
            overallRisk.setText(SentenceAdapter.adapt(getResources().getString(R.string.overall_risk), "Low"));
        else if (card.getMetrics().size() < 60)
            overallRisk.setText(SentenceAdapter.adapt(getResources().getString(R.string.overall_risk), "Medium"));
        else
            overallRisk.setText(SentenceAdapter.adapt(getResources().getString(R.string.overall_risk), "High"));

        if(!card.isService()) {
            UserDataType userDataType = UserDataType.valueOf(card.getTitle().toUpperCase());
            logo.setImageResource(userDataType.getRes());
        }
        else {
            PriVELT priVELT = (PriVELT) logo.getContext().getApplicationContext();
            logo.setImageResource(priVELT.getServiceHelper().getResIdWithName(card.getTitle()));
        }
        setTitle(card.getTitle());

        configureRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_CARD, card);
    }

    private void configureRecyclerView() {

        if (card.getMetrics().size() != 0) {
            progressBar.setVisibility(View.GONE);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        detailedCardAdapter = new DetailedCardAdapter(card.getMetrics(), !card.isService());
        recyclerView.setAdapter(detailedCardAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onLaunchListData(LaunchListDataEvent event) {
        Intent intent = new Intent(this, DataActivity.class);

        if (card.isService()) {
            intent.putExtra(PARAM_SERVICE, card.getTitle());
            intent.putExtra(PARAM_TYPE, event.card);
        }
        else {
            intent.putExtra(PARAM_SERVICE, event.card);
            intent.putExtra(PARAM_TYPE, card.getTitle());
        }

        startActivity(intent);
    }
}

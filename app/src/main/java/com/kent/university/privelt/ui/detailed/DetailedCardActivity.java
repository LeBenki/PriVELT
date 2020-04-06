/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.detailed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.events.LaunchListDataEvent;
import com.kent.university.privelt.model.Card;
import com.kent.university.privelt.model.CardItem;
import com.kent.university.privelt.ui.data.DataActivity;
import com.university.kent.dataextractor.model.UserDataTypes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

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

    @BindView(R.id.risk_progress)
    ProgressBar overallRisk;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_detailed_card;
    }

    @Override
    protected void configureViewModel() {
        //TODO use viewmodel
    }

    @Override
    protected void configureDesign(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            card = (Card) savedInstanceState.getSerializable(PARAM_CARD);
        } else if (getIntent() != null) {
            card = (Card) getIntent().getSerializableExtra(PARAM_CARD);
        }

        title.setText(card.getTitle());

        int progress = 0;
        for (CardItem cardItem : card.getMetrics())
            progress += cardItem.getNumber();

        overallRisk.setProgress(progress * 100 / 200);

        if(!card.isService()) {
            UserDataTypes userDataType = UserDataTypes.valueOf(card.getTitle().toUpperCase());
            logo.setImageResource(userDataType.getRes());
        }
        else {
            PriVELTApplication priVELTApplication = (PriVELTApplication) logo.getContext().getApplicationContext();
            logo.setImageResource(priVELTApplication.getServiceHelper().getResIdWithName(card.getTitle()));
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
        DetailedCardAdapter detailedCardAdapter = new DetailedCardAdapter(card.getMetrics(), !card.isService());
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

package com.kent.university.privelt.ui.risk_value;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RiskValueActivity extends BaseActivity {

    public static final String PARAM_SERVICE = "service";
    public static final String PARAM_DATA = "data";

    @BindView(R.id.chart)
    RadarChart chart;

    @BindView(R.id.no_data)
    TextView noData;

    private RiskValueViewModel riskValueViewModel;

    private List<Service> services;
    private List<UserData> userDatas;

    private String type;
    private String service;
    private boolean isDataCentric;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_value);

        ButterKnife.bind(this);

        services = new ArrayList<>();
        userDatas = new ArrayList<>();

        if (savedInstanceState != null) {
            type = savedInstanceState.getString(PARAM_DATA);
            service = savedInstanceState.getString(PARAM_SERVICE);
        }
        else if (getIntent() != null) {
            type = getIntent().getStringExtra(PARAM_DATA);
            service = getIntent().getStringExtra(PARAM_SERVICE);
        }

        isDataCentric = !(this.type == null || this.type.isEmpty());

        chart.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);

        configureViewModel();

        getServices();

        getUserdatas();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PARAM_SERVICE, service);
        outState.putString(PARAM_DATA, type);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        riskValueViewModel = ViewModelProviders.of(this, viewModelFactory).get(RiskValueViewModel.class);
        riskValueViewModel.init();
    }

    private void updateUserDatas(List<UserData> userData) {
        this.userDatas = userData;
        if (!userDatas.isEmpty() && !services.isEmpty())
            configureChart();
    }

    private void updateServices(List<Service> services) {
        this.services = services;
        if (!userDatas.isEmpty() && !services.isEmpty())
            configureChart();
    }

    private void getServices() {
        riskValueViewModel.getServices().observe(this, this::updateServices);
    }

    private void getUserdatas() {
        riskValueViewModel.getUserDatas().observe(this, this::updateUserDatas);
    }

    private void configureChart() {

        chart.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);

        chart.getDescription().setEnabled(false);

        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_markerview);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        String[] mActivities = getNumberOfTypes();

        setData(mActivities);

        chart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (!isDataCentric)
                    return mActivities[(int) value % mActivities.length];
                else
                    return services.get((int) value % services.size()).getName();
            }
        });
        //xAxis.setTextColor(Color.WHITE);
        //l.setTextColor(Color.WHITE);
    }

    private String[] getNumberOfTypes() {
        Set<String> types = new HashSet<>();
        for (UserData userData : userDatas) {
            types.add(userData.getType());
        }
        return types.toArray(new String[types.size()]);
    }

    private void setData(String[] mActivities) {

        List<IRadarDataSet> sets = new ArrayList<>();

        // Looking if we draw a data centric graph or a service centric graph
        if (!isDataCentric) {
            for (Service service : services) {
                if (service.getName().equals(this.service) || this.service == null || this.service.isEmpty()) {
                    RadarDataSet set1 = new RadarDataSet(getDataEntriesForEachService(mActivities, service), service.getName());
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    set1.setColor(color);
                    set1.setFillColor(color);
                    set1.setDrawFilled(true);
                    set1.setFillAlpha(180);
                    set1.setLineWidth(2f);
                    set1.setDrawHighlightCircleEnabled(true);
                    set1.setDrawHighlightIndicators(false);
                    sets.add(set1);
                }
            }
        }
        else {
            for (String type : mActivities) {
                if (type.equals(this.type) || this.type == null || this.type.isEmpty()) {
                    RadarDataSet set1 = new RadarDataSet(getDataEntriesForEachType(type, services), type);
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    set1.setColor(color);
                    set1.setFillColor(color);
                    set1.setDrawFilled(true);
                    set1.setFillAlpha(180);
                    set1.setLineWidth(2f);
                    set1.setDrawHighlightCircleEnabled(true);
                    set1.setDrawHighlightIndicators(false);
                    sets.add(set1);
                }
            }
        }

        RadarData data = new RadarData(sets);
        data.setValueTextSize(15f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.RED);

        YAxis yAxis = chart.getYAxis();

        if (!isDataCentric)
            yAxis.setLabelCount(mActivities.length, false);
        else
            yAxis.setLabelCount(1, false);

        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0);

        //TODO: 200 HARDCODED (MAX DATA)
        yAxis.setAxisMaximum(getMaximumValue(sets));
        yAxis.setDrawLabels(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        chart.setData(data);
        chart.invalidate();
    }

    private float getMaximumValue(List<IRadarDataSet> sets) {
        float max = 0;
        for (IRadarDataSet set : sets) {
            for (int i = 0; i < set.getEntryCount(); i++) {
                if (max < set.getEntryForIndex(i).getValue()) {
                    max = set.getEntryForIndex(i).getValue();
                }
            }
        }
        return (max);
    }

    private List<RadarEntry> getDataEntriesForEachService(String[] mActivities, Service service) {

        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (String mActivity : mActivities) {
            int val = countTypeForEachService(mActivity, service) + 1;
            entries.add(new RadarEntry(val));
        }
        return entries;
    }

    private List<RadarEntry> getDataEntriesForEachType(String type, List<Service> services) {

        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (Service service : services) {
            int val = countServiceForEachType(type, service) + 1;
            entries.add(new RadarEntry(val));
        }
        return entries;
    }

    private int countTypeForEachService(String mActivity, Service service) {
        int count = 0;
        for (UserData userData : userDatas) {
            if (userData.getServiceId() == service.getId() && userData.getType().equals(mActivity))
                count += 1;
        }
        return count;
    }

    private int countServiceForEachType(String mActivity, Service service) {
        int count = 0;
        for (UserData userData : userDatas) {
            if (userData.getServiceId() == service.getId() && userData.getType().equals(mActivity) && mActivity.equals(userData.getType()))
                count += 1;
        }
        return count;
    }
}

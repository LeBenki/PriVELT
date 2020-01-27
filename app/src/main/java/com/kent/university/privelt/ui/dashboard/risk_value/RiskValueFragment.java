package com.kent.university.privelt.ui.dashboard.risk_value;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.kent.university.privelt.base.BaseFragment;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RiskValueFragment extends BaseFragment {

    @BindView(R.id.chart)
    RadarChart chart;

    private RiskValueViewModel riskValueViewModel;

    private List<Service> services;
    private List<UserData> userDatas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_risk_value, container, false);
        ButterKnife.bind(this, view);

        services = new ArrayList<>();
        userDatas = new ArrayList<>();

        configureViewModel();

        getServices();

        getUserdatas();

        return view;
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
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
//        chart.setBackgroundColor(Color.rgb(60, 65, 82));

        chart.getDescription().setEnabled(false);

        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(getContext(), R.layout.radar_markerview);
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
                return mActivities[(int) value % mActivities.length];
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

        for (Service service : services) {
            RadarDataSet set1 = new RadarDataSet(getDataEntriesForEachService(mActivities, service), service.getName());
            set1.setColor(Color.YELLOW);
            set1.setFillColor(Color.YELLOW);
            set1.setDrawFilled(true);
            set1.setFillAlpha(180);
            set1.setLineWidth(2f);
            set1.setDrawHighlightCircleEnabled(true);
            set1.setDrawHighlightIndicators(false);
            sets.add(set1);
        }

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        YAxis yAxis = chart.getYAxis();
        yAxis.setLabelCount(mActivities.length, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);

        //TODO: 200 HARDCODED (MAX DATA)
        yAxis.setAxisMaximum(200f);
        yAxis.setDrawLabels(false);

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
        return (max + 50);
    }

    private List<RadarEntry> getDataEntriesForEachService(String[] mActivities, Service service) {

        ArrayList<RadarEntry> entries = new ArrayList<>();

        for (int i = 0; i < mActivities.length; i++) {
            int val = countTypeForEachService(mActivities[i], service);
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
}

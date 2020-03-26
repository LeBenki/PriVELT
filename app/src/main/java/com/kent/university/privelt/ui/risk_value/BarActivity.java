package com.kent.university.privelt.ui.risk_value;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseActivity;
import com.kent.university.privelt.injections.Injection;
import com.kent.university.privelt.injections.ViewModelFactory;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.kent.university.privelt.ui.risk_value.RiskValueActivity.PARAM_DATA;
import static com.kent.university.privelt.ui.risk_value.RiskValueActivity.PARAM_SERVICE;
import static com.kent.university.privelt.utils.sentence.SentenceAdapter.capitaliseFirstLetter;

public class BarActivity extends BaseActivity {

    private BarChart chart;

    private RiskValueViewModel riskValueViewModel;

    private List<Service> services;
    private List<UserData> userDatas;

    private String type;
    private String service;
    private boolean isDataCentric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barchart);

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

        if (isDataCentric)
            setTitle(capitaliseFirstLetter(type));
        else
            setTitle(service);

        chart = findViewById(R.id.chart1);

        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

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
            loadData();
    }

    private void updateServices(List<Service> services) {
        this.services = services;
        if (!userDatas.isEmpty() && !services.isEmpty())
            loadData();
    }

    //TODO: refactor les requêtes SQL
    private void getServices() {
        riskValueViewModel.getServices().observe(this, this::updateServices);
    }

    private void getUserdatas() {
        riskValueViewModel.getUserDatas().observe(this, this::updateUserDatas);
    }

    private String[] getNumberOfTypes() {
        Set<String> types = new HashSet<>();
        for (UserData userData : userDatas) {
            types.add(userData.getType());
        }
        return types.toArray(new String[types.size()]);
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

    public void loadData() {

        List<ArrayList<BarEntry>> allValues = new ArrayList<>();

        String[] mActivities = getNumberOfTypes();

        int i = 0;
        if (!isDataCentric) {
            for (Service service : services) {
                if (service.getName().equals(this.service)) {
                    for (String mActivity : mActivities) {
                        int val = countTypeForEachService(mActivity, service);
                        ArrayList<BarEntry> values = new ArrayList<>();
                        values.add(new BarEntry(i++, val));
                        allValues.add(values);
                    }
                    break;
                }
            }
        }
        else {
            for (String type : mActivities) {
                if (type.equals(this.type)) {
                    for (Service service : services) {
                        int val = countServiceForEachType(type, service);
                        ArrayList<BarEntry> values = new ArrayList<>();
                        values.add(new BarEntry(i++, val));
                        allValues.add(values);
                    }
                    break;
                }
            }
        }

        List<IBarDataSet> sets = new ArrayList<>();

        int j = 0;
        for (ArrayList<BarEntry> values : allValues) {
            BarDataSet set = new BarDataSet(values, isDataCentric ? services.get(j).getName() : mActivities[j]);
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            set.setColor(color);
            sets.add(set);
            j++;
        }

        BarData data = new BarData(sets);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);

        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = 0.4f;

        chart.getBarData().setBarWidth(barWidth);

        data.getGroupWidth(groupSpace, barSpace);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(allValues.size() / 2);
        if (allValues.size() > 1)
            chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();
        chart.setFitBars(true);

        chart.invalidate();
    }
}

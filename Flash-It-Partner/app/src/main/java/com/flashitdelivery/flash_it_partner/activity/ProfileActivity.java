package com.flashitdelivery.flash_it_partner.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.util.XAxisMonthFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Lindan on 2016-08-05.
 */
public class ProfileActivity extends AppCompatActivity {

    final private float MAX_VALUE_GRAPH = 12;
    final private float LINE_WIDTH = 3;
    final private float AXIS_MIN_VALUE = 0;
    final private float AXIS_MAX_VALUE = 12;
    final private float CIRCLE_RADIUS = Float.parseFloat("" + 4.5);

    private LineChart driverStats;
    private TextView payChequeAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        payChequeAmount = (TextView) findViewById(R.id.paychequeAmount);
        payChequeAmount.setTypeface(Typeface.DEFAULT_BOLD);

        driverStats = (LineChart) findViewById(R.id.driverStatsGraph);
        setUpStatsChart(driverStats);


    }

    public ArrayList<Entry> createEntryData(ArrayList<Float> plotEntry) {
        ArrayList<Entry> entryData = new ArrayList<Entry>();
        for (int j = 0; j < plotEntry.size(); j = j + 1) {
            entryData.add(new Entry(j + 1, plotEntry.get(j)));

        }

        return entryData;
    }

    private void setUpStatsChart(LineChart lineChart) {
        lineChart.setBorderColor(getResources().getColor(R.color.flashItPartner_purple));

        ArrayList<Float> data = new ArrayList<Float>();
        for (int i = 1; i <= 12; i = i + 1) {
            data.add((float) (i * i));
        }
        ArrayList<Entry> dataEntry = createEntryData(data);
        LineDataSet dataEntrySet = new LineDataSet(dataEntry, "");
        dataEntrySet.setColor(getResources().getColor(R.color.flashItPartner_purple));
        dataEntrySet.setCircleColor(getResources().getColor(R.color.flashItPartner_purple));
        dataEntrySet.setCircleColorHole(Color.WHITE);
        dataEntrySet.setCircleRadius(CIRCLE_RADIUS);
        dataEntrySet.setLineWidth(LINE_WIDTH);

        LineData dataEntryLine = new LineData(dataEntrySet);
        lineChart.setDescription("");
        lineChart.setData(dataEntryLine);

        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(true);

        //x axis
        XAxis perMonth = lineChart.getXAxis();
        perMonth.setValueFormatter(new XAxisMonthFormatter());
        perMonth.setAxisMinValue(AXIS_MIN_VALUE);
        perMonth.setAxisMaxValue(AXIS_MAX_VALUE);
        perMonth.setLabelCount(12);

        perMonth.setDrawGridLines(false);
        perMonth.setGranularity(1f);
        perMonth.setPosition(XAxis.XAxisPosition.BOTTOM);
        perMonth.setAxisMaxValue(MAX_VALUE_GRAPH);
        perMonth.setTypeface(Typeface.DEFAULT_BOLD);
        perMonth.setTextColor(getResources().getColor(R.color.flashItPartner_purple));

        //y axis left
        YAxis totalEarningsPerMonth = lineChart.getAxisLeft();
        totalEarningsPerMonth.setTypeface(Typeface.DEFAULT_BOLD);
        totalEarningsPerMonth.setDrawGridLines(false);

        //y axis right
        totalEarningsPerMonth.setTextColor(getResources().getColor(R.color.flashItPartner_purple));
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setDrawLabels(false);
        rightYAxis.setDrawAxisLine(false);

        lineChart.invalidate();

    }

    private ArrayList<String> getMonthsOfYear() {
        ArrayList<String> months = new ArrayList<String>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");
        return months;
    }

}

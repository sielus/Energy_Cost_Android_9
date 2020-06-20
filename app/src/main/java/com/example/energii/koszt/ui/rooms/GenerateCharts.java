package com.example.energii.koszt.ui.rooms;

import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.manager.DeviceManager;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenerateCharts {
    public void generateChartsInRoom(final View root, String room_name, int numberAfterDot, String defaultCurrency){
        TableLayout tableLayout;
        final ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<Integer> colorList = new ArrayList<>();
        TextView title_summary;
        DeviceManager deviceManager = new DeviceManager(root.getContext());
        final PieChart pieChart;
        BarChart barChart;
        int labelNumberIndex = 0;
        final List<String> deviceName = new ArrayList<>();
        Cursor cursor = deviceManager.getDeviceDetails(room_name);

        tableLayout = root.findViewById(R.id.tableLayout);
        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        title_summary = root.findViewById(R.id.title_summary);

        barChart.getAxisLeft().setEnabled(true);
        barChart.getAxisRight().setEnabled(false);

        if(deviceManager.getRoomDeviceList(room_name).getCount()==0){
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            title_summary.setVisibility(View.GONE);
        }else {
            title_summary.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
        }

        pieChart.invalidate();
        barChart.invalidate();
        deviceName.clear();
        barEntries.clear();
        pieEntry.clear();

        final Random rnd = new Random();

        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {
                pieEntry.add(new PieEntry(cursor.getInt(1), String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) + " kWh"));
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));
                colorList.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                deviceName.add(cursor.getString(0));

                labelNumberIndex++;
            }
        }else if(cursor.getCount() == 1){
            cursor.moveToFirst();

            pieEntry.add(new PieEntry(cursor.getInt(1), String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) + " kWh"));

            colorList.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

            barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));

            deviceName.add(cursor.getString(0));


        }

        BarDataSet barDataSet = new BarDataSet(barEntries, root.getContext().getResources().getString(R.string.chart_daily_costs) + " (" + defaultCurrency + ")");

        barDataSet.setColors(Arrays.asList(Arrays.copyOf(colorList.toArray(), colorList.size(), Integer[].class)));

        XAxis axis = barChart.getXAxis();

        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.getAxisLeft().setTextSize(14);
        barChart.getLegend().setTextSize(14f);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setTouchEnabled(true);
        barChart.setFitBars(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.isAutoScaleMinMaxEnabled();

        axis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        axis.setTextSize(16);
        axis.setDrawGridLines(true);
        axis.setTextColor(Color.WHITE);
        axis.setDrawAxisLine(true);
        axis.setCenterAxisLabels(false);
        axis.setLabelCount(deviceName.size());

        String[] xAxisLabeled = Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabeled));
        axis.setGranularity(1);
        axis.setGranularityEnabled(true);

        barChart.setDrawValueAboveBar(false);
        barDataSet.setDrawValues(false);

        barDataSet.setValueTextSize(14);
        barDataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setText("");
        barChart.getLegend().setEnabled(true);
        barChart.animateY(1000);

        final PieDataSet pieDataSet = new PieDataSet(pieEntry,"");
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieChart.getLegend().setEnabled(false);

        pieDataSet.setColors(Arrays.asList(Arrays.copyOf(colorList.toArray(), colorList.size(), Integer[].class)));
        pieDataSet.setValueLineColor(Color.WHITE);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(14);

        final PieData pieData = new PieData(pieDataSet);

        pieChart.setTouchEnabled(true);

        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(30);
        pieChart.setTransparentCircleRadius(10);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(root.getContext().getResources().getString(R.string.chart_kwh_consumption));
        pieChart.animateY(1000);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(root.getContext(), deviceName.get((int) h.getX()) + " : " + e.getY() + root.getResources().getString(R.string.currency_type), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(root.getContext(), deviceName.get((int) h.getX()) + " : " + e.getY() / 1000 + " kWh", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(YAxis.AxisDependency.LEFT)));

    }

    private ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
    private ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();



    public void generateChart(View root){
        PieChart pieChart;
        BarChart barChart;
        RoomManager roomManager = new RoomManager(root.getContext());
        SettingActivity settingActivity = new SettingActivity();
        int numberAfterDot = settingActivity.getNumberAfterDot(root);
        String defaultCurrency = settingActivity.getDefaultCurrency(root);
        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);
        TextView title_summary = root.findViewById(R.id.title_summary);
        int labelNumberIndex = 0;
        List<String> roomName = new ArrayList<>();
        Cursor cursor = roomManager.getRoomDetails();

        if(roomManager.getRoomList().getCount()==0){
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            title_summary.setVisibility(View.GONE);

        }

        pieChart.invalidate();
        barChart.invalidate();
        roomName.clear();
        barEntries.clear();
        pieEntry.clear();

        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {

                pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f",cursor.getFloat(2)).replace(",","."))));
                roomName.add(cursor.getString(0).replace("_"," ") + " ");
                labelNumberIndex++;

            }
        }else if(cursor.getCount() == 1){

            cursor.moveToFirst();
            pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) ));
            barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f",cursor.getFloat(2)).replace(",","."))));
            roomName.add(cursor.getString(0).replace("_"," ") + " ");

        }else {
            return;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,root.getContext().getResources().getString(R.string.chart_daily_costs) + " (" + defaultCurrency + ")");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);
        XAxis axis = barChart.getXAxis();

        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextSize(14);
        barChart.getAxisLeft().setTextSize(14);

        barChart.getLegend().setTextSize(14f);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setTouchEnabled(false);
        barChart.setFitBars(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);

        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setTextSize(16f);
        axis.setDrawGridLines(true);
        axis.setTextColor(Color.WHITE);
        axis.setDrawAxisLine(true);
        axis.setCenterAxisLabels(false);
        axis.setLabelCount(roomName.size());

        String[] xAxisLabels = Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        axis.setGranularity(1f);
        axis.setGranularityEnabled(true);

        barDataSet.setValueTextSize(14);
        barDataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setText("");
        barChart.getLegend().setEnabled(true);

        barChart.animateY(1000);

        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Data");
        pieChart.getLegend().setEnabled(false);

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueLineColor(R.color.colorAccent);
        pieDataSet.setValueTextSize(14);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(30);
        pieChart.setTransparentCircleRadius(10);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(root.getContext().getResources().getString(R.string.chart_kwh_consumption));
        pieChart.animate();

    }
}


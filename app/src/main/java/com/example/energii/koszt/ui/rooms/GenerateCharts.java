package com.example.energii.koszt.ui.rooms;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class GenerateCharts {

    public void generateChartinRoom(View root,String room_name,int numberAfterDot,String defaultCurrency){
        TableLayout tableLayout;
        ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        TextView title_summary;

        SQLLiteDBHelper sqlLiteDBHelper;
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        PieChart pieChart;
        BarChart barChart;

        tableLayout = root.findViewById(R.id.tableLayout);
        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        title_summary = root.findViewById(R.id.title_summary);
        if(sqlLiteDBHelper.getRoomDeviceList(room_name).getCount()==0){
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
        String[] xAxisLables = new String[]{};
        List<String> roomName = new ArrayList<>();
        roomName.clear();
        xAxisLables = null;
        int labelNumberIndex = 0;
        barEntries.clear();
        pieEntry.clear();
        Cursor cursor = sqlLiteDBHelper.getDeviceDetails(room_name);
        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {
                String name = cursor.getString(0).replace("_"," ");
                if(name.length()>9){
                    pieEntry.add(new PieEntry(cursor.getInt(1), name.substring(0,9) + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
                }else{
                    pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
                }
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));
                if(cursor.getString(0).length() > 9) {
                    roomName.add(cursor.getString(0).substring(0,9));
                }else{
                    roomName.add(cursor.getString(0));
                }
                labelNumberIndex++;
            }
        }else if(cursor.getCount() == 1){
            cursor.moveToFirst();
            String name = cursor.getString(0).replace("_"," ");
            if(name.length()>9){
                pieEntry.add(new PieEntry(cursor.getInt(1), name.substring(0,9) + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
            }else{
                pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
            }
            barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));
            if(cursor.getString(0).length() > 9) {
                roomName.add(cursor.getString(0).substring(0,9));
            }else {
                roomName.add(cursor.getString(0));
            }
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, root.getContext().getResources().getString(R.string.chart_daily_costs) + " (" + defaultCurrency + ")");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
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

        axis.removeAllLimitLines();
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setTextSize(14f);
        axis.setDrawGridLines(true);
        axis.setTextColor(Color.WHITE);
        axis.setDrawAxisLine(true);
        axis.setCenterAxisLabels(false);
        axis.setLabelCount(roomName.size());
        xAxisLables = null;
        xAxisLables = Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
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

        PieDataSet pieDataSet = new PieDataSet(pieEntry,"");
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

    ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

    public void generateChart(View root){
        PieChart pieChart;
        BarChart barChart;

        SQLLiteDBHelper sqlLiteDBHelper;
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());

        SettingActivity settingActivity = new SettingActivity();
        int numberAfterDot = settingActivity.getNumberAfterDot(root);
        String defaultCurrency = settingActivity.getdefaultCurrency(root);

        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);
        TextView title_summary = root.findViewById(R.id.title_summary);

        if(sqlLiteDBHelper.getRoomList().getCount()==0){
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            title_summary.setVisibility(View.GONE);

        }

        pieChart.invalidate();
        barChart.invalidate();
        String[] xAxisLables = new String[]{};
        List<String> roomName = new ArrayList<>();
        roomName.clear();
        xAxisLables = null;
        int labelNumberIndex = 0;
        barEntries.clear();
        pieEntry.clear();
        Cursor cursor = sqlLiteDBHelper.getRoomDetails();
        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {

                pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000))));
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f",cursor.getFloat(2)).replace(",","."))));
                roomName.add(cursor.getString(0).replace("_"," ") + " ");
                labelNumberIndex = labelNumberIndex + 1;

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

        xAxisLables = null;
        xAxisLables = Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
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

    public void invalidateCharts(View view){
        PieChart pieChart;
        BarChart barChart;
        barChart = view.findViewById(R.id.bartChart);
        pieChart = view.findViewById(R.id.pieChart);
        barChart.invalidate();
        pieChart.invalidate();

    }

}

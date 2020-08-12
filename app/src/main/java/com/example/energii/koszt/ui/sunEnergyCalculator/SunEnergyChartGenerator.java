package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.view.View;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;
import java.util.Calendar;

public class SunEnergyChartGenerator {
    public void generateChart(final View root, SunEnergyCalculator sunEnergyCalculator, String moduleCost, String amountModule, String modulePower, int moduleEfficiency){
        double[] profit;
        LineChart lineChart = root.findViewById(R.id.lineChart);
        ArrayList<Entry> yValues = new ArrayList<>();
        profit =  sunEnergyCalculator.calculateProfitability(Double.parseDouble(amountModule.replace(",",".")),
                Integer.parseInt(moduleCost.replace(",",".")),
                Integer.parseInt(modulePower.replace(",",".")),
                moduleEfficiency);

        for (int i = 0; i < profit.length; i++) {
            yValues.add(new Entry(i, (float) profit[i]));
        }
        lineChart.setScaleEnabled(false);
        LineDataSet lineDataSet = new LineDataSet(yValues,"cost");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCircles(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineDataSet.setFillAlpha(100);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawValues(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAyis = lineChart.getAxisRight();
        XAxis xAxis = lineChart.getXAxis();

        final ArrayList xAxisValues = new ArrayList();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int x = 0; x<21; x++){
            xAxisValues.add(String.valueOf(year + x));
        }

        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LimitLine lineZero = new LimitLine(0,root.getResources().getString(R.string.investition_return));
        lineZero.setLineWidth(4f);
        lineZero.setTextColor(root.getResources().getColor(R.color.white));
        lineZero.enableDashedLine(10f,10f,0f);
        lineZero.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        lineZero.setTextSize(15f);
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(lineZero);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        leftAxis.setTextSize(13);
        rightAyis.setTextSize(13);
        xAxis.setAxisMaximum(21);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setTextSize(12f);
        xAxis.setTextColor(root.getResources().getColor(R.color.white));
        rightAyis.setTextColor(root.getResources().getColor(R.color.white));
        leftAxis.setTextColor(root.getResources().getColor(R.color.white));
        final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();

        final SettingActivity settingActivity = new SettingActivity();

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(root.getContext(),root.getResources().getString(R.string.year)  + " " + xAxisValues.get((int) e.getX())
                        + "\n" + root.getResources().getString(R.string.investition_toast) + " " + e.getY() + " " + settingActivity.getDefaultCurrency(root),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }
}

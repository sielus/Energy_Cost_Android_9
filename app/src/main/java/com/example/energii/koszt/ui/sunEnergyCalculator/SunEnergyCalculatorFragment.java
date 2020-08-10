package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.renderscript.Element;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class SunEnergyCalculatorFragment extends Fragment {
    static View root;

    @SuppressLint("CutPasteId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sun_energy_calculator_layout, container, false);
        Button getHomeKWHButton = root.findViewById(R.id.getHomeKWHButton);
        final TextView kwhUsage = root.findViewById(R.id.kwhUsage);
        final SunEnergyCalculator sunEnergyCalculator = new SunEnergyCalculator(root.getContext());

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange, null));
        requireActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.orange));


        final EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);
        final EditText kwhCost = root.findViewById(R.id.kwhCost);
        final SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(getContext());

        final EditText targetPower = root.findViewById(R.id.targetPowerText);

        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);
        final TextView moduleEfficiencyPercectText = root.findViewById(R.id.moduleEfficiencyPercect);

        moduleEfficiency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                moduleEfficiencyPercectText.setText(i + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        moduleEfficiencyPercectText.setText(moduleEfficiency.getProgress() + " %");
        checkIfEmpty(root);
        kwhCost.setText(sqlLiteDBHelper.getVariable("powerCost").getString(0));

        getHomeKWHButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double homeKwhCost = sunEnergyCalculator.getHouseEnergyCost();
                homePowerCostText.setText(String.valueOf(homeKwhCost));
            }
        });

        homePowerCostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newHomePowerCost = homePowerCostText.getText().toString();
                if (!newHomePowerCost.isEmpty()) {
                    if (newHomePowerCost.equals(".")) {
                        homePowerCostText.append("0");
                    } else {
                        kwhUsage.setText(String.valueOf(Double.parseDouble(newHomePowerCost) /
                                Double.valueOf(kwhCost.getText().toString())));

                        doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfEmpty(root);
            }
        });
        kwhCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newkwhCost = kwhCost.getText().toString();
                if (!newkwhCost.isEmpty()) {
                    if (newkwhCost.equals(".")) {
                        kwhCost.append("0");
                    } else {
                        kwhUsage.setText(String.valueOf(Double.parseDouble(kwhCost.getText().toString()) /
                                Double.parseDouble(newkwhCost)));
                        doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        final EditText moduleCostText = root.findViewById(R.id.moduleCostText);
        moduleCostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newmoduleCost = moduleCostText.getText().toString();
                if (!newmoduleCost.isEmpty()) {
                    if (newmoduleCost.equals(".")) {
                        homePowerCostText.append("0");
                    } else {
                        doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        final EditText modulePowerText = root.findViewById(R.id.modulePowerText);
        modulePowerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newmodulePower = modulePowerText.getText().toString();
                if (!newmodulePower.isEmpty()) {
                    doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });








        return root;
    }

    @SuppressLint("DefaultLocale")
    private void doSomeCalc(View root, SunEnergyCalculator sunEnergyCalculator, EditText targetPower, TextView kwhUsage) {
        Double targetValue = Double.parseDouble(kwhUsage.getText().toString()) * 1.1;
        targetPower.setText(String.format("%.2f", targetValue));

        EditText modulePowerText = root.findViewById(R.id.modulePowerText);
        TextView moduleCountText = root.findViewById(R.id.moduleCountText);
        EditText moduleCostText = root.findViewById(R.id.moduleCostText);
        EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);

        TextView instalationCostText = root.findViewById(R.id.instalationCost);

        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);
        String modulePower = modulePowerText.getText().toString();
        if (!modulePower.isEmpty()) {
            moduleCountText.setText(String.valueOf(sunEnergyCalculator.howManyModuleNeed(
                    Integer.parseInt(modulePower),
                    (double) moduleEfficiency.getProgress() / 100,
                    targetValue)));
            instalationCostText.setText(String.valueOf(Double.valueOf(moduleCountText.getText().toString()) *
                    Double.valueOf(moduleCostText.getText().toString())));
                    generateChart(root,sunEnergyCalculator,moduleCostText.getText().toString(),moduleCountText.getText().toString(),modulePowerText.getText().toString(),moduleEfficiency.getProgress(),homePowerCostText.getText().toString());
        }
    }

    private void generateChart(View root, SunEnergyCalculator sunEnergyCalculator, String moduleCost, String ammountModule, String modulePower, int moduleEffeciency, String homePowerCostText){

        double profit[];
        LineChart lineChart = root.findViewById(R.id.lineChart);
        ArrayList<Entry> yValues = new ArrayList<>();
        System.out.println("ammountModule " + Integer.parseInt(ammountModule));
        System.out.println("moduleCost " + Integer.parseInt(moduleCost));
        System.out.println("modulePower " + Integer.parseInt(modulePower));
        System.out.println("moduleEffeciency " + moduleEffeciency);
        System.out.println("homePowerCostText " + Double.parseDouble(homePowerCostText));


        profit =  sunEnergyCalculator.calculateProfitability(Integer.parseInt(ammountModule),Integer.parseInt(moduleCost),Integer.parseInt(modulePower),moduleEffeciency,Double.parseDouble(homePowerCostText));
        System.out.println("dziwne" + moduleEffeciency / 100);
        for (int i = 0; i < profit.length; i++) {

            yValues.add(new Entry(i, (float) profit[i]));
            System.out.println("profit " + profit[i]);
        }

        lineChart.setScaleEnabled(false);

        LineDataSet lineDataSet = new LineDataSet(yValues,"Koszty");
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


        LimitLine lineZero = new LimitLine(0,"Zwrot Inwestycji");
        lineZero.setLineWidth(4f);
        lineZero.setTextColor(getResources().getColor(R.color.white));
        lineZero.enableDashedLine(10f,10f,0f);
        lineZero.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        lineZero.setTextSize(15f);
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(lineZero);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(getResources().getColor(R.color.white));
        lineChart.getLegend().setTextSize(14f);
        leftAxis.setTextSize(13);
        rightAyis.setTextSize(13);
        xAxis.setAxisMaximum(21);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        rightAyis.setTextColor(getResources().getColor(R.color.white));
        leftAxis.setTextColor(getResources().getColor(R.color.white));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private void checkIfEmpty(View root) {
        EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);
        TextInputLayout kwhCostLayout = root.findViewById(R.id.kwhCostLayout);
        EditText kwhCost = root.findViewById(R.id.kwhCost);

        EditText targetPower = root.findViewById(R.id.targetPowerText);

        EditText modulePowerText = root.findViewById(R.id.modulePowerText);
        TextView moduleCountText = root.findViewById(R.id.moduleCountText);
        EditText moduleCostText = root.findViewById(R.id.moduleCostText);
        TextView title = root.findViewById(R.id.textView);
        TextView moduleEfficiencyPercect = root.findViewById(R.id.moduleEfficiencyPercect);
        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);

        TextInputLayout targetPowerLayout = root.findViewById(R.id.targetPowerLayout);
        TextInputLayout modulePowerLayout = root.findViewById(R.id.modulePowerLayout);
        TextInputLayout moduleCostLayout = root.findViewById(R.id.moduleCostLayout);

        TableLayout sunnyTable = root.findViewById(R.id.sunnyTable);

        if (homePowerCostText.getText().toString().isEmpty() || kwhCost.getText().toString().isEmpty()) {

            title.setTextColor(getResources().getColor(R.color.disabled));
            moduleEfficiencyPercect.setTextColor(getResources().getColor(R.color.disabled));

            kwhCost.setTextColor(getResources().getColor(R.color.disabled));
            targetPower.setTextColor(getResources().getColor(R.color.disabled));
            modulePowerText.setTextColor(getResources().getColor(R.color.disabled));
            moduleCountText.setTextColor(getResources().getColor(R.color.disabled));
            moduleCostText.setTextColor(getResources().getColor(R.color.disabled));

            modulePowerLayout.setEnabled(false);
            moduleCostLayout.setEnabled(false);
            title.setEnabled(false);
            sunnyTable.setEnabled(false);
            moduleEfficiencyPercect.setEnabled(false);
            targetPowerLayout.setEnabled(false);
            moduleEfficiency.setEnabled(false);

            kwhCostLayout.setEnabled(false);
        } else {
            title.setTextColor(getResources().getColor(R.color.white));
            moduleEfficiencyPercect.setTextColor(getResources().getColor(R.color.white));

            kwhCost.setTextColor(getResources().getColor(R.color.white));
            targetPower.setTextColor(getResources().getColor(R.color.white));
            modulePowerText.setTextColor(getResources().getColor(R.color.white));
            moduleCountText.setTextColor(getResources().getColor(R.color.white));
            moduleCostText.setTextColor(getResources().getColor(R.color.white));

            modulePowerLayout.setEnabled(true);
            moduleCostLayout.setEnabled(true);
            title.setEnabled(true);
            sunnyTable.setEnabled(true);
            moduleEfficiencyPercect.setEnabled(true);
            targetPowerLayout.setEnabled(true);
            moduleEfficiency.setEnabled(true);

            kwhCostLayout.setEnabled(true);
        }
    }
}
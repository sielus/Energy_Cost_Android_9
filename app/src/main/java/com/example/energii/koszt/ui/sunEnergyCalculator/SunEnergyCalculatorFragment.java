package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.textfield.TextInputLayout;

public class SunEnergyCalculatorFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static View root;

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sun_energy_calculator_layout, container, false);
        Button getHomeKWHButton = root.findViewById(R.id.getHomeKWHButton);
        setNewDefaultCurrency(root);
        final TextView kwhUsage = root.findViewById(R.id.kwhUsage);
        final SunEnergyCalculator sunEnergyCalculator = new SunEnergyCalculator(root.getContext());
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange, null));
        requireActivity().getWindow().setStatusBarColor(requireActivity().getResources().getColor(R.color.orange));
        final EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);
        final EditText kwhCost = root.findViewById(R.id.kwhCost);
        final SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(getContext());
        final TextView targetPower = root.findViewById(R.id.targetPowerText);
        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);
        final TextView moduleEfficiencyPercentText = root.findViewById(R.id.moduleEfficiencyPercect);


        moduleEfficiency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                sunEnergyCalculator.doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                moduleEfficiencyPercentText.setText(i + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        moduleEfficiencyPercentText.setText(moduleEfficiency.getProgress() + "%");
        checkIfEmpty(root);
        kwhCost.setText(sqlLiteDBHelper.getVariable("powerCost").getString(0));

        getHomeKWHButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                double homeKwhCost = sunEnergyCalculator.getHouseEnergyCost();
                homePowerCostText.setText(String.valueOf(String.format("%.2f",homeKwhCost).replace(",",".")));
            }
        });

        homePowerCostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newHomePowerCost = homePowerCostText.getText().toString().replace(",",".");
                if (!newHomePowerCost.isEmpty()) {
                    if (!newHomePowerCost.equals(".")) {
                        kwhUsage.setText(String.format("%.2f", Double.parseDouble(newHomePowerCost.replace(",", ".")) /
                                Double.parseDouble(kwhCost.getText().toString().replace(",", "."))));

                        sunEnergyCalculator.doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
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

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newKwhCost = kwhCost.getText().toString();
                if (!newKwhCost.isEmpty()) {
                    if (!newKwhCost.equals(".")) {
                        if(!homePowerCostText.getText().toString().isEmpty()){
                            kwhUsage.setText(String.format("%.2f",Double.parseDouble(homePowerCostText.getText().toString().replace(",",".")) /
                                    Double.parseDouble(newKwhCost.replace(",","."))));
                            sunEnergyCalculator.doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                        }
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
                String newModuleCost = moduleCostText.getText().toString();
                if (!newModuleCost.isEmpty()) {
                    if (!newModuleCost.equals(".")) {
                        sunEnergyCalculator.doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
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
                String newModulePower = modulePowerText.getText().toString();
                if (!newModulePower.isEmpty()) {
                    sunEnergyCalculator.doSomeCalc(root, sunEnergyCalculator, targetPower, kwhUsage);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return root;
    }
    private void checkIfEmpty(View root) {
        EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);
        TextInputLayout kwhCostLayout = root.findViewById(R.id.kwhCostLayout);
        EditText kwhCost = root.findViewById(R.id.kwhCost);
        TextView targetPower = root.findViewById(R.id.targetPowerText);
        EditText modulePowerText = root.findViewById(R.id.modulePowerText);
        TextView moduleCountText = root.findViewById(R.id.moduleCountText);
        EditText moduleCostText = root.findViewById(R.id.moduleCostText);
        TextView title = root.findViewById(R.id.textView);
        TextView moduleEfficiencyPercent = root.findViewById(R.id.moduleEfficiencyPercect);
        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);
        TextInputLayout modulePowerLayout = root.findViewById(R.id.modulePowerLayout);
        TextInputLayout moduleCostLayout = root.findViewById(R.id.moduleCostLayout);
        LineChart lineChart = root.findViewById(R.id.lineChart);

        TableLayout sunnyTable = root.findViewById(R.id.sunnyTable);
        if (homePowerCostText.getText().toString().isEmpty() || kwhCost.getText().toString().isEmpty()) {
            title.setTextColor(getResources().getColor(R.color.disabled));
            moduleEfficiencyPercent.setTextColor(getResources().getColor(R.color.disabled));
            kwhCost.setTextColor(getResources().getColor(R.color.disabled));
            targetPower.setTextColor(getResources().getColor(R.color.disabled));
            modulePowerText.setTextColor(getResources().getColor(R.color.disabled));
            moduleCountText.setTextColor(getResources().getColor(R.color.disabled));
            moduleCostText.setTextColor(getResources().getColor(R.color.disabled));

            lineChart.setEnabled(false);
            lineChart.setVisibility(View.GONE);
            modulePowerLayout.setEnabled(false);
            moduleCostLayout.setEnabled(false);
            title.setEnabled(false);
            sunnyTable.setEnabled(false);
            moduleEfficiencyPercent.setEnabled(false);
            targetPower.setEnabled(false);
            moduleEfficiency.setEnabled(false);
            kwhCostLayout.setEnabled(false);

        } else {
            title.setTextColor(getResources().getColor(R.color.white));
            moduleEfficiencyPercent.setTextColor(getResources().getColor(R.color.white));
            kwhCost.setTextColor(getResources().getColor(R.color.white));
            targetPower.setTextColor(getResources().getColor(R.color.white));
            modulePowerText.setTextColor(getResources().getColor(R.color.white));
            moduleCountText.setTextColor(getResources().getColor(R.color.white));
            moduleCostText.setTextColor(getResources().getColor(R.color.white));
            modulePowerLayout.setEnabled(true);
            moduleCostLayout.setEnabled(true);
            title.setEnabled(true);
            sunnyTable.setEnabled(true);
            moduleEfficiencyPercent.setEnabled(true);
            targetPower.setEnabled(true);
            moduleEfficiency.setEnabled(true);
            kwhCostLayout.setEnabled(true);
            lineChart.setEnabled(true);
            lineChart.setVisibility(View.VISIBLE);
        }
    }
    @SuppressLint("SetTextI18n")
    public static void setNewDefaultCurrency(View root){
        final SettingActivity settingActivity = new SettingActivity();
        TextView installationCostTitle = root.findViewById(R.id.instalationCostTitleTextView);
        installationCostTitle.setText(root.getResources().getString(R.string.instalation_title) + " " + settingActivity.getDefaultCurrency(root));
    }
}

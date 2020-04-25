package com.example.energii.koszt.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Map;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.getSystemServiceName;

public class HomeFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static View root;
    private int numberDevice;
    private int hours;
    private int minutes;
    private Double energyCost;
    private Double powerValue;
    private SQLLiteDBHelper sqlLiteDBHelper;
    private String powerCost;
    private TextInputEditText inputEnergyCost;
    int numberAfterDot;
    private AdView mAdView;
    String defaultCurrency;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        Button buttonCalcCostEnergy = root.findViewById(R.id.buttonCalcCostEnergy);


        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        SettingActivity settingActivity = new SettingActivity();
        numberAfterDot = settingActivity.getNumberAfterDot(root);

        defaultCurrency = settingActivity.getdefaultCurrency(root);

        mAdView = root.findViewById(R.id.adView);

        TextInputLayout text_field_inputEnergyCost = root.findViewById(R.id.text_field_inputEnergyCost);
        text_field_inputEnergyCost.setHint(getText(R.string.home_energy_cost_hint) + " / " + defaultCurrency);

        ConstraintLayout constraintLayout = root.findViewById(R.id.ConstraintLayoutHome);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(getActivity());
                return false;
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        buttonCalcCostEnergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check(root)) {

                    getInputValue(root);

                    CostEnergy costEnergy = new CostEnergy(powerValue, energyCost, numberDevice, hours, minutes);

                    Map <String, String> costValueMap = costEnergy.sumCostEnergy();

                    displayCostEnergy(costValueMap, root);

                    Toast.makeText(getContext(),"Policzono!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView outputEnergyCostUser = root.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostDay = root.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostMonth = root.findViewById(R.id.OutputEnergyCostMonth);
        outputEnergyCostUser.setText("0 " + defaultCurrency);
        outputEnergyCostDay.setText("0 " + defaultCurrency);
        outputEnergyCostMonth.setText("0 " + defaultCurrency);

        TextInputEditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        TextInputEditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);

        inputEnergyCost = root.findViewById(R.id.inputEnergyCost);

        TextInputEditText inputHours = root.findViewById(R.id.inputhours);
        TextInputEditText inputMinutes = root.findViewById(R.id.inputminutes);

        inputEnergyCost.setText(ViewpowerCostFromDB(sqlLiteDBHelper.getVariable("powerCost")));
        inputPowerValue.addTextChangedListener(textWatcher_text_field_inputPowerValue);
        inputNumberDevices.addTextChangedListener(textWatcher_text_field_inputNumberDevices);
        inputEnergyCost.addTextChangedListener(textWatcher_text_field_inputEnergyCost);
        inputHours.addTextChangedListener(textWatcher_text_field_inputHours);
        inputMinutes.addTextChangedListener(textWatcher_text_field_inputMinutes);
        return root;
    }

    private String ViewpowerCostFromDB(Cursor cursor) {
       powerCost = cursor.getString(0);
       return powerCost;
    }

    private String ViewdefaultCurrencyFromDB(Cursor cursor) {
        defaultCurrency = cursor.getString(0);
        return defaultCurrency;
    }


    public void refresh(View root){
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        inputEnergyCost.setText(ViewpowerCostFromDB(sqlLiteDBHelper.getVariable("powerCost")));
        TextInputLayout text_field_inputEnergyCost = root.findViewById(R.id.text_field_inputEnergyCost);

        text_field_inputEnergyCost.setHint(root.getContext().getResources().getString(R.string.settings_energy_cost_global)  + " / " + ViewdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));

        TextView outputEnergyCostUser = root.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostDay = root.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostMonth = root.findViewById(R.id.OutputEnergyCostMonth);
        outputEnergyCostUser.setText("0 " + ViewdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));
        outputEnergyCostDay.setText("0 " + ViewdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));
        outputEnergyCostMonth.setText("0 " + ViewdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));

    }



    private void getInputValue(View root) {
        TextInputEditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        TextInputEditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        TextInputEditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        TextInputEditText inputHours = root.findViewById(R.id.inputhours);
        TextInputEditText inputMinutes = root.findViewById(R.id.inputminutes);

        powerValue = Double.parseDouble(inputPowerValue.getText().toString());
        energyCost = Double.parseDouble(inputEnergyCost.getText().toString());
        numberDevice = Integer.parseInt(inputNumberDevices.getText().toString());
        hours = Integer.parseInt(inputHours.getText().toString());
        minutes = Integer.parseInt(inputMinutes.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    private void displayCostEnergy(Map<String, String> costValueMap, View root) {
        TextView outputEnergyCostUser = root.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostDay = root.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostMonth = root.findViewById(R.id.OutputEnergyCostMonth);
        TextView outputEnergyCostUserKwh = root.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostDayKwh = root.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostMonthKwh = root.findViewById(R.id.OutputEnergyCostMonthKwh);

        outputEnergyCostUserKwh.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("userCostKwh")).replace(",", ""))) + " kWh");
        outputEnergyCostDayKwh.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("dayCostKwh")).replace(",", ""))) + " kWh");
        outputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("monthCostKwh")).replace(",", ""))) + " kWh");

        outputEnergyCostUser.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("userCost")).replace(",", "")))+ " " + defaultCurrency);
        outputEnergyCostDay.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("dayCost")).replace(",", ""))) + " " + defaultCurrency);
        outputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f",Double.parseDouble(Objects.requireNonNull(costValueMap.get("monthCost")).replace(",", ""))) + " " + defaultCurrency);

        hideKeyboard(requireActivity());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        activity.getWindow().getDecorView().clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean Check(View root) {
        EditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        EditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        EditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        EditText inputHours = root.findViewById(R.id.inputhours);
        EditText inputMinutes = root.findViewById(R.id.inputminutes);
        TextInputLayout text_field_inputPowerValue = root.findViewById(R.id.text_field_inputPowerValue);
        TextInputLayout text_field_inputNumberDevices = root.findViewById(R.id.text_field_inputNumberDevices);
        TextInputLayout text_field_inputEnergyCost = root.findViewById(R.id.text_field_inputEnergyCost);
        TextInputLayout text_field_inputHours = root.findViewById(R.id.text_field_inputHours);
        TextInputLayout text_field_inputMinutes = root.findViewById(R.id.text_field_inputMinutes);
        boolean isNotEmpty = true;


        if(inputPowerValue.getText().toString().isEmpty()) {
            text_field_inputPowerValue.setError("Brak danych!");
            isNotEmpty = false;
        }else {
            text_field_inputPowerValue.setError(null);
        }

        if(inputNumberDevices.getText().toString().isEmpty()) {
            text_field_inputNumberDevices.setError("Brak danych!");
            isNotEmpty = false;
        }else {
            text_field_inputNumberDevices.setError(null);
        }

        if(inputEnergyCost.getText().toString().isEmpty()) {
            text_field_inputEnergyCost.setError("Brak danych!");
            isNotEmpty = false;
        }else {
            text_field_inputEnergyCost.setError(null);
        }

        if(inputHours.getText().toString().isEmpty()) {
            text_field_inputHours.setError("Brak danych!");
            isNotEmpty = false;
        }else {
            text_field_inputHours.setError(null);
        }

        if(inputMinutes.getText().toString().isEmpty()) {
            text_field_inputMinutes.setError("Brak danych!");
            isNotEmpty = false;
        }else {
            text_field_inputMinutes.setError(null);
        }
        return isNotEmpty;
    }

    private TextWatcher textWatcher_text_field_inputPowerValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextInputLayout text_field_inputPowerValue = root.findViewById(R.id.text_field_inputPowerValue);
            text_field_inputPowerValue.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textWatcher_text_field_inputNumberDevices = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextInputLayout text_field_inputNumberDevices = root.findViewById(R.id.text_field_inputNumberDevices);
            text_field_inputNumberDevices.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textWatcher_text_field_inputEnergyCost = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextInputLayout text_field_inputEnergyCost = root.findViewById(R.id.text_field_inputEnergyCost);
            text_field_inputEnergyCost.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textWatcher_text_field_inputHours = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextInputLayout text_field_inputHours = root.findViewById(R.id.text_field_inputHours);
            text_field_inputHours.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textWatcher_text_field_inputMinutes = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextInputLayout text_field_inputMinutes = root.findViewById(R.id.text_field_inputMinutes);
            text_field_inputMinutes.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };






}


package com.example.energii.koszt.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Map;

public class HomeFragment extends Fragment {
    private int numberDevice;
    private int hours;
    private int minutes;
    private Double energyCost;
    private Double powerValue;
    static public View root;
    SQLLiteDBHelper sqlLiteDBHelper;
    String powerCost;
    TextInputEditText inputEnergyCost;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        Button buttonCalcCostEnergy = root.findViewById(R.id.buttonCalcCostEnergy);
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());


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

        TextInputEditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        TextInputEditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        inputEnergyCost = root.findViewById(R.id.inputEnergyCost);

        TextInputEditText inputHours = root.findViewById(R.id.inputhours);
        TextInputEditText inputMinutes = root.findViewById(R.id.inputminutes);
        inputEnergyCost.setText(ViewDataFromDB(sqlLiteDBHelper.getVariable("powerCost")));

        inputPowerValue.addTextChangedListener(textWatcher_text_field_inputPowerValue);
        inputNumberDevices.addTextChangedListener(textWatcher_text_field_inputNumberDevices);
        inputEnergyCost.addTextChangedListener(textWatcher_text_field_inputEnergyCost);
        inputHours.addTextChangedListener(textWatcher_text_field_inputHours);
        inputMinutes.addTextChangedListener(textWatcher_text_field_inputMinutes);
        return root;
    }

    String ViewDataFromDB(Cursor cursor) {
        if(cursor.getCount()==0) {

        }else {
            while(cursor.moveToNext()) {
                powerCost = cursor.getString(0);
            }
        }
        return powerCost;
    }

    public void refrest(View root){
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        inputEnergyCost.setText(ViewDataFromDB(sqlLiteDBHelper.getVariable("powerCost")));

    }

    private void getInputValue(View root) {
        TextInputEditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        TextInputEditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        TextInputEditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        TextInputEditText inputHours = root.findViewById(R.id.inputhours);
        TextInputEditText inputMinutes = root.findViewById(R.id.inputminutes);
        powerValue = Double.parseDouble(inputPowerValue.getText().toString());
        energyCost = Double.parseDouble(inputEnergyCost.getText().toString());
        numberDevice = Integer.parseInt(inputNumberDevices.getText().toString());;
        hours = Integer.parseInt(inputHours.getText().toString());
        minutes = Integer.parseInt(inputMinutes.getText().toString());
    }

    private void displayCostEnergy(Map<String, String> costValueMap,View root) {
        TextView outputEnergyCostUser = root.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostDay = root.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostMonth = root.findViewById(R.id.OutputEnergyCostMonth);

        TextView outputEnergyCostUserKwh = root.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostDayKwh = root.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostMonthKwh = root.findViewById(R.id.OutputEnergyCostMonthKwh);

        outputEnergyCostUserKwh.setText(costValueMap.get("userCostKwh") + " kWh");
        outputEnergyCostDayKwh.setText(costValueMap.get("dayCostKwh") + " kWh");
        outputEnergyCostMonthKwh.setText(costValueMap.get("monthCostKwh") + " kWh");

        outputEnergyCostUser.setText(costValueMap.get("userCost") + " zł");
        outputEnergyCostDay.setText(costValueMap.get("dayCost") + " zł");
        outputEnergyCostMonth.setText(costValueMap.get("monthCost") + " zł");
    }


    private boolean Check(View root) {
        EditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        EditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        EditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        EditText inputHours = root.findViewById(R.id.inputhours);
        EditText inputMinutes = root.findViewById(R.id.inputminutes);
        boolean isNotEmpty = true;

        TextInputLayout text_field_inputPowerValue = (TextInputLayout) root.findViewById(R.id.text_field_inputPowerValue);
        TextInputLayout text_field_inputNumberDevices = (TextInputLayout) root.findViewById(R.id.text_field_inputNumberDevices);
        TextInputLayout text_field_inputEnergyCost = (TextInputLayout) root.findViewById(R.id.text_field_inputEnergyCost);
        TextInputLayout text_field_inputHours = (TextInputLayout) root.findViewById(R.id.text_field_inputHours);
        TextInputLayout text_field_inputMinutes = (TextInputLayout) root.findViewById(R.id.text_field_inputMinutes);


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
            TextInputLayout text_field_inputPowerValue = (TextInputLayout) root.findViewById(R.id.text_field_inputPowerValue);
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
            TextInputLayout text_field_inputNumberDevices = (TextInputLayout) root.findViewById(R.id.text_field_inputNumberDevices);
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
            TextInputLayout text_field_inputEnergyCost = (TextInputLayout) root.findViewById(R.id.text_field_inputEnergyCost);
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
            TextInputLayout text_field_inputHours = (TextInputLayout) root.findViewById(R.id.text_field_inputHours);
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
            TextInputLayout text_field_inputMinutes = (TextInputLayout) root.findViewById(R.id.text_field_inputMinutes);
            text_field_inputMinutes.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}


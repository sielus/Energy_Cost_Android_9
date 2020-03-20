package com.example.energii.koszt.ui.home;

import android.os.Bundle;
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
import java.util.Map;

public class HomeFragment extends Fragment {
    private int numberDevice;
    private int hours;
    private int minutes;
    private Double energyCost;
    private Double powerValue;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        Button buttonCalcCostEnergy = root.findViewById(R.id.buttonCalcCostEnergy);

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

        return root;
    }

    private void getInputValue(View root) {
        EditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        EditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        EditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        EditText inputHours = root.findViewById(R.id.inputhours);
        EditText inputMinutes = root.findViewById(R.id.inputminutes);

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
        outputEnergyCostUser.setText(costValueMap.get("userCost"));
        outputEnergyCostDay.setText(costValueMap.get("dayCost"));
        outputEnergyCostMonth.setText(costValueMap.get("monthCost"));
    }

    private boolean Check(View root) {
        EditText inputPowerValue = root.findViewById(R.id.inputPowerValue);
        EditText inputNumberDevices = root.findViewById(R.id.inputNumberDevices);
        EditText inputEnergyCost = root.findViewById(R.id.inputEnergyCost);
        EditText inputHours = root.findViewById(R.id.inputhours);
        EditText inputMinutes = root.findViewById(R.id.inputminutes);

        boolean isNotEmpty = true;

        if(inputPowerValue.getText().toString().isEmpty()) {
            inputPowerValue.setError("Brak danych!");
            isNotEmpty = false;
        }

        if(inputNumberDevices.getText().toString().isEmpty()) {
            inputNumberDevices.setError("Brak danych!");
            isNotEmpty = false;
        }

        if(inputEnergyCost.getText().toString().isEmpty()) {
            inputEnergyCost.setError("Brak danych!");
            isNotEmpty = false;
        }

        if(inputHours.getText().toString().isEmpty()) {
            inputHours.setError("Brak danych!");
            isNotEmpty = false;
        }

        if(inputMinutes.getText().toString().isEmpty()) {
            inputMinutes.setError("Brak danych!");
            isNotEmpty = false;
        }
        return isNotEmpty;
    }
}


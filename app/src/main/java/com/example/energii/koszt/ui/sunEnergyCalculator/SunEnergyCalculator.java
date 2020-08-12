package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;

public class SunEnergyCalculator extends SQLLiteDBHelper {
    public SunEnergyCalculator(Context context) {
        super(context);
    }

    @SuppressLint("Recycle")
    public double getHouseEnergyCost() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor energyCost;

        query = "SELECT SUM(energy_cost) " +
                "FROM room_list";

        energyCost = dbhRead.rawQuery(query, null);
        energyCost.moveToFirst();

        return energyCost.getDouble(0) * 30;
    }

    public double[] calculateProfitability(double amountOfModule, int costPerModule, int modulePower, double moduleEfficiency) {
        double[] profitability = new double[20];
        double yearsProfit = amountOfModule * modulePower * (moduleEfficiency) / 100  * 0.65;

        profitability[0] =  amountOfModule * costPerModule * -1 * 1.2;

        for(int i = 1; i < 20; i++) {
            profitability[i] = profitability[i - 1] + yearsProfit;
        }

        return profitability;
    }

    public int howManyModuleNeed(double modulePower, double moduleEfficiency, double houseEnergyAmount) {
        double modulePowerGenerator = houseEnergyAmount / (4.38 * 30);

        modulePowerGenerator = modulePowerGenerator + modulePowerGenerator * 0.1 + modulePowerGenerator * (1 - moduleEfficiency);

        double amountOfModule = modulePowerGenerator / (modulePower / 1000);

        return (int)Math.ceil(amountOfModule);
    }

    @SuppressLint("DefaultLocale")
    public void doSomeCalc(View root, SunEnergyCalculator sunEnergyCalculator, TextView targetPower, TextView kwhUsage) {
        double targetValue = Double.parseDouble(kwhUsage.getText().toString().replace(",",".")) * 1.1;
        targetPower.setText(String.format("%.2f", targetValue).replace(",","."));

        EditText modulePowerText = root.findViewById(R.id.modulePowerText);
        TextView moduleCountText = root.findViewById(R.id.moduleCountText);
        EditText moduleCostText = root.findViewById(R.id.moduleCostText);
        EditText homePowerCostText = root.findViewById(R.id.homePowerCostText);
        TextView installationCostText = root.findViewById(R.id.instalationCost);
        SeekBar moduleEfficiency = root.findViewById(R.id.moduleEfficiency);
        String modulePower = modulePowerText.getText().toString();


        if (!modulePower.isEmpty()) {
            if(!moduleCostText.getText().toString().isEmpty()) {


                moduleCountText.setText(String.valueOf(sunEnergyCalculator.howManyModuleNeed(Integer.parseInt(modulePower),
                        (double) (moduleEfficiency.getProgress()) / 100,
                        targetValue)));
                installationCostText.setText(String.valueOf(Double.parseDouble(moduleCountText.getText().toString().replace(",", ".")) *
                        Double.parseDouble(moduleCostText.getText().toString().replace(",", ".")) * 1.2));

                SunEnergyChartGenerator sunEnergyChartGenerator = new SunEnergyChartGenerator();
                sunEnergyChartGenerator.generateChart(root, sunEnergyCalculator, moduleCostText.getText().toString(),
                        moduleCountText.getText().toString(), modulePowerText.getText().toString(), moduleEfficiency.getProgress());

            }
        }
    }
}

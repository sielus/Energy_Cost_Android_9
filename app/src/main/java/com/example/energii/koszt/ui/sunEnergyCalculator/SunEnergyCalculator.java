package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.energii.koszt.ui.SQLLiteDBHelper;

public class SunEnergyCalculator extends SQLLiteDBHelper {
    public SunEnergyCalculator(Context context) {
        super(context);
    }

    @SuppressLint("Recycle")
    public double getHouseEnergyAmount() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor energyAmount;

        query = "SELECT SUM(energy_amount) " +
                "FROM room_list";

        energyAmount = dbhRead.rawQuery(query, null);
        energyAmount.moveToFirst();

        return energyAmount.getDouble(0) / 1000;
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

    public double[] calculateProfitability(double amountOfModule, int costPerModule, int modulePower, double moduleEfficiency, double energyCost) {
        double[] profitability = new double[20];
        double yearsProfit = amountOfModule * modulePower * (100 - moduleEfficiency) / 100  * 0.65;

        profitability[0] =  amountOfModule * costPerModule * -1;

        for(int i = 1; i < 20; i++) {
            profitability[i] = profitability[i - 1] + yearsProfit;
        }

        return profitability;
    }

    public int howManyModuleNeed(double modulePower, double moduleEfficiency, double houseEnergyAmount) {

        System.out.println("modulePower" + modulePower);
        System.out.println("+++houseEnergyAmount" + houseEnergyAmount);

        System.out.println("moduleEfficiency" + moduleEfficiency);

        double modulePowerGenerator = houseEnergyAmount / (4.38 * 30); //4.38 ilość średniych dni nasłończenienia w pl
        modulePowerGenerator = modulePowerGenerator + modulePowerGenerator * 0.1 + modulePowerGenerator * (1 - moduleEfficiency);


        double amountOfModule = modulePowerGenerator / (modulePower / 1000);





        return (int)Math.ceil(amountOfModule);
    }
}

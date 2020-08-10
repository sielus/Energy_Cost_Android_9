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

        return energyCost.getDouble(0);
    }

    public double[] calculateProfitability(int amountOfModule, int costPerModule, int modulePower, double moduleEfficiency, double energyCost) {
        double[] profitability = new double[20];
        double yearsProfit = amountOfModule * modulePower * moduleEfficiency  * energyCost * 1.5;

        profitability[0] =  amountOfModule * costPerModule * -1;

        for(int i = 1; i < 20; i++) {
            profitability[i] = profitability[i - 1] + yearsProfit;
        }

        return profitability;
    }

    public int howManyModuleNeed(int modulePower, double moduleEfficiency, double houseEnergyAmount) {
        int amountOfModule = 0;
        double currencyModulePower = 0;
        houseEnergyAmount *= 12;

        while(currencyModulePower < houseEnergyAmount) {
            currencyModulePower += modulePower * moduleEfficiency * 1.5;
            amountOfModule++;
        }

        return amountOfModule;
    }
}

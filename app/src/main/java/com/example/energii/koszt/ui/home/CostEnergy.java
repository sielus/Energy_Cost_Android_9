package com.example.energii.koszt.ui.home;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class CostEnergy {
    private double powerValue;
    private int numberDevices;
    private double energyCost;
    private double hours;

    CostEnergy(double powerValue, double energyCost, int numberDevices, int hours, int minutes) {
        this.powerValue =  powerValue / 1000;
        this.energyCost = energyCost;
        this.numberDevices = numberDevices;
        this.hours = hours + minutes / 60;
    }

    Map<String, String> sumCostEnergy() {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        Map <String, String> costValueMap = new HashMap<>();
        costValueMap.put("userCost", formatter.format(sumCostEnergyUser()));
        costValueMap.put("dayCost", formatter.format(sumCostEnergyDay()));
        costValueMap.put("monthCost", formatter.format(sumCostEnergyMonth()));

        costValueMap.put("userCostKwh", formatter.format(sumCostEnergyUserKwh()));
        costValueMap.put("dayCostKwh", formatter.format(sumCostEnergyDayKwh()));
        costValueMap.put("monthCostKwh", formatter.format(sumCostEnergyMonthKwh()));

        return costValueMap;
    }


    private float sumCostEnergyUser() {
        return (float) (powerValue * hours * energyCost * numberDevices);
    }

    private float sumCostEnergyDay() {
        return sumCostEnergyUser() * 24;
    }

    private float sumCostEnergyMonth() {
        return sumCostEnergyDay() * 30;
    }

    private float sumCostEnergyUserKwh() {
        return (float) (powerValue * hours * numberDevices);
    }

    private float sumCostEnergyDayKwh() {
        return sumCostEnergyUserKwh() * 24;
    }

    private float sumCostEnergyMonthKwh() {
        return sumCostEnergyDayKwh() * 30;
    }
}

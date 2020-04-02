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
        costValueMap.put("userCost", formatter.format(sumCostEnergyUserZl()));
        costValueMap.put("dayCost", formatter.format(sumCostEnergyDayZl()));
        costValueMap.put("monthCost", formatter.format(sumCostEnergyMonthZl()));

        costValueMap.put("userCostKwh", formatter.format(sumCostEnergyUserKwh()));
        costValueMap.put("dayCostKwh", formatter.format(sumCostEnergyDayKwh()));
        costValueMap.put("monthCostKwh", formatter.format(sumCostEnergyMonthKwh()));

        return costValueMap;
    }


    private float sumCostEnergyUserZl() {
        return (float) (powerValue * hours * energyCost * numberDevices);
    }

    private float sumCostEnergyDayZl() {
        return (float) (powerValue * energyCost * numberDevices * 24);
    }

    private float sumCostEnergyMonthZl() {
        return sumCostEnergyDayZl() * 30;
    }

    private float sumCostEnergyUserKwh() {
        return (float) (powerValue * hours * numberDevices);
    }

    private float sumCostEnergyDayKwh() {
        return (float) (powerValue * numberDevices * 24);
    }

    private float sumCostEnergyMonthKwh() {
        return sumCostEnergyDayKwh() * 30;
    }
}

package com.devdreams.energii.koszt.ui.home;

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

    public CostEnergy(double powerValue, double energyCost, int numberDevices, int hours, int minutes) {
        this.powerValue =  powerValue / 1000;
        this.energyCost = energyCost;
        this.numberDevices = numberDevices;
        this.hours = hours + minutes / 60;
    }

    public Map<String, String> sumCostEnergy() {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        Map <String, String> costValueMap = new HashMap<>();
        costValueMap.put("userCost", formatter.format(sumCostEnergyUserCurrency()));
        costValueMap.put("dayCost", formatter.format(sumCostEnergyDayCurrency()));
        costValueMap.put("monthCost", formatter.format(sumCostEnergyMonthCurrency()));

        costValueMap.put("userCostKwh", formatter.format(sumCostEnergyUserKwh()));
        costValueMap.put("dayCostKwh", formatter.format(sumCostEnergyDayKwh()));
        costValueMap.put("monthCostKwh", formatter.format(sumCostEnergyMonthKwh()));

        return costValueMap;
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

    private float sumCostEnergyUserCurrency() {
        return (float) (powerValue * hours * energyCost * numberDevices);
    }

    private float sumCostEnergyDayCurrency() {
        return (float) (powerValue * energyCost * numberDevices * 24);
    }

    private float sumCostEnergyMonthCurrency() {
        return sumCostEnergyDayCurrency() * 30;
    }
}

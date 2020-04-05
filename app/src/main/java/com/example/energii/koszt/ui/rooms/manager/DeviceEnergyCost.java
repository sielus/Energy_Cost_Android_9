package com.example.energii.koszt.ui.rooms.manager;

import java.util.HashMap;
import java.util.Map;

public class DeviceEnergyCost {
    private String roomName;
    private double energyCost;

    public DeviceEnergyCost(String roomName, double energyCost) {
        this.roomName = roomName + "_device";
        this.energyCost = energyCost;
    }

    public Map<String, String> sumDeviceCostEnergy() {
        Map <String, String> costValueMap = new HashMap<>();

        return costValueMap;
    }

}

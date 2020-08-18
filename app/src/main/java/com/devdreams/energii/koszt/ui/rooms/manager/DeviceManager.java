package com.devdreams.energii.koszt.ui.rooms.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.exception.SQLEnergyCostException;

public class DeviceManager extends SQLLiteDBHelper {

    public DeviceManager(Context context) {
        super(context);
    }

    public void addDevice(String roomName, String deviceName, double powerValue, int hour, int minutes, int deviceNumber, int colorId) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
        if (roomName.isEmpty() || deviceName.isEmpty() || powerValue == 0 || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField(context);
        }else if(hour == 0 && minutes == 0) {
            throw new SQLEnergyCostException.WrongTime(context);
        }

        double energyAmount;
        double energyCost;
        String workTime = hour + ":" + minutes;
        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        double energyCostCurrency;

        energyCost = Double.parseDouble(getVariable("powerCost").getString(0));

        if(minutes != 0) {
            energyAmount = powerValue * deviceNumber * (hour + (double) minutes / 60);
        }else {
            energyAmount = powerValue * deviceNumber * hour;
        }

        energyCostCurrency = energyCost * energyAmount / 1000;

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost", energyCostCurrency);
        contentValues.put("color_id", colorId);

        long resultInsert = dbhWrite.insert(deviceRoomName, null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName,context);
        }

        updateRoomEnergyAmount(changeSpaceInName(roomName), energyAmount);
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), energyCostCurrency);
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

        String query = "SELECT id, " +
                "name, " +
                "power_value, " +
                "work_time, " +
                "device_number, " +
                "color_id " +
                "FROM " + deviceRoomName;

        return dbhRead.rawQuery(query, null);
    }

    public void deleteDevice(String roomName, String deviceName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        String where = " name = ? ";

        updateRoomEnergyAmount(changeSpaceInName(roomName), (getDeviceAmountEnergy(deviceName, deviceRoomName) * -1));
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), (getDeviceCostEnergy(deviceName, deviceRoomName) * -1));
        dbWriter.delete(deviceRoomName, where, new String[]{deviceName});
    }

    public Cursor getDeviceInfo(String roomName, String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

        String query = "SELECT id, " +
                "name, " +
                "power_value, " +
                "work_time, " +
                "device_number, " +
                "color_id " +
                "FROM " + deviceRoomName +
                " WHERE name = ?";

        return dbhRead.rawQuery(query, new String[]{deviceName});
    }

    public void updateDevice(int deviceId, String roomName, String newDeviceName, Double powerValue, int deviceNumber, int hour, int minutes, int colorId) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
        if (roomName.isEmpty() || newDeviceName.isEmpty() || powerValue == 0 || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField(context);
        }else if(hour == 0 && minutes == 0) {
            throw new SQLEnergyCostException.WrongTime(context);
        }

        if (!checkDeviceName(roomName, deviceId).getString(0).equals(newDeviceName)) {
            if(checkDeviceName(roomName, newDeviceName).getCount() == 1) {
                throw new SQLEnergyCostException.DuplicationDevice(newDeviceName,context);
            }
        }

        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        double energyAmount;
        double energyCost;
        double energyCostCurrency;
        String workTime = hour + ":" + minutes;
        String where = "id = ?";
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

        energyCost = Double.parseDouble(getVariable("powerCost").getString(0));

        if(minutes != 0) {
            energyAmount = powerValue * deviceNumber * (hour + (double) minutes / 60);
        }else {
            energyAmount = powerValue * deviceNumber * hour;
        }

        energyCostCurrency = energyCost * energyAmount / 1000;

        contentValues.put("name", newDeviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost", energyCostCurrency);
        contentValues.put("color_id", colorId);

        updateRoomEnergyAmount(changeSpaceInName(roomName), (energyAmount - getDeviceAmountEnergy(deviceId, deviceRoomName)));
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), (energyCostCurrency - getDeviceCostEnergy(deviceId, deviceRoomName)));

        dbWriter.update(deviceRoomName, contentValues, where, new String[]{Integer.toString(deviceId)});
    }

    public Cursor getDeviceDetails(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        Cursor cursor;
        String query;

        query = "SELECT name," +
                "energy_amount," +
                "energy_cost, " +
                "color_id " +
                "FROM " + deviceRoomName;

        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    private Cursor checkDeviceName(String roomName, int deviceId) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = roomName + "_device";
        Cursor cursor;
        String query;

        query = "SELECT name " +
                "FROM " + deviceRoomName +
                " WHERE  id = ?";

        cursor = dbhRead.rawQuery(query, new String[] {String.valueOf(deviceId)});
        cursor.moveToFirst();

        return cursor;
    }

    private Cursor checkDeviceName(String roomName, String newDeviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = roomName + "_device";
        Cursor cursor;
        String query;

        query = "SELECT id " +
                "FROM " + deviceRoomName +
                " WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[] {newDeviceName});
        cursor.moveToFirst();

        return cursor;
    }

    private void updateRoomEnergyAmount(String roomName, double energyAmount) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;

        query = "UPDATE room_list " +
                "SET    energy_amount = energy_amount + (?) " +
                "WHERE  name = ?";

        dbWriter.execSQL(query, new String[] {Double.toString(energyAmount), roomName});
    }

    private void updateRoomEnergyCostCurrency(String roomName, double energyCost){
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;

        query = "UPDATE room_list " +
                "SET    energy_cost = energy_cost + (?) " +
                "WHERE  name = ?";

        dbWriter.execSQL(query, new String[] {String.valueOf(energyCost), roomName});
    }

    @SuppressLint("Recycle")
    private double getDeviceAmountEnergy(String deviceName, String deviceRoomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_amount " +
                "FROM " + deviceRoomName +
                " WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[]{deviceName});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }

    @SuppressLint("Recycle")
    private double getDeviceAmountEnergy(int deviceId, String deviceRoomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_amount " +
                "FROM " + deviceRoomName +
                " WHERE  id = ?";

        cursor = dbhRead.rawQuery(query, new String[]{String.valueOf(deviceId)});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }

    @SuppressLint("Recycle")
    private double getDeviceCostEnergy(String deviceName, String deviceRoomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_cost " +
                "FROM " + deviceRoomName +
                " WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[]{deviceName});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }

    @SuppressLint("Recycle")
    private double getDeviceCostEnergy(int deviceId, String deviceRoomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_cost " +
                "FROM " + deviceRoomName +
                " WHERE  id = ?";

        cursor = dbhRead.rawQuery(query, new String[]{String.valueOf(deviceId)});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }
}
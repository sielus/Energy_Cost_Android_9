package com.devdreams.energii.koszt.ui.settings;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.exception.SQLEnergyCostException;

public class DefaultDeviceManager extends SQLLiteDBHelper {
    public DefaultDeviceManager(Context context) {
        super(context);
    }

    public Cursor getDefaultDeviceList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT NAME," +
                "power_value," +
                "work_time," +
                "device_number " +
                "FROM default_device_settings " +
                "ORDER BY name";

        return dbhRead.rawQuery(query, null);
    }

    public Cursor getDetailsDefaultDevice(String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT name," +
                "power_value," +
                "work_time," +
                "device_number " +
                "FROM  default_device_settings " +
                "WHERE name = ? ";

        return dbhRead.rawQuery(query, new String[]{deviceName});
    }

    public void deleteDefaultDevice(String deviceName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String where = "name = ?";

        dbWriter.delete("default_device_settings", where, new String[]{deviceName});
    }

    public void addDefaultDevice(String deviceName, double powerValue, int hour, int minutes, int deviceNumber) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
        if (deviceName.isEmpty() || powerValue == 0 ||  deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField(context);
        }else if(hour == 0 && minutes == 0) {
            throw new SQLEnergyCostException.WrongTime(context);
        }

        String workTime = hour + ":" + minutes;
        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);

        long resultInsert = dbhWrite.insert("default_device_settings", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName,context);
        }
    }

    public void updateDefaultDevice(String newDeviceName, String oldDeviceName, double powerValue, int hour, int minutes, int deviceNumber) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
        if (newDeviceName.isEmpty() || oldDeviceName.isEmpty() || powerValue == 0 || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField(context);
        }else if(hour == 0 && minutes == 0) {
            throw new SQLEnergyCostException.WrongTime(context);
        }

        if(!oldDeviceName.equals(newDeviceName)) {
            if(checkDeviceName(newDeviceName).getCount() == 1) {
                throw new SQLEnergyCostException.DuplicationDevice(newDeviceName,context);
            }
        }

        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String workTime = hour + ":" + minutes;
        String where = "name = ?";

        contentValues.put("name", newDeviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);

        dbWriter.update("default_device_settings", contentValues, where, new String[] {oldDeviceName});
    }

    @SuppressLint("Recycle")
    public int countDefaultDevices() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor numberOfDevice;

        query = "SELECT COUNT(id) " +
                "FROM   default_device_settings";

        numberOfDevice = dbhRead.rawQuery(query, null);
        numberOfDevice.moveToFirst();

        return numberOfDevice.getInt(0);
    }

    private Cursor checkDeviceName(String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT name " +
                "FROM   default_device_settings " +
                "WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[] {deviceName});
        cursor.moveToFirst();

        return cursor;
    }
}

package com.example.energii.koszt.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cost_energy.db";
    private static final int DB_VERSION = 1;

    public SQLLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String addVariable;

        String roomListTable = "CREATE TABLE room_list " +
                                    "(" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name varchar(100) NOT NULL UNIQUE, " +
                                        "energy_amount real NOT NULL DEFAULT 0," +
                                        "energy_cost_zl real NOT NULL DEFAULT 0" +
                                    ")";
        db.execSQL(roomListTable);

        String configurationVariableTable = "CREATE TABLE configuration_variable " +
                                                "(" +
                                                    " name varchar(100) PRIMARY KEY, " +
                                                    " value varchar(100) NOT NULL UNIQUE " +
                                                ");";
        db.execSQL(configurationVariableTable);

        addVariable = "INSERT INTO configuration_variable (name, value) values (\"powerCost\", \"0.60\")";
        db.execSQL(addVariable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void addRoom(String roomName) throws SQLEnergyCostException.DuplicationRoom, SQLEnergyCostException.EmptyField {
        if (roomName.isEmpty()) {
            throw new SQLEnergyCostException.EmptyField("Nazwa pokoju");
        }

        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", roomName.trim().replace(" ", "_"));

        long resultInsert = dbhWrite.insert("room_list", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationRoom(roomName);
        }

        addDeviceList(roomName);
    }

    public void deleteRoom(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = " name = ? ";

        dbhWrite.delete("room_list", where, new String[]{roomName});
        deleteDeviceList(roomName);
    }

    public Cursor getRoomList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT id, " +
                              "name," +
                              "energy_amount " +
                       "FROM   room_list " +
                       "ORDER BY energy_amount DESC";
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public void addDevice(String roomName, String deviceName, double powerValue, int hour, int minutes, int deviceNumber) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice {
        if (roomName.isEmpty() || deviceName.isEmpty() || powerValue == 0 || (hour == 0 && minutes == 0) || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField();
        }

        double energyAmount;
        double energyCost;
        String workTime = hour + ":" + minutes;
        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String deviceRoomName = roomName.trim().replace(" ", "_") + "_device";
        double energyCostZl;

        energyCost = Double.parseDouble(getVariable("powerCost").getString(0));

        if(minutes != 0) {
            energyAmount = powerValue * deviceNumber * (hour + (double) minutes / 60);
            energyCostZl = energyCost * energyAmount / 1000 * deviceNumber * (hour + (double) minutes / 60);
        }else {
            energyAmount = powerValue * deviceNumber * hour;
            energyCostZl = energyCost * energyAmount / 1000 * deviceNumber * hour;
        }

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost_zl", energyCostZl);

        long resultInsert = dbhWrite.insert(deviceRoomName, null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName);
        }

        updateRoomEnergyAmount(roomName, energyAmount);
        updateRoomEnergyCostZl(roomName, energyCostZl);
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String deviceRoomName = roomName + "_device";

        String query = "SELECT id, " +
                              "name, " +
                              "power_value, " +
                              "work_time, " +
                              "device_number " +
                       "FROM " + deviceRoomName;
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public void deleteDevice(String roomName, String deviceName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String deviceRoomName = roomName + "_device";
        String where = " name = ? ";

        updateRoomEnergyAmount(roomName, (getDeviceAmountEnergy(deviceName, deviceRoomName) * -1));
        updateRoomEnergyCostZl(roomName, (getDeviceCostEnergy(deviceName, deviceRoomName) * -1));
        dbWriter.delete(deviceRoomName, where, new String[]{deviceName});
    }

    public Cursor getDeviceInfo(String roomName, String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String deviceRoomName = roomName + "_device";

        String query = "SELECT id, " +
                              "name, " +
                              "power_value, " +
                              "work_time, " +
                              "device_number " +
                       "FROM " + deviceRoomName +
                      " WHERE name = ?";
        cursor = dbhRead.rawQuery(query, new String[]{deviceName});

        return cursor;
    }

    public void updateDevice(int deviceId, String roomName, String deviceName, double powerValue, int deviceNumber, int hour, int minutes) throws SQLEnergyCostException.EmptyField {
        if (roomName.isEmpty() || deviceName.isEmpty() || powerValue == 0 || (hour == 0 && minutes == 0) || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField();
        }

        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        double energyAmount;
        double energyCost;
        double energyCostZl;
        String workTime = hour + ":" + minutes;
        String where = "id = ?";
        String deviceRoomName = roomName.trim().replace(" ", "_") + "_device";

        energyCost = Double.parseDouble(getVariable("powerCost").getString(0));

        if(minutes != 0) {
            energyAmount = powerValue * deviceNumber * (hour + (double) minutes / 60);
            energyCostZl = energyCost * energyAmount / 1000 * deviceNumber * (hour + (double) minutes / 60);
        }else {
            energyAmount = powerValue * deviceNumber * hour;
            energyCostZl = energyCost * energyAmount / 1000 * deviceNumber * hour;
        }

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost_zl", energyCostZl);

        updateRoomEnergyAmount(roomName, (energyAmount - getDeviceAmountEnergy(deviceName, deviceRoomName)));
        updateRoomEnergyCostZl(roomName, (energyCostZl - getDeviceCostEnergy(deviceName, deviceRoomName)));

        dbWriter.update(deviceRoomName, contentValues, where, new String[]{Integer.toString(deviceId)});
    }

    public void updateRoomName(String oldRoomName, String newRoomName) throws SQLEnergyCostException.DuplicationRoom {
        if(checkNameRoom(newRoomName).getCount() != 0) {
            throw new SQLEnergyCostException.DuplicationRoom(newRoomName);
        }

        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("name", newRoomName);

        dbWriter.update("room_list", contentValues, where, new String[] {oldRoomName});
    }

    public Cursor getVariable(String variableName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT value " +
                "FROM   configuration_variable " +
                "WHERE  name = ?";
        cursor = dbhRead.rawQuery(query, new String[]{variableName});
        cursor.moveToFirst();

        return cursor;
    }

    public void setVariable(String variableName, String value) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("value", value);

        if(variableName.equals("powerCost")) {
            updateAllEnergyCostZl(Double.parseDouble(value));
        }

        dbWriter.update("configuration_variable", contentValues, where, new String[]{variableName});
    }

    public Cursor getRoomDetails() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT name," +
                       "energy_amount," +
                       "energy_cost_zl " +
                "FROM   room_list";

        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public Cursor getDeviceDetails(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = roomName + "_device";
        Cursor cursor;
        String query;

        query = "SELECT name," +
                       "energy_amount," +
                       "energy_cost_zl " +
                "FROM " + deviceRoomName;

        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public Cursor getRoomCost(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_amount," +
                        "energy_cost_zl " +
                "FROM   room_list " +
                "WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[] {roomName});

        return cursor;
    }

    private Cursor checkNameRoom(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT id " +
                "FROM   room_list " +
                "WHERE  name = ?";
        cursor = dbhRead.rawQuery(query, new String[]{roomName});

        return cursor;
    }

    private void updateAllEnergyCostZl(double newEnergyCost) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;
        Cursor cursor;

        query = "UPDATE room_list " +
                "SET    energy_cost_zl = energy_amount * ? ";

        dbWriter.execSQL(query, new String[] {String.valueOf(newEnergyCost / 1000)});

        cursor = getRoomList();

        while(cursor.moveToNext()) {
            String deviceRoomName = cursor.getString(1) + "_device";

            query = "UPDATE " + deviceRoomName +
                  " SET    energy_cost_zl = energy_amount * ?";

            dbWriter.execSQL(query, new String[] {String.valueOf(newEnergyCost / 1000)});
        }
    }

    private void updateRoomEnergyAmount(String roomName, double energyAmount) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;

        query = "UPDATE room_list " +
                "SET    energy_amount = energy_amount + (?) " +
                "WHERE  name = ?";

        dbWriter.execSQL(query, new String[] {Double.toString(energyAmount), roomName});
    }

    private void updateRoomEnergyCostZl(String roomName, double energyCost){
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;

        query = "UPDATE room_list " +
                "SET    energy_cost_zl = energy_cost_zl + (?) " +
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
    private double getDeviceCostEnergy(String deviceName, String deviceRoomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT energy_cost_zl " +
                "FROM " + deviceRoomName +
               " WHERE  name = ?";

        cursor = dbhRead.rawQuery(query, new String[]{deviceName});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }

    private void addDeviceList(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String deviceRoomName = roomName.trim().replace(" ", "_") + "_device";

        String query = "CREATE TABLE " + deviceRoomName +
                            " (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "name varchar(100) NOT NULL UNIQUE, " +
                                "power_value real NOT NULL, " +
                                "work_time text NOT NULL, " +
                                "device_number INTEGER NOT NULL, " +
                                "energy_amount real NOT NULL DEFAULT 0," +
                                "energy_cost_zl real NOT NULL DEFAULT 0" +
                            ")";

        dbhWrite.execSQL(query);
    }

    private void deleteDeviceList(String roomName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String deviceRoomName = roomName +  "_device";
        String query;

        query = "DROP TABLE " + deviceRoomName;

        dbWriter.execSQL(query);
    }
}
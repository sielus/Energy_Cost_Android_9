package com.example.energii.koszt.ui;

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
                                       " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                       " name varchar(100) NOT NULL UNIQUE," +
                                       " energy_consumption INTEGER NOT NULL DEFAULT 0" +
                                    ")";
        db.execSQL(roomListTable);

        String configurationVariableTable = "CREATE TABLE configuration_variable " +
                                                "(" +
                                                   " name varchar(100) PRIMARY KEY, " +
                                                   " value varchar(100) NOT NULL UNIQUE" +
                                                ");";
        db.execSQL(configurationVariableTable);

        addVariable = "INSERT INTO configuration_variable (name, value) values (\"powerCost\", \"0.60\")";
        db.execSQL(addVariable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void addRoom(String roomName) throws SQLEnergyCostException.DuplicationRoom,  SQLEnergyCostException.EmptyField {
        if(roomName.isEmpty()) {
              throw new SQLEnergyCostException.EmptyField("Nazwa pokoju");
        }

        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", roomName);
        long resultInsert = dbhWrite.insert("room_list", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationRoom(roomName);
        }

        addDeviceList(roomName);
    }

    public void deleteRoom(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = "name = ?";

        dbhWrite.delete("room_list", where, new String[] {roomName});
        deleteDeviceList(roomName);
    }

    public Cursor getRoomList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT id, " +
                             " name " +
                       "FROM   room_list";
        cursor = dbhRead.rawQuery(query,null);

        return cursor;
    }

    public void addDevice(String roomName, String deviceName, double powerValue, int hour, int minutes, int deviceNumber) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice {
        if(roomName.isEmpty() ||  deviceName.isEmpty() || powerValue == 0 || (hour == 0 && minutes == 0) || deviceNumber == 0) {
            throw new SQLEnergyCostException.EmptyField();
        }

        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        double amountEnergyNeeded = hour * (minutes / 60) * (powerValue / 1000) ;
        String workTime = hour + ":" + minutes;

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("amount_energy_needed", amountEnergyNeeded);
        long resultInsert = dbhWrite.insert(roomName + "_device", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName);
        }
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT id, " +
                             " name, " +
                             " power_value, " +
                             " work_time, " +
                             " device_number " +
                       "FROM " + roomName + "_device";
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public void deleteDevice(String roomName, String deviceName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        roomName += "_device";
        String where = "name = ?";

        dbWriter.delete(roomName, where, new String[] {deviceName});
    }

    public Cursor getDeviceInfo(String roomName, String deviceName){
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        roomName += "_device";

        String query = "SELECT id," +
                             " name, " +
                             " power_value, " +
                             " work_time, " +
                             " device_number " +
                       "FROM " + roomName +
                      " WHERE  name = ?";
        cursor = dbhRead.rawQuery(query, new String[] {deviceName});

        return cursor;
    }

    public void updateDevice(int deviceId, String roomName, String deviceName, double powerValue, int deviceNumber, int hour, int minutes) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String workTime = hour + ":" + minutes;
        String where = "id = ?";
        double amountEnergyNeeded = hour * (minutes / 60) * (powerValue / 1000) ;

        roomName += "_device";

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("amount_energy_needed", amountEnergyNeeded);

        dbWriter.update(roomName, contentValues, where, new String[] {Integer.toString(deviceId)})   ;
    }

    void setVariable(String variableName, String value) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("value", value);

        dbWriter.update("configuration_variable", contentValues, where, new String[] {variableName});
    }

    public Cursor getVariable(String variableName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT value" +
                       " FROM   configuration_variable" +
                       " WHERE  name = ?";
        cursor = dbhRead.rawQuery(query, new String[] {variableName});

        return cursor;
    }

    private void addDeviceList(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        roomName += "_device";
        String roomDeviceTable = "CREATE TABLE " + roomName +
                                    " (" +
                                       " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                       " name varchar(100) NOT NULL UNIQUE, " +
                                       " power_value real NOT NULL, " +
                                       " work_time text NOT NULL, " +
                                       " device_number int NOT NULL," +
                                       " amount_energy_needed REAL NOT NULL DEFAULT 0" +
                                    ")";

        dbhWrite.execSQL(roomDeviceTable);
    }

    private void deleteDeviceList(String roomName) {
        SQLiteDatabase dbWriter = getWritableDatabase();

        roomName += "_device";
        String deleteDeviceList = "DROP TABLE " + roomName;

        dbWriter.execSQL(deleteDeviceList);
    }
}
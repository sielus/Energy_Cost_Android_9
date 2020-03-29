package com.example.energii.koszt.ui.Roomlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cost_energy.db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public SQLLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        String roomListTable = "CREATE TABLE room_list " +
                                    "(" +
                                    "    id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "    name varchar(100) NOT NULL UNIQUE" +
                                    ")";
        db.execSQL(roomListTable);

        String configurationVariableTable = "CREATE TABLE configuration_variable " +
                                                "(" +
                                                "    name varchar(100) PRIMARY KEY, " +
                                                "    value varchar(100) NOT NULL UNIQUE" +
                                                ");";
        db.execSQL(configurationVariableTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    void addRoom(String roomName) throws SQLEnergyCostException.DuplicationRoom,  SQLEnergyCostException.EmptyField {
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

    void deleteRoom(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = " name = ? ";

        dbhWrite.delete("room_list", where, new String[] {roomName});
        deleteDeviceList(roomName);
    }

    Cursor getRoomList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT id, " +
                       "       name " +
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
        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", hour + ":" + minutes);
        contentValues.put("device_number", deviceNumber);
        long resultInsert = dbhWrite.insert(roomName + "_device", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName);
        }
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;

        String query = "SELECT id, " +
                       "       name, " +
                       "       power_value, " +
                       "       work_time, " +
                       "       device_number " +
                       "FROM " + roomName + "_device";
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public void deleteDevice(String roomName, String deviceName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = " name = ? ";

        dbhWrite.delete(roomName + "_device", where, new String[] {deviceName});
    }

    private void addDeviceList(String roomName) {
        String roomDeviceTable = "CREATE TABLE " + roomName + "_device " +
                                    "(" +
                                    "    id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "    name varchar(100) NOT NULL UNIQUE, " +
                                    "    power_value real NOT NULL, " +
                                    "    work_time text NOT NULL, " +
                                    "    device_number int NOT NULL" +
                                    ")";
        db.execSQL(roomDeviceTable);
    }

    private void deleteDeviceList(String roomName) {
        String roomListTable = "DROP TABLE" + roomName + "_device";
        db.execSQL(roomListTable);
    }

    void getDeviceInfo(String roomName, String deviceName){


    }
}
package com.example.energii.koszt.ui.rooms;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

public class RoomManager extends SQLLiteDBHelper {
    public RoomManager(Context context) {
        super(context);
    }

    public void addRoom(String roomName, int colorId) throws SQLEnergyCostException.DuplicationRoom, SQLEnergyCostException.EmptyField {
        if (roomName.isEmpty()) {
            throw new SQLEnergyCostException.EmptyField(context.getResources().getString(R.string.just_room_name),context);
        }

        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", changeSpaceInName(roomName));
        contentValues.put("color_id", colorId);

        long resultInsert = dbhWrite.insert("room_list", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationRoom(roomName,context);
        }

        addDeviceList(changeSpaceInName(roomName));
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        String query;

        query = "SELECT id, " +
                    "name, " +
                    "power_value, " +
                    "work_time, " +
                    "device_number " +
                    "FROM " + deviceRoomName;
        return dbhRead.rawQuery(query, null);
    }

    public void deleteRoom(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = " name = ? ";

        dbhWrite.delete("room_list", where, new String[]{changeSpaceInName(roomName)});
        deleteDeviceList(changeSpaceInName(roomName));
    }

    public Cursor getRoomList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT id, " +
                "name," +
                "energy_amount, " +
                "color_id " +
                "FROM   room_list " +
                "ORDER BY energy_amount DESC";

        return dbhRead.rawQuery(query, null);
    }

    public void updateRoomName(String oldRoomName, String newRoomName) throws SQLEnergyCostException.DuplicationRoom {

        if(checkNameRoom(changeSpaceInName(newRoomName)).getCount() != 0) {
            throw new SQLEnergyCostException.DuplicationRoom(newRoomName,context);
        }
        SQLiteDatabase dbWriter = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("name", changeSpaceInName(newRoomName));

        dbWriter.update("room_list", contentValues, where, new String[] {changeSpaceInName(oldRoomName)});

        updateNameDeviceList(changeSpaceInName(oldRoomName), changeSpaceInName(newRoomName));
    }

    public void updateRoomColor(String roomName, int colorId) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("color_id", colorId);

        dbWriter.update("room_list", contentValues, where, new String[] {roomName});
    }

    public Cursor getRoomDetails() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT name," +
                "energy_amount," +
                "energy_cost, " +
                "color_id " +
                "FROM   room_list";

        return dbhRead.rawQuery(query, null);
    }

    public Cursor getRoomColor(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT name," +
                "color_id " +
                "FROM room_list " +
                "WHERE name =?";

        cursor = dbhRead.rawQuery(query, new String[] {roomName});
        cursor.moveToFirst();

        return cursor;
    }
    public Cursor getRoomCost(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT energy_amount," +
                "energy_cost " +
                "FROM   room_list " +
                "WHERE  name = ?";

        return dbhRead.rawQuery(query, new String[] {changeSpaceInName(roomName)});
    }

    public Cursor getHouseCost() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT SUM(energy_amount)," +
                "SUM(energy_cost) " +
                "FROM   room_list";

        return dbhRead.rawQuery(query, null);
    }

    public void updateAllEnergyCostCurrency(double newEnergyCost) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;
        Cursor cursor;

        query = "UPDATE room_list " +
                "SET    energy_cost = energy_amount * ? ";

        dbWriter.execSQL(query, new String[] {String.valueOf(newEnergyCost / 1000)});

        cursor = getRoomList();

        while(cursor.moveToNext()) {
            String deviceRoomName = cursor.getString(1) + "_device";

            query = "UPDATE " + deviceRoomName +
                    " SET    energy_cost = energy_amount * ?";

            dbWriter.execSQL(query, new String[] {String.valueOf(newEnergyCost / 1000)});
        }
    }

    private void addDeviceList(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        String query;

        query = "CREATE TABLE " + deviceRoomName +
                " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name varchar(100) NOT NULL UNIQUE, " +
                "power_value NUMERIC(8,2) NOT NULL, " +
                "work_time text NOT NULL, " +
                "device_number NUMERIC(3,0) NOT NULL, " +
                "energy_amount NUMERIC(6,2) NOT NULL DEFAULT 0," +
                "energy_cost NUMERIC(6,2) NOT NULL DEFAULT 0," +
                "color_id NUMERIC(30,0)" +
                ")";

        dbhWrite.execSQL(query);
    }

    private void deleteDeviceList(String roomName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) +  "_device";
        String query;

        query = "DROP TABLE " + deviceRoomName;

        dbWriter.execSQL(query);
    }

    private Cursor checkNameRoom(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;

        query = "SELECT id " +
                "FROM   room_list " +
                "WHERE  name = ?";

        return dbhRead.rawQuery(query, new String[]{changeSpaceInName(roomName)});
    }

    @SuppressLint("Recycle")
    private void updateNameDeviceList(String oldRoomName, String newRoomName) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        String query;
        String oldDeviceRoomName = changeSpaceInName(oldRoomName) + "_device";
        String newDeviceRoomName = changeSpaceInName(newRoomName) + "_device";

        query = "ALTER TABLE " + oldDeviceRoomName +
                " RENAME TO " + newDeviceRoomName;

        dbWriter.execSQL(query);
    }
}
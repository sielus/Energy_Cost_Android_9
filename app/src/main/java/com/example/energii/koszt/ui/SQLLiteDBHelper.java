package com.example.energii.koszt.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cost_energy.db";
    private static final int DB_VERSION = 1;
    private final Context context;

    public SQLLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String addVariable;
        String numberAfterDot;
        String defaultDevice;

        String roomListTable = "CREATE TABLE room_list " +
                                    "(" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name varchar(100) NOT NULL UNIQUE, " +
                                        "energy_amount NUMERIC(6,2) NOT NULL DEFAULT 0," +
                                        "energy_cost_zl NUMERIC(6,2) NOT NULL DEFAULT 0" +
                                    ")";
        db.execSQL(roomListTable);

        String configurationVariableTable = "CREATE TABLE configuration_variable " +
                                                "(" +
                                                    " name varchar(100) PRIMARY KEY, " +
                                                    " value varchar(100) NOT NULL UNIQUE " +
                                                ");";
        db.execSQL(configurationVariableTable);

        String defaultDeviceTable = "CREATE TABLE default_device_settings " +
                                    "(" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name varchar(100) NOT NULL UNIQUE, " +
                                        "power_value NUMERIC(8,2) NOT NULL, " +
                                        "work_time text NOT NULL, " +
                                        "device_number NUMERIC(3,0) NOT NULL " +
                                    ")";
        db.execSQL(defaultDeviceTable);

        addVariable = "INSERT INTO configuration_variable (name, value) values (\"powerCost\", \"0.60\"), (\"defaultCurrency\", \"zł\")"; // zmienic domyslna walute, wklej mi tu funkcje do pobrania stringu jak w domyslnych
        numberAfterDot = "INSERT INTO configuration_variable (name, value) values (\"numberAfterDot\", \"2\")";
        defaultDevice = "INSERT INTO default_device_settings (name, power_value, work_time, device_number) values (?, 15, \"2:0\", 1)," +
                                                                                                                 "(?, 0.1, \"24:0\", 1)," +
                                                                                                                 "(?, 35, \"24:0\", 1)," +
                                                                                                                 "(?, 8, \"7:0\", 1)," +
                                                                                                                 "(?, 20, \"7:0\", 1)," +
                                                                                                                 "(?, 250, \"8:0\", 1)," +
                                                                                                                 "(?, 130, \"8:0\", 1)," +
                                                                                                                 "(?, 150, \"12:0\", 1)," +
                                                                                                                 "(?, 800, \"0:5\", 1)," +
                                                                                                                 "(?, 100, \"4:0\", 1)," +
                                                                                                                 "(?, 1.4, \"24:0\", 1)," +
                                                                                                                 "(?, 3, \"24:0\", 1)," +
                                                                                                                 "(?, 10, \"1:0\", 1)," +
                                                                                                                 "(?, 20, \"0:20\", 1)," +
                                                                                                                 "(?, 40, \"10:0\", 1)," +
                                                                                                                 "(?, 2, \"24:0\", 1)";
        db.execSQL(defaultDevice, new String[] {context.getResources().getString(R.string.default_device_phone_charger),
                                                context.getResources().getString(R.string.default_device_phone_charger_idle),
                                                context.getResources().getString(R.string.default_device_fridge),
                                                context.getResources().getString(R.string.default_device_led_light),
                                                context.getResources().getString(R.string.default_device_energy_saving_light),
                                                context.getResources().getString(R.string.default_device_pc),
                                                context.getResources().getString(R.string.default_device_laptop),
                                                context.getResources().getString(R.string.default_device_tv_qled),
                                                context.getResources().getString(R.string.default_device_microvave),
                                                context.getResources().getString(R.string.default_device_game_console),
                                                context.getResources().getString(R.string.default_device_game_console_idle),
                                                context.getResources().getString(R.string.default_device_router),
                                                context.getResources().getString(R.string.default_device_night_light_led),
                                                context.getResources().getString(R.string.default_device_vacuum_cleaner),
                                                context.getResources().getString(R.string.default_device_amp),
                                                context.getResources().getString(R.string.default_device_amp_idle)});
        db.execSQL(addVariable);
        db.execSQL(numberAfterDot);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void addRoom(String roomName) throws SQLEnergyCostException.DuplicationRoom, SQLEnergyCostException.EmptyField {
        if (roomName.isEmpty()) {
            throw new SQLEnergyCostException.EmptyField(context.getResources().getString(R.string.just_room_name),context);
        }

        SQLiteDatabase dbhWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", changeSpaceInName(roomName));

        long resultInsert = dbhWrite.insert("room_list", null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationRoom(roomName,context);
        }

        addDeviceList(changeSpaceInName(roomName));
    }

    public void deleteRoom(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String where = " name = ? ";

        dbhWrite.delete("room_list", where, new String[]{changeSpaceInName(roomName)});
        deleteDeviceList(changeSpaceInName(roomName));
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

    public void addDevice(String roomName, String deviceName, double powerValue, int hour, int minutes, int deviceNumber) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
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
            energyCostCurrency = energyCost * energyAmount / 1000;
        }else {
            energyAmount = powerValue * deviceNumber * hour;
            energyCostCurrency = energyCost * energyAmount / 1000;
        }

        contentValues.put("name", deviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost_zl", energyCostCurrency);

        long resultInsert = dbhWrite.insert(deviceRoomName, null, contentValues);

        if (resultInsert == -1) {
            throw new SQLEnergyCostException.DuplicationDevice(deviceName,context);
        }

        updateRoomEnergyAmount(changeSpaceInName(roomName), energyAmount);
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), energyCostCurrency);
    }

    public Cursor getRoomDeviceList(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

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
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
        String where = " name = ? ";

        updateRoomEnergyAmount(changeSpaceInName(roomName), (getDeviceAmountEnergy(deviceName, deviceRoomName) * -1));
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), (getDeviceCostEnergy(deviceName, deviceRoomName) * -1));
        dbWriter.delete(deviceRoomName, where, new String[]{deviceName});
    }

    public Cursor getDeviceInfo(String roomName, String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

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

    public void updateDevice(int deviceId, String roomName, String newDeviceName, double powerValue, int deviceNumber, int hour, int minutes) throws SQLEnergyCostException.EmptyField, SQLEnergyCostException.DuplicationDevice, SQLEnergyCostException.WrongTime {
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
            energyCostCurrency = energyCost * energyAmount / 1000;
        }else {
            energyAmount = powerValue * deviceNumber * hour;
            energyCostCurrency = energyCost * energyAmount / 1000;
        }

        contentValues.put("name", newDeviceName);
        contentValues.put("power_value", powerValue);
        contentValues.put("work_time", workTime);
        contentValues.put("device_number", deviceNumber);
        contentValues.put("energy_amount", energyAmount);
        contentValues.put("energy_cost_zl", energyCostCurrency);

        updateRoomEnergyAmount(changeSpaceInName(roomName), (energyAmount - getDeviceAmountEnergy(deviceId, deviceRoomName)));
        updateRoomEnergyCostCurrency(changeSpaceInName(roomName), (energyCostCurrency - getDeviceCostEnergy(deviceId, deviceRoomName)));

        dbWriter.update(deviceRoomName, contentValues, where, new String[]{Integer.toString(deviceId)});
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
            updateAllEnergyCostCurrency(Double.parseDouble(value));
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
        String deviceRoomName = changeSpaceInName(roomName) + "_device";
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

        cursor = dbhRead.rawQuery(query, new String[] {changeSpaceInName(roomName)});

        return cursor;
    }

    public Cursor getHouseCost() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT SUM(energy_amount)," +
                       "SUM(energy_cost_zl) " +
                "FROM   room_list";
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public Cursor getDefaultDeviceList() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT NAME," +
                       "power_value," +
                       "work_time," +
                       "device_number " +
                "FROM default_device_settings";
        cursor = dbhRead.rawQuery(query, null);

        return cursor;
    }

    public Cursor getDetailsDefaultDevice(String deviceName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT name," +
                       "power_value," +
                       "work_time," +
                       "device_number " +
                "FROM  default_device_settings " +
                "WHERE name = ?";
        cursor = dbhRead.rawQuery(query, new String[] {deviceName});

        return cursor;
    }

    @SuppressLint("Recycle")
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



    private String changeSpaceInName(String name) {
        return name.trim().replace(" ", "_");
    }

    private Cursor checkNameRoom(String roomName) {
        SQLiteDatabase dbhRead = getReadableDatabase();
        Cursor cursor;
        String query;

        query = "SELECT id " +
                "FROM   room_list " +
                "WHERE  name = ?";
        cursor = dbhRead.rawQuery(query, new String[]{changeSpaceInName(roomName)});

        return cursor;
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

    private void updateAllEnergyCostCurrency(double newEnergyCost) {
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

    private void updateRoomEnergyCostCurrency(String roomName, double energyCost){
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

        query = "SELECT energy_cost_zl " +
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

        query = "SELECT energy_cost_zl " +
                "FROM " + deviceRoomName +
                " WHERE  id = ?";

        cursor = dbhRead.rawQuery(query, new String[]{String.valueOf(deviceId)});
        cursor.moveToFirst();

        return cursor.getDouble(0);
    }

    private void addDeviceList(String roomName) {
        SQLiteDatabase dbhWrite = getWritableDatabase();
        String deviceRoomName = changeSpaceInName(roomName) + "_device";

        String query = "CREATE TABLE " + deviceRoomName +
                            " (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "name varchar(100) NOT NULL UNIQUE, " +
                                "power_value NUMERIC(8,2) NOT NULL, " +
                                "work_time text NOT NULL, " +
                                "device_number NUMERIC(3,0) NOT NULL, " +
                                "energy_amount NUMERIC(6,2) NOT NULL DEFAULT 0," +
                                "energy_cost_zl NUMERIC(6,2) NOT NULL DEFAULT 0" +
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
}
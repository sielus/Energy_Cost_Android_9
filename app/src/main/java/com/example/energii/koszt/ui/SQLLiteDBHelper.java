package com.example.energii.koszt.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.RoomManager;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    public final Context context;
    private static final String DB_NAME = "cost_energy.db";
    private static final int DB_VERSION = 1;

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
                                        "energy_amount NUMERIC(60,2) NOT NULL DEFAULT 0," +
                                        "energy_cost NUMERIC(60,2) NOT NULL DEFAULT 0," +
                                        "color_id NUMERIC(30,0)" +
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
                                        "power_value NUMERIC(60,2) NOT NULL, " +
                                        "work_time text NOT NULL, " +
                                        "device_number NUMERIC(3,0) NOT NULL " +
                                    ")";
        db.execSQL(defaultDeviceTable);

        addVariable = "INSERT INTO configuration_variable (name, value) values (\"powerCost\", \"0.60\"), (\"defaultCurrency\", ?)"; // zmienic domyslna walute, wklej mi tu funkcje do pobrania stringu jak w domyslnych
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
        db.execSQL(addVariable, new String[] {context.getResources().getString(R.string.currency_type)});
        db.execSQL(numberAfterDot);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @SuppressLint("Recycle")
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
        RoomManager roomManager = new RoomManager(context);
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("value", value);

        if(variableName.equals("powerCost")) {
            roomManager.updateAllEnergyCostCurrency(Double.parseDouble(value));
        }

        dbWriter.update("configuration_variable", contentValues, where, new String[]{variableName});
    }

    protected String changeSpaceInName(String name) {
        return name.trim().replace(" ", "_");
    }
}
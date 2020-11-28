package com.devdreams.energii.koszt.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;
import com.devdreams.energii.koszt.ui.rooms.RoomManager;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    public Context context = RoomListFragment.root.getContext();
    private static final String DB_NAME = "cost_energy.db";
    private static final int DB_VERSION = 4;

    public SQLLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    private String defaultDeviceTable =
            "CREATE TABLE default_device_settings " +
                    "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name varchar(100) NOT NULL UNIQUE, " +
                    "power_value NUMERIC(60,2) NOT NULL, " +
                    "work_time text NOT NULL, " +
                    "device_number NUMERIC(3,0) NOT NULL " +
                    ")";

    private String defaultDevice = "INSERT INTO default_device_settings (name, power_value, work_time, device_number) values (?, 15, \"2:0\", 1)," +
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
            "(?, 2, \"24:0\", 1)," +

            "(?, 600, \"2:0\", 1)," +
            "(?, 3000, \"1:0\", 1)," +
            "(?, 1500, \"0:30\", 1)," +
            "(?, 1500, \"3:30\", 1)," +
            "(?, 250, \"2:0\", 1)," +
            "(?, 10, \"2:0\", 1)," +
            "(?, 2000, \"3:0\", 1)," +
            "(?, 2400, \"1:0\", 1)," +
            "(?, 500, \"0:10\", 1)," +
            "(?, 1200, \"0:5\", 1)," +
            "(?, 1200, \"0:30\", 1)," +
            "(?, 1800, \"2:0\", 1)," +
            "(?, 700, \"7:0\", 1)";

    String defaultRoomListTable = "CREATE TABLE IF NOT EXISTS default_room_list" +
            "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name varchar(100) NOT NULL UNIQUE, " +
            "device_list varchar(3000) NOT NULL " +
            ")";

    String defaultDevicesInDefaultKitchen =
            context.getResources().getString(R.string.default_device_fridge) + ";"
                    + context.getResources().getString(R.string.default_device_small_tv) + ";"
                    + context.getResources().getString(R.string.default_device_induction_cooker) + ";"
                    + context.getResources().getString(R.string.default_device_oven) + ";"
                    + context.getResources().getString(R.string.default_device_Mixer) + ";"
                    + context.getResources().getString(R.string.default_device_kettle) + ";"
                    + context.getResources().getString(R.string.default_device_dishwasher) + ";"
                    + context.getResources().getString(R.string.default_device_toaster);

    String defaultDevicesInDefaultBathroom =
            context.getResources().getString(R.string.default_device_washing_wachine) + ";"
                    + context.getResources().getString(R.string.default_device_laudry_dryer) + ";"
                    + context.getResources().getString(R.string.default_device_electric_heater) + ";"
                    + context.getResources().getString(R.string.default_device_hair_dryer);

    String defaultDevicesInDefaultBedroom =
            context.getResources().getString(R.string.default_device_led_light) + ";"
                    + context.getResources().getString(R.string.default_device_night_light_led) + ";"
                    + context.getResources().getString(R.string.default_device_phone_charger_idle) + ";"
                    + context.getResources().getString(R.string.default_device_small_tv) + ";"
                    + context.getResources().getString(R.string.default_device_radio) + ";"
                    + context.getResources().getString(R.string.default_device_phone_charger);

    String defaultDevicesInDefaultUtilityRoom =
            context.getResources().getString(R.string.default_device_washing_wachine) + ";"
                    + context.getResources().getString(R.string.default_device_air_conditioner) + ";"
                    + context.getResources().getString(R.string.default_device_laudry_dryer);

    String defaultDevicesInDefaultLivingRoom =
            context.getResources().getString(R.string.default_device_amp) + ";"
                    + context.getResources().getString(R.string.default_device_amp_idle) + ";"
                    + context.getResources().getString(R.string.default_device_game_console_idle) + ";"
                    + context.getResources().getString(R.string.default_device_game_console) + ";"
                    + context.getResources().getString(R.string.default_device_led_light);

    String defaultDevicesInDefaultHomeOffice =
            context.getResources().getString(R.string.default_device_pc) + ";"
                    + context.getResources().getString(R.string.default_device_phone_charger) + ";"
                    + context.getResources().getString(R.string.default_device_phone_charger_idle) + ";"
                    + context.getResources().getString(R.string.default_device_router) + ";"
                    + context.getResources().getString(R.string.default_device_led_light);


    String defaultRoomNameKitchen = context.getResources().getString(R.string.default_roomname_kitchen);
    String defaultRoomNameBathroom = context.getResources().getString(R.string.default_roomname_bathroom);
    String defaultRoomNameBedroom = context.getResources().getString(R.string.default_roomname_bedroom);
    String defaultRoomNameUtilityRoom = context.getResources().getString(R.string.default_roomname_utilityroom);
    String defaultRoomNameLivingRoom = context.getResources().getString(R.string.default_roomname_livingroom);
    String defaultRoomNameHomeOffice = context.getResources().getString(R.string.default_roomname_homeoffice);

    String defaultDevicesInRoom = "INSERT INTO default_room_list (name, device_list) values (\"" + defaultRoomNameKitchen + "\", \"" + defaultDevicesInDefaultKitchen + "\"), " +
            "(\"" + defaultRoomNameBathroom + "\", \"" + defaultDevicesInDefaultBathroom + " \"), " +
            "(\"" + defaultRoomNameBedroom + "\", \"" + defaultDevicesInDefaultBedroom + " \"), " +
            "(\"" + defaultRoomNameUtilityRoom + "\", \"" + defaultDevicesInDefaultUtilityRoom + " \"), " +
            "(\"" + defaultRoomNameLivingRoom + "\", \"" + defaultDevicesInDefaultLivingRoom.replace('"', '\"') + " \"), " +
            "(\"" + defaultRoomNameHomeOffice + "\", \"" + defaultDevicesInDefaultHomeOffice + " \") ";


    String firstRunTutFirst = "INSERT INTO configuration_variable (name, value) values (\"runTutFir\", \"false\")";
    String addVariable = "INSERT INTO configuration_variable (name, value) values (\"powerCost\", \"0.60\"), (\"defaultCurrency\", ?)";
    String numberAfterDot = "INSERT INTO configuration_variable (name, value) values (\"numberAfterDot\", \"2\")";
    String token = "INSERT INTO configuration_variable (name, value) values (\"token\", \"\")";
    String adsEnable = "INSERT INTO configuration_variable (name, value) values (\"adsEnable\", \"Y\")";

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String addVariable;
//        String numberAfterDot;
//       // String defaultDevice;
//        String firstRunTutFirst;
//        String token;
//        String adsEnable;


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
                " value varchar(300) NOT NULL UNIQUE " +
                ");";
        db.execSQL(configurationVariableTable);

//        String defaultDeviceTable = "CREATE TABLE default_device_settings " +
//                "(" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "name varchar(100) NOT NULL UNIQUE, " +
//                "power_value NUMERIC(60,2) NOT NULL, " +
//                "work_time text NOT NULL, " +
//                "device_number NUMERIC(3,0) NOT NULL " +
//                ")";
        db.execSQL(defaultDeviceTable);

//        String defaultRoomListTable = "CREATE TABLE IF NOT EXISTS default_room_list" +
//                "(" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "name varchar(100) NOT NULL UNIQUE, " +
//                "device_list varchar(3000) NOT NULL " +
//                ")";
        db.execSQL(defaultRoomListTable);
        
//        String defaultDevicesInDefaultKitchen =
//                context.getResources().getString(R.string.default_device_fridge) + ";"
//                    + context.getResources().getString(R.string.default_device_small_tv) + ";"
//                    + context.getResources().getString(R.string.default_device_induction_cooker) + ";"
//                    + context.getResources().getString(R.string.default_device_oven) + ";"
//                    + context.getResources().getString(R.string.default_device_Mixer) + ";"
//                    + context.getResources().getString(R.string.default_device_kettle) + ";"
//                    + context.getResources().getString(R.string.default_device_dishwasher) + ";"
//                    + context.getResources().getString(R.string.default_device_toaster);
//
//        String defaultDevicesInDefaultBathroom =
//                  context.getResources().getString(R.string.default_device_washing_wachine) + ";"
//                    + context.getResources().getString(R.string.default_device_laudry_dryer) + ";"
//                    + context.getResources().getString(R.string.default_device_electric_heater) + ";"
//                    + context.getResources().getString(R.string.default_device_hair_dryer);
//
//        String defaultDevicesInDefaultBedroom =
//                context.getResources().getString(R.string.default_device_led_light) + ";"
//                    + context.getResources().getString(R.string.default_device_night_light_led) + ";"
//                    + context.getResources().getString(R.string.default_device_phone_charger_idle) + ";"
//                    + context.getResources().getString(R.string.default_device_small_tv) + ";"
//                    + context.getResources().getString(R.string.default_device_radio) + ";"
//                    + context.getResources().getString(R.string.default_device_phone_charger);
//
//        String defaultDevicesInDefaultUtilityRoom =
//                context.getResources().getString(R.string.default_device_washing_wachine) + ";"
//                    + context.getResources().getString(R.string.default_device_air_conditioner) + ";"
//                    + context.getResources().getString(R.string.default_device_laudry_dryer);
//
//        String defaultDevicesInDefaultLivingRoom =
//                context.getResources().getString(R.string.default_device_amp) + ";"
//                    + context.getResources().getString(R.string.default_device_amp_idle) + ";"
//                    + context.getResources().getString(R.string.default_device_game_console_idle) + ";"
//                    + context.getResources().getString(R.string.default_device_game_console) + ";"
//                    + context.getResources().getString(R.string.default_device_led_light);
//
//        String defaultDevicesInDefaultHomeOffice =
//                context.getResources().getString(R.string.default_device_pc) + ";"
//                    + context.getResources().getString(R.string.default_device_phone_charger) + ";"
//                    + context.getResources().getString(R.string.default_device_phone_charger_idle) + ";"
//                    + context.getResources().getString(R.string.default_device_router) + ";"
//                    + context.getResources().getString(R.string.default_device_led_light);
//
//        String defaultRoomNameKitchen = context.getResources().getString(R.string.default_roomname_kitchen);
//        String defaultRoomNameBathroom = context.getResources().getString(R.string.default_roomname_bathroom);
//        String defaultRoomNameBedroom = context.getResources().getString(R.string.default_roomname_bedroom);
//        String defaultRoomNameUtilityRoom = context.getResources().getString(R.string.default_roomname_utilityroom);
//        String defaultRoomNameLivingRoom = context.getResources().getString(R.string.default_roomname_livingroom);
//        String defaultRoomNameHomeOffice = context.getResources().getString(R.string.default_roomname_homeoffice);


//        String defaultDevicesInRoom = "INSERT INTO default_room_list (name, device_list) values (\"" + defaultRoomNameKitchen + "\", \"" + defaultDevicesInDefaultKitchen + "\"), " +
//                "(\"" + defaultRoomNameBathroom + "\", \"" + defaultDevicesInDefaultBathroom + " \"), " +
//                "(\"" + defaultRoomNameBedroom + "\", \"" + defaultDevicesInDefaultBedroom + " \"), " +
//                "(\"" + defaultRoomNameUtilityRoom + "\", \"" + defaultDevicesInDefaultUtilityRoom + " \"), " +
//                "(\"" + defaultRoomNameLivingRoom + "\", \"" + defaultDevicesInDefaultLivingRoom.replace('"','\"') + " \"), " +
//                "(\"" + defaultRoomNameHomeOffice + "\", \"" + defaultDevicesInDefaultHomeOffice + " \") ";

//        defaultDevice = "INSERT INTO default_device_settings (name, power_value, work_time, device_number) values (?, 15, \"2:0\", 1)," +
//                "(?, 0.1, \"24:0\", 1)," +
//                "(?, 35, \"24:0\", 1)," +
//                "(?, 8, \"7:0\", 1)," +
//                "(?, 20, \"7:0\", 1)," +
//                "(?, 250, \"8:0\", 1)," +
//                "(?, 130, \"8:0\", 1)," +
//                "(?, 150, \"12:0\", 1)," +
//                "(?, 800, \"0:5\", 1)," +
//                "(?, 100, \"4:0\", 1)," +
//                "(?, 1.4, \"24:0\", 1)," +
//                "(?, 3, \"24:0\", 1)," +
//                "(?, 10, \"1:0\", 1)," +
//                "(?, 20, \"0:20\", 1)," +
//                "(?, 40, \"10:0\", 1)," +
//                "(?, 2, \"24:0\", 1)," +
//
//                "(?, 600, \"2:0\", 1)," +
//                "(?, 3000, \"1:0\", 1)," +
//                "(?, 1500, \"0:30\", 1)," +
//                "(?, 1500, \"3:30\", 1)," +
//                "(?, 250, \"2:0\", 1)," +
//                "(?, 10, \"2:0\", 1)," +
//                "(?, 2000, \"3:0\", 1)," +
//                "(?, 2400, \"1:0\", 1)," +
//                "(?, 500, \"0:10\", 1)," +
//                "(?, 1200, \"0:5\", 1)," +
//                "(?, 1200, \"0:30\", 1)," +
//                "(?, 1800, \"2:0\", 1)," +
//                "(?, 700, \"7:0\", 1)";


        db.execSQL(defaultDevice, new String[]{context.getResources().getString(R.string.default_device_phone_charger),
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
                context.getResources().getString(R.string.default_device_amp_idle),

                context.getResources().getString(R.string.default_device_washing_wachine),
                context.getResources().getString(R.string.default_device_laudry_dryer),
                context.getResources().getString(R.string.default_device_hair_dryer),
                context.getResources().getString(R.string.default_device_electric_heater),
                context.getResources().getString(R.string.default_device_small_tv),
                context.getResources().getString(R.string.default_device_radio),
                context.getResources().getString(R.string.default_device_induction_cooker),
                context.getResources().getString(R.string.default_device_oven),
                context.getResources().getString(R.string.default_device_Mixer),
                context.getResources().getString(R.string.default_device_toaster),
                context.getResources().getString(R.string.default_device_kettle),
                context.getResources().getString(R.string.default_device_dishwasher),
                context.getResources().getString(R.string.default_device_air_conditioner)
        });

        db.execSQL(defaultDevicesInRoom);
        db.execSQL(addVariable, new String[]{context.getResources().getString(R.string.currency_type)});
        db.execSQL(numberAfterDot);
        db.execSQL(firstRunTutFirst);
        db.execSQL(token);
        db.execSQL(adsEnable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS " + "default_device_settings");
            db.execSQL(defaultDeviceTable);


            db.execSQL(defaultDevice, new String[]{context.getResources().getString(R.string.default_device_phone_charger),
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
                    context.getResources().getString(R.string.default_device_amp_idle),

                    context.getResources().getString(R.string.default_device_washing_wachine),
                    context.getResources().getString(R.string.default_device_laudry_dryer),
                    context.getResources().getString(R.string.default_device_hair_dryer),
                    context.getResources().getString(R.string.default_device_electric_heater),
                    context.getResources().getString(R.string.default_device_small_tv),
                    context.getResources().getString(R.string.default_device_radio),
                    context.getResources().getString(R.string.default_device_induction_cooker),
                    context.getResources().getString(R.string.default_device_oven),
                    context.getResources().getString(R.string.default_device_Mixer),
                    context.getResources().getString(R.string.default_device_toaster),
                    context.getResources().getString(R.string.default_device_kettle),
                    context.getResources().getString(R.string.default_device_dishwasher),
                    context.getResources().getString(R.string.default_device_air_conditioner)
            });
        }
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

    public void checkFirstRunApp() {
        String firstRunTutFirst = "INSERT INTO configuration_variable (name, value) values (\"runTutFir\", \"false\")";
        SQLiteDatabase dbWriter = getWritableDatabase();
        if (getVariable("runTutFir").getCount() == 0) {
            dbWriter.execSQL(firstRunTutFirst);
        }
    }

    public void checkIfDefaultRoomListExist() {
        SQLiteDatabase dbWriter = getWritableDatabase();
//        String defaultRoomNameKitchen = context.getResources().getString(R.string.default_roomname_kitchen);
//        String defaultRoomNameBathroom = context.getResources().getString(R.string.default_roomname_bathroom);
//        String defaultRoomNameBedroom = context.getResources().getString(R.string.default_roomname_bedroom);
//        String defaultRoomNameUtilityRoom = context.getResources().getString(R.string.default_roomname_utilityroom);
//        String defaultRoomNameLivingRoom = context.getResources().getString(R.string.default_roomname_livingroom);
//        String defaultRoomNameHomeOffice = context.getResources().getString(R.string.default_roomname_homeoffice);

        String defaultDevicesInRoom = "REPLACE INTO default_room_list (name, device_list) values (\"" + defaultRoomNameKitchen + "\", \"" + defaultDevicesInDefaultKitchen + "\"), " +
                "(\"" + defaultRoomNameBathroom + "\", \"" + defaultDevicesInDefaultBathroom + " \"), " +
                "(\"" + defaultRoomNameBedroom + "\", \"" + defaultDevicesInDefaultBedroom + " \"), " +
                "(\"" + defaultRoomNameUtilityRoom + "\", \"" + defaultDevicesInDefaultUtilityRoom + " \"), " +
                "(\"" + defaultRoomNameLivingRoom + "\", \"" + defaultDevicesInDefaultLivingRoom.replace('"', '\"') + " \"), " +
                "(\"" + defaultRoomNameHomeOffice + "\", \"" + defaultDevicesInDefaultHomeOffice + " \") ";
//        defaultRoomListTable = "CREATE TABLE IF NOT EXISTS default_room_list" +
//                "(" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "name varchar(100) NOT NULL UNIQUE, " +
//                "device_list varchar(3000) NOT NULL " +
//                ")";
        dbWriter.execSQL(defaultRoomListTable);
        dbWriter.execSQL(defaultDevicesInRoom);
    }

    public void insertToken() {
        String token = "INSERT INTO configuration_variable (name, value) values (\"token\", \"\")";
        SQLiteDatabase dbWriter = getWritableDatabase();
        if (getVariable("token").getCount() == 0) {
            dbWriter.execSQL(token);
        }
    }

    public void insertAdsEnable() {
        String adsEnable = "INSERT INTO configuration_variable (name, value) values (\"adsEnable\", \"Y\")";
        SQLiteDatabase dbWriter = getWritableDatabase();
        if (getVariable("adsEnable").getCount() == 0) {
            dbWriter.execSQL(adsEnable);
        }
    }

    public void setVariable(String variableName, String value) {
        RoomManager roomManager = new RoomManager(context);
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = ?";

        contentValues.put("value", value);

        if (variableName.equals("powerCost")) {
            roomManager.updateAllEnergyCostCurrency(Double.parseDouble(value));
        }

        dbWriter.update("configuration_variable", contentValues, where, new String[]{variableName});
    }

    public void addTokenToDB(String token) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = \"token\"";

        contentValues.put("value", token);

        dbWriter.update("configuration_variable", contentValues, where, null);
    }

    @SuppressLint("Recycle")
    public String getTokenFromDB() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor cursor;

        query = "SELECT value " +
                "FROM   configuration_variable " +
                "WHERE  name = \"token\"";

        cursor = dbhRead.rawQuery(query, null);

        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public void setEnableAds(boolean b) {
        SQLiteDatabase dbWriter = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String where = "name = \"adsEnable\"";

        contentValues.put("value", b ? "Y" : "N");

        dbWriter.update("configuration_variable", contentValues, where, null);
    }

    @SuppressLint("Recycle")
    public boolean getEnableAds() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor cursor;

        query = "SELECT value " +
                "FROM   configuration_variable " +
                "WHERE  name = \"adsEnable\"";

        cursor = dbhRead.rawQuery(query, null);
        cursor.moveToFirst();

        return cursor.getString(0).equals("Y");
    }

    public Cursor getDefaultRoomListName() {
        SQLiteDatabase dbhRead = getReadableDatabase();
        String query;
        Cursor cursor;

        query = "SELECT name " +
                "FROM   default_room_list";

        cursor = dbhRead.rawQuery(query, null);
        cursor.moveToFirst();

        return cursor;
    }

    protected String changeSpaceInName(String name) {
        return name.trim().replace(" ", "_");
    }
}
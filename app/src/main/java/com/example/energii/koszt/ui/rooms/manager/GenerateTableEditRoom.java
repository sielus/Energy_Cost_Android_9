package com.example.energii.koszt.ui.rooms.manager;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.settings.SettingActivity;

import java.util.ArrayList;
import java.util.List;
public class GenerateTableEditRoom {
    private TextView title_summary;
    private List<String> roomCostKWH = new ArrayList<>();
    @SuppressLint("SetTextI18n")
    public void refreshTable(View view, String defaultCurrency, String room_name, int numberAfterDot) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        TextView outputEnergyCostUser = view.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostYearth = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView outputEnergyCostUserKwh = view.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostYearthKwh= view.findViewById(R.id.OutputEnergyCostMonthKwh);
        if(sqlLiteDBHelper.getRoomDeviceList(room_name).getCount() != 0){
            getRoomCostKwh(sqlLiteDBHelper.getRoomCost(room_name));
            outputEnergyCostUser.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000) + " kWh");
            outputEnergyCostUserKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1))) + " " +defaultCurrency);
            outputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 30) + " kWh");
            outputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 30) + " " +defaultCurrency);
            outputEnergyCostYearth.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 365 )+ " kWh");
            outputEnergyCostYearthKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 365 )+ " " + defaultCurrency);
        }else{
            TableLayout tableLayout;
            tableLayout = view.findViewById(R.id.tableLayout);
            tableLayout.setVisibility(View.GONE);
            title_summary = view.findViewById(R.id.title_summary);
            title_summary.setVisibility(View.GONE);
            outputEnergyCostUser.setText("0 " + defaultCurrency);
            outputEnergyCostUserKwh.setText("0 kWh");
            outputEnergyCostMonth.setText("0" + defaultCurrency);
            outputEnergyCostMonthKwh.setText("0 kWh");
            outputEnergyCostYearth.setText("0" + defaultCurrency);
            outputEnergyCostYearthKwh.setText("0 kWh");
        }
    }
    boolean getRoomCostKwh(Cursor cursor){
        roomCostKWH.clear();
        if (cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                roomCostKWH.add(cursor.getString(0));
                roomCostKWH.add(cursor.getString(1));
            } return true;
        } return false;
    }


    public void generateTableRoomList(View view, String defaultCurrency,int numberAfterDot){
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        SettingActivity settingActivity = new SettingActivity();
        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getdefaultCurrency(view);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TextView title_summary = view.findViewById(R.id.title_summary);
        TextView OutputEnergyCostDay = view.findViewById(R.id.OutputEnergyCostDay);
        TextView OutputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView OutputEnergyCostYear = view.findViewById(R.id.OutputEnergyCostYear);

        TextView OutputEnergyCostDayKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView OutputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostMonthKwh);
        TextView OutputEnergyCostYearKwh= view.findViewById(R.id.OutputEnergyCostYearKwh);


        if(sqlLiteDBHelper.getRoomList().getCount()!=0){
            tableLayout.setVisibility(View.VISIBLE);
            title_summary.setVisibility(View.VISIBLE);
            getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost());


            OutputEnergyCostDayKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000) + " kWh");
            OutputEnergyCostDay.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost())) + " " +defaultCurrency);

            OutputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000 * 30 ) + " kWh");
            OutputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f",getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost()) * 30 ) + " " + defaultCurrency);

            OutputEnergyCostYearKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000 * 365 ) + " kWh");
            OutputEnergyCostYear.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost())  * 365 ) + " " + defaultCurrency);


        }

    }

    private float getAllRoomsKwHFromDB(Cursor cursor){

        cursor.moveToFirst();
        return cursor.getFloat(0);

    }

    private float getAllRoomsCostFromDB(Cursor cursor){

        cursor.moveToFirst();
        return cursor.getFloat(1);

    }
}

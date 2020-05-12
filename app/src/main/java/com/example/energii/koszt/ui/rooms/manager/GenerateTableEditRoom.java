package com.example.energii.koszt.ui.rooms.manager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.RoomManager;
import com.example.energii.koszt.ui.settings.SettingActivity;
import java.util.ArrayList;
import java.util.List;

public class GenerateTableEditRoom {
    private List<String> roomCostKWH = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public void refreshTable(View view, String defaultCurrency, String room_name, int numberAfterDot) {
        RoomManager roomManager = new RoomManager(view.getContext());
        TextView outputEnergyCostUser = view.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostYeah = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView outputEnergyCostUserKwh = view.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostYeahKwh= view.findViewById(R.id.OutputEnergyCostMonthKwh);
        if(roomManager.getRoomDeviceList(room_name).getCount() != 0){
            getRoomCostKwh(roomManager.getRoomCost(room_name));
            outputEnergyCostUser.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000) + " kWh");
            outputEnergyCostUserKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1))) + " " +defaultCurrency);
            outputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 30) + " kWh");
            outputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 30) + " " +defaultCurrency);
            outputEnergyCostYeah.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 365 )+ " kWh");
            outputEnergyCostYeahKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 365 )+ " " + defaultCurrency);
        }else{
            TableLayout tableLayout = view.findViewById(R.id.tableLayout);
            tableLayout.setVisibility(View.GONE);
            TextView titleSummary = view.findViewById(R.id.title_summary);
            titleSummary.setVisibility(View.GONE);
            outputEnergyCostUser.setText("0 " + defaultCurrency);
            outputEnergyCostUserKwh.setText("0 kWh");
            outputEnergyCostMonth.setText("0" + defaultCurrency);
            outputEnergyCostMonthKwh.setText("0 kWh");
            outputEnergyCostYeah.setText("0" + defaultCurrency);
            outputEnergyCostYeahKwh.setText("0 kWh");
        }
    }

    @SuppressLint("SetTextI18n")
    public void generateTableRoomList(View view){
        RoomManager roomManager = new RoomManager(view.getContext());
        SettingActivity settingActivity = new SettingActivity();
        int numberAfterDot = settingActivity.getNumberAfterDot(view);
        String defaultCurrency = settingActivity.getDefaultCurrency(view);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TextView title_summary = view.findViewById(R.id.title_summary);
        TextView OutputEnergyCostDay = view.findViewById(R.id.OutputEnergyCostDay);
        TextView OutputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView OutputEnergyCostYear = view.findViewById(R.id.OutputEnergyCostYear);

        TextView OutputEnergyCostDayKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView OutputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostMonthKwh);
        TextView OutputEnergyCostYearKwh= view.findViewById(R.id.OutputEnergyCostYearKwh);

        if(roomManager.getRoomList().getCount()!=0){
            tableLayout.setVisibility(View.VISIBLE);
            title_summary.setVisibility(View.VISIBLE);
            getAllRoomsCostFromDB(roomManager.getHouseCost());

            OutputEnergyCostDayKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(roomManager.getHouseCost()) / 1000) + " kWh");
            OutputEnergyCostDay.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(roomManager.getHouseCost())) + " " +defaultCurrency);

            OutputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(roomManager.getHouseCost()) / 1000 * 30 ) + " kWh");
            OutputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f",getAllRoomsCostFromDB(roomManager.getHouseCost()) * 30 ) + " " + defaultCurrency);

            OutputEnergyCostYearKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(roomManager.getHouseCost()) / 1000 * 365 ) + " kWh");
            OutputEnergyCostYear.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(roomManager.getHouseCost())  * 365 ) + " " + defaultCurrency);

        }
    }

    private void getRoomCostKwh(Cursor cursor){
        roomCostKWH.clear();

        if (cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                roomCostKWH.add(cursor.getString(0));
                roomCostKWH.add(cursor.getString(1));
            }
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
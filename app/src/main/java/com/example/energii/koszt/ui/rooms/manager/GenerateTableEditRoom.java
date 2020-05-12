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

    public void generateDeviceTable(View view,String room_name){
        DeviceManager deviceManager = new DeviceManager(view.getContext());

        Cursor cursor = deviceManager.getDeviceDetails(room_name);

        ArrayList<String> deviceName = new ArrayList<String>();
        ArrayList<String> deviceKwh = new ArrayList<>();
        ArrayList<Float> deviceCost = new ArrayList<Float>();

        deviceName.clear();
        deviceKwh.clear();
        deviceCost.clear();

        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {
                deviceKwh.add( String.format("%."+ RoomEditManager.numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) + " kWh");
                deviceCost.add(Float.parseFloat(String.format("%."+ RoomEditManager.numberAfterDot +"f", cursor.getFloat(2)).replace(",",".")));
                deviceName.add(cursor.getString(0));
            }
        }else if(cursor.getCount() == 1){
            cursor.moveToFirst();
            deviceKwh.add(cursor.getInt(1), String.format("%."+ RoomEditManager.numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) + " kWh");
            deviceCost.add(Float.parseFloat(String.format("%."+ RoomEditManager.numberAfterDot +"f", cursor.getFloat(2)).replace(",",".")));
            deviceName.add(cursor.getString(0));
        }

        int n = 0;
        int index = 1;
        TableLayout tableLayout = view.findViewById(R.id.tableLayoutDeviceDetails);
        TextView tableLayoutTitleDeviceName = new TextView(view.getContext());
        TextView tableLayoutTitleDeviceCost= new TextView(view.getContext());
        TextView tableLayoutTitleDeviceKwh = new TextView(view.getContext());
        TableRow titleRow= new TableRow(view.getContext());



        tableLayoutTitleDeviceName.setText(view.getResources().getString(R.string.dialog_edit_device_name));
        tableLayoutTitleDeviceKwh.setText(view.getResources().getString(R.string.chart_kwh_consumption));
        tableLayoutTitleDeviceCost.setText(view.getResources().getString(R.string.chart_daily_costs));

        tableLayoutTitleDeviceName.setTextSize(17);
        tableLayoutTitleDeviceKwh.setTextSize(17);
        tableLayoutTitleDeviceCost.setTextSize(17);

        tableLayoutTitleDeviceName.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tableLayoutTitleDeviceKwh.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        tableLayoutTitleDeviceCost.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        tableLayoutTitleDeviceName.setBackground(view.getContext().getDrawable(R.drawable.border));
        //titleRow.setBackground(view.getContext().getDrawable(R.drawable.border));
        tableLayoutTitleDeviceKwh.setBackground(view.getContext().getDrawable(R.drawable.border));
        tableLayoutTitleDeviceCost.setBackground(view.getContext().getDrawable(R.drawable.border));

        titleRow.addView(tableLayoutTitleDeviceName);
        titleRow.addView(tableLayoutTitleDeviceKwh);
        titleRow.addView(tableLayoutTitleDeviceCost);
        tableLayout.addView(titleRow,0);

        while (deviceName.size() > n){

            TableRow row= new TableRow(view.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView textViewDeviceName = new TextView(view.getContext());
            TextView textViewDeviceCost= new TextView(view.getContext());
            TextView textViewDeviceKwh = new TextView(view.getContext());

            textViewDeviceName.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            textViewDeviceKwh.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            textViewDeviceCost.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            textViewDeviceName.setBackground(view.getContext().getDrawable(R.drawable.border));
            textViewDeviceKwh.setBackground(view.getContext().getDrawable(R.drawable.border));
            textViewDeviceCost.setBackground(view.getContext().getDrawable(R.drawable.border));

            textViewDeviceName.setText(deviceName.get(n));
            textViewDeviceCost.setText(String.valueOf(deviceCost.get(n)));
            textViewDeviceKwh.setText(deviceKwh.get(n));

            row.addView(textViewDeviceName);
            row.addView(textViewDeviceKwh);
            row.addView(textViewDeviceCost);

            tableLayout.addView(row,index);


            index = index + 1;
            n = n + 1;
        }

    }


}
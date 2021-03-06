package com.devdreams.energii.koszt.ui.rooms.manager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devdreams.energii.koszt.R;
import java.util.ArrayList;
import java.util.Arrays;

public class RoomEditManagerDeviceDetailsListAdapter extends RecyclerView.Adapter<RoomEditManagerDeviceDetailsListAdapter.MyViewHolder> {
    private String[] deviceName;
    private String[] deviceKwh;
    private String[] deviceCost;

    public RoomEditManagerDeviceDetailsListAdapter(View view,String room_name) {
        this.room_name = room_name;
        this.view = view;
    }
    private View view;
    private String room_name;


    public void generateDeviceTable(){
        ArrayList<String> deviceName = new ArrayList<>();
        ArrayList<String> deviceKwh = new ArrayList<>();
        ArrayList<String> deviceCost = new ArrayList<>();
        DeviceManager deviceManager = new DeviceManager(view.getContext());


        Cursor cursor = deviceManager.getDeviceDetails(room_name);

        deviceName.clear();
        deviceKwh.clear();
        deviceCost.clear();

        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {
                deviceKwh.add( String.format("%."+ RoomEditManager.numberAfterDot +"f",(cursor.getDouble(1) / 1000)) + " kWh");
                deviceCost.add(String.format("%."+ RoomEditManager.numberAfterDot +"f", cursor.getDouble(2)).replace(",","."));
                deviceName.add(cursor.getString(0));
            }
        }else if(cursor.getCount() == 1){
            cursor.moveToFirst();
            deviceKwh.add( String.format("%."+ RoomEditManager.numberAfterDot +"f",(cursor.getDouble(1) / 1000)) + " kWh");
            deviceCost.add(String.format("%."+ RoomEditManager.numberAfterDot +"f", cursor.getDouble(2)).replace(",","."));
            deviceName.add(cursor.getString(0));
        }


        this.deviceName = Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class);
        this.deviceKwh = Arrays.copyOf(deviceKwh.toArray(), deviceKwh.size(), String[].class);
        this.deviceCost = Arrays.copyOf(deviceCost.toArray(),deviceCost.size(), String[].class);


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = view.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View rowView = layoutInflater.inflate(R.layout.room_edit_devices_details_list_row,parent,false);
        return new MyViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewDeviceName.setText(deviceName[position]);
        holder.textViewDeviceKwh.setText(deviceKwh[position]);
        holder.textViewDeviceCost.setText(deviceCost[position] + view.getResources().getString(R.string.currency_type));

    }

    @Override
    public int getItemCount() {
        return deviceName.length;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDeviceName;
        TextView textViewDeviceKwh;
        TextView textViewDeviceCost;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.textViewDeviceName);
            textViewDeviceKwh = itemView.findViewById(R.id.textViewDeviceKwh);
            textViewDeviceCost = itemView.findViewById(R.id.textViewDeviceCost);

        }
    }
}

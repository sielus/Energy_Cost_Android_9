package com.example.energii.koszt.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.MyViewHolder> {
    Context context;
    String roomName[];
    String devicePower[];
    String deviceTimeWork[];
    String deviceNumber[];
    private int lastPosition = -1;
    private SettingsListAdapter.onNoteListener onNoteListener;

    public SettingsListAdapter(Context context, String roomName[], SettingsListAdapter.onNoteListener onNoteListener, String devicePower[], String deviceTimeWork[], String deviceNumber[]) {
        this.context = context;
        this.onNoteListener = onNoteListener;
        this.roomName = roomName;
        this.devicePower = devicePower;
        this.deviceTimeWork = deviceTimeWork;
        this.deviceNumber = deviceNumber;
    }

    private SQLLiteDBHelper sqlLiteDBHelper;
    SettingActivity settingActivity = new SettingActivity();

    @NonNull
    @Override
    public SettingsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_row, parent,false);


        return new SettingsListAdapter.MyViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsListAdapter.MyViewHolder holder, int position) {
        holder.textViewName.setText(roomName[position]);
        holder.textViewPower.setText(devicePower[position] + "W");
        holder.textViewTimeWork.setText(deviceTimeWork[position]);
        holder.textViewNumber.setText( deviceNumber[position]);

    }

    @Override
    public int getItemCount() {
        return roomName.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName;
        TextView textViewPower;
        TextView textViewTimeWork;
        TextView textViewNumber;

        SettingsListAdapter.onNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, SettingsListAdapter.onNoteListener onNoteListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.rowTextViewDeviceName);
            textViewPower = itemView.findViewById(R.id.rowTextViewPower);
            textViewTimeWork = itemView.findViewById(R.id.rowTextViewTimeWork);
            textViewNumber = itemView.findViewById(R.id.rowTextViewNumber);

            itemView.setOnClickListener(this);
            this.onNoteListener = onNoteListener;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface onNoteListener{
        void onNoteClick(int position);
    }
}
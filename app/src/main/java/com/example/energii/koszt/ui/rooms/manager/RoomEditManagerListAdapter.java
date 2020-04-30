package com.example.energii.koszt.ui.rooms.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.energii.koszt.R;

public class RoomEditManagerListAdapter extends RecyclerView.Adapter<RoomEditManagerListAdapter.MyViewHolder> {
    private Context context;
    private String[] roomName;
    private String[] devicePower;
    private String[] deviceTimeWork;
    private String[] deviceNumber;
    private RoomEditManagerListAdapter.onNoteListener onNoteListener;

    public RoomEditManagerListAdapter(Context context, String[] roomName, RoomEditManagerListAdapter.onNoteListener onNoteListener, String[] devicePower, String[] deviceTimeWork, String[] deviceNumber){
        this.context = context;
        this.onNoteListener = onNoteListener;
        this.roomName = roomName;
        this.devicePower = devicePower;
        this.deviceTimeWork = deviceTimeWork;
        this.deviceNumber = deviceNumber;
    }

    @NonNull
    @Override
    public RoomEditManagerListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_row, parent,false);

        return new RoomEditManagerListAdapter.MyViewHolder(view, onNoteListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomEditManagerListAdapter.MyViewHolder holder, int position) {
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

        RoomEditManagerListAdapter.onNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, RoomEditManagerListAdapter.onNoteListener onNoteListener) {
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
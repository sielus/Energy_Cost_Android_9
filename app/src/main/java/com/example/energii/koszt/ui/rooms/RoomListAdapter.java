package com.example.energii.koszt.ui.rooms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder>   {
    Context context;
    String roomName[];
    String roomNameKwh[];
    private onNoteListener onNoteListener;
    private int lastPosition = -1;

    public RoomListAdapter(Context context, String roomName[],onNoteListener onNoteListener, String roomNameKwh[]){
        this.context = context;
        this.onNoteListener = onNoteListener;
        this.roomName = roomName;
        this.roomNameKwh = roomNameKwh;
    }



    private RoomListFragment roomlistFragment = new RoomListFragment();
    private SQLLiteDBHelper sqlLiteDBHelper;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row, parent,false);


        return new MyViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewName.setText(roomName[position]);
        holder.textViewSecond.setText(roomNameKwh[position] + " kWh");
    }

    @Override
    public int getItemCount() {
        return roomName.length;
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName;
        TextView textViewSecond;

        onNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView,onNoteListener onNoteListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.rowTextViewTitle);
            textViewSecond = itemView.findViewById(R.id.rowTextViewSecond);
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
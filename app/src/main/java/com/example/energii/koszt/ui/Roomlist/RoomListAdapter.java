package com.example.energii.koszt.ui.Roomlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.Roomlist.ManagerRoom.RoomEditManager;

import java.util.Map;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder>   {
    Context context;
    String roomName[];
    private onNoteListener onNoteListener;
    private int lastPosition = -1;

    public RoomListAdapter(Context context, String roomName[],onNoteListener onNoteListener){
        this.context = context;
        this.onNoteListener = onNoteListener;
        this.roomName = roomName;
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
    }

    @Override
    public int getItemCount() {
        return roomName.length;
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName;
        onNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView,onNoteListener onNoteListener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.rowTextView1);
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
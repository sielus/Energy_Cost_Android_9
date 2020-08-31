package com.devdreams.energii.koszt.ui.rooms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.TutorialShowcase;

import co.mobiwise.materialintro.shape.ShapeType;


public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder> {
    private String[] roomName;
    private String[] roomNameKwh;
    private Context context;
    private onNoteListener onNoteListener;
    static public View view;

    public RoomListAdapter(Context context, String[] roomName, onNoteListener onNoteListener, String[] roomNameKwh){
        this.context = context;
        this.onNoteListener = onNoteListener;
        this.roomName = roomName;
        this.roomNameKwh = roomNameKwh;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.row, parent,false);

        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        RoomListFragment roomListFragment = new RoomListFragment();
        if(roomListFragment.checkFirstRun(view,sqlLiteDBHelper)){
            TutorialShowcase tutorialShowcase = new TutorialShowcase(RoomListFragment.activity);
            tutorialShowcase.tutorialBright(view, ShapeType.RECTANGLE,null,"roomRow");
        }

        return new MyViewHolder(view, onNoteListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewName.setText(roomName[position]);
        holder.textViewSecond.setText(Float.parseFloat(roomNameKwh[position]) / 1000 + " kWh");
    }

    @Override
    public int getItemCount() {
        return roomName.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
package com.example.energii.koszt.ui.Roomlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.ManagerRoom.RoomManagerFragment;
public class RoomListAdapter extends ArrayAdapter<String> {
    private Context roomListContext;
    private String[] roomListName;
    private String[] roomListDescription;

    RoomListAdapter(Context roomListContext, String[] roomListName, String[] roomListDescription) {
        super(roomListContext, R.layout.row, R.id.testTextView1, roomListName);
        this.roomListContext = roomListContext;
        this.roomListName = roomListName;
        this.roomListDescription = roomListDescription;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row, parent, false);
        final TextView roomListTextView1 = row.findViewById(R.id.testTextView1);
        final Button editroombutton = row.findViewById(R.id.editroombutton);
        final Button deleteroombutton = row.findViewById(R.id.deleteroombutton);
        TextView roomListTextView2 = row.findViewById(R.id.testTextView2);

        roomListTextView1.setText(roomListName[position]);
        roomListTextView2.setText(roomListDescription[position]);

        editroombutton.setTag(1);
        editroombutton.setId(2);

        editroombutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editroombutton.setText(roomListName[position]);
                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(editroombutton.getId()),Toast.LENGTH_SHORT).show();

            }
        });

        deleteroombutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteroombutton.setText(roomListName[position]);

                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(deleteroombutton.getId()),Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }
}
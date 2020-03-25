package com.example.energii.koszt.ui.Roomlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.ManagerRoom.RoomEditManager;

public class RoomListAdapter extends ArrayAdapter<String> {
    private final Context roomListContext;
    private View root;
    private String[] roomListName;
    private String[] roomListDescription;
    private RoomListFragment roomlistFragment = new RoomListFragment();

   RoomListAdapter(Context roomListContext, String[] roomListName, String[] roomListDescription,View root) {
        super(roomListContext, R.layout.row, R.id.testTextView1, roomListName);
        this.roomListContext = roomListContext;
        this.roomListName = roomListName;
        this.roomListDescription = roomListDescription;
        this.root = root;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") final View row = layoutInflater.inflate(R.layout.row, parent, false);
        final TextView roomListTextView1 = row.findViewById(R.id.testTextView1);
        final Button editRoomButton = row.findViewById(R.id.editroombutton);
        final Button deleteRoomButton = row.findViewById(R.id.deleteroombutton);
        TextView roomListTextView2 = row.findViewById(R.id.testTextView2);

        roomListTextView1.setText(roomListName[position]);
        roomListTextView2.setText(roomListDescription[position]);
        final RoomEditManager roomEditManager = new RoomEditManager();
        editRoomButton.setTag(roomListName[position]);
        editRoomButton.setId(2);

        editRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  editRoomButton.setText(roomListName[position]);

                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(editRoomButton.getId()),Toast.LENGTH_SHORT).show();
                roomEditManager.title = roomListName[position];



                Intent intent = new Intent(roomListContext , RoomEditManager.class);
                roomListContext.startActivity(intent);

            }
        });

        deleteRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteRoomButton.setText(roomListName[position]);

                row.refreshDrawableState();
                roomlistFragment.getTag(editRoomButton.getTag().toString(),roomListContext);
                Toast.makeText(getContext(),"Pokój usunięty",Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(deleteRoomButton.getId()),Toast.LENGTH_SHORT).show();
                roomlistFragment.refreshListView(root);
            }
        });

        return row;
    }
}


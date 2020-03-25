package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

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
import com.example.energii.koszt.R;

public class RoomEditManagerListAdapter extends ArrayAdapter<String> {
    private final Context roomListContext;
    private View root;
    private String[] roomListName;
    private String[] roomListDescription;

    RoomEditManagerListAdapter(Context roomListContext, String[] roomListName, String[] roomListDescription,View root) {
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
        final Button editDeviceButton = row.findViewById(R.id.editbuttonRow);
        final Button deleteDevicebutton = row.findViewById(R.id.deletebuttonRow);
        TextView roomListTextView2 = row.findViewById(R.id.testTextView2);

        roomListTextView1.setText(roomListName[position]);
        roomListTextView2.setText(roomListDescription[position]);
        final RoomEditManager roomEditManager = new RoomEditManager();
        editDeviceButton.setTag(roomListName[position]);


        editDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  editRoomButton.setText(roomListName[position]);

                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(editDeviceButton.getId()),Toast.LENGTH_SHORT).show();
             //   roomEditManager.title = roomListName[position];
                roomEditManager.showDialog(root);


              //  Intent intent = new Intent(roomListContext , RoomEditManager.class);
            //    roomListContext.startActivity(intent);

            }
        });

        deleteDevicebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteRoomButton.setText(roomListName[position]);

                row.refreshDrawableState();
            //    roomlistFragment.getTag(editRoomButton.getTag().toString(),roomListContext);
                Toast.makeText(getContext(),"Urządzenie usunięte",Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), roomListName[position] +" id : " + String.valueOf(deleteDevicebutton.getId()),Toast.LENGTH_SHORT).show();
               // roomlistFragment.refreshListView(root);
            }
        });

        return row;
    }
}


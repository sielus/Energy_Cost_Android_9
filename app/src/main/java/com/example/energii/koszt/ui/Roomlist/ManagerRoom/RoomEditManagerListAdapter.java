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
import com.example.energii.koszt.ui.Roomlist.SQLLiteDBHelper;

public class RoomEditManagerListAdapter extends ArrayAdapter<String> {
    private final Context roomListContext;
    private View root;
    private String[] deviceListName;
    private String[] roomListDescription;
    SQLLiteDBHelper sqlLiteDBHelper;
    RoomEditManager roomEditManager = new RoomEditManager();
    RoomEditManagerListAdapter(Context roomListContext, String[] deviceListName, String[] roomListDescription,View root) {
        super(roomListContext, R.layout.row, R.id.testTextView1, deviceListName);
        this.roomListContext = roomListContext;
        this.deviceListName = deviceListName;
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
        TextView roomListTextView2 = row.findViewById(R.id.testTextView2);

        final Button editDeviceButton = row.findViewById(R.id.editbuttonRow);
        final Button deleteDevicebutton = row.findViewById(R.id.deletebuttonRow);
        final RoomEditManager roomEditManager = new RoomEditManager();

        roomListTextView1.setText(deviceListName[position]);
        roomListTextView2.setText(roomListDescription[position]);



        editDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  editRoomButton.setText(roomListName[position]);

                Toast.makeText(getContext(), deviceListName[position] +" id : " + String.valueOf(editDeviceButton.getId()),Toast.LENGTH_SHORT).show();
             //   roomEditManager.title = roomListName[position];
                roomEditManager.showDialog(root);



            }
        });

        deleteDevicebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteRoomButton.setText(roomListName[position]);
                //sqlLiteDBHelper.deleteDevice();

                row.refreshDrawableState();
            //    roomlistFragment.getTag(editRoomButton.getTag().toString(),roomListContext);
                Toast.makeText(root.getContext(),"Urządzenie usunięte",Toast.LENGTH_SHORT).show();

                Toast.makeText(root.getContext(), deviceListName[position] +" id : " + String.valueOf(deleteDevicebutton.getId()),Toast.LENGTH_SHORT).show();
              //  System.out.println(roomEditManager.room_name + " " +deviceListName[position]);
                sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
                sqlLiteDBHelper.deleteDevice(roomEditManager.room_name,deviceListName[position]);

                roomEditManager.clearRoomList();
                roomEditManager.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(roomEditManager.room_name));
                roomEditManager.refreshListView(root);
            }
        });

        return row;
    }
}


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
import com.example.energii.koszt.ui.SQLLiteDBHelper;

public class RoomEditManagerListAdapter extends ArrayAdapter<String> {
    private View root;
    private String[] deviceListName;
    private String[] roomListDescription;
    private SQLLiteDBHelper sqlLiteDBHelper;

    RoomEditManagerListAdapter(Context roomListContext, String[] deviceListName, String[] roomListDescription,View root) {
        super(roomListContext, R.layout.row, R.id.testTextView1, deviceListName);
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
        TextView roomListTextView2 = row.findViewById(R.id.testTextView2);
        final TextView roomListTextView1 = row.findViewById(R.id.testTextView1);
        final Button editDeviceButton = row.findViewById(R.id.editbuttonRow);
        final Button deleteDeviceButton = row.findViewById(R.id.deletebuttonRow);
        final RoomEditManager roomEditManager = new RoomEditManager();

        roomListTextView1.setText(deviceListName[position]);
        roomListTextView2.setText(roomListDescription[position]);

        editDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Toast.makeText(getContext(), deviceListName[position] +" id : " + editDeviceButton.getId(),Toast.LENGTH_SHORT).show();
            roomEditManager.showUpdateDialog(root, RoomEditManager.room_name,deviceListName[position]);
            }
        });

        deleteDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            row.refreshDrawableState();
            Toast.makeText(root.getContext(),"Urządzenie usunięte",Toast.LENGTH_SHORT).show();
            Toast.makeText(root.getContext(), deviceListName[position] +" id : " + deleteDeviceButton.getId(),Toast.LENGTH_SHORT).show();

            sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
            sqlLiteDBHelper.deleteDevice(RoomEditManager.room_name,deviceListName[position]);

            roomEditManager.clearRoomList();
            roomEditManager.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(RoomEditManager.room_name));
            roomEditManager.refreshListView(root);
            }
        });

        return row;
    }
}


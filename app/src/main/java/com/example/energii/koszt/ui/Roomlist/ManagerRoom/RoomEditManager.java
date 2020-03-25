package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.RoomListAdapter;
import com.example.energii.koszt.ui.Roomlist.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RoomEditManager extends AppCompatActivity {
    Button buttonAddDevice;
    ListView listViewListDevice;
    EditText editTextDeviceName;
    EditText editTextDevicePower;
    EditText editTextDeviceNumbers;
    EditText editTextDeviceWorkH;
    EditText editTextDeviceWorkM;
    View view;
    private List<String> deviceId = new LinkedList<>();
    private List<String> deviceName = new LinkedList<>();
    public static String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        setTitle(title);
        listViewListDevice = findViewById(R.id.listViewDeviceList);
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        //sqlLiteDBHelper.addDevice();

        ViewDataFromDB(sqlLiteDBHelper.getRoomList());


        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(), Arrays.copyOf(deviceId.toArray(), deviceId.size(), String[].class), Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),view );
        listViewListDevice.setAdapter(adapter);

    }

    private void ViewDataFromDB(Cursor cursor) {
        if(cursor.getCount()==0) {
            Toast.makeText(view.getContext(),"Brak danych",Toast.LENGTH_SHORT).show();
        }else {
            //clearRoomList();
            while(cursor.moveToNext()) {
                deviceId.add(cursor.getString(1));
                deviceName.add(cursor.getString(0));
            }
        }
    }

}

package com.example.energii.koszt.ui.Roomlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RoomListFragment extends Fragment {
    private List<String> roomId = new LinkedList<>();
    private List<String> roomName = new LinkedList<>();
    private View root;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rooms, container, false);
        listView = root.findViewById(R.id.listView);
        final SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        final EditText editTextInsertRoomName = root.findViewById(R.id.editTextInsertRoomName);
        Button buttonAddRoom = root.findViewById(R.id.buttonAddRoom);

        ViewDataFromDB(sqlLiteDBHelper.getRoomList());

        RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class), root);
        listView.setAdapter(adapter);

        buttonAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sqlLiteDBHelper.addRoom(editTextInsertRoomName.getText().toString());
                    ViewDataFromDB(sqlLiteDBHelper.getRoomList());

                    refreshListView(root);

                    Toast.makeText(getContext(),"Pokój dodany",Toast.LENGTH_SHORT).show();
                }catch (SQLEnergyCostException.DuplicationRoom | SQLEnergyCostException.EmptyField errorMessage) {
                    Toast.makeText(getContext(), errorMessage.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void ViewDataFromDB(Cursor cursor) {
        if(cursor.getCount()==0) {
            Toast.makeText(getContext(),"Brak danych",Toast.LENGTH_SHORT).show();
        }else {
            clearRoomList();
            while(cursor.moveToNext()) {
                roomId.add(cursor.getString(1));
                roomName.add(cursor.getString(0));
            }
        }
    }

    private void clearRoomList() {
        refreshListView(root);
        roomName.clear();
        roomId.clear();
    }

    void getTag(String tag, Context root) {
        System.out.println(tag);
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root);
        sqlLiteDBHelper.deleteRoom(tag);
    }

    void refreshListView(View root) {
        RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class),root);
        listView = root.findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}



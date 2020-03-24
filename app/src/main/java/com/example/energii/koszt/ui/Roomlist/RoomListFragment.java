package com.example.energii.koszt.ui.Roomlist;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RoomListFragment extends Fragment {
    private List<String> roomId = new LinkedList<>();
    private List<String> roomName = new LinkedList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_rooms, container, false);
        final ListView listView = root.findViewById(R.id.listView);
        final SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());

        ViewDataFromDB(sqlLiteDBHelper.viewData(),root);

        RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class));
        listView.setAdapter(adapter);
        Button buttonAddRoom = root.findViewById(R.id.buttonAddRoom);
        final EditText editTextInsertRoomName = root.findViewById(R.id.editTextInsertRoomName);
        buttonAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Naprawić odświezanie, sprawdzić zawartośc array itd
                    ViewDataFromDB(sqlLiteDBHelper.viewData(),root);
                    RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class));
                    listView.setAdapter(adapter);

                    sqlLiteDBHelper.addRoom(editTextInsertRoomName.getText().toString());
                    Toast.makeText(getContext(),"Pokój dodany",Toast.LENGTH_SHORT).show();
                }catch (SQLException errorMessage) {
                    Toast.makeText(getContext(),"Pokój o tej nazwie istnieje!",Toast.LENGTH_SHORT).show();
                    System.out.println(errorMessage);
                }
            }
        });

        return root;
    }

    private void ViewDataFromDB(Cursor cursor,View root) {
        if(cursor.getCount()==0) {
            Toast.makeText(getContext(),"Brak danych",Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()) {
                roomId.add(cursor.getString(1));
                roomName.add(cursor.getString(0));


            }
        }
    }

    void getTag(String tag, Context root) {
        System.out.println(tag);
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root);

        sqlLiteDBHelper.deleteRoom(tag);
    }
}



package com.example.energii.koszt.ui.Roomlist;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RoomListFragment extends Fragment {
    private List<String> roomId = new LinkedList<>();
    private List<String> roomName = new LinkedList<>();
    private View root;
    private ListView listView;
    private Dialog dialog;
    private SQLLiteDBHelper sqlLiteDBHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rooms, container, false);
        listView = root.findViewById(R.id.listView);
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());

        FloatingActionButton floatingActionButtonAddDevice = root.findViewById(R.id.buttonAddRoom);

        ViewDataFromDB(sqlLiteDBHelper.getRoomList());

        RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class), root);
        listView.setAdapter(adapter);

        floatingActionButtonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showRoomListDialog(root);
            }
        });

        return root;
    }

    private void showRoomListDialog(View view) {
        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.room_list_dialog);
        dialog.show();

        Button buttonDialogAccept = dialog.findViewById(R.id.ButtonAddRoom);
        final TextInputEditText text_field_inputRoomName = dialog.findViewById(R.id.text_field_inputRoomName);
        final TextInputLayout text_field_inputRoomNameLayout = dialog.findViewById(R.id.text_field_inputRoomNameLayout);

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_field_inputRoomName.addTextChangedListener(textWatcher);
                String RoomName = text_field_inputRoomName.getText().toString();
                if (RoomName.isEmpty()) {
                    text_field_inputRoomNameLayout.setError("Brak danych!");
                }else {
                    try {
                        sqlLiteDBHelper.addRoom(RoomName);
                        ViewDataFromDB(sqlLiteDBHelper.getRoomList());
                        refreshListView(root);
                        dialog.dismiss();
                        Toast.makeText(getContext(),"Pokój dodany",Toast.LENGTH_SHORT).show();
                    }catch (SQLEnergyCostException.DuplicationRoom | SQLEnergyCostException.EmptyField errorMessage) {
                        text_field_inputRoomNameLayout.setError(errorMessage.getMessage());
                    }
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final TextInputLayout text_field_inputRoomNameLayout = dialog.findViewById(R.id.text_field_inputRoomNameLayout);
            text_field_inputRoomNameLayout.setError(null);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    void ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            while(cursor.moveToNext()) {
                roomId.add(cursor.getString(1));
                roomName.add(cursor.getString(0));
            }
        }
    }

     void clearRoomList() {
        roomName.clear();
        roomId.clear();
    }

    void refreshListView(View root) {
        RoomListAdapter adapter = new RoomListAdapter(root.getContext(), Arrays.copyOf(roomId.toArray(), roomId.size(), String[].class), Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class),root);
        listView = root.findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}



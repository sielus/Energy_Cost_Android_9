package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RoomEditManager extends AppCompatActivity {
    Button buttonAddDevice;
    ListView listViewListDevice;
    EditText editTextDeviceName;
    View view;
    public SQLLiteDBHelper sqlLiteDBHelper;
    public String deviceNameInput;
    public static String room_name;
    private List<String> deviceId = new LinkedList<>();
    private List<String> deviceName = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        setTitle(room_name);
        listViewListDevice = findViewById(R.id.listViewDeviceList);
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        buttonAddDevice = findViewById(R.id.buttonAddDevice);
        editTextDeviceName = findViewById(R.id.editTextDeviceName);

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList());

        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(), Arrays.copyOf(deviceId.toArray(), deviceId.size(), String[].class), Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),view );
        listViewListDevice.setAdapter(adapter);
        buttonAddDevice.setEnabled(false);
        editTextDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textInput = editTextDeviceName.getText().toString().trim();
                buttonAddDevice.setEnabled(!textInput.isEmpty());
                deviceNameInput = editTextDeviceName.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view);
            }
        });
    }

    private void ViewDataFromDB(Cursor cursor) {
        if(cursor.getCount()==0) {
            Toast.makeText(view.getContext(),"Brak danych",Toast.LENGTH_SHORT).show();
        }else {
            //clearRoomList();
            while(cursor.moveToNext()) {
                deviceId.add(cursor.getString(1));
                deviceName.add(cursor.getString(0));
                System.out.println(deviceName);
            }
        }
    }

    public void showDialog(final View view){
        Context context;
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manage_device_dialog_layout);
        dialog.show();

        Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
        final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Double powerValue = Double.valueOf(editTextDevicePower.getText().toString());
            int h = Integer.parseInt(editTextDeviceWorkH.getText().toString());
            int m = Integer.parseInt(editTextDeviceWorkM.getText().toString());
            int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                try {
                    sqlLiteDBHelper.addDevice(room_name,deviceNameInput,powerValue,h,m,number);
                    Toast.makeText(view.getContext(),"Urządzenie dodane",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (SQLEnergyCostException.EmptyField emptyField) {
                    emptyField.printStackTrace();
                } catch (SQLEnergyCostException.DuplicationDevice duplicationDevice) {
                    duplicationDevice.printStackTrace();
                }
            }
        });
    }
}
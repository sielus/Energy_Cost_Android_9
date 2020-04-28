package com.example.energii.koszt.ui.rooms;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.example.energii.koszt.ui.rooms.manager.GenerateTableEditRoom;
import com.example.energii.koszt.ui.rooms.manager.RoomEditManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Dialogs {
    private ArrayList<String> defaultListDeviceName;
    private ArrayList<String> defaultListDevicePower;
    private ArrayList<String> defaultListDeviceTimeWork;
    private ArrayList<String> defaultListDeviceNumber;
    public Dialogs(ArrayList<String> defaultListDeviceName, ArrayList<String> defaultListDevicePower, ArrayList<String> defaultListDeviceTimeWork, ArrayList<String> defaultListDeviceNumber) {
        this.defaultListDeviceName = defaultListDeviceName;
        this.defaultListDevicePower = defaultListDevicePower;
        this.defaultListDeviceTimeWork = defaultListDeviceTimeWork;
        this.defaultListDeviceNumber = defaultListDeviceNumber;
    }
    public List<String> devicePower = new LinkedList<>();
    public List<String> deviceName = new ArrayList<>();
    public List<String> deviceTimeWork = new LinkedList<>();
    public List<String> deviceNumber = new LinkedList<>();
    private boolean ifNumberOnStart = false;
    private SQLLiteDBHelper sqlLiteDBHelper;
    public final List<String> device = new LinkedList<>();
    public void showDialogAddDevice(final View view, final String room_name, final int numberAfterDot, final String defaultCurrency){
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manage_device_dialog_layout);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        Spinner spinner = dialog.findViewById(R.id.spinner);

        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(dialog.getContext(),android.R.layout.simple_spinner_dropdown_item, defaultListDeviceName);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        final int[] h = new int[1];
        final int[] m = new int[1];
        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);
        final Switch is24hSwitch = dialog.findViewById(R.id.switch1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(arrayAdapter.getItem(0)==arrayAdapter.getItem(position)){
                }else{
                    editTextDeviceName.setText(defaultListDeviceName.get(position));
                    editTextDevicePower.setText(defaultListDevicePower.get(position));
                    editTextDeviceNumbers.setText(defaultListDeviceNumber.get(position));
                    h[0] = Integer.parseInt(defaultListDeviceTimeWork.get(position).split(":")[0]);
                    m[0] = Integer.parseInt(defaultListDeviceTimeWork.get(position).split(":")[1]);
                    buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + "\n " + h[0] + "h" + " " + m[0] + "m");
                    if(h[0]==24){
                        is24hSwitch.setChecked(true);
                        buttonTimePicker.setEnabled(false);
                    }else{
                        is24hSwitch.setChecked(false);
                        buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + "\n " + h[0] + "h" + " " + m[0] + "m");
                        buttonTimePicker.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    Toast.makeText(dialog.getContext(),R.string.toast_device_wokrs_all_day,Toast.LENGTH_SHORT).show();
                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work));
                }
            }
        });

        final Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);
        final TextInputLayout text_field_inputeditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputeditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputeditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);

        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(dialog.getContext() , R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {


                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePickerV, int hourOfDay, int minute) {
                        timePickerV.setIs24HourView(true);
                        h[0] = hourOfDay;
                        m[0] = minute;
                        buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,h[0],m[0],true);
                timePickerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                timePickerDialog.show();
            }
        });

        editTextDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String deviceName = editTextDeviceName.getText().toString();
                if (!deviceName.isEmpty()) {
                    char first = deviceName.charAt(0);
                    if (Character.isDigit(first)) {
                        text_field_inputeditTextDeviceNameLayout.setError(view.getContext().getResources().getString(R.string.error_name_canot_start_from_number));
                        ifNumberOnStart = true;
                    } else {
                        text_field_inputeditTextDeviceNameLayout.setError(null);
                        ifNumberOnStart = false;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDevicePower.addTextChangedListener(roomPowerTextWatcher);
                editTextDeviceNumbers.addTextChangedListener(roomNumberTextWatcher);
                if(checkInputValue(dialog)) {
                    double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                    String deviceNameInput = editTextDeviceName.getText().toString();
                    int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());
                    try {
                        try {
                            sqlLiteDBHelper.addDevice(room_name, deviceNameInput, powerValue, h[0], m[0], number);
                            Toast.makeText(view.getContext(), R.string.toast_device_added, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                            GenerateCharts generateCharts = new GenerateCharts();
                            generateCharts.generateChartinRoom(view,room_name,numberAfterDot,defaultCurrency);

                            RoomEditManager roomEditManager = new RoomEditManager();
                            roomEditManager.refreshListView(view);

                            GenerateTableEditRoom generateTableEditRoom = new GenerateTableEditRoom();
                            generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);
                        } catch (SQLEnergyCostException.WrongTime wrongTime) {
                            Toast.makeText(view.getContext(),wrongTime.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLEnergyCostException.EmptyField | SQLEnergyCostException.DuplicationDevice errorMessage) {
                        Toast.makeText(view.getContext(), errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private TextWatcher roomPowerTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private TextWatcher roomNumberTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private boolean checkInputValue(Dialog dialog) {
                boolean isNotEmpty = true;
                if(editTextDeviceName.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNameLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else if(ifNumberOnStart){
                    text_field_inputeditTextDeviceNameLayout.setError(view.getContext().getResources().getString(R.string.error_name_canot_start_from_number));
                    isNotEmpty = false;
                }
                else {
                    text_field_inputeditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNumbersLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputeditTextDevicePowerLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }
                return isNotEmpty;
            }
        });
    }

    public void ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            while(cursor.moveToNext()) {
                devicePower.add(cursor.getString(2));
                deviceTimeWork.add(cursor.getString(3));
                deviceNumber.add(cursor.getString(4));
                deviceName.add(cursor.getString(1));
            }
        }
    }

    public void clearRoomList() {
        deviceNumber.clear();
        devicePower.clear();
        deviceTimeWork.clear();
        deviceName.clear();
    }

    public void showUpdateDialog(final View view, final String roomName, String deviceName, final String room_name, final int numberAfterDot, final String defaultCurrency){
        final Dialog dialog = new Dialog(view.getContext());
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.device_edit_dialog_layout_no_spinner);
        dialog.setCancelable(false);
        dialog.show();

        viewDeviceInfoFromDB(sqlLiteDBHelper.getDeviceInfo(roomName,deviceName));

        Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);
        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        final TextInputLayout text_field_inputeditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputeditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputeditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);
        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);

        editTextDeviceName.setText(device.get(1));
        editTextDevicePower.setText(device.get(2));
        editTextDeviceNumbers.setText(device.get(4));

        final int[] h = {Integer.parseInt(device.get(3).split(":")[0])};
        final int[] m = {Integer.parseInt(device.get(3).split(":")[1])};

        Switch is24hSwitch = dialog.findViewById(R.id.switch1);

        buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) +" \n " + h[0] + "h" + " " + m[0] + "m");
        if(h[0]==24){
            is24hSwitch.setChecked(true);
            buttonTimePicker.setEnabled(false);
        }else{
            is24hSwitch.setChecked(false);
            buttonTimePicker.setEnabled(true);
        }
        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    Toast.makeText(dialog.getContext(),R.string.toast_device_wokrs_all_day,Toast.LENGTH_SHORT).show();
                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                }else{
                    final int[] h = {Integer.parseInt(device.get(3).split(":")[0])};
                    final int[] m = {Integer.parseInt(device.get(3).split(":")[1])};
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                }
            }
        });
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(dialog.getContext() , R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker timePickerV, int hourOfDay, int minute) {
                        timePickerV.setIs24HourView(true);
                        h[0] = hourOfDay;
                        m[0] = minute;
                        buttonTimePicker.setText(view.getContext().getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,Integer.parseInt(device.get(3).split(":")[0]),Integer.parseInt(device.get(3).split(":")[1]),true);
                timePickerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                timePickerDialog.show();
            }
        });

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDeviceName.addTextChangedListener(roomNameTextWatcher);
                editTextDevicePower.addTextChangedListener(roomPowerTextWatcher);
                editTextDeviceNumbers.addTextChangedListener(roomNumberTextWatcher);
                if(checkInputValue(dialog)){
                    String deviceName = editTextDeviceName.getText().toString();
                    double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                    int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());
                    try {
                        try {
                            sqlLiteDBHelper.updateDevice(Integer.parseInt(device.get(0)),roomName,deviceName,powerValue,number, h[0], m[0]);
                        } catch (SQLEnergyCostException.WrongTime wrongTime) {
                            wrongTime.printStackTrace();
                        }
                        Toast.makeText(view.getContext(),R.string.toast_device_updated,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                        RoomEditManager roomEditManager = new RoomEditManager();
                        GenerateTableEditRoom generateTableEditRoom = new GenerateTableEditRoom();

                        generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);
                        GenerateCharts generateCharts = new GenerateCharts();
                        generateCharts.generateChartinRoom(view,room_name,numberAfterDot,defaultCurrency);
                        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

                        roomEditManager.refreshListView(view);
                    }catch (SQLEnergyCostException.EmptyField | SQLEnergyCostException.DuplicationDevice exception) {
                        Toast.makeText(view.getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                    }
                }
            }

            private TextWatcher roomNameTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputeditTextDeviceNameLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private TextWatcher roomPowerTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private TextWatcher roomNumberTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private boolean checkInputValue(Dialog dialog) {

                boolean isNotEmpty = true;

                if(editTextDeviceName.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNameLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNumbersLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputeditTextDevicePowerLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }
                return isNotEmpty;
            }
        });
    }

    void viewDeviceInfoFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            device.clear();
            while(cursor.moveToNext()) {
                device.add((cursor.getString(0)));
                device.add((cursor.getString(1)));
                device.add((cursor.getString(2)));
                device.add((cursor.getString(3)));
                device.add((cursor.getString(4)));
            }
        }
    }

    public void showDialogEditRoomName(final View view, final String room_name, final String defaultCurrency, final int numberAfterDot, final RoomEditManager roomEditManager) {
        final Dialog room_name_dialog = new Dialog(view.getContext());
        room_name_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        room_name_dialog.setContentView(R.layout.room_list_dialog);
        room_name_dialog.show();

        Button buttonDialogAccept = room_name_dialog.findViewById(R.id.ButtonAddRoom);
        final TextInputEditText text_field_inputRoomName = room_name_dialog.findViewById(R.id.text_field_inputRoomName);
        final TextInputLayout text_field_inputRoomNameLayout = room_name_dialog.findViewById(R.id.text_field_inputRoomNameLayout);

        text_field_inputRoomNameLayout.setHint(view.getContext().getResources().getString(R.string.dialog_hint_new_room_name));
        text_field_inputRoomName.setText(room_name.replace("_"," "));

        final SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        text_field_inputRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String roomName = text_field_inputRoomName.getText().toString();
                if(!roomName.isEmpty()){
                    char First = roomName.charAt(0);
                    if(Character.isDigit(First)){
                        text_field_inputRoomNameLayout.setError(view.getContext().getResources().getString(R.string.error_name_canot_start_from_number));
                        ifNumberOnStart = true;
                    }else {
                        text_field_inputRoomNameLayout.setError(null);
                        ifNumberOnStart = false;
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRoomName = text_field_inputRoomName.getText().toString();
                if (newRoomName.isEmpty()) {
                    text_field_inputRoomNameLayout.setError(view.getContext().getResources().getString(R.string.error_no_data));
                }else if(ifNumberOnStart){
                    text_field_inputRoomNameLayout.setError(view.getContext().getResources().getString(R.string.error_name_canot_start_from_number));
                }else{
                    roomEditManager.setTitle("Room" + " " + newRoomName.replace("_"," "));
                    room_name_dialog.dismiss();
                    try {
                        sqlLiteDBHelper.updateRoomName(room_name,newRoomName);
                        RoomEditManager.room_name = newRoomName;
                        Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.toast_new_room_name) + " " + newRoomName,Toast.LENGTH_SHORT).show();
                    } catch (SQLEnergyCostException.DuplicationRoom duplicationRoom) {
                        duplicationRoom.printStackTrace();
                    }
                }
            }
        });
    }


}

package com.example.energii.koszt.ui.settings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SettingsDialogs {

    public static List<String> devicePower = new LinkedList<>();
    public static List<String> deviceName = new ArrayList<>();
    public static List<String> deviceTimeWork = new LinkedList<>();
    public static List<String> deviceNumber = new LinkedList<>();
    public static final List<String> device = new LinkedList<>();
    boolean ifNumberOnStart = false;

    public void showDialogAddDevice(final View view){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.device_edit_dialog_layout_no_spinner);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);

        Switch is24hSwitch = dialog.findViewById(R.id.switch1);
        final int[] h = new int[1];
        final int[] m = new int[1];
        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    Toast.makeText(dialog.getContext(),"Urządzenie pracuje całą dobe",Toast.LENGTH_SHORT).show();

                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");

                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText("Czas pracy");

                }
            }
        });

        final Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);
        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        // final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
        //  final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);



        final TextInputLayout text_field_inputeditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputeditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputeditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);
        // final TextInputLayout text_field_inputeditTextDeviceWorkHLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkHLayout);
        //   final TextInputLayout text_field_inputeditTextDeviceWorkMLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkMLayout);


        buttonTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final TimePickerDialog timePickerDialog = new TimePickerDialog(dialog.getContext() , R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker timePickerV, int hourOfDay, int minute) {

                        timePickerV.setIs24HourView(true);
                        h[0] = hourOfDay;
                        m[0] = minute;
                        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,12,0,true);
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
                        text_field_inputeditTextDeviceNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
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
                            SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
                            sqlLiteDBHelper.addDefaultDevice(deviceNameInput, powerValue, h[0], m[0], number);
                            Toast.makeText(view.getContext(), "Urządzenie dodane", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());
                            SettingActivity settingActivity = new SettingActivity();
                            settingActivity.refreshListView(view);

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
                    text_field_inputeditTextDeviceNameLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else if(ifNumberOnStart){
                    text_field_inputeditTextDeviceNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
                    isNotEmpty = false;
                }
                else {
                    text_field_inputeditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNumbersLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputeditTextDevicePowerLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }



                return isNotEmpty;
            }

        })
        ;}

    void ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();

            while(cursor.moveToNext()) {
                devicePower.add(cursor.getString(1));
                deviceTimeWork.add(cursor.getString(2));
                deviceNumber.add(cursor.getString(3));
                deviceName.add(cursor.getString(0));
            }
        }
    }

    void clearRoomList() {
        device.clear();
        deviceNumber.clear();
        devicePower.clear();
        deviceTimeWork.clear();
        deviceName.clear();
    }

    void viewDeviceInfoFromDB(Cursor cursor) {
        clearRoomList();
        device.clear();
        while(cursor.moveToNext()) {
            device.add((cursor.getString(0)));
            device.add((cursor.getString(1)));
            device.add((cursor.getString(2)));
            device.add((cursor.getString(3)));
        }


    }

    public void showUpdateDialog(final View view, final String oldDeviceName, final SettingsListAdapter settings_listAdapter){
        final Dialog dialog = new Dialog(view.getContext());
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        clearRoomList();

        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.device_edit_dialog_layout_no_spinner);
        dialog.setCancelable(false);
        dialog.show();

        viewDeviceInfoFromDB(sqlLiteDBHelper.getDetailsDefaultDevice(oldDeviceName));
        Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);

        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        // final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
        //  final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);

        final TextInputLayout text_field_inputeditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputeditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputeditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);
        // final TextInputLayout text_field_inputeditTextDeviceWorkHLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkHLayout);
        //  final TextInputLayout text_field_inputeditTextDeviceWorkMLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkMLayout);
        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);
        editTextDeviceName.setText(SettingsDialogs.device.get(0));
        editTextDevicePower.setText(SettingsDialogs.device.get(1));
        editTextDeviceNumbers.setText(SettingsDialogs.device.get(3));
        final int[] h = {Integer.parseInt(SettingsDialogs.device.get(2).split(":")[0])};
        final int[] m = {Integer.parseInt(SettingsDialogs.device.get(2).split(":")[1])};
        //  editTextDeviceWorkH.setText(device.get(3).split(":")[0]);
        //editTextDeviceWorkM.setText(device.get(3).split(":")[1]);
        Switch is24hSwitch = dialog.findViewById(R.id.switch1);
        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
        if(h[0]==24){
            Toast.makeText(dialog.getContext(),"Urządzenie pracuje całą dobe",Toast.LENGTH_SHORT).show();

            is24hSwitch.setChecked(true);
            buttonTimePicker.setEnabled(false);
        }else{
            is24hSwitch.setChecked(false);
            buttonTimePicker.setEnabled(true);

        }
        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");

                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");

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

                        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,Integer.parseInt(SettingsDialogs.device.get(2).split(":")[0]),Integer.parseInt(SettingsDialogs.device.get(2).split(":")[1]),true);
                timePickerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                timePickerDialog.show();
            }
        });

        final SQLLiteDBHelper finalSqlLiteDBHelper = sqlLiteDBHelper;
        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDeviceName.addTextChangedListener(roomNameTextWatcher);
                editTextDevicePower.addTextChangedListener(roomPowerTextWatcher);
                editTextDeviceNumbers.addTextChangedListener(roomNumberTextWatcher);


                if(checkInputValue(dialog)){


                    try {
                        String newDeviceName = editTextDeviceName.getText().toString();
                        double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());

                        int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                        try {
                            finalSqlLiteDBHelper.updateDefaultDevice(newDeviceName,oldDeviceName,powerValue,h[0],m[0],number);
                        } catch (SQLEnergyCostException.WrongTime wrongTime) {
                            wrongTime.printStackTrace();
                        }
                        Toast.makeText(view.getContext(),"Urządzenie zaktualizowane",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        clearRoomList();
                        ViewDataFromDB(finalSqlLiteDBHelper.getDefaultDeviceList());
                        SettingActivity settingActivity = new SettingActivity();
                        settingActivity.refreshListView(view);
                        settings_listAdapter.notifyDataSetChanged();

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
                    text_field_inputeditTextDeviceNameLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputeditTextDeviceNumbersLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputeditTextDevicePowerLayout.setError("Brak danych!");
                    isNotEmpty = false;
                }else {
                    text_field_inputeditTextDevicePowerLayout.setError(null);
                }
                return isNotEmpty;
            }
        });
    }

}

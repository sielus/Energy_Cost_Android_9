package com.devdreams.energii.koszt.ui.settings;

import android.annotation.SuppressLint;
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
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.exception.SQLEnergyCostException;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SettingsDialogs {
    public static List<String> devicePower = new LinkedList<>();
    public static List<String> deviceName = new ArrayList<>();
    public static List<String> deviceTimeWork = new LinkedList<>();
    public static List<String> deviceNumber = new LinkedList<>();
    public static final List<String> device = new LinkedList<>();
    private boolean ifNumberOnStart = false;

    public void showDialogAddDevice(final View view){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.device_edit_dialog_layout_no_spinner);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);

        Button buttonColorPicker = dialog.findViewById(R.id.buttonColorPicker);
        buttonColorPicker.setEnabled(false);
        buttonColorPicker.setVisibility(View.INVISIBLE);

        Switch is24hSwitch = dialog.findViewById(R.id.switch1);
        final int[] h = new int[1];
        final int[] m = new int[1];
        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    Toast.makeText(dialog.getContext(),view.getResources().getString(R.string.toast_device_wokrs_all_day),Toast.LENGTH_SHORT).show();

                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work) +"\n " + h[0] + "h" + " " + m[0] + "m");

                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work));

                }
            }
        });

        final Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);
        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);

        final TextInputLayout text_field_inputEditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputEditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputEditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);

        buttonTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final TimePickerDialog timePickerDialog = new TimePickerDialog(dialog.getContext() , R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {


                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePickerV, int hourOfDay, int minute) {

                        timePickerV.setIs24HourView(true);
                        h[0] = hourOfDay;
                        m[0] = minute;
                        buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work) + " \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,12,0,true);
                Objects.requireNonNull(timePickerDialog.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

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
                        text_field_inputEditTextDeviceNameLayout.setError(view.getResources().getString(R.string.error_name_canot_start_from_number));
                        ifNumberOnStart = true;
                    } else {
                        text_field_inputEditTextDeviceNameLayout.setError(null);
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

                if(!editTextDevicePower.getText().toString().equals(".")) {
                    if (checkInputValue()) {
                        double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                        String deviceNameInput = editTextDeviceName.getText().toString();

                        int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                        try {
                            try {
                                DefaultDeviceManager defaultDeviceManager = new DefaultDeviceManager(view.getContext());
                                defaultDeviceManager.addDefaultDevice(deviceNameInput, powerValue, h[0], m[0], number);
                                Toast.makeText(view.getContext(), view.getResources().getString(R.string.toast_device_added), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                ViewDataFromDB(defaultDeviceManager.getDefaultDeviceList());
                                SettingActivity settingActivity = new SettingActivity();
                                settingActivity.refreshListView(view);

                            } catch (SQLEnergyCostException.WrongTime wrongTime) {
                                Toast.makeText(view.getContext(), wrongTime.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (SQLEnergyCostException.EmptyField | SQLEnergyCostException.DuplicationDevice errorMessage) {
                            Toast.makeText(view.getContext(), errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    text_field_inputEditTextDevicePowerLayout.setError(view.getResources().getString(R.string.error_no_data));
                }
            }

            private TextWatcher roomPowerTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputEditTextDevicePowerLayout.setError(null);
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
                    text_field_inputEditTextDeviceNumbersLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            
            private boolean checkInputValue() {

                boolean isNotEmpty = true;

                if(editTextDeviceName.getText().toString().isEmpty()) {
                    text_field_inputEditTextDeviceNameLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else if(ifNumberOnStart){
                    text_field_inputEditTextDeviceNameLayout.setError(view.getResources().getString(R.string.error_name_canot_start_from_number));
                    isNotEmpty = false;
                }
                else {
                    text_field_inputEditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputEditTextDeviceNumbersLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputEditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputEditTextDevicePowerLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputEditTextDevicePowerLayout.setError(null);
                }
                return isNotEmpty;
            }
        })
    ;}

    public void ViewDataFromDB(Cursor cursor) {
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

     public void clearRoomList() {
        device.clear();
        deviceNumber.clear();
        devicePower.clear();
        deviceTimeWork.clear();
        deviceName.clear();
    }

    @SuppressLint("SetTextI18n")
    public void showUpdateDialog(final View view, final String oldDeviceName, final SettingsListAdapter settings_listAdapter){
        final Dialog dialog = new Dialog(view.getContext());
        final DefaultDeviceManager defaultDeviceManager = new DefaultDeviceManager(view.getContext());

        clearRoomList();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.device_edit_dialog_layout_no_spinner);
        dialog.setCancelable(false);
        dialog.show();

        Button buttonColorPicker = dialog.findViewById(R.id.buttonColorPicker);
        buttonColorPicker.setEnabled(false);
        buttonColorPicker.setVisibility(View.INVISIBLE);

        viewDeviceInfoFromDB(defaultDeviceManager.getDetailsDefaultDevice(oldDeviceName));
        Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);

        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);

        final TextInputLayout text_field_inputEditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputEditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputEditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);
        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);

        editTextDeviceName.setText(SettingsDialogs.device.get(0));
        editTextDevicePower.setText(SettingsDialogs.device.get(1));
        editTextDeviceNumbers.setText(SettingsDialogs.device.get(3));

        final int[] h = {Integer.parseInt(SettingsDialogs.device.get(2).split(":")[0])};
        final int[] m = {Integer.parseInt(SettingsDialogs.device.get(2).split(":")[1])};

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch is24hSwitch = dialog.findViewById(R.id.switch1);
        buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work) + "\n " + h[0] + "h" + " " + m[0] + "m");
        if(h[0]==24){
            Toast.makeText(dialog.getContext(),view.getResources().getString(R.string.toast_device_wokrs_all_day),Toast.LENGTH_SHORT).show();

            is24hSwitch.setChecked(true);
            buttonTimePicker.setEnabled(false);
        }else{
            is24hSwitch.setChecked(false);
            buttonTimePicker.setEnabled(true);

        }
        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work)+" \n " + h[0] + "h" + " " + m[0] + "m");
                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work) + "\n " + h[0] + "h" + " " + m[0] + "m");
                }
            }
        });
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

                        buttonTimePicker.setText(view.getResources().getString(R.string.dialog_edit_device_button_time_work)+" \n " + h[0] + "h" + " " + m[0] + "m");
                    }
                } ,Integer.parseInt(SettingsDialogs.device.get(2).split(":")[0]),Integer.parseInt(SettingsDialogs.device.get(2).split(":")[1]),true);
                Objects.requireNonNull(timePickerDialog.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                timePickerDialog.show();
            }
        });

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDeviceName.addTextChangedListener(roomNameTextWatcher);
                editTextDevicePower.addTextChangedListener(roomPowerTextWatcher);
                editTextDeviceNumbers.addTextChangedListener(roomNumberTextWatcher);
                if(!editTextDevicePower.getText().toString().equals(".")){
                    if(checkInputValue()){
                            try {
                                String newDeviceName = editTextDeviceName.getText().toString();
                                double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                                int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());
                                try {
                                    defaultDeviceManager.updateDefaultDevice(newDeviceName,oldDeviceName,powerValue,h[0],m[0],number);
                                } catch (SQLEnergyCostException.WrongTime wrongTime) {
                                    wrongTime.printStackTrace();
                                }
                                Toast.makeText(view.getContext(),view.getResources().getString(R.string.toast_device_updated),Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                clearRoomList();
                                ViewDataFromDB(defaultDeviceManager.getDefaultDeviceList());
                                SettingActivity settingActivity = new SettingActivity();
                                settingActivity.refreshListView(view);
                                settings_listAdapter.notifyDataSetChanged();
                            }catch (SQLEnergyCostException.EmptyField | SQLEnergyCostException.DuplicationDevice exception) {
                                Toast.makeText(view.getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                exception.printStackTrace();
                            }
                        }
                    }else{
                    text_field_inputEditTextDevicePowerLayout.setError(view.getResources().getString(R.string.error_no_data));
                }
            }

            private TextWatcher roomNameTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text_field_inputEditTextDeviceNameLayout.setError(null);
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
                    text_field_inputEditTextDevicePowerLayout.setError(null);
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
                    text_field_inputEditTextDeviceNumbersLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            private boolean checkInputValue() {
                boolean isNotEmpty = true;
                if(editTextDeviceName.getText().toString().isEmpty()) {
                    text_field_inputEditTextDeviceNameLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputEditTextDeviceNameLayout.setError(null);
                }

                if(editTextDeviceNumbers.getText().toString().isEmpty()) {
                    text_field_inputEditTextDeviceNumbersLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputEditTextDeviceNumbersLayout.setError(null);
                }

                if(editTextDevicePower.getText().toString().isEmpty()) {
                    text_field_inputEditTextDevicePowerLayout.setError(view.getResources().getString(R.string.error_no_data));
                    isNotEmpty = false;
                }else {
                    text_field_inputEditTextDevicePowerLayout.setError(null);
                }
                return isNotEmpty;
            }
        });
    }

    private void viewDeviceInfoFromDB(Cursor cursor) {
        clearRoomList();
        device.clear();
        while(cursor.moveToNext()) {
            device.add((cursor.getString(0)));
            device.add((cursor.getString(1)));
            device.add((cursor.getString(2)));
            device.add((cursor.getString(3)));
        }
    }
}
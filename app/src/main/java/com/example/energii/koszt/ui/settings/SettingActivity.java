package com.example.energii.koszt.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.example.energii.koszt.ui.home.HomeFragment;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

@SuppressLint("Registered")
public class SettingActivity extends AppCompatActivity implements SettingsListAdapter.onNoteListener {
    private TextInputEditText inputEnergyCostGlobal;
    private TextInputLayout inputEnergyCostGlobalLayout;
    private TextInputLayout inputDefaultCurrencyGlobalLayout;
    private TextInputEditText inputDefaultCurrencyGlobal;
    private String powerCost;
    private HomeFragment homeFragment;
    RoomListFragment roomListFragment;
    private SQLLiteDBHelper sqlLiteDBHelper;
    SeekBar seekBar;
    boolean ifNumberOnStart = false;
    static View view;
    public static String numberAfterDot;
    private RecyclerView recyclerView;
    SettingsListAdapter settings_listAdapter;
    public static String defaultCurrency;

    private List<String> devicePower = new LinkedList<>();
    private List<String> deviceName = new ArrayList<>();
    private List<String> deviceTimeWork = new LinkedList<>();
    private List<String> deviceNumber = new LinkedList<>();
    public final List<String> device = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeFragment = new HomeFragment();
        super.onCreate(savedInstanceState);
        view = this.findViewById(android.R.id.content);
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        roomListFragment = new RoomListFragment();

        setTitle("Ustawienia");
        setContentView(R.layout.activity_setting_);
        seekBar = findViewById(R.id.seekBarNumericDecimal);
        recyclerView = view.findViewById(R.id.RecyckerViewSettings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(SettingActivity.this);

                return false;
            }
        });
        ConstraintLayout ConstraintLayoutSettings = view.findViewById(R.id.ConstraintLayoutSettings);
        ConstraintLayoutSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(SettingActivity.this);
                return false;
            }
        });

        ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);
        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class), this, Arrays.copyOf(devicePower.toArray(), devicePower.size(), String[].class), Arrays.copyOf(deviceNumber.toArray(), deviceNumber.size(), String[].class), Arrays.copyOf(deviceTimeWork.toArray(), deviceTimeWork.size(), String[].class));
        recyclerView.setAdapter(settings_listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Button adddefaultDevice = view.findViewById(R.id.addDefaultDevice);

        inputDefaultCurrencyGlobal = view.findViewById(R.id.inputDefaultCurrencyGlobal);
        inputDefaultCurrencyGlobalLayout = view.findViewById(R.id.text_field_inputinputDefaultCurrencyGlobal);

        inputDefaultCurrencyGlobal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputDefaultCurrencyGlobalLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkifEmptydefaultCurrency();

            }
        });




        inputDefaultCurrencyGlobal.setText(getdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));

        inputEnergyCostGlobal = view.findViewById(R.id.inputEnergyCostGlobal);
        inputEnergyCostGlobalLayout = view.findViewById(R.id.text_field_inputinputEnergyCostGlobal);
        int progres = Integer.parseInt(getnumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot")));
        seekBar.setProgress(progres);
        inputEnergyCostGlobal.setText(ViewDataPowerCostFromDB(sqlLiteDBHelper.getVariable("powerCost")));
        final TextView textView = view.findViewById(R.id.textViewNumericView);
        String text = String.format("%." + progres + "f", 0.1000);
        textView.setText(text);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.format("%." + progress + "f", 0.1000);
                textView.setText(text);
                numberAfterDot = Integer.toString(progress);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hideKeyboard(SettingActivity.this);


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sqlLiteDBHelper.setVariable("numberAfterDot", numberAfterDot);
                if (RoomListFragment.root != null) {
                    roomListFragment.generateChart(RoomListFragment.root);
                    roomListFragment.refreshTable(RoomListFragment.root);
                }

            }
        });


        adddefaultDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(SettingActivity.this);

                showDialogAddDevice(view);
            }
        });

        inputEnergyCostGlobal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEnergyCostGlobalLayout.setError(null);
                if (inputEnergyCostGlobal.length() == 1) {
                    inputEnergyCostGlobal.append(".");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIfEmptInputEnergyCosty();
            }
        });



    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.onBackPressed();
        return true;
    }

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


    String getValue() {
        return inputEnergyCostGlobal.getText().toString();
    }

    void checkIfEmptInputEnergyCosty() {
        String value = getValue();

        if (value.isEmpty() | value.equals(".")) {
            inputEnergyCostGlobalLayout.setError("Wprowadź dane!");
        } else {
            value = getValue();


            sqlLiteDBHelper.setVariable("powerCost", value);
           // sqlLiteDBHelper.setVariable("numberAfterDot", numberAfterDot);

            homeFragment.refresh(HomeFragment.root);

            if (RoomListFragment.root != null) {
                roomListFragment.generateChart(RoomListFragment.root);
                roomListFragment.refreshTable(RoomListFragment.root);
            }

            //finish();
        }
    }

    String getDefaultCurrency(){
        return  inputDefaultCurrencyGlobal.getText().toString();
    }

    void checkifEmptydefaultCurrency() {
        String defaultCurrency = getDefaultCurrency();

        if (defaultCurrency.isEmpty()) {
            inputDefaultCurrencyGlobalLayout.setError("Wprowadź dane!");
        } else {
            defaultCurrency = getDefaultCurrency();


            sqlLiteDBHelper.setVariable("defaultCurrency", defaultCurrency);
            // sqlLiteDBHelper.setVariable("numberAfterDot", numberAfterDot);

            homeFragment.refresh(HomeFragment.root);

            if (RoomListFragment.root != null) {
                roomListFragment.generateChart(RoomListFragment.root);
                roomListFragment.refreshTable(RoomListFragment.root);
            }

            //finish();
        }
    }

    String ViewDataPowerCostFromDB(Cursor cursor) {
        cursor.moveToFirst();
        powerCost = cursor.getString(0);
        return powerCost;
    }

    String getnumberAfterDotFromDB(Cursor cursor) {
        cursor.moveToFirst();
        numberAfterDot = cursor.getString(0);
        return numberAfterDot;
    }

    public int getNumberAfterDot(View root) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        numberAfterDot = getnumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot"));

        return Integer.parseInt(numberAfterDot);
    }

    @Override
    public void onNoteClick(int position) {

        showUpdateDialog(view, deviceName.get(position));

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
            sqlLiteDBHelper.deleteDefaultDevice(deviceName.get(viewHolder.getAdapterPosition()));
            int position = viewHolder.getAdapterPosition();
            deviceName.remove(position);

            clearRoomList();

            refreshListView(view);

            settings_listAdapter.notifyItemChanged(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.red))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
        }
    };

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

    void refreshListView(View root) {
        clearRoomList();
        ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());
        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class), this, Arrays.copyOf(devicePower.toArray(), devicePower.size(), String[].class), Arrays.copyOf(deviceNumber.toArray(), deviceNumber.size(), String[].class), Arrays.copyOf(deviceTimeWork.toArray(), deviceTimeWork.size(), String[].class));
        recyclerView.setAdapter(settings_listAdapter);
    }

    public void showUpdateDialog(final View view, final String oldDeviceName){
        final Dialog dialog = new Dialog(view.getContext());
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
        editTextDeviceName.setText(device.get(0));
        editTextDevicePower.setText(device.get(1));
        editTextDeviceNumbers.setText(device.get(3));
        final int[] h = {Integer.parseInt(device.get(2).split(":")[0])};
        final int[] m = {Integer.parseInt(device.get(2).split(":")[1])};
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
                } ,Integer.parseInt(device.get(2).split(":")[0]),Integer.parseInt(device.get(2).split(":")[1]),true);
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


                    try {
                        String newDeviceName = editTextDeviceName.getText().toString();
                        double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());

                        int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                        try {
                            sqlLiteDBHelper.updateDefaultDevice(newDeviceName,oldDeviceName,powerValue,h[0],m[0],number);
                        } catch (SQLEnergyCostException.WrongTime wrongTime) {
                            wrongTime.printStackTrace();
                        }
                        Toast.makeText(view.getContext(),"Urządzenie zaktualizowane",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        clearRoomList();
                        ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());
                        refreshListView(view);
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
                            sqlLiteDBHelper.addDefaultDevice(deviceNameInput, powerValue, h[0], m[0], number);
                            Toast.makeText(view.getContext(), "Urządzenie dodane", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());
                            refreshListView(view);

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        activity.getWindow().getDecorView().clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    String getdefaultCurrencyFromDB(Cursor cursor) {
        cursor.moveToFirst();
        defaultCurrency = cursor.getString(0);
        return defaultCurrency;
    }

    public String getdefaultCurrency(View view) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        defaultCurrency = getdefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency"));

        return defaultCurrency;
    }
}







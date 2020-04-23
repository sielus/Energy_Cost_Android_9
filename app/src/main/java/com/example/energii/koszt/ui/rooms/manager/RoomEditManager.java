package com.example.energii.koszt.ui.rooms.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomEditManager extends AppCompatActivity implements RoomEditManagerListAdapter.onNoteListener{
    private RecyclerView recyclerView;

    static public View view;
    RoomEditManagerListAdapter adapter;
    public Dialog room_name_dialog;
    public SQLLiteDBHelper sqlLiteDBHelper;
    public static String room_name;
    public final List<String> device = new LinkedList<>();
    private List<String> devicePower = new LinkedList<>();
    private List<String> deviceName = new ArrayList<>();
    private List<String> deviceTimeWork = new LinkedList<>();
    private List<String> deviceNumber = new LinkedList<>();

    ArrayList<String> defaultListDeviceName = new ArrayList<>();
    ArrayList<String> defaultListDevicePower= new ArrayList<>();
    ArrayList<String> defaultListDeviceTimeWork = new ArrayList<>();
    ArrayList<String> defaultListDeviceNumber = new ArrayList<>();


    public TextInputLayout text_field_inputRoomNameLayout;
    PieChart pieChart;

    BarChart barChart;
    TableLayout tableLayout;
    ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
    TextView title_summary;
    int numberAfterDot;
    boolean ifNumberOnStart = false;
    TimePickerDialog timePicker;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.room_settings:
                showDialogEditRoomName(view);
                break;
        }
        return true;
    }

    private List<String> roomCostKWH = new ArrayList<>();
    private List<String> deviceCostKWH = new ArrayList<>();

    TextView outputEnergyCostUser;
    TextView outputEnergyCostDay;
    TextView outputEnergyCostMonth;
    TextView outputEnergyCostUserKwh;
    TextView outputEnergyCostDayKwh;
    TextView outputEnergyCostMonthKwh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        // getSupportActionBar().hide();
        setTitle("Pokój " + room_name.replace("_"," "));




        SettingActivity settingActivity = new SettingActivity();
        numberAfterDot = settingActivity.getNumberAfterDot(view);

        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        clearDefaultDeviceList();

        getDefaultDeviceList(sqlLiteDBHelper.getDefaultDeviceList());

        getDeviceCostKWH(sqlLiteDBHelper.getDeviceDetails(room_name));

        generateChartinRoom(view);
        refreshTable();


        recyclerView = findViewById(R.id.RecyckerView);

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

        adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),this,Arrays.copyOf(devicePower.toArray(), devicePower.size(), String[].class),Arrays.copyOf(deviceNumber.toArray(), deviceNumber.size(), String[].class),Arrays.copyOf(deviceTimeWork.toArray(), deviceTimeWork.size(), String[].class));
        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        FloatingActionButton floatingActionButtonAddDevice = findViewById(R.id.addButonfl);
        floatingActionButtonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddDevice(view);

            }
        });

    }




    private void showDialogEditRoomName(final View view) {
        final Dialog room_name_dialog = new Dialog(view.getContext());
        room_name_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        room_name_dialog.setContentView(R.layout.room_list_dialog);
        room_name_dialog.show();
        Button buttonDialogAccept = room_name_dialog.findViewById(R.id.ButtonAddRoom);
        final TextInputEditText text_field_inputRoomName = room_name_dialog.findViewById(R.id.text_field_inputRoomName);
        final TextInputLayout text_field_inputRoomNameLayout = room_name_dialog.findViewById(R.id.text_field_inputRoomNameLayout);
        text_field_inputRoomNameLayout.setHint("Wprowadź nową nazwę pokoju");
        text_field_inputRoomName.setText(room_name.replace("_"," "));

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
                        text_field_inputRoomNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
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
                    text_field_inputRoomNameLayout.setError("Brak danych!");
                }                else if(ifNumberOnStart){
                    text_field_inputRoomNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
                }
                else{
                        Toast.makeText(view.getContext(),"Nowa nazwa pokoju : " + newRoomName,Toast.LENGTH_SHORT).show();
                        setTitle("Pokój " + newRoomName);
                        RoomListFragment roomListFragment = new RoomListFragment();


                        room_name_dialog.dismiss();
                  try {
                          sqlLiteDBHelper.updateRoomName(room_name,newRoomName);
                          room_name = newRoomName;

                          roomListFragment.clearRoomList();
                          roomListFragment.ViewDataFromDB(sqlLiteDBHelper.getRoomList());
                          roomListFragment.refreshListView(RoomListFragment.root);
                          roomListFragment.generateChart(RoomListFragment.root);
                          generateChartinRoom(view);

                  } catch (SQLEnergyCostException.DuplicationRoom duplicationRoom) {
                        duplicationRoom.printStackTrace();
                    }


                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void refreshTable() {


        TextView outputEnergyCostUser = view.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostYearth = view.findViewById(R.id.OutputEnergyCostMonth);

        TextView outputEnergyCostUserKwh = view.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostYearthKwh= view.findViewById(R.id.OutputEnergyCostMonthKwh);

        if(sqlLiteDBHelper.getRoomDeviceList(room_name).getCount() != 0){

            getRoomCostKwh(sqlLiteDBHelper.getRoomCost(room_name));
            outputEnergyCostUser.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000) + " kWh");
            outputEnergyCostUserKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1))) + " zł");

            outputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 30) + " kWh");
            outputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 30) + " zł");

            outputEnergyCostYearth.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(0)) / 1000 * 365 )+ " kWh");
            outputEnergyCostYearthKwh.setText(String.format("%."+ numberAfterDot +"f", Float.parseFloat(roomCostKWH.get(1)) * 365 )+ " zł");
        }else{
            tableLayout = view.findViewById(R.id.tableLayout);
            tableLayout.setVisibility(View.GONE);
            title_summary = view.findViewById(R.id.title_summary);
            title_summary.setVisibility(View.GONE);
            outputEnergyCostUser.setText("0 zł");
            outputEnergyCostUserKwh.setText("0 kWh");

            outputEnergyCostMonth.setText("0 zł");
            outputEnergyCostMonthKwh.setText("0 kWh");

            outputEnergyCostYearth.setText("0 zł");
            outputEnergyCostYearthKwh.setText("0 kWh");
        }
    }

    void ViewDataFromDB(Cursor cursor) {
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

    void getDefaultDeviceList(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearDefaultDeviceList();
            defaultListDeviceName.add(0,"Przykłady");
            defaultListDeviceNumber.add(0,"");
            defaultListDeviceTimeWork.add(0,"0:0");
            defaultListDevicePower.add(0,"");


            while(cursor.moveToNext()) {

                defaultListDeviceName.add(cursor.getString(0));
                defaultListDevicePower.add(cursor.getString(1));
                defaultListDeviceNumber.add(cursor.getString(3));
                defaultListDeviceTimeWork.add(cursor.getString(2));
            }

        }
    }

    boolean getRoomCostKwh(Cursor cursor){
        roomCostKWH.clear();

        if (cursor.getCount() != 0) {
            while(cursor.moveToNext()) {
                roomCostKWH.add(cursor.getString(0));
                roomCostKWH.add(cursor.getString(1));

            } return true;

        } return false;

    }

    void getDeviceCostKWH(Cursor cursor){
        if (cursor.getCount() != 0) {

            while(cursor.moveToNext()) {
                deviceCostKWH.add(cursor.getString(0));
                deviceCostKWH.add(cursor.getString(1));
                deviceCostKWH.add(cursor.getString(2));

            }
        }
    }

    void clearRoomList() {
        deviceNumber.clear();
        devicePower.clear();
        deviceTimeWork.clear();
        deviceName.clear();
    }

    void refreshListView(View root) {

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),this,Arrays.copyOf(devicePower.toArray(), devicePower.size(), String[].class),Arrays.copyOf(deviceNumber.toArray(), deviceNumber.size(), String[].class),Arrays.copyOf(deviceTimeWork.toArray(), deviceTimeWork.size(), String[].class));

        recyclerView.setAdapter(adapter);
    }

    public void showDialogAddDevice(final View view){
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(arrayAdapter.getItem(0)==arrayAdapter.getItem(position)){

                }else{
                    Toast.makeText(parent.getContext(),String.valueOf(arrayAdapter.getItem(position)),Toast.LENGTH_SHORT).show();
                    editTextDeviceName.setText(defaultListDeviceName.get(position));
                    editTextDevicePower.setText(defaultListDevicePower.get(position));
                    editTextDeviceNumbers.setText(defaultListDeviceNumber.get(position));
                    h[0] = Integer.parseInt(defaultListDeviceTimeWork.get(position).split(":")[0]);
                    m[0] = Integer.parseInt(defaultListDeviceTimeWork.get(position).split(":")[1]);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
                    if(h[0]==24){
                        is24hSwitch.setChecked(true);
                        buttonTimePicker.setEnabled(false);
                    }else{
                        is24hSwitch.setChecked(false);
                        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
                        buttonTimePicker.setEnabled(true);

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        is24hSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(dialog.getContext(),String.valueOf(isChecked),Toast.LENGTH_SHORT).show();
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;
                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");

                }else{
                    buttonTimePicker.setEnabled(true);
                    buttonTimePicker.setText("Czas pracy");

                }
            }
        });

        final Button buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);

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

                TimePickerDialog timePickerDialog = new TimePickerDialog(dialog.getContext() , R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    

                    @Override
                    public void onTimeSet(TimePicker timePickerV, int hourOfDay, int minute) {
                        timePickerV.setIs24HourView(true);
                         h[0] = hourOfDay;
                         m[0] = minute;
                        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
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
                            sqlLiteDBHelper.addDevice(room_name, deviceNameInput, powerValue, h[0], m[0], number);
                            Toast.makeText(view.getContext(), "Urządzenie dodane", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            RoomListFragment roomListFragment = new RoomListFragment();
                            ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

                            roomListFragment.generateChart(RoomListFragment.root);
                            generateChartinRoom(view);

                            roomListFragment.clearRoomList();
                            roomListFragment.ViewDataFromDB(sqlLiteDBHelper.getRoomList());
                            roomListFragment.refreshListView(RoomListFragment.root);

                            refreshListView(view);
                            refreshTable();

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
        });


    }

    private void clearDefaultDeviceList() {
        defaultListDeviceName.clear();
    }

    public void showUpdateDialog(final View view, final String roomName, String deviceName){
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
       // final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
      //  final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);

        final TextInputLayout text_field_inputeditTextDeviceNameLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNameLayout);
        final TextInputLayout text_field_inputeditTextDeviceNumbersLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceNumbersLayout);
        final TextInputLayout text_field_inputeditTextDevicePowerLayout = dialog.findViewById(R.id.text_field_inputeditTextDevicePowerLayout);
       // final TextInputLayout text_field_inputeditTextDeviceWorkHLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkHLayout);
      //  final TextInputLayout text_field_inputeditTextDeviceWorkMLayout = dialog.findViewById(R.id.text_field_inputeditTextDeviceWorkMLayout);
        final Button buttonTimePicker = dialog.findViewById(R.id.buttonTimePicker);

        editTextDeviceName.setText(device.get(1));
        editTextDevicePower.setText(device.get(2));
        editTextDeviceNumbers.setText(device.get(4));
        final int[] h = {Integer.parseInt(device.get(3).split(":")[0])};
        final int[] m = {Integer.parseInt(device.get(3).split(":")[1])};
      //  editTextDeviceWorkH.setText(device.get(3).split(":")[0]);
        //editTextDeviceWorkM.setText(device.get(3).split(":")[1]);
        Switch is24hSwitch = dialog.findViewById(R.id.switch1);
        buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");
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
                Toast.makeText(dialog.getContext(),String.valueOf(isChecked),Toast.LENGTH_SHORT).show();
                if(isChecked){
                    h[0] = 24;
                    m[0] = 0;

                    buttonTimePicker.setEnabled(false);
                    buttonTimePicker.setText("Czas pracy \n " + h[0] + "h" + " " + m[0] + "m");

                }else{
                    final int[] h = {Integer.parseInt(device.get(3).split(":")[0])};
                    final int[] m = {Integer.parseInt(device.get(3).split(":")[1])};
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
                        Toast.makeText(view.getContext(),"Urządzenie zaktualizowane",Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                        RoomListFragment roomListFragment = new RoomListFragment();
                        roomListFragment.generateChart(RoomListFragment.root);

                        roomListFragment.clearRoomList();
                        roomListFragment.ViewDataFromDB(sqlLiteDBHelper.getRoomList());
                        roomListFragment.refreshListView(RoomListFragment.root);
                        refreshTable();

                        roomListFragment.generateChart(RoomListFragment.root);
                        generateChartinRoom(view);

                        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                        refreshListView(view);
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

    public void onNoteClick(int position) {

        showUpdateDialog(view,room_name,deviceName.get(position));


    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
            sqlLiteDBHelper.deleteDevice(RoomEditManager.room_name,deviceName.get(viewHolder.getAdapterPosition()));

            int position = viewHolder.getAdapterPosition();
            deviceName.remove(position);
            generateChartinRoom(view);

            clearRoomList();
            ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

            refreshListView(view);

            adapter.notifyItemChanged(position);

            RoomListFragment roomListFragment = new RoomListFragment();
            View view;
            BarChart barChart = RoomListFragment.root.findViewById(R.id.bartChart);
            roomListFragment.clearRoomList();
            roomListFragment.ViewDataFromDB(sqlLiteDBHelper.getRoomList());
            roomListFragment.refreshListView(RoomListFragment.root);

            barChart.clear();
            barChart.invalidate();
            barChart.clear();

            roomListFragment.generateChart(RoomListFragment.root);
            refreshTable();
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

    public void generateChartinRoom(View root){
        SQLLiteDBHelper sqlLiteDBHelper;
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        PieChart pieChart;
        BarChart barChart;
        tableLayout = root.findViewById(R.id.tableLayout);
        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        title_summary = view.findViewById(R.id.title_summary);

        if(sqlLiteDBHelper.getRoomDeviceList(room_name).getCount()==0){
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            title_summary.setVisibility(View.GONE);
        }else {
            title_summary.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
        }

        pieChart.invalidate();
        barChart.invalidate();
        String[] xAxisLables = new String[]{};
        List<String> roomName = new ArrayList<>();
        roomName.clear();
        xAxisLables = null;
        int labelNumberIndex = 0;
        barEntries.clear();
        pieEntry.clear();

        Cursor cursor = sqlLiteDBHelper.getDeviceDetails(room_name);
        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {
                pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) +" kWh" ));
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));
                roomName.add(cursor.getString(0));
                labelNumberIndex = labelNumberIndex +1;
            }

        }else if(cursor.getCount() == 1){
            cursor.moveToFirst();
            pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) +" kWh" ));
            barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f", cursor.getFloat(2)).replace(",","."))));
            roomName.add(cursor.getString(0));
        }else {
            return;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Koszty dobowe urządzeń");

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);

        XAxis axis = barChart.getXAxis();

        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextSize(14);
        barChart.getAxisLeft().setTextSize(14);
        barChart.getLegend().setTextSize(14f);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setTouchEnabled(false);
        barChart.setFitBars(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);

        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setTextSize(16f);
        axis.setDrawGridLines(true);
        axis.setTextColor(Color.WHITE);
        axis.setDrawAxisLine(true);
        axis.setCenterAxisLabels(false);
        axis.setLabelCount(roomName.size());

        xAxisLables = null;
        xAxisLables = Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        axis.setGranularity(1f);
        axis.setGranularityEnabled(true);

        barDataSet.setValueTextSize(14);
        barDataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(barDataSet);

        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setText("");
        barChart.getLegend().setEnabled(true);
        barChart.animateY(1000);

        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Dane");

        pieChart.getLegend().setEnabled(false);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueLineColor(R.color.colorAccent);
        pieDataSet.setValueTextSize(14);

        PieData pieData = new PieData(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(30);
        pieChart.setTransparentCircleRadius(10);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Zużycie kWh");
        pieChart.animate();
    }
}
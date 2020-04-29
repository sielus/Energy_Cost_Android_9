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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.home.HomeFragment;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Arrays;
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
    static View view;
    public static String numberAfterDot;
    private RecyclerView recyclerView;
    SettingsListAdapter settings_listAdapter;
    public static String defaultCurrency;
    SettingsDialogs settingsDialogs;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeFragment = new HomeFragment();
        super.onCreate(savedInstanceState);
        view = this.findViewById(android.R.id.content);
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        roomListFragment = new RoomListFragment();

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

        settingsDialogs = new SettingsDialogs();
        settingsDialogs.ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);

        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(SettingsDialogs.deviceName.toArray(), SettingsDialogs.deviceName.size(), String[].class), this, Arrays.copyOf(SettingsDialogs.devicePower.toArray(), SettingsDialogs.devicePower.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceNumber.toArray(), SettingsDialogs.deviceNumber.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceTimeWork.toArray(), SettingsDialogs.deviceTimeWork.size(), String[].class));

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
                SettingsDialogs settingsDialogs = new SettingsDialogs();

                settingsDialogs.showDialogAddDevice(view);
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

    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
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
        SettingsDialogs settingsDialogs = new SettingsDialogs();
        settingsDialogs.showUpdateDialog(view, SettingsDialogs.deviceName.get(position),settings_listAdapter);

    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
            sqlLiteDBHelper.deleteDefaultDevice(SettingsDialogs.deviceName.get(viewHolder.getAdapterPosition()));
            int position = viewHolder.getAdapterPosition();
            SettingsDialogs.deviceName.remove(position);
            SettingsDialogs settingsDialogs = new SettingsDialogs();
            settingsDialogs.clearRoomList();
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
    void refreshListView(View root) {
        SettingsDialogs settingsDialogs = new SettingsDialogs();
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        settingsDialogs.clearRoomList();
        settingsDialogs.ViewDataFromDB(sqlLiteDBHelper.getDefaultDeviceList());
        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(SettingsDialogs.deviceName.toArray(), SettingsDialogs.deviceName.size(), String[].class), this, Arrays.copyOf(SettingsDialogs.devicePower.toArray(), SettingsDialogs.devicePower.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceNumber.toArray(), SettingsDialogs.deviceNumber.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceTimeWork.toArray(), SettingsDialogs.deviceTimeWork.size(), String[].class));
        RecyclerView recyclerView = root.findViewById(R.id.RecyckerViewSettings);
        recyclerView.setAdapter(settings_listAdapter);
    }
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












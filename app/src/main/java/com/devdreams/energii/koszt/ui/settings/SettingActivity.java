package com.devdreams.energii.koszt.ui.settings;

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
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.TutorialShowcase;
import com.devdreams.energii.koszt.ui.home.HomeFragment;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;
import com.devdreams.energii.koszt.ui.sunEnergyCalculator.SunEnergyCalculatorFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Arrays;
import java.util.Objects;


import co.mobiwise.materialintro.shape.ShapeType;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

@SuppressLint("Registered")
public class SettingActivity extends AppCompatActivity implements SettingsListAdapter.onNoteListener {
    public static String numberAfterDot;
    public static String defaultCurrency;
    private TextInputEditText inputEnergyCostGlobal;
    private TextInputLayout inputEnergyCostGlobalLayout;
    private TextInputLayout inputDefaultCurrencyGlobalLayout;
    private TextInputEditText inputDefaultCurrencyGlobal;
    private HomeFragment homeFragment;
    private RoomListFragment roomListFragment;
    private SQLLiteDBHelper sqlLiteDBHelper;
    private DefaultDeviceManager defaultDeviceManager;
    private SettingsListAdapter settings_listAdapter;
    @SuppressLint("StaticFieldLeak")
    private static View view;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeFragment = new HomeFragment();
        super.onCreate(savedInstanceState);
        view = this.findViewById(android.R.id.content);

        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        defaultDeviceManager = new DefaultDeviceManager(view.getContext());
        roomListFragment = new RoomListFragment();


        setContentView(R.layout.activity_setting_);
        SeekBar seekBar = findViewById(R.id.seekBarNumericDecimal);
        RecyclerView recyclerView = view.findViewById(R.id.RecyckerViewSettings);

        TutorialShowcase tutorialShowcase = new TutorialShowcase(this);
        tutorialShowcase.tutorialSettings(recyclerView, ShapeType.RECTANGLE,getResources().getString(R.string.tutorial_settings_start)
                + String.valueOf(defaultDeviceManager.countDefaultDevices()) + " "
                + getResources().getString(R.string.tutorial_settings_end) ,"settings");




        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = view.findViewById(R.id.title_summary);
        titleTextView.setText(getResources().getString(R.string.settings_default_devices_list) + " : " + String.valueOf(defaultDeviceManager.countDefaultDevices()));


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

        SettingsDialogs settingsDialogs = new SettingsDialogs();
        settingsDialogs.ViewDataFromDB(defaultDeviceManager.getDefaultDeviceList());

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);

        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(SettingsDialogs.deviceName.toArray(),
                SettingsDialogs.deviceName.size(), String[].class), this, Arrays.copyOf(SettingsDialogs.devicePower.toArray(),
                SettingsDialogs.devicePower.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceNumber.toArray(), SettingsDialogs.deviceNumber.size(),
                String[].class), Arrays.copyOf(SettingsDialogs.deviceTimeWork.toArray(), SettingsDialogs.deviceTimeWork.size(), String[].class));

        recyclerView.setAdapter(settings_listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Button addDefaultDevice = view.findViewById(R.id.addDefaultDevice);

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
                checkIfEmptyDefaultCurrency();
            }
        });

        inputDefaultCurrencyGlobal.setText(getDefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency")));
        inputEnergyCostGlobal = view.findViewById(R.id.inputEnergyCostGlobal);
        inputEnergyCostGlobalLayout = view.findViewById(R.id.text_field_inputinputEnergyCostGlobal);
        int progress = Integer.parseInt(getNumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot")));

        seekBar.setProgress(progress);
        inputEnergyCostGlobal.clearFocus();
        inputEnergyCostGlobal.setText(ViewDataPowerCostFromDB(sqlLiteDBHelper.getVariable("powerCost")));

        final TextView textView = view.findViewById(R.id.textViewNumericView);
        String text = String.format("%." + progress + "f", 0.1000);
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

        addDefaultDevice.setOnClickListener(new View.OnClickListener() {
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
                if(!inputEnergyCostGlobal.getText().toString().isEmpty()){
                    checkIfEmptyInputEnergyCost();
                }else {
                    inputEnergyCostGlobalLayout.setError(getResources().getString(R.string.error_no_data));
                }
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

    public void checkIfEmptyInputEnergyCost() {
        String value = getValue();
        if (value.isEmpty() | value.equals(".")) {
            inputEnergyCostGlobalLayout.setError(getResources().getString(R.string.error_no_data));
        } else {
            if(!getValue().isEmpty()){
                value = getValue();
                sqlLiteDBHelper.setVariable("powerCost", value);
                if (HomeFragment.root != null) {
                    homeFragment.refresh(HomeFragment.root);
                }

                if (RoomListFragment.root != null) {
                    roomListFragment.generateChart(RoomListFragment.root);
                    roomListFragment.refreshTable(RoomListFragment.root);
                }
            }

        }
    }

    public String getDefaultCurrency(){
        return  inputDefaultCurrencyGlobal.getText().toString();
    }

    public void checkIfEmptyDefaultCurrency() {
        String defaultCurrency = getDefaultCurrency();
        if (defaultCurrency.isEmpty()) {
            inputDefaultCurrencyGlobalLayout.setError(getResources().getString(R.string.error_no_data));
        } else {
            defaultCurrency = getDefaultCurrency();
            sqlLiteDBHelper.setVariable("defaultCurrency", defaultCurrency);

            if (HomeFragment.root != null) {
                homeFragment.refresh(HomeFragment.root);
            }

            if (RoomListFragment.root != null) {
                roomListFragment.generateChart(RoomListFragment.root);
                roomListFragment.refreshTable(RoomListFragment.root);
            }

            if (SunEnergyCalculatorFragment.root != null) {
                SunEnergyCalculatorFragment.setNewDefaultCurrency(SunEnergyCalculatorFragment.root);
            }
        }
    }

    public String ViewDataPowerCostFromDB(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public String getNumberAfterDotFromDB(Cursor cursor) {
        cursor.moveToFirst();
        numberAfterDot = cursor.getString(0);
        return numberAfterDot;
    }

    public int getNumberAfterDot(View root) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        numberAfterDot = getNumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot"));

        return Integer.parseInt(numberAfterDot);
    }

    @Override
    public void onNoteClick(int position) {
        SettingsDialogs settingsDialogs = new SettingsDialogs();
        settingsDialogs.showUpdateDialog(view, SettingsDialogs.deviceName.get(position),settings_listAdapter);

    }

    public ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            defaultDeviceManager = new DefaultDeviceManager(view.getContext());
            defaultDeviceManager.deleteDefaultDevice(SettingsDialogs.deviceName.get(viewHolder.getAdapterPosition()));
            int position = viewHolder.getAdapterPosition();
            SettingsDialogs.deviceName.remove(position);
            SettingsDialogs settingsDialogs = new SettingsDialogs();
            settingsDialogs.clearRoomList();
            refreshListView(view);
            settings_listAdapter.notifyItemChanged(position);
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.red))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
        }
    };

    public void refreshListView(View root) {
        SettingsDialogs settingsDialogs = new SettingsDialogs();
        DefaultDeviceManager defaultDeviceManager = new DefaultDeviceManager(root.getContext());
        settingsDialogs.clearRoomList();
        settingsDialogs.ViewDataFromDB(defaultDeviceManager.getDefaultDeviceList());
        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(SettingsDialogs.deviceName.toArray(),
                SettingsDialogs.deviceName.size(), String[].class), this, Arrays.copyOf(SettingsDialogs.devicePower.toArray(),
                SettingsDialogs.devicePower.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceNumber.toArray(),
                SettingsDialogs.deviceNumber.size(), String[].class), Arrays.copyOf(SettingsDialogs.deviceTimeWork.toArray(),
                SettingsDialogs.deviceTimeWork.size(), String[].class));
        RecyclerView recyclerView = root.findViewById(R.id.RecyckerViewSettings);
        recyclerView.setAdapter(settings_listAdapter);

        TextView titleTextView = view.findViewById(R.id.title_summary);
        titleTextView.setText(root.getResources().getString(R.string.settings_default_devices_list) + " : " + String.valueOf(defaultDeviceManager.countDefaultDevices()));

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        activity.getWindow().getDecorView().clearFocus();
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDefaultCurrencyFromDB(Cursor cursor) {
        cursor.moveToFirst();
        defaultCurrency = cursor.getString(0);
        return defaultCurrency;
    }

    public String getDefaultCurrency(View view) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        defaultCurrency = getDefaultCurrencyFromDB(sqlLiteDBHelper.getVariable("defaultCurrency"));

        return defaultCurrency;
    }
}












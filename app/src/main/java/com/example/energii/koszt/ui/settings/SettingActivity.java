package com.example.energii.koszt.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
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
    private String powerCost;
    private HomeFragment homeFragment;
    RoomListFragment roomListFragment;
    private SQLLiteDBHelper sqlLiteDBHelper;
    SeekBar seekBar;
    static View view;
    public static String numberAfterDot;
    private RecyclerView recyclerView;
    SettingsListAdapter settings_listAdapter;
    private List<String> devicePower = new LinkedList<>();
    private List<String> deviceName = new ArrayList<>();
    private List<String> deviceTimeWork = new LinkedList<>();
    private List<String> deviceNumber = new LinkedList<>();

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

        devicePower.add("test");
        deviceName.add("test");
        deviceTimeWork.add("test");
        deviceNumber.add("test");

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);
        settings_listAdapter = new SettingsListAdapter(view.getContext(), Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class), this, Arrays.copyOf(devicePower.toArray(), devicePower.size(), String[].class), Arrays.copyOf(deviceNumber.toArray(), deviceNumber.size(), String[].class), Arrays.copyOf(deviceTimeWork.toArray(), deviceTimeWork.size(), String[].class));
        recyclerView.setAdapter(settings_listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        inputEnergyCostGlobal = view.findViewById(R.id.inputEnergyCostGlobal);
        inputEnergyCostGlobalLayout = view.findViewById(R.id.text_field_inputinputEnergyCostGlobal);
        int progres = Integer.parseInt(ViewDataNumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot")));
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

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

            }
        });

        Button buttonSettingsAccept = view.findViewById(R.id.buttonSettingsAccept);

        buttonSettingsAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfEmpty();
            }
        });
    }

    String getValue() {
        return inputEnergyCostGlobal.getText().toString();
    }

    void checkIfEmpty() {
        String value = getValue();

        if (value.isEmpty()) {
            inputEnergyCostGlobalLayout.setError("Wprowadź dane!");
        } else {
            value = getValue();


            sqlLiteDBHelper.setVariable("powerCost", value);
            sqlLiteDBHelper.setVariable("numberAfterDot", numberAfterDot);

            homeFragment.refresh(HomeFragment.root);

            if (RoomListFragment.root != null) {
                roomListFragment.generateChart(RoomListFragment.root);
                roomListFragment.refreshTable(RoomListFragment.root);
            }
            finish();
        }
    }

    String ViewDataPowerCostFromDB(Cursor cursor) {
        cursor.moveToFirst();
        powerCost = cursor.getString(0);
        return powerCost;
    }

    String ViewDataNumberAfterDotFromDB(Cursor cursor) {
        cursor.moveToFirst();
        numberAfterDot = cursor.getString(0);
        return numberAfterDot;
    }

    public int getNumberAfterDot(View root) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        numberAfterDot = ViewDataNumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot"));

        return Integer.parseInt(numberAfterDot);
    }

    @Override
    public void onNoteClick(int position) {

        Toast.makeText(view.getContext(), "onClick", Toast.LENGTH_SHORT).show();

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            Toast.makeText(view.getContext(),"test",Toast.LENGTH_SHORT).show();
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
}

package com.example.energii.koszt.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.home.HomeFragment;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("Registered")
public class SettingActivity extends AppCompatActivity {
    private TextInputEditText inputEnergyCostGlobal;
    private TextInputLayout inputEnergyCostGlobalLayout;
    private String powerCost;
    private HomeFragment homeFragment;
    RoomListFragment roomListFragment;
    private SQLLiteDBHelper sqlLiteDBHelper;
    SeekBar seekBar;
    public static String numberAfterDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeFragment = new HomeFragment();
        super.onCreate(savedInstanceState);
        final View view = this.findViewById(android.R.id.content);
        sqlLiteDBHelper= new SQLLiteDBHelper(view.getContext());

        roomListFragment = new RoomListFragment();

        setTitle("Ustawienia");
        setContentView(R.layout.activity_setting_);
        seekBar = findViewById(R.id.seekBarNumericDecimal);

        inputEnergyCostGlobal = view.findViewById(R.id.inputEnergyCostGlobal);
        inputEnergyCostGlobalLayout = view.findViewById(R.id.text_field_inputinputEnergyCostGlobal);
        int progres = Integer.parseInt(ViewDataNumberAfterDotFromDB(sqlLiteDBHelper.getVariable("numberAfterDot")));
        seekBar.setProgress(progres);
        inputEnergyCostGlobal.setText(ViewDataPowerCostFromDB(sqlLiteDBHelper.getVariable("powerCost")));
        final TextView textView = view.findViewById(R.id.textViewNumericView);
        String text = String.format("%."+ progres +"f",0.1000);
        textView.setText(text);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.format("%."+ progress +"f",0.1000);
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
                if(inputEnergyCostGlobal.length()==1){
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

    String getValue(){
        return inputEnergyCostGlobal.getText().toString();
    }

    void checkIfEmpty(){
        String value = getValue();

        if(value.isEmpty()){
            inputEnergyCostGlobalLayout.setError("Wprowadź dane!");
        }else {
            value = getValue();


            sqlLiteDBHelper.setVariable("powerCost",value );
            sqlLiteDBHelper.setVariable("numberAfterDot",numberAfterDot);

            homeFragment.refresh(HomeFragment.root);

            if(RoomListFragment.root!=null){
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
}

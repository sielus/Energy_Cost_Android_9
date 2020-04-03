package com.example.energii.koszt.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.home.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

@SuppressLint("Registered")
public class SettingActivity extends AppCompatActivity {
    private TextInputEditText inputEnergyCostGlobal;
    private TextInputLayout inputEnergyCostGlobalLayout;
    private String powerCost;
    private HomeFragment homeFragment;
    private SQLLiteDBHelper sqlLiteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeFragment = new HomeFragment();
        super.onCreate(savedInstanceState);
        View view = this.findViewById(android.R.id.content);
        sqlLiteDBHelper= new SQLLiteDBHelper(view.getContext());

        setTitle("Ustawienia");
        setContentView(R.layout.activity_setting_);

        inputEnergyCostGlobal = view.findViewById(R.id.inputEnergyCostGlobal);
        inputEnergyCostGlobalLayout = view.findViewById(R.id.text_field_inputinputEnergyCostGlobal);

        inputEnergyCostGlobal.setText(ViewDataFromDB(sqlLiteDBHelper.getVariable("powerCost")));


        inputEnergyCostGlobal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEnergyCostGlobalLayout.setError(null);
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
            Toast.makeText(getApplicationContext(),"Cena globala pradu : " + value + " zł",Toast.LENGTH_SHORT).show();
            sqlLiteDBHelper.setVariable("powerCost",value);
            homeFragment.refresh(HomeFragment.root);
            finish();
        }
    }

    String ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            System.out.println(cursor);

            while(cursor.moveToNext()) {
               powerCost = cursor.getString(0);
            }
        }

        return powerCost;
    }
}

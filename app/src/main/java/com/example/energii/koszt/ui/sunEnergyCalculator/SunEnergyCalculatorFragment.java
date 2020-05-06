package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.MainActivity;
import com.example.energii.koszt.R;
import com.google.android.material.textfield.TextInputLayout;


public class SunEnergyCalculatorFragment extends Fragment {
    public static View root;
    TextInputLayout textInputLayoutLongitude;
    TextInputLayout textInputLayoutLatitude;
    EditText editTextLongitude;
    EditText editTextLatitude;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sun_energy_calculator_layout, container, false);
        Button button = root.findViewById(R.id.button);
        textInputLayoutLongitude = root.findViewById(R.id.text_input_layout_longitude);
        textInputLayoutLatitude = root.findViewById(R.id.text_input_layout_latitude);
        editTextLongitude = root.findViewById(R.id.edit_text_longitude);
        editTextLatitude = root.findViewById(R.id.edit_text_latitude);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogMap(v);
            }
        });
        return root;
    }

    private void openDialogMap(View root) {
        Intent intent = new Intent(root.getContext() , MapActivity.class);
        root.getContext().startActivity(intent);
        Animatoo.animateSlideLeft(root.getContext());
    }

    public void setTextViewText(Double latitude, Double longitude,View root){
        editTextLongitude = root.findViewById(R.id.edit_text_longitude);
        editTextLatitude = root.findViewById(R.id.edit_text_latitude);
        editTextLatitude.setText(String.valueOf(latitude));
        editTextLongitude.setText(String.valueOf(longitude));


    }

}

package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.energii.koszt.R;


public class SunEnergyCalculatorFragment extends Fragment {
    public static View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_sun_energy_calculator_layout, container, false);


        TextView textView = root.findViewById(R.id.dane);
        Button button = root.findViewById(R.id.button);

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

    }

    public void setTextViewText(Double x, Double y,View root){
        TextView textView = root.findViewById(R.id.dane);
        textView.setText((double)x + " " + (double)y);

    }


}

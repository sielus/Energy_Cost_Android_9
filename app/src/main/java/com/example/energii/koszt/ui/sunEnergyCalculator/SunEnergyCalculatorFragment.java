package com.example.energii.koszt.ui.sunEnergyCalculator;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.energii.koszt.MainActivity;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.manager.RoomEditManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class SunEnergyCalculatorFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_sun_energy_calculator_layout, container, false);


        //TextView textView = root.findViewById(R.id.dane);
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


}

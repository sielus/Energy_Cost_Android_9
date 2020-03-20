package com.example.energii.koszt.ui.Roomlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.energii.koszt.R;

public class RoomlistFragment extends Fragment {

    private RoomlistViewModel roomlistViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        roomlistViewModel =
                ViewModelProviders.of(this).get(RoomlistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rooms, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        roomlistViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}

package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.energii.koszt.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomManagerFragment extends Fragment {

    public RoomManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_room_manager, container, false);
        return v;
    }
}

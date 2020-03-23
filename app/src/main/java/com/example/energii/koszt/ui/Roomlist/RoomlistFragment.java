package com.example.energii.koszt.ui.Roomlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.ManagerRoom.RoomManagerFragment;

public class RoomlistFragment extends Fragment {
    private RoomlistViewModel roomlistViewModel;

    RoomManagerFragment roomManagerFragment = new RoomManagerFragment();

    private ListView listView;
    private String[] mTitle = {"test1","test2","test1","test2","test1","test2","test1","test2","test1","test2","test1","test2"};
    private String[] mDescription = {"des1","des2","des1","des2","des1","des2","des1","des2","des1","des2","des1","des2"};
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        roomlistViewModel = ViewModelProviders.of(this).get(RoomlistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rooms, container, false);

        listView = root.findViewById(R.id.listView);

        RoomListAdapter adapter = new RoomListAdapter(root.getContext(),mTitle,mDescription);
        listView.setAdapter(adapter);


        return root;
    }

    public void StartNewFragment()
    {


    }

}




package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.example.energii.koszt.R;

public class RoomEditManager extends AppCompatActivity {
   public static String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        setTitle(title);

    }


}

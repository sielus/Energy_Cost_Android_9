package com.devdreams.energii.koszt.ui.rooms.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.devdreams.energii.koszt.R;
import com.devdreams.energii.koszt.ui.SQLLiteDBHelper;
import com.devdreams.energii.koszt.ui.TutorialShowcase;
import com.devdreams.energii.koszt.ui.rooms.Dialogs;
import com.devdreams.energii.koszt.ui.rooms.GenerateCharts;
import com.devdreams.energii.koszt.ui.rooms.RoomListFragment;
import com.devdreams.energii.koszt.ui.settings.DefaultDeviceManager;
import com.devdreams.energii.koszt.ui.settings.SettingActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomEditManager extends AppCompatActivity implements RoomEditManagerListAdapter.onNoteListener{
    @SuppressLint("StaticFieldLeak")
    public static View view;
    public static String room_name;
    public DeviceManager deviceManager;
    public DefaultDeviceManager defaultDeviceManager;
    private RoomEditManagerListAdapter adapter;
    private ArrayList<String> defaultListDeviceName = new ArrayList<>();
    private ArrayList<String> defaultListDevicePower= new ArrayList<>();
    private ArrayList<String> defaultListDeviceTimeWork = new ArrayList<>();
    private ArrayList<String> defaultListDeviceNumber = new ArrayList<>();
    private String defaultCurrency;
    private Dialogs dialogs;
    private RecyclerView recyclerView;
    static public int numberAfterDot;
    private AdView mAdView;
    public static Activity activity;

    public RoomEditManager() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        defaultDeviceManager = new DefaultDeviceManager(view.getContext());
        deviceManager = new DeviceManager(view.getContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        if(!room_name.isEmpty()){
            setTitle(view.getContext().getResources().getString(R.string.just_room) + " " + room_name.replace("_"," "));
        }
        SettingActivity settingActivity = new SettingActivity();

        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getDefaultCurrency(view);
        defaultDeviceManager= new DefaultDeviceManager(view.getContext());

        GenerateCharts generateCharts = new GenerateCharts();

        clearDefaultDeviceList();
        getDefaultDeviceList(defaultDeviceManager.getDefaultDeviceList());

        dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);

        TableLayout tableLayout = view.findViewById(R.id.sunnyTable);
        tableLayout.setVisibility(View.GONE);

        GenerateTableEditRoom generateTableEditRoom = new  GenerateTableEditRoom();
        generateCharts.generateChartsInRoom(view,room_name,numberAfterDot,defaultCurrency);
        generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);

        activity  = this;
        ActionBar actionBar = getSupportActionBar();

        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        recyclerView = view.findViewById(R.id.RecyckerView);

        dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));
        adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(dialogs.deviceName.toArray(), dialogs.deviceName.size(), String[].class),this,Arrays.copyOf(dialogs.devicePower.toArray(), dialogs.devicePower.size(), String[].class),Arrays.copyOf(dialogs.deviceNumber.toArray(),dialogs.deviceNumber.size(), String[].class),Arrays.copyOf(dialogs.deviceTimeWork.toArray(), dialogs.deviceTimeWork.size(), String[].class));

        generateDevicesListDetails(view,room_name);

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        if(!RoomListFragment.checkFirstRun(view,sqlLiteDBHelper)){
            runAdsInRoomList(sqlLiteDBHelper,view);
        }else{
                fixLayoutAds();
        }

        final FloatingActionButton floatingActionButtonAddDevice = findViewById(R.id.addButonfl);
        if(deviceManager.getRoomDeviceList(room_name).getCount()==0){
            checkFirstRun(view,floatingActionButtonAddDevice);
        }

        floatingActionButtonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.showDialogAddDevice(view,room_name,numberAfterDot,defaultCurrency);
            }
        });
    }

    public void runAdsInRoomList(SQLLiteDBHelper sqlLiteDBHelper, View root) {
        if (sqlLiteDBHelper.getEnableAds()) {
            mAdView = root.findViewById(R.id.adViewEditManager);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView = root.findViewById(R.id.adViewHome);
            fixLayoutAds();
        }

    }

    private void fixLayoutAds() {
        ConstraintLayout constraintLayout = view.findViewById(R.id.deviceListConstraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(recyclerView.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP,0);
        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu,menu);
        return true;
    }

    public void checkFirstRun(View root,FloatingActionButton floatingActionButton) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        Cursor cursor = sqlLiteDBHelper.getVariable("runTutFir");
        if(cursor.getCount()!=0) {
            if(cursor.getString(0).equals("false")){
                sqlLiteDBHelper.setVariable("runTutFir", "true");
                startTutorial(floatingActionButton);
            }
        }
    }

    private void startTutorial(FloatingActionButton floatingActionButtonAddDevice) {
        TutorialShowcase tutorialShowcase = new TutorialShowcase(this);
        tutorialShowcase.tutorialWithNoListener(floatingActionButtonAddDevice,
                getResources().getString(R.string.tutorial_welcome_in_room_start) + " '" + room_name+"'",
                getResources().getString(R.string.tutorial_welcome_in_room_end));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.room_settings:
                dialogs.showDialogEditRoomName(view,room_name,defaultCurrency,numberAfterDot,this);
                break;
            case 16908332:
                this.onBackPressed();
                Animatoo.animateSlideRight(this);
        }
        return true;
    }



    void generateDevicesListDetails(View view, String room_name){
        RoomEditManagerDeviceDetailsListAdapter roomEditManagerDeviceDetailsListAdapter = new RoomEditManagerDeviceDetailsListAdapter(view,room_name);
        RecyclerView recycleViewDeviceDetailsList = view.findViewById(R.id.recycleViewDeviceDetailsList);
        roomEditManagerDeviceDetailsListAdapter.generateDeviceTable();
        recycleViewDeviceDetailsList.setAdapter(roomEditManagerDeviceDetailsListAdapter);
        recycleViewDeviceDetailsList.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
    public void onBackPressed() {

//        if(RoomListFragment.adRequest == null){
//            RoomListFragment.adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(RoomListFragment.adRequest);
//        }
        RoomListFragment.runAdsInRoomList();
        fullRefreshRoomList();
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }


    public void refreshListView(View root) {
        generateDevicesListDetails(view,room_name);

        DeviceManager deviceManager = new DeviceManager(view.getContext());
        Dialogs dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);

        dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));

        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(dialogs.deviceName.toArray(), dialogs.deviceName.size(), String[].class),this,Arrays.copyOf(dialogs.devicePower.toArray(), dialogs.devicePower.size(), String[].class),Arrays.copyOf(dialogs.deviceNumber.toArray(), dialogs.deviceNumber.size(), String[].class),Arrays.copyOf(dialogs.deviceTimeWork.toArray(), dialogs.deviceTimeWork.size(), String[].class));

        recyclerView = view.findViewById(R.id.RecyckerView);
        recyclerView.setAdapter(adapter);
    }

    public void onNoteClick(int position) {
        Dialogs dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);
        DeviceManager deviceManager = new DeviceManager(view.getContext());
        SettingActivity settingActivity = new SettingActivity();

        dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));
        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getDefaultCurrency(view);
        dialogs.showUpdateDialog(RoomEditManager.view,room_name,dialogs.deviceName.get(position),room_name,numberAfterDot,defaultCurrency);
    }

    public ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deviceManager = new DeviceManager(view.getContext());
            deviceManager.deleteDevice(RoomEditManager.room_name,dialogs.deviceName.get(viewHolder.getAdapterPosition()));
            int position = viewHolder.getAdapterPosition();

            dialogs.deviceName.remove(position);

            GenerateTableEditRoom generateTableEditRoom = new  GenerateTableEditRoom();

            GenerateCharts generateCharts = new GenerateCharts();
            generateCharts.generateChartsInRoom(view,room_name,numberAfterDot,defaultCurrency);

            Dialogs.clearRoomList();
            dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));

            refreshListView(view);
            adapter.notifyItemChanged(position);

            generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);
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

    private void getDefaultDeviceList(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearDefaultDeviceList();
            defaultListDeviceName.add(0,view.getContext().getResources().getString(R.string.just_templates));
            defaultListDeviceNumber.add(0,"");
            defaultListDeviceTimeWork.add(0,"0:0");
            defaultListDevicePower.add(0,"");

            while(cursor.moveToNext()) {
                defaultListDeviceName.add(cursor.getString(0));
                defaultListDevicePower.add(cursor.getString(1));
                defaultListDeviceNumber.add(cursor.getString(3));
                defaultListDeviceTimeWork.add(cursor.getString(2));
            }
        }
    }

    private void clearDefaultDeviceList() {
        defaultListDeviceName.clear();
    }

    private void fullRefreshRoomList(){
        RoomListFragment roomListFragment = new RoomListFragment();
        roomListFragment.refreshListView(RoomListFragment.root);
        roomListFragment.generateChart(RoomListFragment.root);
    }
}
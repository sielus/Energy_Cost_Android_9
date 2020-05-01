package com.example.energii.koszt.ui.rooms.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.Dialogs;
import com.example.energii.koszt.ui.rooms.GenerateCharts;
import com.example.energii.koszt.ui.settings.DefaultDeviceManager;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
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
    private int numberAfterDot;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        defaultDeviceManager = new DefaultDeviceManager(view.getContext());
        deviceManager = new DeviceManager(view.getContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        setTitle(view.getContext().getResources().getString(R.string.just_room) + " " + room_name.replace("_"," "));
        SettingActivity settingActivity = new SettingActivity();

        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getDefaultCurrency(view);
        defaultDeviceManager= new DefaultDeviceManager(view.getContext());

        GenerateCharts generateCharts = new GenerateCharts();

        clearDefaultDeviceList();
        getDefaultDeviceList(defaultDeviceManager.getDefaultDeviceList());

        dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.setVisibility(View.GONE);

        GenerateTableEditRoom generateTableEditRoom = new  GenerateTableEditRoom();
        generateCharts.generateChartsInRoom(view,room_name,numberAfterDot,defaultCurrency);
        generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ActionBar actionBar = getSupportActionBar();

        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.RecyckerView);

        dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));
        adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(dialogs.deviceName.toArray(), dialogs.deviceName.size(), String[].class),this,Arrays.copyOf(dialogs.devicePower.toArray(), dialogs.devicePower.size(), String[].class),Arrays.copyOf(dialogs.deviceNumber.toArray(),dialogs.deviceNumber.size(), String[].class),Arrays.copyOf(dialogs.deviceTimeWork.toArray(), dialogs.deviceTimeWork.size(), String[].class));

        new ItemTouchHelper(itemTouchHelperCallbackDelete).attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FloatingActionButton floatingActionButtonAddDevice = findViewById(R.id.addButonfl);
        floatingActionButtonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.showDialogAddDevice(view,room_name,numberAfterDot,defaultCurrency);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu,menu);
        return true;
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

    public void onBackPressed() {
        fullRefreshRoomList();
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

    public void refreshListView(View root) {
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

            GenerateCharts generateCharts = new GenerateCharts();
            generateCharts.generateChartsInRoom(view,room_name,numberAfterDot,defaultCurrency);

            Dialogs.clearRoomList();
            dialogs.ViewDataDeviceFromDB(deviceManager.getRoomDeviceList(room_name));

            refreshListView(view);
            adapter.notifyItemChanged(position);

            GenerateTableEditRoom generateTableEditRoom = new GenerateTableEditRoom();
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
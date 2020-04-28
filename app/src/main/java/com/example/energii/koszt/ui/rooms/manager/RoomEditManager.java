package com.example.energii.koszt.ui.rooms.manager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.example.energii.koszt.ui.rooms.RoomListFragment;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Arrays;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomEditManager extends AppCompatActivity implements RoomEditManagerListAdapter.onNoteListener{
    static public View view;
    public SQLLiteDBHelper sqlLiteDBHelper;
    public static String room_name;
    int numberAfterDot;
    RoomEditManagerListAdapter adapter;
    ArrayList<String> defaultListDeviceName = new ArrayList<>();
    ArrayList<String> defaultListDevicePower= new ArrayList<>();
    ArrayList<String> defaultListDeviceTimeWork = new ArrayList<>();
    ArrayList<String> defaultListDeviceNumber = new ArrayList<>();
    private RecyclerView recyclerView;
    String defaultCurrency;
    Dialogs dialogs;
    private AdView mAdView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
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
    void fullRefreshRoomList(){
        RoomListFragment roomListFragment = new RoomListFragment();
        roomListFragment.clearRoomList();
        roomListFragment.ViewDataFromDB(sqlLiteDBHelper.getRoomList());
        roomListFragment.refreshListView(RoomListFragment.root);
        roomListFragment.generateChart(RoomListFragment.root);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        setTitle(view.getContext().getResources().getString(R.string.just_room) + " " + room_name.replace("_"," "));
        SettingActivity settingActivity = new SettingActivity();

        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getdefaultCurrency(view);
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        GenerateCharts generateCharts = new GenerateCharts();

        clearDefaultDeviceList();
        getDefaultDeviceList(sqlLiteDBHelper.getDefaultDeviceList());

        dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.setVisibility(View.GONE);

        GenerateTableEditRoom generateTableEditRoom = new  GenerateTableEditRoom();
        generateCharts.generateChartinRoom(view,room_name,numberAfterDot,defaultCurrency);
        generateTableEditRoom.refreshTable(view,defaultCurrency,room_name,numberAfterDot);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.RecyckerView);

        dialogs.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
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
    void getDefaultDeviceList(Cursor cursor) {
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
    public void refreshListView(View root) {
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        Dialogs dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);

        dialogs.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(dialogs.deviceName.toArray(), dialogs.deviceName.size(), String[].class),this,Arrays.copyOf(dialogs.devicePower.toArray(), dialogs.devicePower.size(), String[].class),Arrays.copyOf(dialogs.deviceNumber.toArray(), dialogs.deviceNumber.size(), String[].class),Arrays.copyOf(dialogs.deviceTimeWork.toArray(), dialogs.deviceTimeWork.size(), String[].class));

        recyclerView = view.findViewById(R.id.RecyckerView);
        recyclerView.setAdapter(adapter);
    }
    private void clearDefaultDeviceList() {
        defaultListDeviceName.clear();
    }
    public void onNoteClick(int position) {
        Dialogs dialogs = new Dialogs(defaultListDeviceName,defaultListDevicePower,defaultListDeviceTimeWork,defaultListDeviceNumber);
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        SettingActivity settingActivity = new SettingActivity();

        dialogs.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
        numberAfterDot = settingActivity.getNumberAfterDot(view);
        defaultCurrency = settingActivity.getdefaultCurrency(view);
        dialogs.showUpdateDialog(RoomEditManager.view,room_name,dialogs.deviceName.get(position),room_name,numberAfterDot,defaultCurrency);
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallbackDelete = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
            sqlLiteDBHelper.deleteDevice(RoomEditManager.room_name,dialogs.deviceName.get(viewHolder.getAdapterPosition()));
            int position = viewHolder.getAdapterPosition();
            dialogs.deviceName.remove(position);

            GenerateCharts generateCharts = new GenerateCharts();
            generateCharts.generateChartinRoom(view,room_name,numberAfterDot,defaultCurrency);

            dialogs.clearRoomList();
            dialogs.ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

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

}


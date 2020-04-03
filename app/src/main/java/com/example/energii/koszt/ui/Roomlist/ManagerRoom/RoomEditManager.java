package com.example.energii.koszt.ui.Roomlist.ManagerRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.Roomlist.RoomListAdapter;
import com.example.energii.koszt.ui.Roomlist.RoomListFragment;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomEditManager extends AppCompatActivity implements RoomEditManagerListAdapter.onNoteListener{
    private RecyclerView recyclerView;

    public View view;
    RoomEditManagerListAdapter adapter;

    public SQLLiteDBHelper sqlLiteDBHelper;
    public static String room_name;
    public final List<String> device = new LinkedList<>();
    private List<String> deviceId = new LinkedList<>();
    private List<String> deviceName = new ArrayList<>();
    TextView outputEnergyCostUser;
    TextView outputEnergyCostDay;
    TextView outputEnergyCostMonth;
    TextView outputEnergyCostUserKwh;
    TextView outputEnergyCostDayKwh;
    TextView outputEnergyCostMonthKwh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        view = this.findViewById(android.R.id.content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_manager);
        // getSupportActionBar().hide();
        setTitle("Pokój " + room_name);
        TextView outputEnergyCostUser = view.findViewById(R.id.OutputEnergyCostUser);
        TextView outputEnergyCostDay = view.findViewById(R.id.OutputEnergyCostDay);
        TextView outputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView outputEnergyCostUserKwh = view.findViewById(R.id.OutputEnergyCostUserKwh);
        TextView outputEnergyCostDayKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView outputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostMonthKwh);

        recyclerView = findViewById(R.id.RecyckerView);
        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

        adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));



        FloatingActionButton floatingActionButtonAddDevice = findViewById(R.id.addButonfl);
        floatingActionButtonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddDevice(view);

            }
        });

        adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),this);
    }




    void ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            while(cursor.moveToNext()) {
                deviceId.add(cursor.getString(2));
                deviceName.add(cursor.getString(1));
            }
        }
    }

    void clearRoomList() {
        deviceId.clear();
        deviceName.clear();
    }

    void refreshListView(View root) {
        RoomEditManagerListAdapter adapter = new RoomEditManagerListAdapter(view.getContext(),Arrays.copyOf(deviceName.toArray(), deviceName.size(), String[].class),this);

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));

        recyclerView.setAdapter(adapter);
    }

    public void showDialogAddDevice(final View view){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manage_device_dialog_layout);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);


        ImageButton buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);

        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
        final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                int h = Integer.parseInt(editTextDeviceWorkH.getText().toString());
                String deviceNameInput = editTextDeviceName.getText().toString();
                int m = Integer.parseInt(editTextDeviceWorkM.getText().toString());
                int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                try {
                    sqlLiteDBHelper.addDevice(room_name,deviceNameInput,powerValue,h,m,number);

                    Toast.makeText(view.getContext(),"Urządzenie dodane",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    RoomListFragment roomListFragment = new RoomListFragment();
                    roomListFragment.generateChart(RoomListFragment.root);

                    ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                    refreshListView(view);

                } catch (SQLEnergyCostException.EmptyField | SQLEnergyCostException.DuplicationDevice errorMessage) {
                    Toast.makeText(view.getContext(), errorMessage.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showUpdateDialog(final View view, final String roomName, String deviceName){
        final Dialog dialog = new Dialog(view.getContext());

        ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manage_device_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();

        sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());

        viewDeviceInfoFromDB(sqlLiteDBHelper.getDeviceInfo(roomName,deviceName));

        ImageButton buttonDialogAccept = dialog.findViewById(R.id.buttonDialogAccept);

        final EditText editTextDeviceName = dialog.findViewById(R.id.editTextDeviceName);
        final EditText editTextDevicePower = dialog.findViewById(R.id.editTextDevicePower);
        final EditText editTextDeviceNumbers = dialog.findViewById(R.id.editTextDeviceNumbers);
        final EditText editTextDeviceWorkH = dialog.findViewById(R.id.editTextDeviceWorkH);
        final EditText editTextDeviceWorkM = dialog.findViewById(R.id.editTextDeviceWorkM);

        editTextDeviceName.setText(device.get(1));
        editTextDevicePower.setText(device.get(2));
        editTextDeviceNumbers.setText(device.get(4));
        editTextDeviceWorkH.setText(device.get(3).split(":")[0]);
        editTextDeviceWorkM.setText(device.get(3).split(":")[1]);

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = editTextDeviceName.getText().toString();
                double powerValue = Double.parseDouble(editTextDevicePower.getText().toString());
                int h = Integer.parseInt(editTextDeviceWorkH.getText().toString());
                int m = Integer.parseInt(editTextDeviceWorkM.getText().toString());
                int number = Integer.parseInt(editTextDeviceNumbers.getText().toString());

                try {
                    sqlLiteDBHelper.updateDevice(Integer.parseInt(device.get(0)),roomName,deviceName,powerValue,number,h,m);
                } catch (SQLEnergyCostException.EmptyField emptyField) {
                    emptyField.printStackTrace();
                }
                RoomListFragment roomListFragment = new RoomListFragment();
                roomListFragment.generateChart(RoomListFragment.root);

                Toast.makeText(view.getContext(),"Urządzenie zaktualizowane",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                System.out.println(RoomListFragment.root);



                ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
                refreshListView(view);
            }
        });
    }

    void viewDeviceInfoFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            while(cursor.moveToNext()) {
                device.add((cursor.getString(0)));
                device.add((cursor.getString(1)));
                device.add((cursor.getString(2)));
                device.add((cursor.getString(3)));
                device.add((cursor.getString(4)));
            }
        }
    }

    @Override
    public void onNoteClick(int position) {

        System.out.println(position);
        showUpdateDialog(view,room_name,deviceName.get(position));


    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
            sqlLiteDBHelper.deleteDevice(RoomEditManager.room_name,deviceName.get(viewHolder.getAdapterPosition()));

            int position = viewHolder.getAdapterPosition();
            deviceName.remove(position);


            clearRoomList();
            ViewDataFromDB(sqlLiteDBHelper.getRoomDeviceList(room_name));
            refreshListView(view);

            adapter.notifyItemChanged(position);




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
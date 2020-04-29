package com.example.energii.koszt.ui.rooms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.manager.GenerateTableEditRoom;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.example.energii.koszt.ui.rooms.manager.RoomEditManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Arrays;
import com.example.energii.koszt.ui.SQLLiteDBHelper;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomListFragment extends Fragment implements RoomListAdapter.onNoteListener {
    private RecyclerView recyclerView;
    private SQLLiteDBHelper sqlLiteDBHelper;
    private RoomListAdapter adapter;
    private Dialogs dialogs;
    private TextView title_summary;
    private PieChart pieChart;
    private BarChart barChart;
    public static View root;
    private int position;
    private int numberAfterDot;
    public static String defaultCurrency;

    private AdView mAdView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rooms, container, false);
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);
        SettingActivity settingActivity = new SettingActivity();

        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        title_summary = root.findViewById(R.id.title_summary);
        recyclerView = root.findViewById(R.id.RecyckerView);

        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        title_summary.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);

        numberAfterDot = settingActivity.getNumberAfterDot(root);
        defaultCurrency = settingActivity.getdefaultCurrency(root);

        hideKeyboard(getActivity());
        dialogs = new Dialogs(null,null,null,null);
        dialogs.ViewRoomListFromDB(sqlLiteDBHelper.getRoomList());

        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf(dialogs.roomNameArray.toArray(), dialogs.roomNameArray.size(), String[].class),this,Arrays.copyOf(dialogs.roomNameKwhArray.toArray(), dialogs.roomNameKwhArray.size(), String[].class));

        FloatingActionButton floatingActionButtonAddRoomDialog = root.findViewById(R.id.buttonAddRoom);

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        pieChart.setVisibility(View.VISIBLE);
        barChart.setVisibility(View.VISIBLE);

        generateChart(root);
        refreshTable(root);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        floatingActionButtonAddRoomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.showRoomListDialog(root,adapter,getActivity());
            }
        });
        return root;
    }

    @SuppressLint("SetTextI18n")
    public void refreshTable(View view){
        GenerateTableEditRoom generateTableEditRoom = new GenerateTableEditRoom();
        generateTableEditRoom.generateTableRoomList(view,defaultCurrency,numberAfterDot);
    }

    public void refreshListView(View root) {
        Dialogs dialogs = new Dialogs(null,null,null,null);
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        dialogs.ViewRoomListFromDB(sqlLiteDBHelper.getRoomList());
        RoomListAdapter adapter;
        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf( dialogs.roomNameArray.toArray(),  dialogs.roomNameArray.size(), String[].class),this,Arrays.copyOf(dialogs.roomNameKwhArray.toArray(),dialogs.roomNameKwhArray.size(), String[].class));
        RecyclerView recyclerView = root.findViewById(R.id.RecyckerView);
        refreshTable(root);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(int position) {
        RoomEditManager.room_name =  Dialogs.roomNameArray.get(position).replace(" ","_");
        Intent intent = new Intent(root.getContext() , RoomEditManager.class);
        root.getContext().startActivity(intent);
        Animatoo.animateSlideLeft(root.getContext());
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pieChart.invalidate();
            sqlLiteDBHelper.deleteRoom(dialogs.roomNameArray.get(viewHolder.getAdapterPosition()).replace(" ","_"));
            position = viewHolder.getAdapterPosition();
            dialogs.roomNameArray.remove(position);
            dialogs.clearRoomList();
            dialogs.ViewRoomListFromDB(sqlLiteDBHelper.getRoomList());
            refreshListView(root);
            generateChart(root);
            refreshTable(root);
          //  adapter.notifyItemChanged(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(root.getContext(), R.color.red))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
        }

    };

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        activity.getWindow().getDecorView().clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void generateChart(View root) {
        GenerateCharts generateCharts = new GenerateCharts();
        generateCharts.generateChart(root);
    }


}



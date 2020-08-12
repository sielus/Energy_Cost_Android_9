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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.rooms.manager.GenerateTableEditRoom;
import com.example.energii.koszt.ui.rooms.manager.RoomEditManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Arrays;
import java.util.Objects;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomListFragment extends Fragment implements RoomListAdapter.onNoteListener {
    @SuppressLint("StaticFieldLeak")
    public static View root;
    private RoomListAdapter adapter;
    private Dialogs dialogs;
    private PieChart pieChart;
    private AdView mAdView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rooms, container, false);
        RoomManager roomManager = new RoomManager(root.getContext());
        TableLayout tableLayout = root.findViewById(R.id.sunnyTable);

        pieChart =  root.findViewById(R.id.pieChart);
        BarChart barChart = root.findViewById(R.id.bartChart);
        TextView titleSummary = root.findViewById(R.id.title_summary);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.startBart,null));
        requireActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.startBart));

        RecyclerView recyclerView = root.findViewById(R.id.RecyckerView);

        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        titleSummary.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);

        hideKeyboard(requireActivity());
        dialogs = new Dialogs(null,null,null,null);
        dialogs.ViewRoomListFromDB(roomManager.getRoomList());

        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf(Dialogs.roomNameArray.toArray(), Dialogs.roomNameArray.size(), String[].class),this,Arrays.copyOf(Dialogs.roomNameKwhArray.toArray(), Dialogs.roomNameKwhArray.size(), String[].class));

        FloatingActionButton floatingActionButtonAddRoomDialog = root.findViewById(R.id.buttonAddRoom);

        mAdView = root.findViewById(R.id.adViewRooms);
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
        generateTableEditRoom.generateTableRoomList(view);
    }

    public void refreshListView(View root) {
        Dialogs dialogs = new Dialogs(null,null,null,null);
        RoomManager roomManager = new RoomManager(root.getContext());
        dialogs.ViewRoomListFromDB(roomManager.getRoomList());
        RoomListAdapter adapter;
        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf(Dialogs.roomNameArray.toArray(),  Dialogs.roomNameArray.size(), String[].class),this,Arrays.copyOf(Dialogs.roomNameKwhArray.toArray(), Dialogs.roomNameKwhArray.size(), String[].class));
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        activity.getWindow().getDecorView().clearFocus();
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void generateChart(View root) {
        GenerateCharts generateCharts = new GenerateCharts();
        generateCharts.generateChart(root);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            pieChart.invalidate();
            RoomManager roomManager = new RoomManager(root.getContext());
            roomManager.deleteRoom(Dialogs.roomNameArray.get(viewHolder.getAdapterPosition()).replace(" ","_"));
            int position = viewHolder.getAdapterPosition();
            Dialogs.roomNameArray.remove(position);
            Dialogs.clearRoomList();
            dialogs.ViewRoomListFromDB(roomManager.getRoomList());
            refreshListView(root);
            generateChart(root);
            refreshTable(root);
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
}
package com.example.energii.koszt.ui.rooms;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.energii.koszt.R;
import com.example.energii.koszt.ui.settings.SettingActivity;
import com.example.energii.koszt.ui.rooms.manager.RoomEditManager;
import com.example.energii.koszt.ui.exception.SQLEnergyCostException;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Arrays;
import com.example.energii.koszt.ui.SQLLiteDBHelper;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RoomListFragment extends Fragment implements RoomListAdapter.onNoteListener {
    private List<String> roomName = new ArrayList<>();
    private List<String> roomNameKwh = new ArrayList<>();
    private List<String> allRoomsCostKWH = new ArrayList<>();
    private List<String> allRoomsCost = new ArrayList<>();

    boolean ifNumberOnStart = false;

    public static View root;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private SQLLiteDBHelper sqlLiteDBHelper;
    RoomListAdapter adapter;
    private int position;
    PieChart pieChart;
    BarChart barChart;
    int numberAfterDot;
    ArrayList<PieEntry> pieEntry = new ArrayList<PieEntry>();
    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
    TextView title_summary;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rooms, container, false);
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        ViewDataFromDB(sqlLiteDBHelper.getRoomList());
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);
        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        title_summary = root.findViewById(R.id.title_summary);
        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        title_summary.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);
        recyclerView = root.findViewById(R.id.RecyckerView);
        SettingActivity settingActivity = new SettingActivity();
        numberAfterDot = settingActivity.getNumberAfterDot(root);

        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class),this,Arrays.copyOf(roomNameKwh.toArray(), roomNameKwh.size(), String[].class));

        FloatingActionButton floatingActionButtonAddRoomDialog = root.findViewById(R.id.buttonAddRoom);

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

                showRoomListDialog(root);
            }
        });

        return root;
    }

    @SuppressLint("SetTextI18n")
    public void refreshTable(View view){
        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(view.getContext());
        SettingActivity settingActivity = new SettingActivity();
        numberAfterDot = settingActivity.getNumberAfterDot(root);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TextView title_summary = view.findViewById(R.id.title_summary);
        TextView OutputEnergyCostDay = view.findViewById(R.id.OutputEnergyCostDay);
        TextView OutputEnergyCostMonth = view.findViewById(R.id.OutputEnergyCostMonth);
        TextView OutputEnergyCostYear = view.findViewById(R.id.OutputEnergyCostYear);

        TextView OutputEnergyCostDayKwh = view.findViewById(R.id.OutputEnergyCostDayKwh);
        TextView OutputEnergyCostMonthKwh = view.findViewById(R.id.OutputEnergyCostMonthKwh);
        TextView OutputEnergyCostYearKwh= view.findViewById(R.id.OutputEnergyCostYearKwh);


        if(sqlLiteDBHelper.getRoomList().getCount()!=0){
            tableLayout.setVisibility(View.VISIBLE);
            title_summary.setVisibility(View.VISIBLE);
            getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost());


            OutputEnergyCostDayKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000) + " kWh");
            OutputEnergyCostDay.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost())) + " zł");

            OutputEnergyCostMonthKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000 * 30 ) + " kWh");
            OutputEnergyCostMonth.setText(String.format("%."+ numberAfterDot +"f",getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost()) * 30 ) + " zł");

            OutputEnergyCostYearKwh.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsKwHFromDB(sqlLiteDBHelper.getHouseCost()) / 1000 * 365 ) + " kWh");
            OutputEnergyCostYear.setText(String.format("%."+ numberAfterDot +"f", getAllRoomsCostFromDB(sqlLiteDBHelper.getHouseCost())  * 365 ) + " zł");


        }


    }


    private float getAllRoomsKwHFromDB(Cursor cursor){

        cursor.moveToFirst();
        return cursor.getFloat(0);

    }

    private float getAllRoomsCostFromDB(Cursor cursor){

        cursor.moveToFirst();
        return cursor.getFloat(1);

    }

    private void showRoomListDialog(final View view) {
        dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.room_list_dialog);
        dialog.show();
        ifNumberOnStart = false;
        Button buttonDialogAccept = dialog.findViewById(R.id.ButtonAddRoom);
        final TextInputEditText text_field_inputRoomName = dialog.findViewById(R.id.text_field_inputRoomName);
        final TextInputLayout text_field_inputRoomNameLayout = dialog.findViewById(R.id.text_field_inputRoomNameLayout);
        text_field_inputRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String roomName = text_field_inputRoomName.getText().toString();

                if(!roomName.isEmpty()){
                    char First = roomName.charAt(0);

                    if(Character.isDigit(First)){
                        text_field_inputRoomNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
                        ifNumberOnStart = true;
                    }else {
                        text_field_inputRoomNameLayout.setError(null);
                        ifNumberOnStart = false;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = text_field_inputRoomName.getText().toString();
                if (roomName.isEmpty()) {
                    text_field_inputRoomNameLayout.setError("Brak danych!");
                }
                else if(ifNumberOnStart){
                    text_field_inputRoomNameLayout.setError("Nazwa nie może zaczynać się od cyfry");
                }
                else{
                    text_field_inputRoomNameLayout.setError(null);
                    try {
                        sqlLiteDBHelper.addRoom(roomName);
                        clearRoomList();
                        ViewDataFromDB(sqlLiteDBHelper.getRoomList());
                        refreshListView(view);
                        refreshTable(view);
                        generateChart(root);
                        adapter.notifyDataSetChanged();

                        barChart.setVisibility(View.VISIBLE);
                        pieChart.setVisibility(View.VISIBLE);


                        dialog.dismiss();
                        Toast.makeText(getContext(),"Pokój dodany",Toast.LENGTH_SHORT).show();
                    }catch (SQLEnergyCostException.DuplicationRoom | SQLEnergyCostException.EmptyField errorMessage) {
                        text_field_inputRoomNameLayout.setError(errorMessage.getMessage());
                    }
                }
            }
        });
    }



    public void ViewDataFromDB(Cursor cursor) {
        if (cursor.getCount() != 0) {
            clearRoomList();
            while(cursor.moveToNext()) {
                roomName.add(cursor.getString(1).replace("_"," "));
                roomNameKwh.add(String.valueOf(cursor.getInt(2)));
            }
        }
    }

    public void clearRoomList() {
        roomName.clear();
        roomNameKwh.clear();
    }

    public void refreshListView(View root) {

        SQLLiteDBHelper sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        ViewDataFromDB(sqlLiteDBHelper.getRoomList());
        RoomListAdapter adapter;
        adapter = new RoomListAdapter(root.getContext(),Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class),this,Arrays.copyOf(roomNameKwh.toArray(), roomNameKwh.size(), String[].class));
        RecyclerView recyclerView = root.findViewById(R.id.RecyckerView);
        refreshTable(root);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(int position) {

        RoomEditManager.room_name = roomName.get(position).replace(" ","_");

        Intent intent = new Intent(root.getContext() , RoomEditManager.class);
        root.getContext().startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            pieChart.invalidate();
            sqlLiteDBHelper.deleteRoom(roomName.get(viewHolder.getAdapterPosition()).replace(" ","_"));
            position = viewHolder.getAdapterPosition();
            roomName.remove(position);
            pieEntry.clear();
            clearRoomList();
            ViewDataFromDB(sqlLiteDBHelper.getRoomList());
            refreshListView(root);
            generateChart(root);
            refreshTable(root);
            adapter.notifyItemChanged(position);
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

    public void generateChart(View root){
        SQLLiteDBHelper sqlLiteDBHelper;
        sqlLiteDBHelper = new SQLLiteDBHelper(root.getContext());
        PieChart pieChart;
        BarChart barChart;

        pieChart =  root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.bartChart);
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);
        TextView textView = root.findViewById(R.id.title_summary);

        if(sqlLiteDBHelper.getRoomList().getCount()==0){
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            title_summary.setVisibility(View.GONE);

        }

        pieChart.setNoDataText("Brak pokojów");
        pieChart.invalidate();
        barChart.invalidate();
        String[] xAxisLables = new String[]{};
        List<String> roomName = new ArrayList<>();
        roomName.clear();
        xAxisLables = null;
        int labelNumberIndex = 0;
        barEntries.clear();
        pieEntry.clear();
        Cursor cursor = sqlLiteDBHelper.getRoomDetails();
        if (cursor.getCount() > 1) {
            while(cursor.moveToNext()) {

                pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) +" kWh"));
                barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f",cursor.getFloat(2)).replace(",","."))));
                roomName.add(cursor.getString(0).replace("_"," ") + " ");
                labelNumberIndex = labelNumberIndex + 1;

            }
        }else if(cursor.getCount() == 1){

            cursor.moveToFirst();
            pieEntry.add(new PieEntry(cursor.getInt(1), cursor.getString(0).replace("_"," ") + " " + String.format("%."+ numberAfterDot +"f",((float)cursor.getInt(1) / 1000)) +" kWh" ));
            barEntries.add(new BarEntry(labelNumberIndex, Float.parseFloat(String.format("%."+ numberAfterDot +"f",cursor.getFloat(2)).replace(",","."))));
            roomName.add(cursor.getString(0).replace("_"," ") + " ");

        }else {
            return;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Koszty dobowe pokoi");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);
        XAxis axis = barChart.getXAxis();

        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextSize(14);
        barChart.getAxisLeft().setTextSize(14);

        barChart.getLegend().setTextSize(14f);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setTouchEnabled(false);
        barChart.setFitBars(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);

        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setTextSize(16f);
        axis.setDrawGridLines(true);
        axis.setTextColor(Color.WHITE);
        axis.setDrawAxisLine(true);
        axis.setCenterAxisLabels(false);
        axis.setLabelCount(roomName.size());

        xAxisLables = null;
        xAxisLables = Arrays.copyOf(roomName.toArray(), roomName.size(), String[].class);
        axis.setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        axis.setGranularity(1f);
        axis.setGranularityEnabled(true);

        barDataSet.setValueTextSize(14);
        barDataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setText("");
        barChart.getLegend().setEnabled(true);

        barChart.animateY(1000);

        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Dane");
        pieChart.getLegend().setEnabled(false);

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueLineColor(R.color.colorAccent);
        pieDataSet.setValueTextSize(14);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(30);
        pieChart.setTransparentCircleRadius(10);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Zużycie kWh ");
        pieChart.animate();

    }

}



package com.madgroup.appcompany;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class AnalyticsTab1 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private AnyChartView anyChartView;
    private Map<String, String> months;


    public AnalyticsTab1() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        initMapMonths();

    }

    private void initMapMonths() {
        months = new HashMap<>();
        months.put("1", "January");
        months.put("2", "February");
        months.put("3", "March");
        months.put("4", "April");
        months.put("5", "May");
        months.put("6", "June");
        months.put("7", "July");
        months.put("8", "August");
        months.put("9", "September");
        months.put("10", "October");
        months.put("11", "November");
        months.put("12", "December");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics_tab1, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = view.findViewById(R.id.daily_histogram);
        anyChartView.setProgressBar(view.findViewById(R.id.daily_progress_bar));
        initializeDailyHistogram();
    }

    public void initializeDailyHistogram() {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        final String year = "2019";
        final String month = "5";
        String weekOfMonth = "4";
        String node = year+"_"+month+"_"+weekOfMonth;
        final String dayOfMonth = "23";
        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID).child(node);

        final Cartesian cartesian = AnyChart.column();
        timingOrederRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Inizializzo un'hashmap con tutti i valori da mostrare sull'asse delle x (cioè le fasce orarie)
                // Uso TreeMap perchè tiene in ordine le chiavi.
                TreeMap<Integer,Integer> hashMap = new TreeMap<>();
                for (int i=0; i<24; i++) {
                    hashMap.put(i, 0);
                }

                // Leggo il numero di consegne per ogni fascia oraria e aggiorno la mappa
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String day_nameDay_hourSlot = ds.getKey();
                    Integer amountOfOrders = ds.getValue(Integer.class);
                    // Se il giorno corrisponde al giorno selezionato lo vado a mostrare con la relativa fascia oraria
                    if(day_nameDay_hourSlot.startsWith(dayOfMonth)) {
                        String fields[] = day_nameDay_hourSlot.split("_");
                        String hourSlot = fields[2];
                        hashMap.put(Integer.parseInt(hourSlot), amountOfOrders);
                    }
                }

                // Converto la mappa in ArrayList (la libreria accetta questo formato)
                List<DataEntry> data = new ArrayList<>();
                for (TreeMap.Entry<Integer, Integer> entry : hashMap.entrySet()) {
                    Integer hourSlot = entry.getKey();
                    Integer amountOfOrders = entry.getValue();
                    data.add(new ValueDataEntry(hourSlot, amountOfOrders));
                }

                Column column = cartesian.column(data);
                column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_TOP)
                        .anchor(Anchor.CENTER_TOP)
                        .offsetX(0d)
                        .offsetY(5d);
                cartesian.animation(true);
                cartesian.yScale().minimum(0d);
                cartesian.yAxis(0).labels().enabled(false);
                //.format("{%Value}{groupsSeparator: }");
                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.tooltip().title().text("Amount of deliveries");
                cartesian.tooltip().format("{%value}");
                cartesian.interactivity().hoverMode(HoverMode.BY_X);
                cartesian.xAxis(0).title("");
                //cartesian.yAxis(0).title("Amount of orders");
                String wordMonth = months.get(month);
                cartesian.title(dayOfMonth+" "+wordMonth+" "+year);
                anyChartView.setChart(cartesian);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

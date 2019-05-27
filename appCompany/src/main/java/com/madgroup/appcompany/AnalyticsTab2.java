package com.madgroup.appcompany;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChartView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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


public class AnalyticsTab2 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private AnyChartView anyChartView;
    private Map<String, String> mapDayOfWeek;

    public AnalyticsTab2() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //initializeWeeklyHistogram();
        } else {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        initMapDayOfWeek();
    }

    private void initMapDayOfWeek() {
        mapDayOfWeek = new HashMap<>();
        mapDayOfWeek.put("1","Mon");
        mapDayOfWeek.put("2","Tue");
        mapDayOfWeek.put("3","Wed");
        mapDayOfWeek.put("4","Thu");
        mapDayOfWeek.put("5","Fri");
        mapDayOfWeek.put("6","Sat");
        mapDayOfWeek.put("7","Sun");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        return view;
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
        BarChart chart = view.findViewById(R.id.bar_chart);
        initializeWeeklyHistogram(chart, "4");
    }


    public void initializeWeeklyHistogram(final BarChart chart, final String weekOfMonth) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        Calendar calendar = Calendar.getInstance();
        String year = "2019";
        final String month = "5";
        String node = year+"_"+month+"_"+weekOfMonth;


        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID).child(node);

        // Riferimenti all'istogramma


        timingOrederRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Inizializzo un'hashmap con tutti i valori da mostrare sull'asse delle x (cioè i giorni della settimana)
                // Uso TreeMap perchè tiene in ordine le chiavi.
                TreeMap<String,Integer> hashMap = new TreeMap<>();
                for (int i=1; i<=7; i++) {
                    hashMap.put(""+i, 0);
                }

                // Leggo il numero di consegne per ogni fascia oraria e aggiorno la mappa
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String day_nameDay_hourSlot = ds.getKey();
                    Integer amountOfOrders = ds.getValue(Integer.class);
                    String fields[] = day_nameDay_hourSlot.split("_");
                    String nameDay = fields[1];
                    Integer currentAmount = hashMap.get(nameDay);
                    if (currentAmount==null)
                        hashMap.put(nameDay, amountOfOrders);
                    else
                        hashMap.put(nameDay, currentAmount+amountOfOrders);
                }

                // Converto la mappa in ArrayList (la libreria accetta questo formato)
                List<BarEntry> entries = new ArrayList<>();
                int i =1;
                for (TreeMap.Entry<String, Integer> entry : hashMap.entrySet()) {
                    String dayOfWeek = entry.getKey();
                    Integer amountOfOrders = entry.getValue();
                    String nameDay = mapDayOfWeek.get(dayOfWeek);
                    entries.add(new BarEntry(i, amountOfOrders));
                    i++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
                dataSet.setDrawValues(false);
                BarData lineData = new BarData(dataSet);
                chart.getAxisLeft().setDrawGridLines(false);
                chart.getXAxis().setDrawGridLines(false);
                chart.getAxisRight().setDrawGridLines(false);
                chart.getDescription().setEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.setDrawGridBackground(false);

                chart.setData(lineData);
                chart.invalidate(); // refresh
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

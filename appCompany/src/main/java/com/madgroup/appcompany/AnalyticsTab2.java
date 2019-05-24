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
        return inflater.inflate(R.layout.fragment_analytics_tab2, container, false);
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
        //anyChartView = view.findViewById(R.id.weekly_histogram);
        //anyChartView.setProgressBar(view.findViewById(R.id.weekly_progress_bar));
        //initializeWeeklyHistogram();
    }


    public void initializeWeeklyHistogram() {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        Calendar calendar = Calendar.getInstance();
        String year = "2019";
        final String month = "5";
        final String weekOfMonth = "4";
        String node = year+"_"+month+"_"+weekOfMonth;


        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID).child(node);

        // Riferimenti all'istogramma
        final Cartesian cartesian = AnyChart.column();

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
                List<DataEntry> data = new ArrayList<>();
                for (TreeMap.Entry<String, Integer> entry : hashMap.entrySet()) {
                    String dayOfWeek = entry.getKey();
                    Integer amountOfOrders = entry.getValue();
                    String nameDay = mapDayOfWeek.get(dayOfWeek);
                    data.add(new ValueDataEntry(nameDay, amountOfOrders));
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
                cartesian.title(weekOfMonth+"th week of "+month);
                //cartesian.yAxis(0).title("Amount of orders");
                anyChartView.setChart(cartesian);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

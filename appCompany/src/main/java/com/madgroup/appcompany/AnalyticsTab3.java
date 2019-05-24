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

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class AnalyticsTab3 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private AnyChartView anyChartView;
    private Map<String, String> mapDayOfWeek;
    private Map<String, String> months;

    public AnalyticsTab3() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //initializeMonthlyHistogram();
        } else {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        initMapDayOfWeek();
        initMapMonths();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics_tab3, container, false);
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
        //anyChartView = view.findViewById(R.id.monthly_histogram);
        //anyChartView.setProgressBar(view.findViewById(R.id.monthly_progress_bar));
    }


    public void initializeMonthlyHistogram() {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        final String year = "2019";
        final String month = "5";
        final String weekOfMonth = "4";

        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID);

        // Riferimenti all'istogramma
        final Cartesian cartesian = AnyChart.column();

        timingOrederRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get the number of days in that month
                Calendar mycal = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month)-1, 1);
                int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

                // Inizializzo un'hashmap con tutti i valori da mostrare sull'asse delle x (cioè i giorni della settimana)
                // Uso TreeMap perchè tiene in ordine le chiavi.
                TreeMap<Integer,Long> hashMap = new TreeMap<>();
                for (int i=1; i<=daysInMonth; i++) {
                    hashMap.put(i, 0L);
                }

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String year_month_week = ds.getKey();
                    if (year_month_week.startsWith(year+"_"+month)) {
                        HashMap<String, Long> mapValues = (HashMap<String, Long>) ds.getValue();
                        for (Map.Entry<String, Long> entry : mapValues.entrySet()) {
                            String dayMonth_dayName_hourSlot = entry.getKey();
                            Long amount = entry.getValue();
                            String fields[] = dayMonth_dayName_hourSlot.split("_");
                            String dayMonth = fields[0];
                            Long currentAmount = hashMap.get(Integer.parseInt(dayMonth));
                            if (currentAmount==null)
                                hashMap.put(Integer.parseInt(dayMonth), amount);
                            else
                                hashMap.put(Integer.parseInt(dayMonth), currentAmount+amount);
                        }
                    }
                }

                // Converto la mappa in ArrayList (la libreria accetta questo formato)
                List<DataEntry> data = new ArrayList<>();
                for (TreeMap.Entry<Integer, Long> entry : hashMap.entrySet()) {
                    Integer dayOfMonth = entry.getKey();
                    Long amountOfOrders = entry.getValue();
                    data.add(new ValueDataEntry(dayOfMonth, amountOfOrders));
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
                String wordMonth = months.get(month);
                cartesian.title(wordMonth+" "+year);
                //cartesian.yAxis(0).title("Amount of orders");
                anyChartView.setChart(cartesian);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
}

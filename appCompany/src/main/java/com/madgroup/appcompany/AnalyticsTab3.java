package com.madgroup.appcompany;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class AnalyticsTab3 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private Map<String, String> mapDayOfWeek;
    private Map<String, String> months;
    private String selectedDay = "";
    private String selectedMonth = "";
    private String selectedYear = "";
    private TextView currentFilter;

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
        return inflater.inflate(R.layout.fragment_analytics, container, false);
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
        final BarChart chart = view.findViewById(R.id.bar_chart);
        ImageView previousButton = view.findViewById(R.id.previous_button);
        ImageView nextButton = view.findViewById(R.id.next_button);
        currentFilter = view.findViewById(R.id.current_filter);

        Calendar calendar = Calendar.getInstance();
        String currentDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String currentMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        String currentYear = Integer.toString(calendar.get(Calendar.YEAR));
        currentFilter.setText(months.get(currentMonth) + " " + currentYear);
        this.selectedDay = currentDay;
        this.selectedMonth = currentMonth;
        this.selectedYear = currentYear;

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedYear+"/"+selectedMonth+"/"+selectedDay;
                String prevDate = getPreviousMonth(selectedDate);
                String prevYear = (prevDate.split("/"))[0];
                String prevMonth = Integer.toString(Integer.parseInt((prevDate.split("/"))[1]));
                String prevDay = (prevDate.split("/"))[2];
                currentFilter.setText(months.get(prevMonth) + " " + prevYear);
                selectedDay = prevDay;
                selectedMonth = prevMonth;
                selectedYear = prevYear;
                initializeMonthlyHistogram(chart, selectedMonth, selectedYear);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedYear+"/"+selectedMonth+"/"+selectedDay;
                String nextDate = getNextMonth(selectedDate);
                String nextYear = (nextDate.split("/"))[0];
                String nextMonth = Integer.toString(Integer.parseInt((nextDate.split("/"))[1]));
                String nextDay = (nextDate.split("/"))[2];
                currentFilter.setText(months.get(nextMonth) + " " + nextYear);
                selectedDay = nextDay;
                selectedMonth = nextMonth;
                selectedYear = nextYear;
                initializeMonthlyHistogram(chart, selectedMonth, selectedYear);

            }
        });

        initializeMonthlyHistogram(chart, selectedMonth, selectedYear);
    }

    public void initializeMonthlyHistogram(final BarChart chart, final String month, final String year) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");

        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID);

        timingOrederRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get the number of days in that month
                Calendar mycal = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month)-1, 1);
                int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

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
                List<BarEntry> entries = new ArrayList<>();
                for (TreeMap.Entry<Integer, Long> entry : hashMap.entrySet()) {
                    Integer dayOfMonth = entry.getKey();
                    Long amountOfOrders = entry.getValue();
                    entries.add(new BarEntry(dayOfMonth, amountOfOrders));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset

                dataSet.setColor(Color.parseColor("#42B0F4")); //resolved color
                dataSet.setBarBorderColor(Color.parseColor("#41A9F4"));
                dataSet.setBarBorderWidth(1);
                dataSet.setDrawValues(false);

                chart.getAxisRight().setEnabled(false);
                chart.getAxisLeft().setEnabled(false);
                chart.getAxisLeft().setAxisMinimum(0f);

                chart.getXAxis().setDrawGridLines(false);
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getXAxis().setLabelCount(20);

                chart.getDescription().setEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.setDrawGridBackground(false);

                BarData lineData = new BarData(dataSet);
                chart.setData(lineData);

                chart.invalidate(); // refresh
                chart.animateY(1000);

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

    public static String getNextMonth(String curDate) {
        String nextDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.MONTH, 1);
            nextDate = format.format(today.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }

    public static String getPreviousMonth(String curDate) {
        String previousDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.MONTH, -1);
            previousDate = format.format(today.getTime());
        } catch (Exception e) {
            return previousDate;
        }
        return previousDate;
    }
}

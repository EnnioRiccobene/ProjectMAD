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

import com.anychart.AnyChartView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private String selectedMonth;
    private String selectedWeek;
    private String selectedYear;
    private TextView currentFilter;
    private Map<String, String> months;



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
        initMapMonths();
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
        final BarChart chart = view.findViewById(R.id.bar_chart);
        ImageView previousButton = view.findViewById(R.id.previous_button);
        ImageView nextButton = view.findViewById(R.id.next_button);
        currentFilter = view.findViewById(R.id.current_filter);

        Calendar calendar = Calendar.getInstance();
        String currentWeek = Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH));
        String currentMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        String currentYear = Integer.toString(calendar.get(Calendar.YEAR));
        String titleWeek = "";
        if (currentWeek.equals("1"))
            titleWeek = currentWeek + "st";
        else if (currentWeek.equals("2"))
            titleWeek = currentWeek + "nd";
        else if (currentWeek.equals("3"))
            titleWeek = currentWeek + "rd";
        else
            titleWeek = currentWeek + "th";
        currentFilter.setText(titleWeek + " Week " + " of " + months.get(currentMonth) + " " + currentYear);
        this.selectedMonth = currentMonth;
        this.selectedYear = currentYear;
        this.selectedWeek = currentWeek;

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( Integer.parseInt(selectedWeek) == 1 ) {
                    String selectedDate = selectedYear+"/"+selectedMonth+"/01";
                    String prevDate = getPreviousMonth(selectedDate);
                    String prevMonth = Integer.toString(Integer.parseInt((prevDate.split("/"))[1]));
                    if (prevMonth.equals("12")) {
                        Integer numWeeks = getNumberOfWeeks(Integer.toString(Integer.parseInt(selectedYear)-1), prevMonth);
                        selectedWeek = Integer.toString(numWeeks);
                        selectedMonth = prevMonth;
                        selectedYear = Integer.toString(Integer.parseInt(selectedYear)-1);
                    } else {
                        Integer numWeeks = getNumberOfWeeks(selectedYear, prevMonth);
                        selectedWeek = Integer.toString(numWeeks);
                        selectedMonth = prevMonth;
                    }
                } else {
                    selectedWeek = Integer.toString(Integer.parseInt(selectedWeek) - 1);
                }
                String titleWeek = "";
                if (selectedWeek.equals("1"))
                    titleWeek = selectedWeek + "st";
                else if (selectedWeek.equals("2"))
                    titleWeek = selectedWeek + "nd";
                else if (selectedWeek.equals("3"))
                    titleWeek = selectedWeek + "rd";
                else
                    titleWeek = selectedWeek + "th";
                currentFilter.setText(titleWeek + " Week " + " of " + months.get(selectedMonth) + " " + selectedYear);                initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer numberOfWeeks = getNumberOfWeeks(selectedYear, selectedMonth);
                if (Integer.parseInt(selectedWeek) == numberOfWeeks) {
                    String selectedDate = selectedYear+"/"+selectedMonth+"/01";
                    String nextMonth = getNextMonth(selectedDate);
                    selectedMonth = nextMonth;
                    selectedWeek= "1";
                    if(selectedMonth.equals("01"))
                        selectedYear = Integer.toString(Integer.parseInt(selectedYear)+1);
                } else {
                    selectedWeek = Integer.toString(Integer.parseInt(selectedWeek)+1);
                }
                String titleWeek = "";
                if (selectedWeek.equals("1"))
                    titleWeek = selectedWeek + "st";
                else if (selectedWeek.equals("2"))
                    titleWeek = selectedWeek + "nd";
                else if (selectedWeek.equals("3"))
                    titleWeek = selectedWeek + "rd";
                else
                    titleWeek = selectedWeek + "th";
                currentFilter.setText(titleWeek + " Week " + " of " + months.get(selectedMonth) + " " + selectedYear);
                initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);
            }
        });

        initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);

    }

    private Integer getNumberOfWeeks(String selectedYear, String selectedMonth) {
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String selectedDate = selectedYear+"/"+selectedMonth+"/01";
        Date date = null;
        try {
            date = format.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        Integer numberOfWeeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        return numberOfWeeks;
    }


    public void initializeWeeklyHistogram(final BarChart chart, final String weekOfMonth, final String month,
                                          final String year) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        Calendar calendar = Calendar.getInstance();
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

                dataSet.setColor(Color.parseColor("#42B0F4")); //resolved color
                dataSet.setBarBorderColor(Color.parseColor("#41A9F4"));
                dataSet.setBarBorderWidth(1);
                dataSet.setDrawValues(false);

                chart.getAxisRight().setEnabled(false);
                chart.getAxisLeft().setEnabled(false);
                chart.getAxisLeft().setAxisMinimum(0f);

                chart.getXAxis().setDrawGridLines(false);
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.getXAxis().setLabelCount(entries.size());

                chart.getDescription().setEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.setDrawGridBackground(false);

                BarData lineData = new BarData(dataSet);
                chart.setData(lineData);
                chart.setTouchEnabled(false);

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
        months.put("01", "January");
        months.put("02", "February");
        months.put("03", "March");
        months.put("04", "April");
        months.put("05", "May");
        months.put("06", "June");
        months.put("07", "July");
        months.put("08", "August");
        months.put("09", "September");
        months.put("10", "October");
        months.put("11", "November");
        months.put("12", "December");
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
        return ((nextDate.split("/"))[1]);
    }
}

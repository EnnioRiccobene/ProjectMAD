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
import android.widget.Button;
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

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class AnalyticsTab1 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private Map<String, String> months;
    private String selectedDay = "";
    private String selectedMonth = "";
    private String selectedYear = "";
    private TextView currentFilter;


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
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
        currentFilter.setText(currentDay + " " + months.get(currentMonth) + " " + currentYear);
        this.selectedDay = currentDay;
        this.selectedMonth = currentMonth;
        this.selectedYear = currentYear;

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedYear+"/"+selectedMonth+"/"+selectedDay;
                String prevDate = getPreviousDay(selectedDate);
                String prevYear = (prevDate.split("/"))[0];
                String prevMonth = Integer.toString(Integer.parseInt((prevDate.split("/"))[1]));
                String prevDay = (prevDate.split("/"))[2];
                currentFilter.setText(prevDay + " " + months.get(prevMonth) + " " + prevYear);
                selectedDay = prevDay;
                selectedMonth = prevMonth;
                selectedYear = prevYear;
                initializeDailyHistogram(chart, selectedDay, selectedMonth, selectedYear);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedYear+"/"+selectedMonth+"/"+selectedDay;
                String nextDate = getNextDay(selectedDate);
                String nextYear = (nextDate.split("/"))[0];
                String nextMonth = Integer.toString(Integer.parseInt((nextDate.split("/"))[1]));
                String nextDay = (nextDate.split("/"))[2];
                currentFilter.setText(nextDay + " " + months.get(nextMonth) + " " + nextYear);
                selectedDay = nextDay;
                selectedMonth = nextMonth;
                selectedYear = nextYear;
                initializeDailyHistogram(chart, selectedDay, selectedMonth, selectedYear);

            }
        });

        initializeDailyHistogram(chart, currentDay, currentMonth, currentYear);
    }


    public void initializeDailyHistogram(final BarChart chart, final String dayOfMonth, final String month,
                                         final String year ) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");

        String weekOfMonth = getWeekOfMonth(dayOfMonth, month, year);

        String node = year+"_"+month+"_"+weekOfMonth;
        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
                .child(restaurantID).child(node);


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
                List<BarEntry> entries = new ArrayList<>();
                for (TreeMap.Entry<Integer, Integer> entry : hashMap.entrySet()) {
                    Integer hourSlot = entry.getKey();
                    Integer amountOfOrders = entry.getValue();
                    entries.add(new BarEntry(hourSlot, amountOfOrders) {
                    });
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
                chart.setPinchZoom(false);
                chart.setDoubleTapToZoomEnabled(false);

                chart.invalidate(); // refresh
                chart.animateY(1000);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @NotNull
    private String getWeekOfMonth(String dayOfMonth, String month, String year) {

        if (month.length()==1)
            month = "0"+month;
        String input = year+"/"+month+"/"+dayOfMonth;
        String format = "yyyy/MM/dd";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
        return Integer.toString(weekOfMonth);
    }


    public static String getNextDay(String curDate) {
        String nextDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = format.format(today.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }

    public static String getPreviousDay(String curDate) {
        String previousDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, -1);
            previousDate = format.format(today.getTime());
        } catch (Exception e) {
            return previousDate;
        }
        return previousDate;
    }
}

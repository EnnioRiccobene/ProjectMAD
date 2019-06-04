package com.madgroup.appcompany;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.Dish;

import org.jetbrains.annotations.NotNull;

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
import de.hdodenhof.circleimageview.CircleImageView;


public class AnalyticsTab2 extends Fragment implements OnChartValueSelectedListener {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private Map<String, String> mapDayOfWeek;
    private String selectedMonth;
    private String selectedWeek;
    private String selectedYear;
    private TextView currentFilter;
    private Map<String, String> months;
    private CircleImageView topMeal;
    private TextView salesTextView;
    private TextView topDishName;
    private TextView descriptionChart;




    public AnalyticsTab2() {
        // Required empty public constructor
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
        mapDayOfWeek.put("1",getString(R.string.short_mon));
        mapDayOfWeek.put("2",getString(R.string.short_tue));
        mapDayOfWeek.put("3",getString(R.string.short_wed));
        mapDayOfWeek.put("4",getString(R.string.short_thu));
        mapDayOfWeek.put("5",getString(R.string.short_fri));
        mapDayOfWeek.put("6",getString(R.string.short_sat));
        mapDayOfWeek.put("7",getString(R.string.short_sun));

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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        String day = Integer.toString(Math.round(h.getX()));
        String nameOfDay = mapDayOfWeek.get(day);
        Integer totalSales = Math.round(h.getY());
        if (totalSales==0) {
            descriptionChart.setText("No orders detected on " + nameOfDay + "\n");
        } else {
            String startingString = totalSales + " orders detected on " + nameOfDay;
            setDescriptionChart(startingString, selectedWeek, day, selectedMonth, selectedYear);
        }
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnFragmentInteractionListener {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final BarChart chart = view.findViewById(R.id.bar_chart);
        descriptionChart = view.findViewById(R.id.description_chart);
        ImageView previousButton = view.findViewById(R.id.previous_button);
        ImageView nextButton = view.findViewById(R.id.next_button);
        currentFilter = view.findViewById(R.id.current_filter);
        topMeal = view.findViewById(R.id.top_meal);
        salesTextView = view.findViewById(R.id.sales_number);
        topDishName = view.findViewById(R.id.top_dish_name);
        final Resources res = getResources();

        Calendar calendar = Calendar.getInstance();
        String currentWeek = Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH)+1);
        String currentMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        String currentYear = Integer.toString(calendar.get(Calendar.YEAR));
        String titleWeek = "";
        if (currentWeek.equals("1"))
            titleWeek = currentWeek + getString(R.string.st);
        else if (currentWeek.equals("2"))
            titleWeek = currentWeek + getString(R.string.nd);
        else if (currentWeek.equals("3"))
            titleWeek = currentWeek + getString(R.string.rd);
        else
            titleWeek = currentWeek + getString(R.string.th);
        currentFilter.setText(titleWeek + " " + getString(R.string.week) + " " + getString(R.string.of) + " " + months.get(currentMonth) + " " + currentYear);
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
                    titleWeek = selectedWeek + getString(R.string.st);
                else if (selectedWeek.equals("2"))
                    titleWeek = selectedWeek + getString(R.string.nd);
                else if (selectedWeek.equals("3"))
                    titleWeek = selectedWeek + getString(R.string.rd);
                else
                    titleWeek = selectedWeek + getString(R.string.th);
                currentFilter.setText(titleWeek + " " + getString(R.string.week) + " " +getString(R.string.of) + " " +months.get(selectedMonth) + " " + selectedYear);
                initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);
                getTopMealOfWeek(res, topMeal, salesTextView, topDishName, selectedWeek, selectedMonth, selectedYear);

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
                    titleWeek = selectedWeek + getString(R.string.st);
                else if (selectedWeek.equals("2"))
                    titleWeek = selectedWeek + getString(R.string.nd);
                else if (selectedWeek.equals("3"))
                    titleWeek = selectedWeek + getString(R.string.rd);
                else
                    titleWeek = selectedWeek + getString(R.string.th);
                currentFilter.setText(titleWeek + " " + getString(R.string.week) + " " + getString(R.string.of) + " " + months.get(selectedMonth) + " " + selectedYear);
                initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);
                getTopMealOfWeek(res, topMeal, salesTextView, topDishName, selectedWeek, selectedMonth, selectedYear);

            }
        });

        initializeWeeklyHistogram(chart, selectedWeek, selectedMonth, selectedYear);
        getTopMealOfWeek(res, topMeal, salesTextView, topDishName, selectedWeek, selectedMonth, selectedYear);

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
        Integer numberOfWeeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + 1;
        return numberOfWeeks;
    }


    public void initializeWeeklyHistogram(final BarChart chart, final String weekOfMonth, final String month,
                                          final String year) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");
        Calendar calendar = Calendar.getInstance();
        String node = year+"_"+month+"_"+weekOfMonth;


        DatabaseReference timingOrederRef = database.getReference()
                .child("Analytics").child("TimingOrder")
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
                chart.setPinchZoom(false);
                chart.setDoubleTapToZoomEnabled(false);

                chart.setOnChartValueSelectedListener(AnalyticsTab2.this);

                chart.invalidate(); // refresh


                chart.animateY(1000);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getTopMealOfWeek(final Resources res, final CircleImageView topMeal, final TextView salesTextView, final TextView topDishName,
            final String weekOfMonth, final String month, final String year) {

        // Database references
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String restaurantID = prefs.getString("currentUser", "");
        Calendar calendar = Calendar.getInstance();
        String node = year+"_"+month+"_"+weekOfMonth;

        DatabaseReference topMealRef = database.getReference()
                .child("Analytics").child("TopMeals")
                .child(restaurantID).child(node);

        final HashMap<String, Integer> dishesIDQuantity = new HashMap<>();
        topMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Per ogni fascia oraria
                for (DataSnapshot hourSlot : dataSnapshot.getChildren()) {
                        // Per ogni ID
                        for (DataSnapshot dishIDQuantity : hourSlot.getChildren()) {
                            Integer amount = dishIDQuantity.getValue(Integer.class);
                            Integer previousAmount = dishesIDQuantity.get(dishIDQuantity.getKey());
                            if (previousAmount==null)
                                dishesIDQuantity.put(dishIDQuantity.getKey(), amount);
                            else
                                dishesIDQuantity.put(dishIDQuantity.getKey(), amount+previousAmount);
                        }
                }

                // Cerco il massimo
                Map.Entry<String, Integer> maxEntry = null;
                for (Map.Entry<String, Integer> entry : dishesIDQuantity.entrySet())
                {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        maxEntry = entry;
                }

                if (maxEntry!=null) {
                    final String topDishID = maxEntry.getKey();
                    final Integer topDishQuantity = maxEntry.getValue();

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference("dish_pics").child(restaurantID)
                            .child(topDishID);
                    GlideApp.with(AnalyticsTab2.this)
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(res.getDrawable(R.drawable.ic_sand_clock))
                            .error(GlideApp.with(AnalyticsTab2.this).load(R.drawable.ic_dish))
                            .into(topMeal);

                    DatabaseReference ref = database.getReference().child("Company").child("Menu")
                            .child(restaurantID).child(topDishID);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Dish dish = dataSnapshot.getValue(Dish.class);
                            topDishName.setText(dish.getName() + " (" + dish.getPrice() + ")");
                            salesTextView.setText(topDishQuantity + " " + getString(R.string.vendite));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else {
                    topMeal.setImageResource(R.drawable.ic_dish);
                    topDishName.setText("");
                    salesTextView.setText(getString(R.string.no_vendite));
                }
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
        Integer weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH) + 1;
        return Integer.toString(weekOfMonth);
    }

    public void setDescriptionChart(final String startingString, final String weekOfMonth, final String dayOfWeek,
                                    final String month, final String year) {
        // Database references
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String restaurantID = prefs.getString("currentUser", "");

        String node = year+"_"+month+"_"+weekOfMonth;
        DatabaseReference topMealRef = database.getReference().child("Analytics").child("TopMeals")
                .child(restaurantID).child(node);

        final HashMap<String, Integer> dishesIDQuantity = new HashMap<>();
        topMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Per ogni fascia oraria relativa al giorno
                for (DataSnapshot hourSlot : dataSnapshot.getChildren()) {
                    if(hourSlot.getKey().contains("_"+dayOfWeek+"_")) {
                        // Per ogni ID
                        for (DataSnapshot dishIDQuantity : hourSlot.getChildren()) {
                            Integer amount = dishIDQuantity.getValue(Integer.class);
                            Integer previousAmount = dishesIDQuantity.get(dishIDQuantity.getKey());
                            if (previousAmount==null)
                                dishesIDQuantity.put(dishIDQuantity.getKey(), amount);
                            else
                                dishesIDQuantity.put(dishIDQuantity.getKey(), amount+previousAmount);
                        }
                    }
                }

                // Cerco il massimo tra tutte le fasce orarie
                Map.Entry<String, Integer> maxEntry = null;
                for (Map.Entry<String, Integer> entry : dishesIDQuantity.entrySet())
                {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                        maxEntry = entry;
                }
                if (maxEntry!=null) {
                    final String topDishID = maxEntry.getKey();
                    final Integer topDishQuantity = maxEntry.getValue();

                    DatabaseReference ref = database.getReference().child("Company").child("Menu")
                            .child(restaurantID).child(topDishID);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Dish dish = dataSnapshot.getValue(Dish.class);
                            String bestMealString = "\nTop meal: " + dish.getName() + "("+dish.getPrice()+"), "+ topDishQuantity + " sales.";
                            descriptionChart.setText(startingString + bestMealString);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
//                else {
//                    topMeal.setImageResource(R.drawable.ic_dish);
//                    topDishName.setText("");
//                    salesTextView.setText(getString(R.string.no_vendite));
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void initMapMonths() {
        months = new HashMap<>();
        months.put("1", getString(R.string.january));
        months.put("2", getString(R.string.february));
        months.put("3", getString(R.string.march));
        months.put("4", getString(R.string.april));
        months.put("5", getString(R.string.may));
        months.put("6", getString(R.string.june));
        months.put("7", getString(R.string.july));
        months.put("8", getString(R.string.august));
        months.put("9", getString(R.string.september));
        months.put("01", getString(R.string.january));
        months.put("02", getString(R.string.february));
        months.put("03", getString(R.string.march));
        months.put("04", getString(R.string.april));
        months.put("05", getString(R.string.may));
        months.put("06", getString(R.string.june));
        months.put("07", getString(R.string.july));
        months.put("08", getString(R.string.august));
        months.put("09", getString(R.string.september));
        months.put("10", getString(R.string.october));
        months.put("11", getString(R.string.november));
        months.put("12", getString(R.string.december));
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

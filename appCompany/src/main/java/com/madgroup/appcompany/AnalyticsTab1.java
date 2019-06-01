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


public class AnalyticsTab1 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private Map<String, String> months;
    private String selectedDay = "";
    private String selectedMonth = "";
    private String selectedYear = "";
    private TextView currentFilter;
    private TextView salesTextView;
    private TextView topDishName;


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
        final CircleImageView topMeal = view.findViewById(R.id.top_meal);
        salesTextView = view.findViewById(R.id.sales_number);
        topDishName = view.findViewById(R.id.top_dish_name);
        final Resources res = getResources();

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
                getTopMealOfDay(res, topMeal, salesTextView, topDishName, selectedDay, selectedMonth, selectedYear);
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
                getTopMealOfDay(res, topMeal, salesTextView, topDishName, selectedDay, selectedMonth, selectedYear);
            }
        });

        initializeDailyHistogram(chart, currentDay, currentMonth, currentYear);
        getTopMealOfDay(res, topMeal,salesTextView, topDishName, currentDay, currentMonth, currentYear);
    }


    public void initializeDailyHistogram(final BarChart chart, final String dayOfMonth, final String month,
                                         final String year ) {

        // Database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String restaurantID = prefs.getString("currentUser", "");

        String weekOfMonth = getWeekOfMonth(dayOfMonth, month, year);

        String node = year+"_"+month+"_"+weekOfMonth;
        DatabaseReference timingOrederRef = database.getReference()
                .child("Analytics").child("TimingOrder")
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


    private void getTopMealOfDay(final Resources res, final CircleImageView topMeal, final TextView salesTextView, final TextView topDishName,
                                 final String dayOfMonth, final String month, final String year) {

        // Database references
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String restaurantID = prefs.getString("currentUser", "");

        String weekOfMonth = getWeekOfMonth(dayOfMonth, month, year);

        String node = year+"_"+month+"_"+weekOfMonth;
        DatabaseReference topMealRef = database.getReference().child("Analytics").child("TopMeals")
                .child(restaurantID).child(node);

        final HashMap<String, Integer> dishesIDQuantity = new HashMap<>();
        topMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Per ogni fascia oraria relativa al giorno
                for (DataSnapshot hourSlot : dataSnapshot.getChildren()) {
                    if(hourSlot.getKey().startsWith(dayOfMonth)) {
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

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference("dish_pics").child(restaurantID)
                            .child(topDishID);
                    GlideApp.with(AnalyticsTab1.this)
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(res.getDrawable(R.drawable.ic_sand_clock))
                            .error(GlideApp.with(AnalyticsTab1.this).load(R.drawable.ic_dish))
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
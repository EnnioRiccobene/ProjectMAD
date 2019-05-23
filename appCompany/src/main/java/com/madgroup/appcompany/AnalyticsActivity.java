package com.madgroup.appcompany;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.format;

public class AnalyticsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AnalyticsTab1.OnFragmentInteractionListener,
        AnalyticsTab2.OnFragmentInteractionListener,
        AnalyticsTab3.OnFragmentInteractionListener {

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_analytics);
        stub.inflate();
        this.setTitle("Analytics");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        initializeNavigationDrawer();
        initializeTabs();

    }

    public void initializeTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.analytics_pager);
        AnalyticsPageAdapter myPagerAdapter = new AnalyticsPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.analytics_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            // Change activity to Daily Offer
            Intent myIntent = new Intent(this, DailyOfferActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_reservations) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, ReservationActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } if (id == R.id.nav_analytics) {

        } else if (id == R.id.nav_profile) {
            onBackPressed();
        } else if (id == R.id.nav_logout) {
            startLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        editor.clear();
                        editor.apply();
                        //startLogin();
                        Intent intent = new Intent(AnalyticsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public void initializeNavigationDrawer() {

        // Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(        // Three lines icon on the left corner
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the photo of the Navigation Bar Icon (Need to be completed: refresh when new image is updated)
//        ImageView nav_profile_icon = (ImageView) findViewById(R.id.nav_profile_icon);
//        nav_profile_icon.setImageDrawable(personalImage.getDrawable());

        // Set restaurant name and email on navigation header
        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String username = prefs.getString("Name", "");
        navUsername.setText(username);

        String email = prefs.getString("Email", "");
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        updateNavigatorPersonalIcon();
    }

    public void updateNavigatorPersonalIcon() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);
    }


//    public void initializeDailyHistogram() {
//
//        // Database references
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        String restaurantID = prefs.getString("currentUser", "");
//        Calendar calendar = Calendar.getInstance();
//        String year = Integer.toString(calendar.get(Calendar.YEAR));
//        String month = Integer.toString(calendar.get(Calendar.MONTH));
//        String weekOfMonth = Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH));
//        String node = year+"_"+month+"_"+weekOfMonth;
//        //final String dayOfMonth = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
//        final String dayOfMonth = "22";
//        DatabaseReference timingOrederRef = database.getReference().child("Company").child("Reservation").child("TimingOrder")
//                .child(restaurantID).child(node);
//
//
//        // Riferimenti all'istogramma
//        final AnyChartView anyChartView = findViewById(R.id.daily_histogram);
//        anyChartView.setProgressBar(findViewById(R.id.daily_progress_bar));
//        final Cartesian cartesian = AnyChart.column();
//
//        timingOrederRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                // Inizializzo un'hashmap con tutti i valori da mostrare sull'asse delle x (cioè le fasce orarie)
//                // Uso TreeMap perchè tiene in ordine le chiavi.
//                TreeMap<String,Integer> hashMap = new TreeMap<>();
//                for (int i=18; i<24; i++) {
//                    hashMap.put(""+i, 0);
//                }
//
//                // Leggo il numero di consegne per ogni fascia oraria e aggiorno la mappa
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String day_hourSlot = ds.getKey();
//                    Integer amountOfOrders = ds.getValue(Integer.class);
//                    // Se il giorno corrisponde al giorno selezionato lo vado a mostrare con la relativa fascia oraria
//                    if(day_hourSlot.startsWith(dayOfMonth)) {
//                        String fields[] = day_hourSlot.split("_");
//                        String hourSlot = fields[1];
//                        hashMap.put(hourSlot, amountOfOrders);
//                        //data.add(new ValueDataEntry(hourSlot, amountOfOrders));
//                    }
//                }
//
//                // Converto la mappa in ArrayList (la libreria accetta questo formato)
//                List<DataEntry> data = new ArrayList<>();
//                for (TreeMap.Entry<String, Integer> entry : hashMap.entrySet()) {
//                    String hourSlot = entry.getKey();
//                    Integer amountOfOrders = entry.getValue();
//                    data.add(new ValueDataEntry(hourSlot, amountOfOrders));
//                }
//
//                Column column = cartesian.column(data);
//                column.tooltip()
//                        .titleFormat("{%X}")
//                        .position(Position.CENTER_TOP)
//                        .anchor(Anchor.CENTER_TOP)
//                        .offsetX(0d)
//                        .offsetY(5d);
//                cartesian.animation(true);
//                cartesian.title("Timing of orders");
//                cartesian.yScale().minimum(0d);
//                cartesian.yAxis(0).labels().enabled(false);
//                //.format("{%Value}{groupsSeparator: }");
//                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
//                cartesian.tooltip().title().text("Amount of deliveries");
//                cartesian.tooltip().format("{%value}");
//                cartesian.interactivity().hoverMode(HoverMode.BY_X);
//                cartesian.xAxis(0).title("");
//                //cartesian.yAxis(0).title("Amount of orders");
//                anyChartView.setChart(cartesian);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

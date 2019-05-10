package com.madgroup.appbikers;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliveryActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DeliveryPendingTab1.OnFragmentInteractionListener,
        DeliveryHistoryTab2.OnFragmentInteractionListener {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String currentUser;
    String notificationTitle = "MAD Company";
    String notificationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_deliveries);
        stub.inflate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = prefs.getString("currentUser", "");
        editor = prefs.edit();
        this.setTitle("Deliveries");
        initializeTabs();
        navigationDrawerInitialization();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //final DatabaseReference newOrderRef = database.getReference().child("Rider").child("Delivery").child("Pending").child(prefs.getString("currentUser", ""));
        final DatabaseReference newOrderRef = database.getReference().child("Rider").child("Delivery").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", "")).child("seen");


        final Map<String, Object> childUpdates = new HashMap<>();
        final ArrayList<String> reservationKeys = new ArrayList<>();

        //aggiorno il db con i nuovi valori di "seen"
        newOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
                    for ( String key : data.keySet() ) {
                        reservationKeys.add(key);
                    }

                    for(int i = 0; i < reservationKeys.size(); i++){
                        childUpdates.put("/" + reservationKeys.get(i) + "/" + "seen", true);
                    }
                    newOrderRef.updateChildren(childUpdates);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {

            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }
    }


    public void navigationDrawerInitialization() {
        // Navigation Drawer Initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavigatorInformation(navigationView);
        verifyRiderAvailability(navigationView);
    }

    private void verifyRiderAvailability(NavigationView navigationView) {
        final SwitchCompat riderAvailability = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_switch).getActionView().findViewById(R.id.drawer_switch);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference riderStatusRef = database.child("Rider").child("Profile").child(currentUser).child("status");
        Boolean status = prefs.getBoolean("Status", false);
        SmartLogger.d("status letto: " + status.toString());
        riderAvailability.setChecked(status);
        riderAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newStatus;
                if (riderAvailability.isChecked())
                    newStatus = true;
                else
                    newStatus = false;
                editor.putBoolean("Status", newStatus);
                riderStatusRef.setValue(newStatus);
                editor.apply();
            }
        });
    }

    public void updateNavigatorInformation(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("bikers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);


        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String name = prefs.getString("Name", "");
        if (!name.equals(""))
            navUsername.setText(name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        String email = prefs.getString("Email", "");
        if (!email.equals(""))
            navEmail.setText(email);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_deliviries) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_logout)
                startLogout();
        if (id == R.id.nav_switch)
            return true;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Tabs
    public void initializeTabs() {
        // Remove black line under toolbar
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(findViewById(android.R.id.content), "elevation", 0));
        findViewById(R.id.appBarLayout).setStateListAnimator(stateListAnimator);

        // Add tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        DeliveryPageAdapter myPagerAdapter = new DeliveryPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tablayout = (TabLayout) findViewById(R.id.tabLayout);
        tablayout.setupWithViewPager(viewPager);

        // Add actions on tab selected/reselected/unselected
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void startLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(DeliveryActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }
}

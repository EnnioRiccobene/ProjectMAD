package com.madgroup.appbikers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.SmartLogger;

public class DeliveryActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DeliveryPendingTab1.OnFragmentInteractionListener,
        DeliveryHistoryTab2.OnFragmentInteractionListener {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String currentUser;

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
        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
        if (!ImageBitmap.equals("NoImage")) {
            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            nav_profile_icon.setImageBitmap(bitmap);
        } else {
            Drawable defaultImg = getResources().getDrawable(R.mipmap.ic_launcher_round);
            nav_profile_icon.setImageDrawable(defaultImg);
        }

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
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }
        if(id  == R.id.nav_switch)
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

}

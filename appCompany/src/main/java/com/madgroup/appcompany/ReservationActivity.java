package com.madgroup.appcompany;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class ReservationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        reservationTab1.OnFragmentInteractionListener,
        reservationTab2.OnFragmentInteractionListener,
        reservationTab3.OnFragmentInteractionListener{
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public final static int PENDING_RESERVATION_CODE = 0;
    public final static int ACCEPTED_RESERVATION_CODE = 1;
    public final static int CALLED_RESERVATION_CODE = 2;
    public final static int HISTORY_ACCEPTED_RESERVATION_CODE = 3;
    public final static int HISTORY_REJECT_RESERVATION_CODE = 4;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_reservation);
        stub.inflate();

        initializeTabs();
        this.setTitle("Reservations");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();


        // OVERRIDE DEL ONBACKPRESSED
        initializeNavigationDrawer();

    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, DailyOfferActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_reservations) {
            onBackPressed();

        } else if (id == R.id.nav_profile) {
            // Change activity to Daily Offer
            Intent myIntent = new Intent(this, ProfileActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // What happens if I press back button
    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(GravityCompat.START))
            layout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void initializeNavigationDrawer(){

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
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        updateNavigatorPersonalIcon();
    }

    public void updateNavigatorPersonalIcon(){
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
        if(!ImageBitmap.equals("NoImage")){
            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            nav_profile_icon.setImageBitmap(bitmap);
        } else {
            Drawable defaultImg = getResources().getDrawable(R.mipmap.ic_launcher_round);
            nav_profile_icon.setImageDrawable(defaultImg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reservation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reservation_reload) {
            switch (tabLayout.getSelectedTabPosition()){
                case 0:

                    break;
                case 1:

                    break;
                case 2:

                    break;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Tabs
    public void initializeTabs(){

        // Remove black line under toolbar
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(findViewById(android.R.id.content), "elevation", 0));
        findViewById(R.id.appBarLayout).setStateListAnimator(stateListAnimator);

        // Add tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.reservationViewPager);
        reservationPageAdapter myPagerAdapter = new reservationPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        // Add actions on tab selected/reselected/unselected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
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

    // TODO
    public void reloadReservationList() {

//        reservationTab1.pendingReservation = new ArrayList<>();
//        reservationTab2.acceptedReservation = new ArrayList<>();
//        reservationTab3.historyReservation = new ArrayList<>();
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
//        DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted");
//        DatabaseReference hisotryReservationRef = database.child("Company").child("Reservation").child("History");
        // RIEMPIERE LE LISTE E AGGIORNARE LE RECYCLER VIEW


    }
}

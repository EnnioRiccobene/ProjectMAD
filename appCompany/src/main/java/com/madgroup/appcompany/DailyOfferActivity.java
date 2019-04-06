package com.madgroup.appcompany;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class DailyOfferActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    ArrayList<Dish> myList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_offer);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // OVERRIDE DEL ONBACKPRESSED
        initializeNavigationDrawer();

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.personicon);

        myList.add(new Dish(0,"Pollo", 3, 1, "Curry",icon));
        myList.add(new Dish(2,"Pollo2", 3.90f, 1, "Curry",icon));
        myList.add(new Dish(0,"Pollo", 3, 1, "Curry",icon));
        myList.add(new Dish(2,"Pollo2", 3, 1, "Curry",icon));
        myList.add(new Dish(0,"Pollo", 3, 1, "Curry",icon));
        myList.add(new Dish(2,"Pollo2", 3, 1, "Curry",icon));
        myList.add(new Dish(0,"Pollo", 3, 1, "Curry",icon));
        myList.add(new Dish(2,"Pollo2", 3, 1, "Curry",icon));

        initDailyOfferRecyclerView();
    }


    private void initDailyOfferRecyclerView() {
        RecyclerView recyclerView = this.findViewById(R.id.my_recycler_view);
        DailyOfferRecyclerViewAdapter adapter = new DailyOfferRecyclerViewAdapter(this,  myList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            onBackPressed();

        } else if (id == R.id.nav_reservations) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, ReservationActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_profile) {
            // Change activity to Daily Offer
            Intent myIntent = new Intent(this, MainActivity.class);
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
}


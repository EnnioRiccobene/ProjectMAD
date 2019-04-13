package com.madgroup.appbikers;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import android.view.ViewStub;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DeliveriesActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private DeliveriesAdapter adapter;
    private ArrayList<Delivery> deliveriesList = new ArrayList<>();
    private CardView cardView;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_deliveries);
        stub.inflate();

        cardView = findViewById(R.id.deliveryItemCardView);
        recyclerView = this.findViewById(R.id.deliveryRecycleView);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        deliveriesList.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
        deliveriesList.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
        deliveriesList.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
        deliveriesList.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));

        initDeliveriesRecyclerView();
        navigationDrawerInitialization();
    }

    private void initDeliveriesRecyclerView() {
        adapter = new DeliveriesAdapter(this,  deliveriesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    public void navigationDrawerInitialization(){
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
    }

    public void updateNavigatorInformation(NavigationView navigationView){
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

        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String name = prefs.getString("Name", "");
        if(!name.equals(""))
            navUsername.setText(name);
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        String email = prefs.getString("Email", "");
        if(!email.equals(""))
            navEmail.setText(email);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_deliviries) {
            Intent myIntent = new Intent(this, DeliveriesActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

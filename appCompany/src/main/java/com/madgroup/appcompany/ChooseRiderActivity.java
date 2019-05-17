package com.madgroup.appcompany;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.RiderProfile;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

public class ChooseRiderActivity extends AppCompatActivity {

    private String restaurantAddress;
    private ArrayList<RiderProfile> riderList;
    private RecyclerView recyclerView;
    private ChooseRiderAdapter adapter;
    private Reservation reservation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rider);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        restaurantAddress = prefs.getString("Address", "noAddress");
        if (restaurantAddress.equals("noAddress")) {
            Toast.makeText(this, "No Address setted", Toast.LENGTH_LONG).show();
            finish();
        }

        riderList = (ArrayList<RiderProfile>) getIntent().getSerializableExtra("riderList");
        reservation = (Reservation) getIntent().getSerializableExtra("reservation");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Choose a rider");

        // RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new ChooseRiderAdapter(this, riderList, reservation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
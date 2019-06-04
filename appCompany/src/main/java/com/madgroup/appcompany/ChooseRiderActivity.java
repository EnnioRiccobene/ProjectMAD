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

    String notificationTitle = "MADelivery";
    String notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rider);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        restaurantAddress = prefs.getString("Address", "noAddress");
        if (restaurantAddress.equals("noAddress")) {
            Toast.makeText(this, getString(R.string.no_address), Toast.LENGTH_LONG).show();
            finish();
        }

        riderList = (ArrayList<RiderProfile>) getIntent().getSerializableExtra("riderList");
        reservation = (Reservation) getIntent().getSerializableExtra("reservation");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle(R.string.choose_rider);

        // RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new ChooseRiderAdapter(this, riderList, reservation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {

            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }
    }
}

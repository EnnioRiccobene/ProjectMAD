package com.madgroup.appcompany;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.madgroup.appcompany.MainActivity.currentUser;


public class DetailedReservation extends AppCompatActivity {

    private ImageView confirmButton;
    private TextView totalPrice;
    private TextView address;
    private TextView lunchTime;
    private TextView notes;
    private Reservation reservation;
    private RecyclerView recyclerView;
    private DetailedReservationDishesAdapter dAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reservation);
        reservation = (Reservation) getIntent().getSerializableExtra("Reservation");
        ArrayList<OrderedDish> orderedFood = (ArrayList<OrderedDish>) getIntent().getSerializableExtra("OrderedFood");

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailed_reservation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Detailed Reservation");

        // RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        dAdapter = new DetailedReservationDishesAdapter(orderedFood);

        // Make it unscrollable
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dAdapter);

        // Fields
        confirmButton = findViewById(R.id.detailed_reservation_confirm_button);
        totalPrice = findViewById(R.id.totalPrice);
        totalPrice.setText(reservation.getPrice() + " €");
        address = findViewById(R.id.address);
        address.setText(reservation.getAddress());
        lunchTime = findViewById(R.id.time);
        lunchTime.setText(reservation.getDeliveryTime());
        notes = findViewById(R.id.notes);
        if(reservation.getNotes() != null)
            notes.setText(reservation.getNotes());
        if(reservation.getStatus() != 0)
            confirmButton.setVisibility(View.GONE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending").child(currentUser);
                DatabaseReference acceptedReservationRef = database.child("Company").child("Reservation").child("Accepted").child(currentUser);
                String orderID = reservation.getOrderID();
                reservation.setStatus(ReservationActivity.ACCEPTED_RESERVATION_CODE);
                pendingReservationRef.child(orderID).removeValue();
                acceptedReservationRef.child(orderID).setValue(reservation);

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(reservation.getStatus() == 0)
            getMenuInflater().inflate(R.menu.detailed_reservation_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.reject_order:
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending").child(currentUser);
                DatabaseReference historyReservationRef = database.child("Company").child("Reservation").child("History").child(currentUser);
                String orderID = reservation.getOrderID();
                reservation.setStatus(ReservationActivity.HISTORY_REJECT_RESERVATION_CODE);
                pendingReservationRef.child(orderID).removeValue();
                historyReservationRef.child(orderID).setValue(reservation);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

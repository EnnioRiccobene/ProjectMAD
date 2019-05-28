package com.madgroup.appcompany;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class DetailedReservation extends AppCompatActivity {

    private ImageView confirmButton;
    private TextView totalPrice;
    private TextView address;
    private TextView lunchTime;
    private TextView notes;
    private Reservation reservation;
    private RecyclerView recyclerView;
    private DetailedReservationDishesAdapter dAdapter;
    private String currentUser;
    private SharedPreferences prefs;

    String notificationTitle = "MAD Company";
    String notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reservation);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = prefs.getString("currentUser", "noUser");
        reservation = (Reservation) getIntent().getSerializableExtra("Reservation");
        ArrayList<OrderedDish> orderedFood = (ArrayList<OrderedDish>) getIntent().getSerializableExtra("OrderedFood");

        totalPrice = findViewById(R.id.totalPrice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        totalPrice.setText(reservation.getPrice());
        address = findViewById(R.id.address);
        address.setText(reservation.getAddress());
        lunchTime = findViewById(R.id.time);
        lunchTime.setText(reservation.getDeliveryTime());
        notes = findViewById(R.id.notes);
        if(reservation.getNotes() != null)
            notes.setText(reservation.getNotes());
        else
            findViewById(R.id.notes_bold).setVisibility(View.GONE);
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

        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }
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
                rejectOrder();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void rejectOrder() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> multipleAtomicQueries = new HashMap<>();
        String orderID = reservation.getOrderID();
        multipleAtomicQueries.put("Company/Reservation/Pending/" + currentUser + "/" + orderID, null);
        reservation.setStatus(ReservationActivity.HISTORY_REJECT_RESERVATION_CODE);
        multipleAtomicQueries.put("Company/Reservation/History/" + currentUser + "/" + orderID, reservation);
        // DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending").child(currentUser);
        // DatabaseReference historyReservationRef = database.child("Company").child("Reservation").child("History").child(currentUser);
        // pendingReservationRef.child(orderID).removeValue();
        // historyReservationRef.child(orderID).setValue(reservation);
        database.updateChildren(multipleAtomicQueries);
    }
}

package com.madgroup.appcompany;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailedReservation extends AppCompatActivity {


    private TextView totalPrice;
    private TextView address;
    private TextView lunchTime;
    private TextView notes;

    private RecyclerView recyclerView;
    private DetailedReservationDishesAdapter dAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reservation);
        Reservation reservation = (Reservation) getIntent().getSerializableExtra("Reservation");
        ArrayList<OrderedDish> orderedFood = (ArrayList<OrderedDish>) getIntent().getSerializableExtra("OrderedFood");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Order number " + reservation.getOrderID());

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
        totalPrice = findViewById(R.id.totalPrice);
        totalPrice.setText(reservation.getPrice() + " €");
        address = findViewById(R.id.address);
        address.setText(reservation.getAddress());
        lunchTime = findViewById(R.id.time);
        lunchTime.setText(reservation.getDeliveryTime());
        notes = findViewById(R.id.notes);
        if(reservation.getNotes() != null)
            notes.setText(reservation.getNotes());




//        confirm = findViewById(R.id.detailed_reservation_confirm_button);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent replyIntent = new Intent();
//                replyIntent.putExtra("Result", "Confirmed");
//                finish();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_reservation_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
//            case R.id.call_customer:
//
//                return true;
//            case R.id.reject_order:
//
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

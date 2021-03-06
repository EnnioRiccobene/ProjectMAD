package com.madgroup.madproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailedOrder extends AppCompatActivity {
    TextView subtotalPrice;
    TextView deliveryPrice;
    TextView totalPrice;
    TextView name;
    TextView address;
    TextView phone;
    TextView time;
    TextView notes;
    TextView confirm_button;
    RelativeLayout notEnoughCartLayout;
    RelativeLayout emptyCartLayout;
    NestedScrollView nestedScrollView2;

    private SharedPreferences prefs;

    private Reservation currentReservation;
    private ArrayList<OrderedDish> currentOrderedFood;

    public static void start(Context context, Reservation reservation, ArrayList<OrderedDish> orderedDishes) {
        Intent starter = new Intent(context, DetailedOrder.class);
        starter.putExtra("Reservation", reservation);
        starter.putExtra("OrderedFood", orderedDishes);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_order);

        // init Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        this.setTitle(R.string.order_recap);
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        subtotalPrice = findViewById(R.id.subtotalPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
        totalPrice = findViewById(R.id.totalPrice);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        time = findViewById(R.id.time);
        notes = findViewById(R.id.notes);
        confirm_button = findViewById(R.id.confirm_button);
        nestedScrollView2 = findViewById(R.id.nestedScrollView2);
        emptyCartLayout = findViewById(R.id.emptyCartLayout);
        notEnoughCartLayout = findViewById(R.id.notEnoughCartLayout);

        getIncomingIntent();

        String deliveryTime = currentReservation.getDeliveryTime();
        String note = currentReservation.getNotes();
        String deliveryCostAmount = currentReservation.getDeliveryCost();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ShoppingCartReservationAdapter adapter = new ShoppingCartReservationAdapter(this, currentOrderedFood);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String currency = " €";


        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        final String deliverCost = deliveryCostAmount.replace(",", ".").replace("€", "").replace("£", "").replace("$", "").replaceAll("\\s", "");
        float subtotal = Float.valueOf(currentReservation.getPrice().replace(" €", "").replace(",", ".")) - Float.valueOf(deliverCost);
        subtotalPrice.setText(df.format(subtotal).replace(".", ",") + currency);
        name.setText(prefs.getString("Name", ""));
        address.setText(prefs.getString("Address", ""));
        phone.setText(prefs.getString("Phone", ""));
        time.setText(deliveryTime);
        notes.setText(note);
        deliveryPrice.setText(df.format(Float.valueOf(deliverCost)) + currency);
        totalPrice.setText(currentReservation.getPrice());
        currentReservation.setPrice(totalPrice.getText().toString());
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("Reservation")) {
            currentReservation = (Reservation) getIntent().getSerializableExtra("Reservation");
            currentOrderedFood = (ArrayList<OrderedDish>) getIntent().getSerializableExtra("OrderedFood");
        }
    }
}

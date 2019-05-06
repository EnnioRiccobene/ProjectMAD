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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private String deliveryTime;
    private String note;
    private String deliveryCostAmount;
    private String minimumOrder;
    private String restaurantId;
    private ArrayList<OrderedDish> currentOrderedFood;

    public static void start(Context context, Reservation reservation, ArrayList<OrderedDish> orderedDishes) {
        Intent starter = new Intent(context, ShoppingCartActivity.class);
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
        this.setTitle("Order recap");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


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

        deliveryTime = currentReservation.getDeliveryTime();
        note = currentReservation.getNotes();
        deliveryCostAmount = currentReservation.getDeliveryCost();
        restaurantId = currentReservation.getRestaurantID();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ShoppingCartReservationAdapter adapter = new ShoppingCartReservationAdapter(this, currentOrderedFood);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Locale current = getResources().getConfiguration().locale;
        String currency = " €";
        if (current.equals("en_US")) {
            currency = " $";
        } else if (current.equals("en_GB")) {
            currency = " £";
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        subtotalPrice.setText(currentReservation.getPrice().replace(",", ".") + currency);
        name.setText(prefs.getString("Name", ""));
        address.setText(prefs.getString("Address", ""));
        phone.setText(prefs.getString("Phone", ""));
        time.setText(deliveryTime);
        notes.setText(note);
        final String deliverCost = deliveryCostAmount.replace(",", ".").replace("€", "").replace("£", "").replace("$", "").replaceAll("\\s", "");
        float total = Float.valueOf(deliverCost) + Float.valueOf(currentReservation.getPrice().replace(",", ".").replace("€", "").replace("£", "").replace("$", "").replaceAll("\\s", ""));
        deliveryPrice.setText(String.valueOf(df.format(Float.valueOf(deliverCost))) + currency);
        totalPrice.setText(String.valueOf(df.format(total)) + currency);
        currentReservation.setPrice(totalPrice.getText().toString());

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("Reservation")) {
            currentReservation = (Reservation) getIntent().getSerializableExtra("Reservation");
            currentOrderedFood = (ArrayList<OrderedDish>) getIntent().getSerializableExtra("OrderedFood");
        }
    }
}

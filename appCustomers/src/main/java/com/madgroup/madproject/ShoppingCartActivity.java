package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.SmartLogger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ShoppingCartActivity extends AppCompatActivity {

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

    public static void start(Context context, Reservation reservation, String delivery_cost_amount, String minimumOrder) {
        Intent starter = new Intent(context, ShoppingCartActivity.class);
        starter.putExtra("Reservation", reservation);
        starter.putExtra("DeliveryCost", delivery_cost_amount);
        starter.putExtra("MinimumOrder", minimumOrder);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);
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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ShoppingCartReservationAdapter adapter = new ShoppingCartReservationAdapter(this, currentReservation.getOrderedDishList());
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

        float minOrder = Float.valueOf(minimumOrder.replace("€", "").replace(",", ".").replace("£", "").replace("$", "").replaceAll("\\s", ""));

        //setto la visibilità del layout corretto a seconda dello stato del carrello
        if (currentReservation.getOrderedDishList().isEmpty()) {
            emptyCartLayout.setVisibility(View.VISIBLE);
            notEnoughCartLayout.setVisibility(View.GONE);
            nestedScrollView2.setVisibility(View.GONE);
        } else {
            if (minOrder > total) {
                emptyCartLayout.setVisibility(View.GONE);
                notEnoughCartLayout.setVisibility(View.VISIBLE);
                nestedScrollView2.setVisibility(View.GONE);
            } else {
                emptyCartLayout.setVisibility(View.GONE);
                notEnoughCartLayout.setVisibility(View.GONE);
                nestedScrollView2.setVisibility(View.VISIBLE);
            }
        }

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: conferma ordine e invia l'oggetto corrispondente al db (e notifica). Fare eventuale email di conferma ordine al cliente e cambiare activity
                // Creare oggetto Reservation e caricarlo sia sul restaurant (pending) sia nel customer (pending)

                String userID = prefs.getString("currentUser", "Error");
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                final String orderID = database.child("Company").child("Reservation").child("Pending").child(restaurantId).push().getKey();
                // Customer Reference
                final DatabaseReference pendingCustomerRef = database.child("Customer").child("Order").child("Pending").child(userID).child(orderID);
                // Company References
                final DatabaseReference pendingRestaurantRef = database.child("Company").child("Reservation").child("Pending").child(restaurantId).child(orderID);
                final DatabaseReference orderedFoodRef = database.child("Company").child("Reservation").child("OrderedFood").child(restaurantId).child(orderID);
                final DatabaseReference menuRef = database.child("Company").child("Menu").child(restaurantId);

                // Verificare che la quantità richiesta sia disponibile e ridurla
                menuRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        ArrayList<OrderedDish> orderedDishes = currentReservation.getOrderedDishList();
                        for (final OrderedDish orderedDish : orderedDishes) {
                            String availableQuantity = (String) mutableData.child(orderedDish.getId()).child("availableQuantity").getValue();
                            Integer remainQuantity = Integer.parseInt(availableQuantity) - Integer.parseInt(orderedDish.getQuantity());
                            if (remainQuantity  < 0)
                                Transaction.abort();
                            mutableData.child(orderedDish.getId()).child("availableQuantity").setValue(String.valueOf(remainQuantity));
                        }

                        currentReservation.setOrderID(orderID);
                        currentReservation.setDeliveryCost(deliverCost);
                        orderedFoodRef.setValue(currentReservation.getOrderedDishList());
                        currentReservation.setOrderedDishList(null);
                        pendingCustomerRef.setValue(currentReservation);
                        pendingRestaurantRef.setValue(currentReservation);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Intent homepage = new Intent(ShoppingCartActivity.this, ProfileActivity.class);
                        startActivity(homepage);
                    }
                });
            }
        });
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("Reservation")) {
            currentReservation = (Reservation) getIntent().getSerializableExtra("Reservation");
            deliveryTime = currentReservation.getDeliveryTime();
            note = currentReservation.getNotes();
            restaurantId = currentReservation.getRestaurantID();
            deliveryCostAmount = getIntent().getStringExtra("DeliveryCost");
            minimumOrder = getIntent().getStringExtra("MinimumOrder");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}

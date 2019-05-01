package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.Locale;

public class ShoppingCartActivity extends AppCompatActivity {

    TextView subtotalPrice;
    TextView deliveryPrice;
    TextView totalPrice;
    TextView address;
    TextView time;
    TextView notes;
    TextView confirm_button;
    RelativeLayout notEnoughCartLayout;
    RelativeLayout emptyCartLayout;
    NestedScrollView nestedScrollView2;

    private Reservation currentReservation;
    private String deliveryTime;
    private String note;
    private String deliveryCostAmount;
    private String minimumOrder;

    public static void start(Context context, Reservation reservation, String deliveryTime, String notes, String delivery_cost_amount, String minimumOrder){
        Intent starter = new Intent(context, ShoppingCartActivity.class);
        starter.putExtra("Reservation", reservation);
        starter.putExtra("DeliveryTime", deliveryTime);
        starter.putExtra("Notes", notes);
        starter.putExtra("DeliveryCost", delivery_cost_amount);
        starter.putExtra("MinimumOrder", minimumOrder);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);

        subtotalPrice = findViewById(R.id.subtotalPrice);
        deliveryPrice = findViewById(R.id.deliveryPrice);
        totalPrice = findViewById(R.id.totalPrice);
        address = findViewById(R.id.address);
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
        if(current.equals("en_US")){
            currency = " $";
        } else if(current.equals("en_GB")){
            currency = " £";
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);

        subtotalPrice.setText(currentReservation.getPrice().replace(",", ".") + currency);
        address.setText(currentReservation.getAddress());
        time.setText(deliveryTime);
        notes.setText(note);
        String deliverCost = deliveryCostAmount.replace(",", ".").replace("€", "").replace("£", "").replace("$", "").replaceAll("\\s","");
        float total = Float.valueOf(deliverCost) + Float.valueOf(currentReservation.getPrice().replace(",", ".").replace("€", "").replace("£", "").replace("$", "").replaceAll("\\s",""));
        deliveryPrice.setText(String.valueOf(df.format(Float.valueOf(deliverCost)))+ currency);
        totalPrice.setText(String.valueOf(df.format(total))+ currency);

        float minOrder = Float.valueOf(minimumOrder.replace("€", "").replace(",", ".").replace("£", "").replace("$", "").replaceAll("\\s",""));

        //setto la visibilità del layout corretto a seconda dello stato del carrello
        if(currentReservation.getOrderedDishList().isEmpty()){
            emptyCartLayout.setVisibility(View.VISIBLE);
            notEnoughCartLayout.setVisibility(View.GONE);
            nestedScrollView2.setVisibility(View.GONE);
        } else {
            if(minOrder > total){
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
            }
        });
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("Reservation")){
            currentReservation = (Reservation) getIntent().getSerializableExtra("Reservation");
            deliveryTime = getIntent().getStringExtra("DeliveryTime");
            note = getIntent().getStringExtra("Notes");
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

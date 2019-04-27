package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingCartActivity extends AppCompatActivity {

    TextView subtotalPrice;
    TextView deliveryPrice;
    TextView totalPrice;
    TextView address;
    TextView time;
    TextView notes;
    TextView confirm_button;

    private Reservation currentReservation;
    private String deliveryTime;
    private String note;

    public static void start(Context context, Reservation reservation, String deliveryTime, String notes){
        Intent starter = new Intent(context, ShoppingCartActivity.class);
        starter.putExtra("Reservation", reservation);
        starter.putExtra("DeliveryTime", deliveryTime);
        starter.putExtra("Notes", notes);
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

        getIncomingIntent();

        //todo: se l'arraylist della recycle view è vuoto mostrare solo la scritta carrello vuoto

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ShoppingCartReservationAdapter adapter = new ShoppingCartReservationAdapter(this, currentReservation.getOrderedDishList());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        subtotalPrice.setText(currentReservation.getPrice()); //todo: castare la stringa con il simbolo della valuta come fatto nell'adapter. Mancano da rendere dinamici il delivery cost e il prezzo totale
        address.setText(currentReservation.getAddress());
        time.setText(deliveryTime);
        notes.setText(note);

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
        }
    }
}

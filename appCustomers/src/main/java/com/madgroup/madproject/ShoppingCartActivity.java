package com.madgroup.madproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShoppingCartActivity extends AppCompatActivity {

    TextView subtotalPrice;
    TextView deliveryPrice;
    TextView totalPrice;
    TextView address;
    TextView time;
    TextView notes;
    TextView confirm_button;

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

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: conferma ordine e invia l'oggetto corrispondente al db (e notifica)
            }
        });
    }
}

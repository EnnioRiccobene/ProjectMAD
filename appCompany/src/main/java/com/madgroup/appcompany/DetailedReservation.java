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

    TextView dishesText;
    private RecyclerView recyclerView;
    private ArrayList<Dish> dishes;
    private DetailedReservationDishesAdapter dAdapter;
    private TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_reservation);

        totalPrice = findViewById(R.id.totalPrice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle("Order number 123");

        // ARRAY DI PROVA
        dishes = new ArrayList<>();
        dishes.add(new Dish(0, "Pollo al curry", 34, 30));
        dishes.add(new Dish(1, "Carbonara", 34, 2));
        dishes.add(new Dish(2, "Cacio e pepe", 34, 35));
        dishes.add(new Dish(3, "Insalta", 34, 10));
        dishes.add(new Dish(4, "Polpette", 34, 60));

        // Compute Total price

        float localPrice = 0;
        for (Dish x:dishes)
            localPrice += x.getPrice() * x.getAvailableQuantity();
        totalPrice.setText(String.valueOf(localPrice) + " €");

        // RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        dAdapter = new DetailedReservationDishesAdapter(dishes);
        dishesText = findViewById(R.id.dishesText);
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
            case R.id.call_customer:

                return true;
            case R.id.reject_order:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

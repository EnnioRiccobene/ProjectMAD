package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays the Target screen
 * Required extras:
 * - Restaurant: Restaurant
*/
public class RestaurantMenuActivity extends AppCompatActivity {

    private Restaurant restaurant;
    ArrayList<Dish> menu = new ArrayList<>();

    public static void start(Context context, int restaurantId){
        Intent starter = new Intent(context, RestaurantMenuActivity.class);
        starter.putExtra("Restaurant", restaurantId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("Restaurant")){
            restaurant.setId(getIntent().getIntExtra("Restaurant", 0));
            //todo: una volta ottenuto l'id del ristorante tramite intent, fare una query al database per ottenere i campi del ristorante con quell'id (photo, Name, foodcategories, orari di apertura, ordine minimo e costo consegna)
            //todo: poi fare un'altra queri al db per ottenere tutti i piatti del ristorante con quell'id e riempire l'ArrayList di Dish per la recycleview
        }
    }
}

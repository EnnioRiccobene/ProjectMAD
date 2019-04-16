package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.madgroup.sdk.SmartLogger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays the Target screen
 * Required extras:
 * - Restaurant: Restaurant
*/
public class RestaurantMenuActivity extends AppCompatActivity {

    private Restaurant restaurant;

    public static void start(Context context, Restaurant restaurant){
        Intent starter = new Intent(context, RestaurantMenuActivity.class);
        starter.putExtra("Restaurant", restaurant);
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
            restaurant = getIntent().getParcelableExtra("Restaurant");
            SmartLogger.d("intent_ristorante", restaurant.getName().toString());
        }
    }
}

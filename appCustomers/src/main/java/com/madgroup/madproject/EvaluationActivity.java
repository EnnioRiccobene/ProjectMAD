package com.madgroup.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.madgroup.sdk.Reservation;

public class EvaluationActivity extends AppCompatActivity {

    RatingBar restaurantBar;
    RatingBar foodBar;
    RatingBar serviceBar;
    FloatingActionButton sendBtn;

    public static void start(Context context, String orderId, String restaurantId, String customerId, String bikerId) {
        Intent starter = new Intent(context, EvaluationActivity.class);
        starter.putExtra("OrderId", orderId);
        starter.putExtra("RestaurantId", restaurantId);
        starter.putExtra("CustomerId", customerId);
        starter.putExtra("RiderId", bikerId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        setTitle(R.string.RatingTitle);
        restaurantBar = findViewById(R.id.ratingRestaurant);
        foodBar = findViewById(R.id.ratingFood);
        serviceBar = findViewById(R.id.ratingService);
        sendBtn = findViewById(R.id.sendReview);

    }
}

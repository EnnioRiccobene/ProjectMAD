package com.madgroup.madproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.Reservation;
import com.madgroup.sdk.SmartLogger;

public class EvaluationActivity extends AppCompatActivity {

    TextView hiddenMessage;
    RatingBar restaurantBar;
    RatingBar foodBar;
    RatingBar serviceBar;
    TextInputEditText reviewEditText;
    FloatingActionButton sendBtn;

    private String orderId;
    private String restaurantId;
    private String customerId;
    private String riderId;

    private int restaurantRating = 0;
    private int foodRating = 0;
    private int serviceRating = 0;
    private String restaurantReview = "";

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

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

        getIncomingIntent();

        this.setTitle(R.string.RatingTitle);
        hiddenMessage = findViewById(R.id.textViewHiddenMessage);
        restaurantBar = findViewById(R.id.ratingRestaurant);
        foodBar = findViewById(R.id.ratingFood);
        serviceBar = findViewById(R.id.ratingService);
        reviewEditText = findViewById(R.id.review_edit_text);
        sendBtn = findViewById(R.id.sendReview);


        rootRef.child("Company").child("Rating").child(restaurantId).child(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SmartLogger.d("Il nodo esiste");
                    hiddenMessage.setVisibility(View.VISIBLE);
                    restaurantBar.setIsIndicator(true);
                    foodBar.setIsIndicator(true);
                    reviewEditText.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                SmartLogger.d("Il nodo non esiste");
            }
        });
        /*todo: se ristorante già valutato:
        restaurantBar.setIsIndicator(true);
        foodBar.setIsIndicator(true);
        reviewEditText.setEnabled(false); e setta il suo contenuto dal db*/
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("OrderId")) {
            orderId = getIntent().getStringExtra("OrderId");
            restaurantId = getIntent().getStringExtra("RestaurantId");
            customerId = getIntent().getStringExtra("CustomerId");
            riderId = getIntent().getStringExtra("RiderId");
        }
    }
}

package com.madgroup.madproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    DatabaseReference companyRatingRef = rootRef.child("Company").child("Rating").child(restaurantId).child(customerId);
    DatabaseReference riderProfileRef = rootRef.child("Rider").child("Profile").child(riderId);
    DatabaseReference companyProfileRef = rootRef.child("Company").child("Profile").child(restaurantId);

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

        restaurantRating = handleStarRating(restaurantBar);
        foodRating = handleStarRating(foodBar);
        serviceRating = handleStarRating(serviceBar);

        checkPreviousEvaluation();


    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("OrderId")) {
            orderId = getIntent().getStringExtra("OrderId");
            restaurantId = getIntent().getStringExtra("RestaurantId");
            customerId = getIntent().getStringExtra("CustomerId");
            riderId = getIntent().getStringExtra("RiderId");
        }
    }

    private void checkPreviousEvaluation(){

        companyRatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SmartLogger.d("Il nodo esiste");
                    restaurantReview = (String) snapshot.child("comment").getValue();
                    restaurantRating = (int) snapshot.child("restaurantRating").getValue();
                    foodRating = (int) snapshot.child("foodRating").getValue();
                    reviewEditText.setText(restaurantReview);
                    restaurantBar.setRating(restaurantRating);
                    foodBar.setRating(foodRating);
                    hiddenMessage.setVisibility(View.VISIBLE);
                    restaurantBar.setIsIndicator(true);
                    foodBar.setIsIndicator(true);
                    reviewEditText.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int handleStarRating(RatingBar mRatingBar){
        final int[] vote = {0};
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                switch ((int) ratingBar.getRating()) {
                    case 1:
                        vote[0] = 1;
                        break;
                    case 2:
                        vote[0] = 2;
                        break;
                    case 3:
                        vote[0] = 3;
                        break;
                    case 4:
                        vote[0] = 4;
                        break;
                    case 5:
                        vote[0] = 5;
                        break;
                    default:
                        vote[0] = 0;
                }
            }
        });
        return vote[0];
    }

    public void sendEvaluation(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restaurantRating == 0 || foodRating == 0 || serviceRating == 0){
                    Toast.makeText(EvaluationActivity.this, getString(R.string.mandatory_evaluate), Toast.LENGTH_SHORT).show();
                } else {
                    //todo: inserisci/aggiorna i valori nel db nelle app Rider, Company  profile e Company rating(nelle prime due con transaction)
                    //Inserisco la valutazione in Company
                    companyProfileRef.child("customerId").setValue(customerId);
                    companyProfileRef.child("orderId").setValue(orderId);
                    companyProfileRef.child("restaurantRating").setValue(String.valueOf(restaurantRating));
                    companyProfileRef.child("foodRating").setValue(String.valueOf(foodRating));
                    companyProfileRef.child("comment").setValue(reviewEditText.getText().toString());

                    //Update restaurant profile
                    //todo: impedire aggiornamento dei primi due dati se ho già recensito il ristorante. (non eseguire questa transazione)
                    companyProfileRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            String ratingCounter = mutableData.child("ratingCounter").getValue(String.class);
                            String ratingAvg = mutableData.child("ratingAvg").getValue(String.class);
                            String foodRatingAvg = mutableData.child("foodRatingAvg").getValue(String.class);

                            if(ratingCounter.equals("0")){
                                mutableData.child("ratingCounter").setValue("1");
                            } else {
                                int a = Integer.valueOf(ratingCounter) + 1;
                                mutableData.child("ratingCounter").setValue(String.valueOf(a));
                            }

                            if(ratingAvg.equals("0")){
                                mutableData.child("ratingAvg").setValue(String.valueOf(restaurantRating));
                            } else {
                                float a = (Float.valueOf(ratingAvg) + restaurantRating) / (Integer.valueOf(ratingCounter) + 1);
                                mutableData.child("ratingAvg").setValue(String.valueOf(a));
                            }

                            if(foodRatingAvg.equals("0")){
                                mutableData.child("foodRatingAvg").setValue(String.valueOf(foodRating));
                            } else {
                                float a = (Float.valueOf(ratingAvg) + foodRating) / (Integer.valueOf(ratingCounter) + 1);
                                mutableData.child("foodRatingAvg").setValue(String.valueOf(a));
                            }

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });

                    //Update Rider profile
                    riderProfileRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            String ratingCounter = mutableData.child("ratingCounter").getValue(String.class);
                            String ratingAvg = mutableData.child("ratingAvg").getValue(String.class);

                            if(ratingCounter.equals("0")){
                                mutableData.child("ratingCounter").setValue("1");
                            } else {
                                int a = Integer.valueOf(ratingCounter) + 1;
                                mutableData.child("ratingCounter").setValue(String.valueOf(a));
                            }

                            if(ratingAvg.equals("0")){
                                mutableData.child("ratingAvg").setValue(String.valueOf(restaurantRating));
                            } else {
                                float a = (Float.valueOf(ratingAvg) + restaurantRating) / (Integer.valueOf(ratingCounter) + 1);
                                mutableData.child("ratingAvg").setValue(String.valueOf(a));
                            }

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                }
            }
        });
    }
}

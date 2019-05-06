package com.madgroup.madproject;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Displays the Target screen
 * Required extras:
 * - Restaurant: Restaurant
 */
public class RestaurantMenuActivity extends AppCompatActivity {

    private Restaurant restaurant;
    ArrayList<Dish> menu = new ArrayList<>();
    private ArrayList<OrderedDish> orderedDishes = new ArrayList<>();
    private String address;
    private String notes = "";
    private String deliveryTime;
    private String delivery_cost_amount;
    private String minimumOrder;

    CircleImageView restaurantPhoto;
    TextView restaurantName;
    TextView foodCategories;
    TextView minimumOrderAmount;
    TextView deliveryCostAmount;
    ImageView arrowbtn;
    ExpandableLayout hiddenHours;
    TextView mondayHours;
    TextView tuesdayHours;
    TextView wednesdayHours;
    TextView thursdayHours;
    TextView fridayHours;
    TextView saturdayHours;
    TextView sundayHours;
    private DatabaseReference restaurantRef;
    private DatabaseReference dishRef;
    private String restaurantID;
    FirebaseRecyclerOptions<Dish> options;

    public static void start(Context context, String restaurantId) {
        Intent starter = new Intent(context, RestaurantMenuActivity.class);
        starter.putExtra("Restaurant", restaurantId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);
        // init Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        this.setTitle("Place an order!");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        restaurantPhoto = findViewById(R.id.restaurant_photo);
        restaurantName = findViewById(R.id.restaurant_name);
        foodCategories = findViewById(R.id.food_category);
        minimumOrderAmount = findViewById(R.id.minimum_order_amount);
        deliveryCostAmount = findViewById(R.id.delivery_cost_amount);
        hiddenHours = findViewById(R.id.hiddenhours);
        arrowbtn = findViewById(R.id.arrowbtn);
        mondayHours = findViewById(R.id.mondayhour);
        tuesdayHours = findViewById(R.id.tuesdayhour);
        wednesdayHours = findViewById(R.id.wednesdayhour);
        thursdayHours = findViewById(R.id.thursdayhour);
        fridayHours = findViewById(R.id.fridayhour);
        saturdayHours = findViewById(R.id.saturdayhour);
        sundayHours = findViewById(R.id.sundayhour);

        //Mi assicuro che l'Expandable Layout sia chiuso all'apertura dell'app
        if (!hiddenHours.isExpanded()) {
            hiddenHours.collapse();
        }

        getIncomingIntent();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantRef = database.getReference().child("Profiles").child("Restaurants").child(restaurantID);
        dishRef = database.getReference().child("Company").child("Menu").child(restaurantID);

        SharedPreferences prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        address = prefs.getString("Address", "No address defined");

        initRecicleView();

        //todo: questo elemento è stato spostato nel'appbar per questo ora crasha. Dovrebbe averlo sistemato Simone
//        imageButtonCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog();
//            }
//        });

        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Restaurant currentRestaurant = dataSnapshot.getValue(Restaurant.class);
//                restaurantPhoto
                restaurantName.setText(currentRestaurant.getName());
                foodCategories.setText(currentRestaurant.getFoodCategory());
                minimumOrderAmount.setText(currentRestaurant.getMinOrder());
                deliveryCostAmount.setText(currentRestaurant.getDeliveryCost());
                mondayHours.setText(currentRestaurant.getMondayOpeningHours());
                tuesdayHours.setText(currentRestaurant.getTuesdayOpeningHours());
                wednesdayHours.setText(currentRestaurant.getWednesdayOpeningHours());
                thursdayHours.setText(currentRestaurant.getThursdayOpeningHours());
                fridayHours.setText(currentRestaurant.getFridayOpeningHours());
                saturdayHours.setText(currentRestaurant.getSaturdayOpeningHours());
                sundayHours.setText(currentRestaurant.getSundayOpeningHours());

                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/projectmad-18b01.appspot.com/o/uploads%2Fdish.png?alt=media&token=37431f23-81a7-4d57-8686-4b93640b29ad");

//                GlideApp.with(RestaurantMenuActivity.this).load(storageReference).into(restaurantPhoto);

                final long ONE_MEGABYTE = 1024 * 1024;
                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        restaurantPhoto.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("Restaurant")) {
            restaurantID = getIntent().getStringExtra("Restaurant");
//            restaurant.setId(restaurantID);//l'oggetto restaurant non è stato costruito e non ho gli attributi per farlo oltre all'id
            //todo: una volta ottenuto l'id del ristorante tramite intent, fare una query al database per ottenere i campi del ristorante con quell'id (photo, Name, foodcategories, orari di apertura, ordine minimo e costo consegna)
            //todo: poi fare un'altra query al db per ottenere tutti i piatti del ristorante con quell'id e riempire l'ArrayList di Dish per la recycleview
        }
    }

    private void initRecicleView() {
        //todo temporanea, poi prendere dal db e usare il costruttore che mette anche le foto'
//        menu.add(new Dish(1, "Margherita", 5, 30));
//        menu.add(new Dish(1, "Capricciosa", 7.5f, 30));
//        menu.add(new Dish(1, "Quattro salumi", 7, 30));
//        menu.add(new Dish(1, "Quattro formaggi", 8, 30));
//        menu.add(new Dish(1, "Parmiggiana", 7.5f, 30));
//        menu.add(new Dish(1, "Prosciutto", 6, 30));
//        menu.add(new Dish(1, "Burrata", 10, 30));

        options = new FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(dishRef, Dish.class)
                .build();

        RecyclerView recyclerView = findViewById(R.id.menu_recycleView);
//        RecycleViewMenuAdapter adapter = new RecycleViewMenuAdapter(this, menu, orderedDishes);
        RecycleViewMenuAdapter adapter = new RecycleViewMenuAdapter(options, dishRef, RestaurantMenuActivity.this, orderedDishes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.startListening();
    }

    public void showHoursDetails(View view) {
        if (hiddenHours.isExpanded()) {
            createRotateAnimator(arrowbtn, 180f, 0f).start();
        } else {
            createRotateAnimator(arrowbtn, 0f, 180f).start();
        }
        hiddenHours.toggle();
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void showDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.reservation_details_dialog);

        final EditText reservationNotes = (EditText) dialog.findViewById(R.id.reservation_notes);
        final Spinner reservationDeliveryHours = (Spinner) dialog.findViewById(R.id.reservationDeliveryHours);
        TextView dialogDismiss = (TextView) dialog.findViewById(R.id.back_button);
        TextView dialogConfirm = (TextView) dialog.findViewById(R.id.confirm_button);

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deliveryTime = reservationDeliveryHours.getSelectedItem().toString();
                notes = reservationNotes.getText().toString();
                delivery_cost_amount = deliveryCostAmount.getText().toString();
                minimumOrder = minimumOrderAmount.getText().toString();
                Reservation currentReservation = new Reservation(orderedDishes, address, deliveryTime, notes, restaurantID);
                ShoppingCartActivity.start(RestaurantMenuActivity.this, currentReservation, delivery_cost_amount, minimumOrder);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shoppingCart:
                showDialog();
                break;
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        return true;
    }
}
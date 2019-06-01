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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.Reservation;
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
    ImageView arrowbtnHours, arrowbtnMenu, arrowbtnFavorite;
    ExpandableLayout hiddenHours;
    net.cachapa.expandablelayout.ExpandableLayout hiddenFavorite, hiddenMenu;
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
    private RecycleViewMenuAdapter adapter;

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
        hiddenMenu = findViewById(R.id.hiddenMenu);
        hiddenFavorite = findViewById(R.id.hiddenFavorite);
        arrowbtnHours = findViewById(R.id.arrowbtn_hour);
        arrowbtnMenu = findViewById(R.id.arrowbtn_menu);
        arrowbtnFavorite = findViewById(R.id.arrowbtn_favorite);
        mondayHours = findViewById(R.id.mondayhour);
        tuesdayHours = findViewById(R.id.tuesdayhour);
        wednesdayHours = findViewById(R.id.wednesdayhour);
        thursdayHours = findViewById(R.id.thursdayhour);
        fridayHours = findViewById(R.id.fridayhour);
        saturdayHours = findViewById(R.id.saturdayhour);
        sundayHours = findViewById(R.id.sundayhour);

        //Mi assicuro che l'Expandable Layout sia chiuso all'apertura dell'app
        if (!hiddenHours.isExpanded())
            hiddenHours.collapse();
        getIncomingIntent();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantRef = database.getReference().child("Company").child("Profile").child(restaurantID);
        dishRef = database.getReference().child("Company").child("Menu").child(restaurantID);
        SharedPreferences prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        address = prefs.getString("Address", "No address defined");
        loadRestaurantPhoto();
        initRecyclerView();
        loadRestaurantInformation();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("Restaurant")) {
            restaurantID = getIntent().getStringExtra("Restaurant");
        }
    }

    private void initRecyclerView() {
        // Menu Recycler View
        options = new FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(dishRef, Dish.class)
                .build();

        RecyclerView menuRecycler = findViewById(R.id.menu_recycleView);
//        RecycleViewMenuAdapter adapter = new RecycleViewMenuAdapter(this, menu, orderedDishes);
        adapter = new RecycleViewMenuAdapter(options, dishRef, RestaurantMenuActivity.this, orderedDishes, restaurantID);
        menuRecycler.setAdapter(adapter);
        menuRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();

        // Favorites Recycler View
        final ArrayList<Dish> topRatedDish = new ArrayList<>();
        Query query = dishRef.orderByChild("orderedQuantityTot");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;
                int i = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(i == 3)
                        break;
                    Dish currentDish = postSnapshot.getValue(Dish.class);
                    topRatedDish.add(currentDish);
                    i++;
                }
                RecyclerView favoriteRecycler = (RecyclerView) findViewById(R.id.favorite_recycleView);
                FavoriteTopMealAdapter adapter = new FavoriteTopMealAdapter(getApplicationContext(), topRatedDish, restaurantID);
                favoriteRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                favoriteRecycler.setItemAnimator(new DefaultItemAnimator());
                favoriteRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void showHoursDetails(View view) {
        if (hiddenHours.isExpanded())
            createRotateAnimator(arrowbtnHours, 180f, 0f).start();
        else
            createRotateAnimator(arrowbtnHours, 0f, 180f).start();
        hiddenHours.toggle();
    }

    public void showMenu(View view) {
        if (hiddenMenu.isExpanded())
            createRotateAnimator(arrowbtnMenu, 180f, 0f).start();
        else
            createRotateAnimator(arrowbtnMenu, 0f, 180f).start();
        hiddenMenu.toggle();
    }

    public void showFavorite(View view) {
        if (hiddenFavorite.isExpanded())
            createRotateAnimator(arrowbtnFavorite, 180f, 0f).start();
        else
            createRotateAnimator(arrowbtnFavorite, 0f, 180f).start();
        hiddenFavorite.toggle();
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
                String currentCustomer = getSharedPreferences("MyData", MODE_PRIVATE).getString("currentUser", "noUser");
                Reservation currentReservation = new Reservation(restaurantName.getText().toString(), orderedDishes, address, deliveryTime, notes, restaurantID, delivery_cost_amount);
                currentReservation.setCustomerID(currentCustomer);
                ShoppingCartActivity.start(RestaurantMenuActivity.this, currentReservation, minimumOrder);
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

    private void loadRestaurantInformation() {
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

    private void loadRestaurantPhoto() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(restaurantID);
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(restaurantPhoto);
    }
}
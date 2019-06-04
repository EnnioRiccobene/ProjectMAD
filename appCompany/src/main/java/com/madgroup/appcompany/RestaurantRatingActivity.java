package com.madgroup.appcompany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantRatingActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseDatabase database;
    private String currentUser;
    private AppCompatRatingBar averageRestaurantRatingStars;
    private AppCompatRatingBar averageFoodRatingStars;
    private TextView averageFoodRatingNumber;
    private TextView averageRestaurantRatingNumber;
    private TextView reviewNumber;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private NavigationView navigationView;
    private SharedPreferences.Editor editor;
    String notificationTitle = "MADelivery";
    String notificationText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_restaurant_rating);
        stub.inflate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        currentUser = prefs.getString("currentUser", "");
        if (currentUser.equals(""))
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.setTitle(R.string.restaurantRatingActivity);
        navigationDrawerInitialization();

        averageRestaurantRatingStars = findViewById(R.id.averageRestaurantRatingStars);
        averageRestaurantRatingNumber = findViewById(R.id.averageRestaurantRatingNumber);
        averageFoodRatingStars = findViewById(R.id.averageFoodRatingStars);
        averageFoodRatingNumber = findViewById(R.id.averageFoodRatingNumber);
        reviewNumber = findViewById(R.id.reviewNumber);
        progressBar = findViewById(R.id.progressBar);

        // Load rating information
        database = FirebaseDatabase.getInstance();
        DatabaseReference profileRef = database.getReference().child("Company").child("Profile").child(currentUser);
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;
                if(dataSnapshot.child("ratingAvg").getValue() == null ||
                        dataSnapshot.child("ratingCounter").getValue() == null ||
                        dataSnapshot.child("foodRatingAvg").getValue() == null)
                    return;
                String ratingAvg = dataSnapshot.child("ratingAvg").getValue(String.class);
                String ratingCounter = dataSnapshot.child("ratingCounter").getValue(String.class);
                String foodRatingAvg = dataSnapshot.child("foodRatingAvg").getValue(String.class);

                averageFoodRatingStars.setRating(Float.parseFloat(foodRatingAvg));
                averageRestaurantRatingStars.setRating(Float.parseFloat(ratingAvg));
                averageFoodRatingNumber.setText(translateFoodRating(foodRatingAvg));
                averageRestaurantRatingNumber.setText(ratingAvg);
                reviewNumber.setText(ratingCounter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {

            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }

        initRecyclerView();
    }

    private void navigationDrawerInitialization() {
// Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(        // Three lines icon on the left corner
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the photo of the Navigation Bar Icon (Need to be completed: refresh when new image is updated)
//        ImageView nav_profile_icon = (ImageView) findViewById(R.id.nav_profile_icon);
//        nav_profile_icon.setImageDrawable(personalImage.getDrawable());

        // Set restaurant name and email on navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String username = prefs.getString("Name", "");
        navUsername.setText(username);
        String email = prefs.getString("Email", "");
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        updateNavigatorPersonalIcon();
    }

    public void updateNavigatorPersonalIcon() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);
    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            Intent myIntent = new Intent(this, DailyOfferActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_reservations) {
            Intent myIntent = new Intent(this, ReservationActivity.class);
            this.startActivity(myIntent);
        } if (id == R.id.nav_analytics) {
            Intent myIntent = new Intent(this, AnalyticsActivity.class);
            this.startActivity(myIntent);
        } else if(id == R.id.nav_reviews){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_logout) {
            startLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void startLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        editor.clear();
                        editor.apply();
                        //startLogin();
                        Intent intent = new Intent(RestaurantRatingActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }


    private void initRecyclerView() {

        database = FirebaseDatabase.getInstance();
        DatabaseReference reviewRef = database.getReference().child("Company").child("Rating").child(currentUser);
        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(reviewRef, Review.class)
                .build();

        final FirebaseRecyclerAdapter<Review, ReviewViewHolder> adapter =
                new FirebaseRecyclerAdapter<Review, ReviewViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int i, @NonNull final Review currentItem) {
                        final int index = i;
                        holder.customerName.setText(currentItem.getCustomerName());
                        holder.foodRatingStar.setRating(Float.parseFloat(currentItem.getFoodRating()));
                        holder.restaurantRatingStar.setRating(Float.parseFloat(currentItem.getRestaurantRating()));
                        if(!currentItem.getComment().equals(""))
                            holder.comment.setText("\"" + currentItem.getComment() + "\"");
                        else
                            holder.comment.setVisibility(View.GONE);
                    }

                    @NonNull
                    @Override
                    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
                        ReviewViewHolder evh = new ReviewViewHolder(v);
                        return evh;
                    }
                };

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mImageView;
        private AppCompatRatingBar foodRatingStar;
        private AppCompatRatingBar restaurantRatingStar;
        private TextView comment;
        private TextView customerName;
        View mView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            // mImageView = itemView.findViewById(R.id.customerPhoto);
            foodRatingStar = itemView.findViewById(R.id.foodRatingStar);
            restaurantRatingStar = itemView.findViewById(R.id.restaurantRatingStars);
            comment = itemView.findViewById(R.id.comment);
            customerName = itemView.findViewById(R.id.customerName);
        }

    }
    private String translateFoodRating(String foodRatingAvg) {
        String restaurantPrice = "";
        String[] removeDecimal = foodRatingAvg.split("\\.");
        foodRatingAvg = removeDecimal[0];
        switch (Integer.parseInt(foodRatingAvg)){
            case 1:
                restaurantPrice = "€";
                break;
            case 2:
                restaurantPrice = "€€";
                break;
            case 3:
                restaurantPrice = "€€€";
                break;
            default:
                break;
        }
        return restaurantPrice;
    }
}

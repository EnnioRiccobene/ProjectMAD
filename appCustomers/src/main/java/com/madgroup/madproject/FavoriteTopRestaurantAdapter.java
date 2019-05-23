package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.OrderedDish;
import com.madgroup.sdk.SmartLogger;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class FavoriteTopRestaurantAdapter extends
        RecyclerView.Adapter<FavoriteTopRestaurantAdapter.ViewHolder> {
    private ArrayList<Restaurant> topRestaurants;
    private SharedPreferences prefs;
    private String currentUser;
    private Context context;

    public FavoriteTopRestaurantAdapter(Context context, ArrayList<Restaurant> topRestaurants) {
        this.topRestaurants = topRestaurants;
        this.context = context;
        prefs = context.getSharedPreferences("MyData", MODE_PRIVATE);
        currentUser = prefs.getString("currentUser", "noUser");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardLayout;
        RelativeLayout restaurant_item_layout;
        CircleImageView restaurant_photo;
        RelativeLayout name_button_layout;
        TextView restaurant_name;
        TextView food_category;
        TextView minimum_order;
        TextView minimum_order_amount;
        TextView delivery_cost;
        TextView delivery_cost_amount;
        CheckBox favoriteCheckBox;
        AppCompatRatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.cardLayout);
            restaurant_item_layout = itemView.findViewById(R.id.restaurant_item_layout);
            restaurant_photo = itemView.findViewById(R.id.restaurant_photo);
            name_button_layout = itemView.findViewById(R.id.name_button_layout);
            restaurant_name = itemView.findViewById(R.id.restaurant_name);
            food_category = itemView.findViewById(R.id.food_category);
            minimum_order = itemView.findViewById(R.id.minimum_order);
            minimum_order_amount = itemView.findViewById(R.id.minimum_order_amount);
            delivery_cost = itemView.findViewById(R.id.delivery_cost);
            delivery_cost_amount = itemView.findViewById(R.id.delivery_cost_amount);
            favoriteCheckBox = itemView.findViewById(R.id.favoriteCheckBox);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public int getItemCount() {
        return topRestaurants.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Restaurant model = topRestaurants.get(position);
        if(model.isFavorite() != null)
            holder.favoriteCheckBox.setChecked(model.isFavorite());

        holder.restaurant_name.setText(model.getName());
        holder.food_category.setText(model.getFoodCategory());
        holder.minimum_order_amount.setText(model.getMinOrder());
        holder.delivery_cost_amount.setText(model.getDeliveryCost());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(model.getId());

        GlideApp.with(context)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(context).load(R.drawable.personicon))
                .into(holder.restaurant_photo);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                if (prefs.getString("Name", "").isEmpty() ||
                        prefs.getString("Email", "").isEmpty() ||
                        prefs.getString("Phone", "").isEmpty() ||
                        prefs.getString("Address", "").isEmpty()) {

                    //Il profilo è da riempire
                    Intent homepage = new Intent(context, ProfileActivity.class);
                    context.startActivity(homepage);
                } else {
                    //il profilo è pieno e c'è in save preference
                    //Avvio la seguente Activity
                    RestaurantMenuActivity.start(context, model.getId());
                }
            }
        });
        refreshFavoriteList(holder, model);
        holder.favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFavorites(holder, model);
            }
        });
        if(model.getRestaurantRating() != "0" || model.getRestaurantRating() != null)
            holder.ratingBar.setRating(Float.parseFloat(model.getRestaurantRating()));
    }

    public void refreshFavoriteList(final ViewHolder holder, final Restaurant model) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoritesRef = database.child("Customer").child("Favorite").child(currentUser).child(model.getId());
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.favoriteCheckBox.setChecked(true);
                    model.setFavorite(true);
                }
                else
                    model.setFavorite(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void manageFavorites(ViewHolder holder, final Restaurant model) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference favoriteListRef = database.child("Customer").child("Favorite").child(currentUser).child(model.getId());
        if (holder.favoriteCheckBox.isChecked()) {
            // Add to favorite
            DatabaseReference restaurantRef = database.child("Company").child("Profile").child(model.getId());
            restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Restaurant newFavorite = dataSnapshot.getValue(Restaurant.class);
                        favoriteListRef.setValue(newFavorite);
                        model.setFavorite(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // Remove from favorite
            favoriteListRef.removeValue();
            model.setFavorite(false);
        }
    }


}

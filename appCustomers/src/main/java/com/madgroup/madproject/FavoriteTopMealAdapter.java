package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteTopMealAdapter extends
        RecyclerView.Adapter<FavoriteTopMealAdapter.ViewHolder> {

    private final SharedPreferences prefs;
    private ArrayList<Dish> topDish;
    private Context context;
    private String restaurantID;
    private int counter;

    public FavoriteTopMealAdapter(Context context, ArrayList<Dish> topDish, String restaurantID) {
        counter = 0;
        this.context = context;
        this.topDish = topDish;
        this.restaurantID = restaurantID;
        prefs = context.getSharedPreferences("MyData", MODE_PRIVATE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView layout;
        TextView dishName;
        CircleImageView dishPhoto;
        ImageView positionIcon;
//        TextView dishRestaurant;
//        TextView dishDescription;
//        TextView dishPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.card_layout);
            dishPhoto = itemView.findViewById(R.id.dish_photo);
            dishName = itemView.findViewById(R.id.dish_name);
            positionIcon = itemView.findViewById(R.id.positionIcon);
        }
    }

    @Override
    public int getItemCount() {
        return topDish.size();
    }

    @NonNull
    @Override
    public FavoriteTopMealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_meal_item, parent, false);
        FavoriteTopMealAdapter.ViewHolder holder = new FavoriteTopMealAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FavoriteTopMealAdapter.ViewHolder holder, int position) {
        final Dish model = topDish.get(position);

        holder.dishName.setText(model.getName());
//        holder.dishRestaurant.setText(model.getRestaurant());
//        holder.dishDescription.setText(model.getDescription());
//        holder.dishPrice.setText(model.getPrice());
        switch (counter) {
            case 0:
                holder.positionIcon.setImageResource(R.drawable.ic_first);
                holder.positionIcon.setColorFilter(Color.argb(255, 255, 193, 7));
                break;
            case 1:
                holder.positionIcon.setImageResource(R.drawable.ic_second);
                holder.positionIcon.setColorFilter(Color.argb(255, 158, 158, 158));
                break;
            case 2:
                holder.positionIcon.setImageResource(R.drawable.ic_third);
                holder.positionIcon.setColorFilter(Color.argb(255, 145, 111, 13));
                break;
            default:
                return;
        }
        counter++;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("dish_pics")
                .child(restaurantID).child(model.getId());

        GlideApp.with(context)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(false)
                .error(GlideApp.with(context).load(R.drawable.personicon))
                .into(holder.dishPhoto);

//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("ShowToast")
//            @Override
//            public void onClick(View v) {
//                if (prefs.getString("Name", "").isEmpty() ||
//                        prefs.getString("Email", "").isEmpty() ||
//                        prefs.getString("Phone", "").isEmpty() ||
//                        prefs.getString("Address", "").isEmpty()) {
//
//                    //Il profilo è da riempire
//                    Intent homepage = new Intent(context, ProfileActivity.class);
//                    context.startActivity(homepage);
//                } else {
//                    //il profilo è pieno e c'è in save preference
//                    //Avvio la seguente Activity
//                    RestaurantMenuActivity.start(context, model.getRestaurantId());
//                }
//            }
//        });
    }
}

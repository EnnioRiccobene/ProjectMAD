package com.madgroup.madproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewRestaurantAdapter extends RecyclerView.Adapter<RecycleViewRestaurantAdapter.RestaurantViewHolder> {

    private ArrayList<Restaurant> restaurantsList = new ArrayList<>();
    Context mContext;

    public RecycleViewRestaurantAdapter(Context mContext, ArrayList<Restaurant> restaurantsList) {
        this.restaurantsList = restaurantsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecycleViewRestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        RestaurantViewHolder holder = new RestaurantViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewRestaurantAdapter.RestaurantViewHolder holder, final int position) {
        holder.restaurant_name.setText(restaurantsList.get(position).getName());
        holder.food_category.setText(restaurantsList.get(position).getFoodCategory());
        holder.minimum_order_amount.setText(restaurantsList.get(position).getMinOrder());
        holder.delivery_cost_amount.setText(restaurantsList.get(position).getDeliveryCost());
        holder.restaurant_photo.setImageBitmap(restaurantsList.get(position).getPhoto());

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Avvio la seguente Activity
                RestaurantMenuActivity.start(mContext, restaurantsList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

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

        public RestaurantViewHolder(@NonNull View itemView){
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
        }
    }
}

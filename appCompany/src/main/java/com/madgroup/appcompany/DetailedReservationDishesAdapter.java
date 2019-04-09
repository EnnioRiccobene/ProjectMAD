package com.madgroup.appcompany;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedReservationDishesAdapter extends
        RecyclerView.Adapter<DetailedReservationDishesAdapter.ViewHolder>{

    private ArrayList<Dish> dishes;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        RelativeLayout relativeItemLayout;
        TextView dishName;
        TextView dishQuantity;
        TextView dishPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            relativeItemLayout = itemView.findViewById(R.id.relativeItemLayout);
            dishName = itemView.findViewById(R.id.dishName);
            dishQuantity = itemView.findViewById(R.id.dishQuantity);
            dishPrice = itemView.findViewById(R.id.price);
        }
    }

    public DetailedReservationDishesAdapter(ArrayList<Dish> dishes) {
        this.dishes= dishes;
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailed_reservation_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishQuantity.setText(String.valueOf("x " + dish.getAvailableQuantity()));
        float price = dish.getPrice() * dish.getAvailableQuantity();
        holder.dishPrice.setText(String.valueOf(price) + " €");
    }
}

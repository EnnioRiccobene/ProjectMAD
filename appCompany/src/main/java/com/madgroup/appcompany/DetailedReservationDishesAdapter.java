package com.madgroup.appcompany;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailedReservationDishesAdapter extends
        RecyclerView.Adapter<DetailedReservationDishesAdapter.ViewHolder>{

    private ArrayList<OrderedDish> orderedDishes;

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

    public DetailedReservationDishesAdapter(ArrayList<OrderedDish> orderedDishes) {
        this.orderedDishes = orderedDishes;
    }

    @Override
    public int getItemCount() {
        return orderedDishes.size();
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
        OrderedDish dish = orderedDishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishQuantity.setText(String.valueOf("x " + dish.getQuantity()));
        float price = dish.getPrice() * dish.getQuantity();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        holder.dishPrice.setText(df.format(price) + " €");

        //Lab3Ennio
        // holder.dishQuantity.setText(String.valueOf("x " + dish.getAvailableQuantity()));
        // float price = Float.valueOf(dish.getPrice()) * Integer.valueOf(dish.getAvailableQuantity());
        // holder.dishPrice.setText(String.valueOf(price) + " €");

        // Simone
        // holder.dishQuantity.setText(String.valueOf("x " + dish.getQuantity()));
        // float price = dish.getPrice() * dish.getQuantity();
        // DecimalFormat df = new DecimalFormat("#.##");
        // df.setMinimumFractionDigits(2);
        // holder.dishPrice.setText(df.format(price) + " €");

    }
}

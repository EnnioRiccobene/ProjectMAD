package com.madgroup.appcompany;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madgroup.sdk.OrderedDish;

public class DetailedReservationDishesAdapter extends
        RecyclerView.Adapter<DetailedReservationDishesAdapter.ViewHolder>{

    private ArrayList<OrderedDish> orderedDishes;
    private Context context;

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

    public DetailedReservationDishesAdapter(ArrayList<OrderedDish> orderedDishes, Context context) {
        this.orderedDishes = orderedDishes;
        this.context = context;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderedDish dish = orderedDishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishQuantity.setText("x " + dish.getQuantity());
        float price = Float.valueOf(dish.getPrice().replace(",", ".").replace("£", "")
                .replace("$", "").replace("€", "")
                .replace("$", "").replace("€", "")
                .replaceAll("\\s", "")) * Integer.valueOf(dish.getQuantity());

        Locale current = context.getResources().getConfiguration().locale;
        String currency = " €";
//        if (current.equals("en_US")) {
//            currency = " $";
//        } else if (current.equals("en_GB")) {
//            currency = " £";
//        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        holder.dishPrice.setText(String.valueOf(df.format(price)) + currency);

    }
}

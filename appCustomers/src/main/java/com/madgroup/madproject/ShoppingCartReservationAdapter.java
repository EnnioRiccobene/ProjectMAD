package com.madgroup.madproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madgroup.sdk.OrderedDish;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ShoppingCartReservationAdapter extends RecyclerView.Adapter<ShoppingCartReservationAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<OrderedDish> dishes;

    public ShoppingCartReservationAdapter(Context mContext, ArrayList<OrderedDish> dishes) {
        this.mContext = mContext;
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailed_reservation_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        OrderedDish dish = dishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishQuantity.setText(String.valueOf("x " + dish.getQuantity()));
        float price = Float.valueOf(dish.getPrice().replace(",", ".").replace("£", "")
                .replace("$", "").replace("€", "")
                .replaceAll("\\s", "")) * Integer.valueOf(dish.getQuantity());

        String currency = " €";

        holder.dishPrice.setText(String.valueOf(df.format(price))+ currency);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

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
}

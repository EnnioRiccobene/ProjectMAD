package com.madgroup.appbikers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DeliveryAdapter
        extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder> {

    private ArrayList<Delivery> deliveriesList;

    public DeliveryAdapter(ArrayList<Delivery> deliveriesList) {
        this.deliveriesList = deliveriesList;
    }

    @NonNull
    @Override
    public DeliveryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delivery_item, viewGroup, false);
        DeliveryAdapter.ViewHolder holder = new DeliveryAdapter.ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final DeliveryAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.restaurantName.setText(deliveriesList.get(i).getRestaurantName());
        viewHolder.restaurantAddress.setText(deliveriesList.get(i).getRestaurantAddress());
        viewHolder.distance.setText(String.valueOf(deliveriesList.get(i).calculateDistance("123", "123")) + " mt");
        viewHolder.customerAddress.setText(deliveriesList.get(i).getCustomerAddress());

        viewHolder.deliveryItemCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Set action on click

            }
        });

    }

    @Override
    public int getItemCount() {
        return deliveriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView deliveryItemCardView;
        RelativeLayout relativeLayout;
        TextView restaurantName;
        TextView restaurantAddress;
        TextView distance;
        TextView customerAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryItemCardView = itemView.findViewById(R.id.deliveryItemCardView);
            relativeLayout = itemView.findViewById(R.id.deliveryItemLayout);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantAddress = itemView.findViewById(R.id.restaurantAddress);
            distance = itemView.findViewById(R.id.distance);
            customerAddress = itemView.findViewById(R.id.customerAddress);
        }

    }


}

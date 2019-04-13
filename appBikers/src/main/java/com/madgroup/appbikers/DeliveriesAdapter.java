package com.madgroup.appbikers;

import android.annotation.SuppressLint;
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

public class DeliveriesAdapter
        extends RecyclerView.Adapter<DeliveriesAdapter.ViewHolder> {

    private ArrayList<Delivery> deliveriesList = new ArrayList<>();
    private Context mContext;

    public DeliveriesAdapter(Context mContext, ArrayList<Delivery> deliveriesList) {
        this.deliveriesList = deliveriesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DeliveriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delivery_item, viewGroup, false);
        DeliveriesAdapter.ViewHolder holder = new DeliveriesAdapter.ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final DeliveriesAdapter.ViewHolder viewHolder, final int i) {
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

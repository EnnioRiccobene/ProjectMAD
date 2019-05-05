package com.madgroup.appcompany;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindDishViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout layout;
    TextView dishName;
    TextView dishDescription;
    TextView dishPrice;
    CircleImageView dishPhoto;
    TextView dishQuantity;
    ImageView popupButton;

    public FindDishViewHolder(@NonNull View itemView) {
        super(itemView);

        layout = itemView.findViewById(R.id.dish_item_layout);
        dishName = itemView.findViewById(R.id.dish_name);
        dishDescription = itemView.findViewById(R.id.dish_description);
        dishPhoto = itemView.findViewById(R.id.dish_photo);
        dishPrice = itemView.findViewById(R.id.dish_price);
        dishQuantity = itemView.findViewById(R.id.dish_quantity);
        popupButton = itemView.findViewById(R.id.popupButton);
    }

}

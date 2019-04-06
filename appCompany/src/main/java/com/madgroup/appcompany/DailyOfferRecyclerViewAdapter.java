package com.madgroup.appcompany;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

// Creo un ViewHolder (che contiene il layout della cella) e l'Adapter lo setto di tipo ViewHolder

public class DailyOfferRecyclerViewAdapter extends RecyclerView.Adapter<DailyOfferRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Dish> dailyOfferList = new ArrayList<>();
    private Context mContext;

    public DailyOfferRecyclerViewAdapter(Context mContext, ArrayList<Dish> dailyOfferList) {
        this.dailyOfferList = dailyOfferList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dish_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.dishName.setText(dailyOfferList.get(i).getName());
        viewHolder.dishPrice.setText(""+dailyOfferList.get(i).getPrice()+"€");
        viewHolder.dishQuantity.setText("Quantità disponibile: "+dailyOfferList.get(i).getAvailableQuantity());
        viewHolder.dishDescription.setText(dailyOfferList.get(i).getDescription());
        viewHolder.dishPhoto.setImageBitmap(dailyOfferList.get(i).getPhoto());

//        viewHolder.layout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, ""+i, Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Selected menu with index: "+i, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return dailyOfferList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView dishName;
        TextView dishDescription;
        TextView dishPrice;
        CircleImageView dishPhoto;
        TextView dishQuantity;
        Button popupButton;

        public ViewHolder(@NonNull View itemView) {
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
}

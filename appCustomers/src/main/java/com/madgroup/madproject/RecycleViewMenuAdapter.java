package com.madgroup.madproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewMenuAdapter extends RecyclerView.Adapter<RecycleViewMenuAdapter.MenuViewHolder>{

    private ArrayList<Dish> menu = new ArrayList<>();
    Context mContext;

    @NonNull
    @Override
    public RecycleViewMenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_item, parent, false);
        MenuViewHolder holder = new MenuViewHolder(view);

        if (!holder.expandableDishQuantity.isExpanded()) {
            holder.expandableDishQuantity.collapse();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecycleViewMenuAdapter.MenuViewHolder holder, int position) {
        final int[] orderedQuantity = {0};
        holder.dishPhoto.setImageBitmap(menu.get(position).getPhoto());
        holder.dishName.setText(menu.get(position).getName());
        holder.dishPrice.setText(menu.get(position).getPrice());//gestire la edittext del prezzo come fatto nell'app company
        holder.dishIngredientsList.setText(menu.get(position).getDescription());
        holder.dishQuantity.setText(orderedQuantity[0]);

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderedQuantity[0]++;
                holder.dishQuantity.setText(orderedQuantity[0]);

                if (!holder.expandableDishQuantity.isExpanded()) {
                    holder.expandableDishQuantity.expand();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{

        CardView cardLayout;
        RelativeLayout dishItemLayout;
        CircleImageView dishPhoto;
        RelativeLayout name_button_layout;
        TextView dishName;
        ImageView incrementButton;
        TextView dishPrice;
        TextView dishIngredients;
        TextView dishIngredientsList;
        ExpandableRelativeLayout expandableDishQuantity;
        View lineItem1;
        RelativeLayout relativeQuantity;
        TextView dishQuantity;
        ImageView reduceButton;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.cardViewDish);
            dishItemLayout = itemView.findViewById(R.id.dish_item_layout);
            dishPhoto = itemView.findViewById(R.id.dish_photo);
            name_button_layout = itemView.findViewById(R.id.name_button_layout);
            dishName = itemView.findViewById(R.id.dish_name);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            dishPrice = itemView.findViewById(R.id.dish_price);
            dishIngredients = itemView.findViewById(R.id.dish_ingredients);
            dishIngredientsList = itemView.findViewById(R.id.dish_ingredients_list);
            expandableDishQuantity = itemView.findViewById(R.id.expandable_dish_quantity);
            lineItem1 = itemView.findViewById(R.id.line_item_1);
            relativeQuantity = itemView.findViewById(R.id.relative_quantity);
            dishQuantity = itemView.findViewById(R.id.dish_quantity);
            reduceButton = itemView.findViewById(R.id.reduceButton);
        }
    }
}

package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.OrderedDish;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewMenuAdapter extends FirebaseRecyclerAdapter<Dish, MenuViewHolder> {

    //    private ArrayList<Dish> menu = new ArrayList<>();
    private ArrayList<OrderedDish> orderedDishes;
    Context mContext;
    private DatabaseReference dishRef;
    private String restaurantID;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RecycleViewMenuAdapter(@NonNull FirebaseRecyclerOptions<Dish> options, DatabaseReference dishRef, Context mContext, ArrayList<OrderedDish> orderedDishes, String restaurantID) {
        super(options);
        this.dishRef = dishRef;
        this.mContext = mContext;
        this.orderedDishes = orderedDishes;
        this.restaurantID = restaurantID;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MenuViewHolder holder, int i, @NonNull final Dish dish) {
        final int[] orderedQuantity = {0};
        //holder.dishPhoto.setImageBitmap(dish.getPhoto());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("dish_pics")
                .child(restaurantID).child(dish.getId());
        GlideApp.with(mContext)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(mContext).load(R.drawable.personicon))
                .into(holder.dishPhoto);
        holder.dishName.setText(dish.getName());
        holder.dishPrice.setText(dish.getPrice());
        holder.dishIngredientsList.setText(dish.getDescription());
        holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
//        float currDish = Float.valueOf(menu.get(position).getPrice().replace(",", ".").replace("£", "").replace("$", "").replace("€", "").replaceAll("\\s",""));
        final OrderedDish currentDish = new OrderedDish(dish.getId(), dish.getName(), "0", dish.getPrice());

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {

                orderedQuantity[0]++;
                if(orderedQuantity[0] <= Integer.valueOf(dish.getAvailableQuantity())){
                    holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
                } else {
                    orderedQuantity[0]--;
                    Toast.makeText(mContext, "The quantity selected is not available", Toast.LENGTH_LONG);
                }                

                //Aggiungo il piatto ordinato all'arraylist o ne incremento la quantità
                if (orderedDishes.contains(currentDish)) {
                    int index = orderedDishes.indexOf(currentDish);
                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));
                    orderedDishes.get(index).setQuantity(currentDish.getQuantity());
                } else {
                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));//la quantità è 1
                    orderedDishes.add(currentDish);
                }

                //prima aggiunta del piatto all'ordine
                if (!holder.expandableDishQuantity.isExpanded()) {
                    holder.expandableDishQuantity.expand();
                }

            }
        });

        holder.reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderedQuantity[0] > 0){
                    orderedQuantity[0]--;
                }
                holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
                if (orderedDishes.contains(currentDish)) {
                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));
                    int index = orderedDishes.indexOf(currentDish);
                    orderedDishes.get(index).setQuantity(currentDish.getQuantity());
                    //Rimuovo il piatto ordinato dall'ArrayList
                    if (Integer.valueOf(orderedDishes.get(index).getQuantity()) == 0) {
                        orderedDishes.remove(index);
                    }
                }

                //piatto rimosso dall'ordine
                if (holder.expandableDishQuantity.isExpanded()) {
                    if (orderedQuantity[0] == 0) {
                        holder.expandableDishQuantity.collapse();
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_item, parent, false);
        MenuViewHolder holder = new MenuViewHolder(view);

        if (!holder.expandableDishQuantity.isExpanded()) {
            holder.expandableDishQuantity.collapse();
        }

        return holder;
    }

//    public RecycleViewMenuAdapter(Context mContext, ArrayList<Dish> menu, ArrayList<OrderedDish> orderedDishes) {
//        this.menu = menu;
//        this.orderedDishes = orderedDishes;
//        this.mContext = mContext;
//    }
//
//    @NonNull
//    @Override
//    public RecycleViewMenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_item, parent, false);
//        MenuViewHolder holder = new MenuViewHolder(view);
//
//        if (!holder.expandableDishQuantity.isExpanded()) {
//            holder.expandableDishQuantity.collapse();
//        }
//
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecycleViewMenuAdapter.MenuViewHolder holder, int position) {
//        final int[] orderedQuantity = {0};
//        holder.dishPhoto.setImageBitmap(menu.get(position).getPhoto());
//        holder.dishName.setText(menu.get(position).getName());
//        holder.dishPrice.setText(menu.get(position).getPrice());//todo: gestire la edittext del prezzo come fatto nell'app company
//        holder.dishIngredientsList.setText(menu.get(position).getDescription());
//        holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
////        float currDish = Float.valueOf(menu.get(position).getPrice().replace(",", ".").replace("£", "").replace("$", "").replace("€", "").replaceAll("\\s",""));
//        final OrderedDish currentDish = new OrderedDish(menu.get(position).getName(), "0", menu.get(position).getPrice());
//
//        //todo: fare controllo sull'available quantity del piatto (forse va controllato il db)
//        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                orderedQuantity[0]++;
//                holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
//
//                //Aggiungo il piatto ordinato all'arraylist o ne incremento la quantità
//                if(orderedDishes.contains(currentDish)){
//                    int index = orderedDishes.indexOf(currentDish);
//                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));
//                    orderedDishes.get(index).setQuantity(currentDish.getQuantity());
//                } else {
//                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));//la quantità è 1
//                    orderedDishes.add(currentDish);
//                }
//
//                //prima aggiunta del piatto all'ordine
//                if (!holder.expandableDishQuantity.isExpanded()) {
//                    holder.expandableDishQuantity.expand();
//                }
//
//            }
//        });
//
//        holder.reduceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                orderedQuantity[0]--;
//                holder.dishQuantity.setText(String.valueOf(orderedQuantity[0]));
//                if(orderedDishes.contains(currentDish)){
//                    currentDish.setQuantity(String.valueOf(orderedQuantity[0]));
//                    int index = orderedDishes.indexOf(currentDish);
//                    orderedDishes.get(index).setQuantity(currentDish.getQuantity());
//                    //Rimuovo il piatto ordinato dall'ArrayList
//                    if(Integer.valueOf(orderedDishes.get(index).getQuantity()) == 0){
//                        orderedDishes.remove(index);
//                    }
//                }
//
//                //piatto rimosso dall'ordine
//                if (holder.expandableDishQuantity.isExpanded()) {
//                    if(orderedQuantity[0] == 0){
//                        holder.expandableDishQuantity.collapse();
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return menu.size();
//    }
//
//    public class MenuViewHolder extends RecyclerView.ViewHolder{
//
//        CardView cardLayout;
//        RelativeLayout dishItemLayout;
//        CircleImageView dishPhoto;
//        RelativeLayout name_button_layout;
//        TextView dishName;
//        ImageView incrementButton;
//        TextView dishPrice;
//        TextView dishIngredients;
//        TextView dishIngredientsList;
//        ExpandableRelativeLayout expandableDishQuantity;
//        View lineItem1;
//        RelativeLayout relativeQuantity;
//        TextView dishQuantity;
//        ImageView reduceButton;
//
//        public MenuViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            cardLayout = itemView.findViewById(R.id.cardViewDish);
//            dishItemLayout = itemView.findViewById(R.id.dish_item_layout);
//            dishPhoto = itemView.findViewById(R.id.dish_photo);
//            name_button_layout = itemView.findViewById(R.id.name_button_layout);
//            dishName = itemView.findViewById(R.id.dish_name);
//            incrementButton = itemView.findViewById(R.id.incrementButton);
//            dishPrice = itemView.findViewById(R.id.dish_price);
//            dishIngredients = itemView.findViewById(R.id.dish_ingredients);
//            dishIngredientsList = itemView.findViewById(R.id.dish_ingredients_list);
//            expandableDishQuantity = itemView.findViewById(R.id.expandable_dish_quantity);
//            lineItem1 = itemView.findViewById(R.id.line_item_1);
//            relativeQuantity = itemView.findViewById(R.id.relative_quantity);
//            dishQuantity = itemView.findViewById(R.id.dish_quantity);
//            reduceButton = itemView.findViewById(R.id.reduceButton);
//        }
//    }
}

class MenuViewHolder extends RecyclerView.ViewHolder {

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

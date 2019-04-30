package com.madgroup.madproject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRestaurantActivity extends AppCompatActivity {

    private CircleImageView photo;//temporanea
    private ImageButton btnSearch;
    private ImageButton btnFilter;
    private SearchView searchRestaurant;
    private String restaurantCategory = null;

    private ArrayList<Restaurant> searchedRestaurantList = new ArrayList<>();
    private DatabaseReference restaurantRef;

    RecyclerView recyclerView;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchrestaurant);

        // Getting the instance of Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantRef = database.getReference().child("Company").child("Profile");

        mContext = this;
//todo: questo inserimento di ristoranti nel db è temporaneo, in questa activity devo solo leggere
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
        Restaurant r1 = new Restaurant("email1", "Da Saro", "0695555555", "Via X, Acireale", "panini", "Photo", "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso");
        Restaurant r2 = new Restaurant("email2", "Napples Pizza", "0695555555", "Via X, Acireale", "pizza", "Photo", "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso");
        Restaurant r3 = new Restaurant("email3", "Horace Kebab", "0695555555", "Via X, Acireale", "Kebab", "photo", "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso");
        Restaurant r4 = new Restaurant("email4", "Greek Lab", "0695555555", "Via X, Acireale", "Mediterranea", "photo", "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso");
        Restaurant r5 = new Restaurant("email5", "Acqua e farina", "0695555555", "Via X, Acireale", "Pizza, Fritti", "photo", "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso");

        restaurantRef.setValue("email1");
        restaurantRef.child("email1").setValue(r1);
        restaurantRef.child("email2").setValue(r2);
        restaurantRef.child("email3").setValue(r3);
        restaurantRef.child("email4").setValue(r4);
        restaurantRef.child("email5").setValue(r5);

        photo = findViewById(R.id.restaurant_photo);
        btnSearch = findViewById(R.id.imageButtonSearch);
        btnFilter = findViewById(R.id.imageButtonFilter);
        searchRestaurant = findViewById(R.id.searchWidget);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchRestaurant.getVisibility() == View.GONE) {
                    searchRestaurant.setVisibility(View.VISIBLE);
                } else {
                    searchRestaurant.setVisibility(View.GONE);
                }
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        recyclerView = findViewById(R.id.restaurantsrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Restaurant> options =
                new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(restaurantRef, Restaurant.class)
                        .build();

        FirebaseRecyclerAdapter<Restaurant, FindRestaurantViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurant, FindRestaurantViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindRestaurantViewHolder holder, final int position, @NonNull final Restaurant model) {
                        holder.restaurant_name.setText(model.getName());
                        holder.food_category.setText(model.getFoodCategory());
                        holder.minimum_order_amount.setText(model.getMinOrder());
                        holder.delivery_cost_amount.setText(model.getDeliveryCost());
//                        holder.restaurant_photo.setImageBitmap(R.drawable.); todo: prendere l'immagine dal database

                        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Avvio la seguente Activity
                                RestaurantMenuActivity.start(mContext, model.getId());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
                        FindRestaurantViewHolder viewHolder = new FindRestaurantViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindRestaurantViewHolder extends RecyclerView.ViewHolder {

        CardView cardLayout;
        RelativeLayout restaurant_item_layout;
        CircleImageView restaurant_photo;
        RelativeLayout name_button_layout;
        TextView restaurant_name;
        TextView food_category;
        TextView minimum_order;
        TextView minimum_order_amount;
        TextView delivery_cost;
        TextView delivery_cost_amount;

        public FindRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = itemView.findViewById(R.id.cardLayout);
            restaurant_item_layout = itemView.findViewById(R.id.restaurant_item_layout);
            restaurant_photo = itemView.findViewById(R.id.restaurant_photo);
            name_button_layout = itemView.findViewById(R.id.name_button_layout);
            restaurant_name = itemView.findViewById(R.id.restaurant_name);
            food_category = itemView.findViewById(R.id.food_category);
            minimum_order = itemView.findViewById(R.id.minimum_order);
            minimum_order_amount = itemView.findViewById(R.id.minimum_order_amount);
            delivery_cost = itemView.findViewById(R.id.delivery_cost);
            delivery_cost_amount = itemView.findViewById(R.id.delivery_cost_amount);
        }
    }

    private void showFilterDialog() {
        //custom Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getString(R.string.filter_restaurants));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.restaurant_filter_dialog);

        TextView dialogDismiss = dialog.findViewById(R.id.dialogCancel);
        TextView dialogConfirm = dialog.findViewById(R.id.dialogConfirm);
        TextView food_category = dialog.findViewById(R.id.food_category);
        RadioGroup radioGroupFoodCategory = dialog.findViewById(R.id.radio_group_food_category);
        RadioButton radioAll = dialog.findViewById(R.id.radio_all);
        RadioButton radioPizza = dialog.findViewById(R.id.radio_pizza);
        RadioButton radioSandwiches = dialog.findViewById(R.id.radio_sandwiches);
        RadioButton radioKebab = dialog.findViewById(R.id.radio_kebab);
        RadioButton radioItalian = dialog.findViewById(R.id.radio_italian);
        RadioButton radioAmerican = dialog.findViewById(R.id.radio_american);
        RadioButton radioDessert = dialog.findViewById(R.id.radio_desserts);
        RadioButton radioFry = dialog.findViewById(R.id.radio_fry);
        RadioButton radioVegetarian = dialog.findViewById(R.id.radio_vegetarian);
        RadioButton radioAsian = dialog.findViewById(R.id.radio_asian);
        RadioButton radioMediterranean = dialog.findViewById(R.id.radio_mediterranean);
        RadioButton radioSouthAmerican = dialog.findViewById(R.id.radio_south_american);
        final CheckBox freeDeliveryCheckbox = dialog.findViewById(R.id.freeDeliveryCheckBox);

        radioGroupFoodCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radio_all) {
                    restaurantCategory = getResources().getString(R.string.All);
                } else if (checkedId == R.id.radio_pizza) {
                    restaurantCategory = getResources().getString(R.string.Pizza);
                } else if (checkedId == R.id.radio_sandwiches) {
                    restaurantCategory = getResources().getString(R.string.Sandwiches);
                } else if (checkedId == R.id.radio_kebab) {
                    restaurantCategory = getResources().getString(R.string.Kebab);
                } else if (checkedId == R.id.radio_italian) {
                    restaurantCategory = getResources().getString(R.string.Italian);
                } else if (checkedId == R.id.radio_american) {
                    restaurantCategory = getResources().getString(R.string.American);
                } else if (checkedId == R.id.radio_desserts) {
                    restaurantCategory = getResources().getString(R.string.Desserts);
                } else if (checkedId == R.id.radio_fry) {
                    restaurantCategory = getResources().getString(R.string.Fry);
                } else if (checkedId == R.id.radio_vegetarian) {
                    restaurantCategory = getResources().getString(R.string.Vegetarian);
                } else if (checkedId == R.id.radio_asian) {
                    restaurantCategory = getResources().getString(R.string.Asian);
                } else if (checkedId == R.id.radio_mediterranean) {
                    restaurantCategory = getResources().getString(R.string.Mediterranean);
                } else if (checkedId == R.id.radio_south_american) {
                    restaurantCategory = getResources().getString(R.string.South_American);
                }
            }
        });

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantCategory = null;
                //todo annullare anche il filtro del costo di consegna
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo gestire comportamento interno, filtri, interagire con il db per fare una query, ripopolare l'arrayList di ristoranti in base ai filtri e aggiornare la pagina principale
                if (restaurantCategory != null) {
                    //todo
                }

                if (freeDeliveryCheckbox.isChecked()) {
                    //todo gestire click sul checkbox
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //todo, occuparsi del comportamento della searchView che popoli l'arrayList di ristoranti in base al nome digitato

}

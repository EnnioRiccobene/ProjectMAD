package com.madgroup.madproject;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRestaurantActivity extends AppCompatActivity {

    private CircleImageView photo;//temporanea
    private ImageButton btnSearch;
    private ImageButton btnFilter;
    private SearchView searchRestaurant;
    private String restaurantCategory;

    private ArrayList<Restaurant> searchedRestaurantList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchrestaurant);

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

        initRecycleView();
    }

    private void showFilterDialog() {
        //custom Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(getString(R.string.filter_restaurants));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.restaurant_filter_dialog);

        TextView dialogDismiss = dialog.findViewById(R.id.dialogCancel);
        TextView dialogConfirm = dialog.findViewById(R.id.dialogConfirm);
        final ExpandableRelativeLayout hiddenFoodCategories = dialog.findViewById(R.id.hiddenFoodCategories);
        final ImageView arrowbtn = dialog.findViewById(R.id.arrowbtn);
        LinearLayout food_category_layout = dialog.findViewById(R.id.food_category_layout);
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
        CheckBox freeDeliveryCheckbox = dialog.findViewById(R.id.freeDeliveryCheckBox);

        //Mi assicuro che l'Expandable Layout sia chiuso all'apertura dell'app
        if (!hiddenFoodCategories.isExpanded()) {
            hiddenFoodCategories.collapse();
        }

        food_category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenFoodCategories.isExpanded()) {
                    createRotateAnimator(arrowbtn, 180f, 0f).start();
                } else {
                    createRotateAnimator(arrowbtn, 0f, 180f).start();
                }
                hiddenFoodCategories.toggle();
            }
        });

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

        //todo gestire click sul checkbox

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo gestire comportamento interno, filtri, interagire con il db per fare una query, ripopolare l'arrayList di ristoranti in base ai filtri e aggiornare la pagina principale
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initRecycleView() {
        //todo temporanea, poi prendere dal db
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
//        Bitmap bitmap = (Bitmap) getResources().getDrawable(R.drawable.personicon, null);   //((BitmapDrawable) photo.getDrawable()).getBitmap();
        searchedRestaurantList.add(new Restaurant(1, "Da Saro", "0695555555", "Via X, Acireale", "panini", bitmap, "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso"));
        searchedRestaurantList.add(new Restaurant(1, "Napples Pizza", "0695555555", "Via X, Acireale", "pizza", bitmap, "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso"));
        searchedRestaurantList.add(new Restaurant(1, "Horace Kebab", "0695555555", "Via X, Acireale", "Kebab", bitmap, "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso"));
        searchedRestaurantList.add(new Restaurant(1, "Greek Lab", "0695555555", "Via X, Acireale", "Mediterranea", bitmap, "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso"));
        searchedRestaurantList.add(new Restaurant(1, "Acqua e farina", "0695555555", "Via X, Acireale", "Pizza, Fritti", bitmap, "10€",
                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
                "Chiuso", "Chiuso"));


        RecyclerView recyclerView = findViewById(R.id.restaurantsrecycleview);
        RecycleViewRestaurantAdapter adapter = new RecycleViewRestaurantAdapter(this, searchedRestaurantList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}

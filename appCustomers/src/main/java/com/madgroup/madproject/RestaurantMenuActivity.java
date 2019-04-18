package com.madgroup.madproject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.Utils;
import com.madgroup.sdk.SmartLogger;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Displays the Target screen
 * Required extras:
 * - Restaurant: Restaurant
*/
public class RestaurantMenuActivity extends AppCompatActivity {

    private Restaurant restaurant;
    ArrayList<Dish> menu = new ArrayList<>();

    CircleImageView restaurantPhoto;
    TextView restaurantName;
    TextView foodCategories;
    TextView minimumOrderAmount;
    TextView deliveryCostAmount;
    ImageView arrowbtn;
    ExpandableLayout hiddenHours;
    TextView mondayHours;
    TextView tuesdayHours;
    TextView wednesdayHours;
    TextView thursdayHours;
    TextView fridayHours;
    TextView saturdayHours;
    TextView sundayHours;

    public static void start(Context context, int restaurantId){
        Intent starter = new Intent(context, RestaurantMenuActivity.class);
        starter.putExtra("Restaurant", restaurantId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_menu);

        restaurantPhoto = findViewById(R.id.restaurant_photo);
        restaurantName = findViewById(R.id.restaurant_name);
        foodCategories = findViewById(R.id.food_category);
        minimumOrderAmount = findViewById(R.id.minimum_order_amount);
        deliveryCostAmount = findViewById(R.id.delivery_cost_amount);
        hiddenHours = findViewById(R.id.hiddenhours);
        arrowbtn = findViewById(R.id.arrowbtn);
        mondayHours = findViewById(R.id.mondayhour);
        tuesdayHours = findViewById(R.id.tuesdayhour);
        wednesdayHours = findViewById(R.id.wednesdayhour);
        thursdayHours = findViewById(R.id.thursdayhour);
        fridayHours = findViewById(R.id.fridayhour);
        saturdayHours = findViewById(R.id.saturdayhour);
        sundayHours = findViewById(R.id.sundayhour);

        //Mi assicuro che l'Expandable Layout sia chiuso all'apertura dell'app
        if(!hiddenHours.isExpanded()){
            hiddenHours.collapse();
        }

        getIncomingIntent();
        //todo: prendere i valori dall'id e settare i componenti della pagina

        //todo:aggiungere button carrello al layout e qui fare il clickListener

        initRecicleView();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("Restaurant")){
            int idRestaurant = getIntent().getIntExtra("Restaurant", 0);
//            restaurant.setId(idRestaurant);//l'oggetto restaurant non è stato costruito e non ho gli attributi per farlo oltre all'id

            //todo: una volta ottenuto l'id del ristorante tramite intent, fare una query al database per ottenere i campi del ristorante con quell'id (photo, Name, foodcategories, orari di apertura, ordine minimo e costo consegna)
            //todo: poi fare un'altra query al db per ottenere tutti i piatti del ristorante con quell'id e riempire l'ArrayList di Dish per la recycleview
        }
    }

    private void initRecicleView(){
        //todo temporanea, poi prendere dal db
        menu.add(new Dish(1, "Margherita", 5, 30));
        menu.add(new Dish(1, "Capricciosa", 7.5f, 30));
        menu.add(new Dish(1, "Quattro salumi", 7, 30));
        menu.add(new Dish(1, "Quattro formaggi", 8, 30));
        menu.add(new Dish(1, "Parmiggiana", 7.5f, 30));
        menu.add(new Dish(1, "Prosciutto", 6, 30));
        menu.add(new Dish(1, "Burrata", 10, 30));

        RecyclerView recyclerView = findViewById(R.id.menu_recycleView);
        RecycleViewMenuAdapter adapter = new RecycleViewMenuAdapter(this, menu);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showHoursDetails(View view) {
        if (hiddenHours.isExpanded()) {
            createRotateAnimator(arrowbtn, 180f, 0f).start();
        } else {
            createRotateAnimator(arrowbtn, 0f, 180f).start();
        }
        hiddenHours.toggle();
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}

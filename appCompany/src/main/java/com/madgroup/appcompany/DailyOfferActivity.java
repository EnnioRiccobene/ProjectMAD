package com.madgroup.appcompany;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DailyOfferActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final int THUMBSIZE = 50;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private DailyOfferRecyclerViewAdapter adapter;

    ArrayList<Dish> myList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // FloatingActionButton fc = findViewById(R.id.add_button);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_offer);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        initializeNavigationDrawer();

        Bitmap carbonaraIcon = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(this.getResources(), R.drawable.carbonara), THUMBSIZE, THUMBSIZE);
        Bitmap gnocchiIcon = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(this.getResources(), R.drawable.gnocchi), THUMBSIZE, THUMBSIZE);


        myList.add(new Dish(0,"Spaghetti alla Carbonara", 5.5f, 5, "" +
                "Guanciale, uova, pecorino, pepe nero.",carbonaraIcon));
        myList.add(new Dish(1,"Gnocchi di patet", 5.90f, 19, "Patate, " +
                "Farina, " +
                "Uova, ", gnocchiIcon));
        myList.add(new Dish(2,"Lasagne alla Bolognese", 8.5f, 3, "Ragù, Besciamella." +
                "Olio extravergine d'oliva, " +
                "Pepe nero, ", gnocchiIcon));

        initDailyOfferRecyclerView();
    }


    private void initDailyOfferRecyclerView() {
        recyclerView = this.findViewById(R.id.my_recycler_view);
        adapter = new DailyOfferRecyclerViewAdapter(this,  myList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            onBackPressed();

        } else if (id == R.id.nav_reservations) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, ReservationActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_profile) {
            // Change activity to Daily Offer
            Intent myIntent = new Intent(this, MainActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // What happens if I press back button
    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(GravityCompat.START))
            layout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void initializeNavigationDrawer(){

        // Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(        // Three lines icon on the left corner
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the photo of the Navigation Bar Icon (Need to be completed: refresh when new image is updated)
//        ImageView nav_profile_icon = (ImageView) findViewById(R.id.nav_profile_icon);
//        nav_profile_icon.setImageDrawable(personalImage.getDrawable());

        // Set restaurant name and email on navigation header
        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String username = prefs.getString("Name", "");
        navUsername.setText(username);

        String email = prefs.getString("Email", "");
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        updateNavigatorPersonalIcon();
    }

    public void updateNavigatorPersonalIcon(){
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
        if(!ImageBitmap.equals("NoImage")){
            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            nav_profile_icon.setImageBitmap(bitmap);
        } else {
            Drawable defaultImg = getResources().getDrawable(R.mipmap.ic_launcher_round);
            nav_profile_icon.setImageDrawable(defaultImg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_offer_menu, menu);
        return true;
    }

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Edit icon
            case R.id.addIcon:
                showDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.edit_dish);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dish_dialog);

        ImageButton dialogDismiss = (ImageButton) dialog.findViewById(R.id.dialogDismiss);
        ImageButton dialogConfirm = (ImageButton) dialog.findViewById(R.id.dialogConfirm);
        final CircleImageView dishImage = (CircleImageView) dialog.findViewById(R.id.dishImage);
        final EditText editDishName = (EditText) dialog.findViewById(R.id.editDishName);
        final EditText editDishDescription = (EditText) dialog.findViewById(R.id.editDishDescription);
        final EditText editDishQuantity = (EditText) dialog.findViewById(R.id.editDishQuantity);
        final EditText editPrice = (EditText) dialog.findViewById(R.id.editPrice);
        editPrice.addTextChangedListener(new NumberTextWatcher(editPrice, "#,###"));

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo fare un parsing della stringa per togliere il simbolo dell'euro e tutto quello che non è float
                //todo far funzionare il metodo per visualizzare l'editText come valuta
                // TODO controllare che i campi non siano vuoti
                String dishName = editDishName.getText().toString();
                float dishPrice = 10;
                //Float.parseFloat(editPrice.getText().toString());
                int dishQuantity = Integer.parseInt(editDishQuantity.getText().toString());
                String dishDesc = editDishDescription.getText().toString();
                Bitmap dishPhoto = ((BitmapDrawable) dishImage.getDrawable()).getBitmap();
                myList.add(new Dish(0,dishName, dishPrice, dishQuantity, dishDesc, dishPhoto));
                adapter.notifyItemInserted(myList.size()-1);
                adapter.notifyItemRangeChanged(myList.size()-1, myList.size());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


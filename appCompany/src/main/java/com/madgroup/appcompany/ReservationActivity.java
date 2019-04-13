package com.madgroup.appcompany;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ReservationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private ReservationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Reservation> mReservationList;

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        createReservationList();
        buildRecyclerView();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // OVERRIDE DEL ONBACKPRESSED
        initializeNavigationDrawer();

    }
    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, DailyOfferActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_reservations) {
            onBackPressed();

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

    // Add new item at ReservationList specifying 4 element: status_biker , text_address, lunch_time, order_price
    public void createReservationList(){
        mReservationList = new ArrayList<>();
        mReservationList.add(new Reservation(R.drawable.green_bike, "Via Moretta 2", "18:45", "19.99 €"));
        mReservationList.add(new Reservation(R.drawable.red_bike, "Piazza Sabotino 8","19:00", "18.95 €" ));
        mReservationList.add(new Reservation(R.drawable.red_bike,"Via Villarbasse 12" , "20:45", "12.50 €"));
        mReservationList.add(new Reservation(R.drawable.green_bike, "Corso Rosselli 15", "21:00", "11.90 €"));
        mReservationList.add(new Reservation(R.drawable.red_bike, "Address5","Delivery Time", "1090.00 €" ));
        mReservationList.add(new Reservation(R.drawable.red_bike,"Address6" , "Delivery Time", "100.90 € "));
        mReservationList.add(new Reservation(R.drawable.green_bike, "Address7", "Delivery Time", "Price €"));
        mReservationList.add(new Reservation(R.drawable.red_bike, "Address8","Delivery Time" , "Price €"));
        mReservationList.add(new Reservation(R.drawable.red_bike,"Address9" , "Delivery Time", "Price €"));
        mReservationList.add(new Reservation(R.drawable.green_bike, "Address10", "Delivery Time", "Price €"));
        mReservationList.add(new Reservation(R.drawable.red_bike, "Address11","Delivery Time", "Price €" ));
        mReservationList.add(new Reservation(R.drawable.red_bike,"Address12" , "Delivery Time", "Price €"));
    }

    // The following function set up the RecyclerView
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        //rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ReservationAdapter(mReservationList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
       // mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mAdapter.setOnItemClickListener(new ReservationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent openPage= new Intent(ReservationActivity.this, DetailedReservation.class);
                startActivity(openPage);
                //changeItem(position,"Clicked!");
            }
             //This Function is useful if we want to delete an item in the list
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        /*ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT ,this );
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(mRecyclerView);

        */
    }
     public void removeItem(int position) {
        mReservationList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text) {
        mReservationList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reservation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

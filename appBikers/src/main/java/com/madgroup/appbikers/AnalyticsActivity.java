package com.madgroup.appbikers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madgroup.sdk.SmartLogger;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

// To do:
// Add NavigationDrawer and add to navigationdrawer Analytic Page
// Transizione come le altre activity
// Ricorda di implementare (dove?) la funzione che aggiorna i km percorsi una volta che il rider ha effettuato la consegna
// Lo stesso per aumentare il numero di consegne totali
//
public class AnalyticsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String currentUser;
    private RatingBar ratingBar;
    private TextView numberDeliveries;
    private TextView kmTravelled;
    private TextView mEarned;
    private TextView ratingCounter;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private ProgressBar progressBar;
    private GridLayout gridLayout;

    private FirebaseDatabase database;

    String notificationTitle = "MAD Bikers";
    String notificationText;


    //Nel profilo del Biker ratingCounter e ratingAvg ne definiscono il rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_analytics);
        stub.inflate();
        this.setTitle("Analytics");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = prefs.getString("currentUser", "noUser");
        if (currentUser.equals("noUser")) {
            SmartLogger.e("Error noUser");
        }
        editor = prefs.edit();

        navigationDrawerInitialization();

        ratingBar = findViewById(R.id.ratingBar);  //problema stessa lettera maiuscola B?
        numberDeliveries = findViewById(R.id.ndeliveries);
        kmTravelled = findViewById(R.id.kmtravelled);
        mEarned = findViewById(R.id.mearned);
        ratingCounter = findViewById(R.id.ratingcounter);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        progressBar = findViewById(R.id.progress);
        gridLayout = findViewById(R.id.gridLayout);

        textView.setText(R.string.average_rating);
        textView1.setText(R.string.number_deliveries);
        textView2.setText(R.string.km_travelled);
        textView3.setText(R.string.money_earned);


        gridLayout.setVisibility(View.GONE);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference newOrderRef = database.getReference().child("Rider").child("Delivery").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
        NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
        notify.newOrderListner();
        checkLocationpermissions();
        DatabaseReference statsReference = FirebaseDatabase.getInstance().getReference().child("Rider").child("Profile").child(currentUser);
        statsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;
                numberDeliveries.setText(dataSnapshot.child("deliveryNumber").getValue(String.class));
                ratingCounter.setText("(" + (dataSnapshot.child("ratingCounter").getValue(String.class) + ")" ));
                kmTravelled.setText(dataSnapshot.child("totDistance").getValue(String.class));
                ratingBar.setRating(Float.parseFloat(dataSnapshot.child("ratingAvg").getValue(String.class)));
                getMoneyEarned(numberDeliveries.getText().toString(), kmTravelled.getText().toString());
                progressBar.setVisibility(View.GONE);
                gridLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }

        });

    }


    void getMoneyEarned (String ndeliveries, String km ) {
        double money = 0;
        String earned;
        money = Float.parseFloat(ndeliveries)*2 + Float.parseFloat(km)*0.10;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        earned = df.format(money);
        mEarned.setText((earned) + " €");
    }

    private void checkLocationpermissions() {
        int gpsPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
            // Permessi già accettati: comincio a tracciare la posizione
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener(currentUser);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        } else {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            this.startActivity(myIntent);
        }
    }
    public void navigationDrawerInitialization() {
        // Navigation Drawer Initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavigatorInformation(navigationView);
        verifyRiderAvailability(navigationView);
    }

    private void verifyRiderAvailability(NavigationView navigationView) {
        final SwitchCompat riderAvailability = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_switch).getActionView().findViewById(R.id.drawer_switch);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference riderStatusRef = database.child("Rider").child("Profile").child(currentUser).child("status");
        Boolean status = prefs.getBoolean("Status", false);
        SmartLogger.d("status letto: " + status.toString());
        riderAvailability.setChecked(status);
        riderAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newStatus;
                if (riderAvailability.isChecked())
                    newStatus = true;
                else
                    newStatus = false;
                editor.putBoolean("Status", newStatus);
                riderStatusRef.setValue(newStatus);
                editor.apply();
            }
        });
    }

    public void updateNavigatorInformation(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("bikers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);


        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String name = prefs.getString("Name", "");
        if (!name.equals(""))
            navUsername.setText(name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        String email = prefs.getString("Email", "");
        if (!email.equals(""))
            navEmail.setText(email);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_deliviries) {
            Intent myIntent = new Intent(this, DeliveryActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            this.startActivity(myIntent);
        } else if (id==R.id.nav_analytic){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_logout)
            startLogout();
        if (id == R.id.nav_switch)
            return true;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void startLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        editor.clear();
                        editor.apply();
                        //startLogin();
                        Intent intent = new Intent(AnalyticsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

package com.madgroup.madproject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.sdk.SmartLogger;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRestaurantActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private CircleImageView photo;//temporanea
    private ImageButton btnSearch;
    private ImageButton btnFilter;
    private SearchView searchRestaurant;
    private String restaurantCategory = null;

    private SharedPreferences prefs;

    private ArrayList<Restaurant> searchedRestaurantList = new ArrayList<>();
    private DatabaseReference restaurantRef;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef; //di prova
    private SharedPreferences.Editor editor;


    private FirebaseRecyclerOptions<Restaurant> options;

    RecyclerView recyclerView;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_searchrestaurant);
        stub.inflate();
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        initializeNavigationDrawer();
        // Getting the instance of Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        restaurantRef = database.getReference().child("Company").child("Profile");

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //uploadFile();

        mContext = this;
//todo: questo inserimento di ristoranti nel db è temporaneo, in questa activity devo solo leggere
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
//        Restaurant r1 = new Restaurant("email1", "Da Saro", "0695555555", "Via X, Acireale", "Panini", "Photo", "10€",
//                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
//                "Chiuso", "Chiuso");
//        Restaurant r2 = new Restaurant("email2", "Napples Pizza", "0695555555", "Via X, Acireale", "Pizza", "Photo", "10€",
//                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
//                "Chiuso", "Chiuso");
//        Restaurant r3 = new Restaurant("email3", "Horace Kebab", "0695555555", "Via X, Acireale", "Kebab", "photo", "10€",
//                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
//                "Chiuso", "Chiuso");
//        Restaurant r4 = new Restaurant("email4", "Greek Lab", "0695555555", "Via X, Acireale", "Mediterranea", "photo", "10€",
//                "2€", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
//                "Chiuso", "Chiuso");
//        Restaurant r5 = new Restaurant("email5", "Acqua e farina", "0695555555", "Via X, Acireale", "Pizza, Fritti", "photo", "10€",
//                "0,00 €", "Chiuso", "Chiuso", "Chiuso", "Chiuso", "Chiuso",
//                "Chiuso", "Chiuso");

//        restaurantRef.setValue("email1");
//        restaurantRef.child("email1").setValue(r1);
//        restaurantRef.child("email2").setValue(r2);
//        restaurantRef.child("email3").setValue(r3);
//        restaurantRef.child("email4").setValue(r4);
//        restaurantRef.child("email5").setValue(r5);

        photo = findViewById(R.id.restaurant_photo);
        searchRestaurant = findViewById(R.id.searchWidget);

//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchRestaurant.getVisibility() == View.GONE) {
//                    searchRestaurant.setVisibility(View.VISIBLE);
//                } else {
//                    searchRestaurant.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        btnFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFilterDialog();
//            }
//        });

        searchRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Query applyQuery = restaurantRef.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(applyQuery, Restaurant.class)
                        .build();
                onStart();
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query applyQuery = restaurantRef.orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(applyQuery, Restaurant.class)
                        .build();
                onStart();
                return true;
            }
        });

        recyclerView = findViewById(R.id.restaurantsrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                .setQuery(restaurantRef, Restaurant.class)
                .build();
    }

    //Il metodo serve a prendere l'estensione dell'immagine
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //todo: metodo di prova per mettere le immagini nello storage e ricavarne l'uri da scrivere nel db. Poi metterlo in libreria, passangogli il percorso dove salvare l'uri nel db
    private void uploadFile() {

        //todo: al momento l'immagine è presa da drawable, poi si dovrà prendere da fotocamera e libreria
        //https://firebase.google.com/docs/storage/android/upload-files
        Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
        Bitmap bitmap = ((BitmapDrawable) defaultImg).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".bmp");

        UploadTask uploadTask = fileReference.putBytes(data); // Salvo l'immagine nello storage

        /* Gestione successo e insuccesso */
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(SearchRestaurantActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SearchRestaurantActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
            }
        });

        /* Ricavo Uri e lo inserisco nel db */
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult(); // URI Dell'immagine
                    mDatabaseRef.setValue(downloadUri.toString());

                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerOptions<Restaurant> options =
//                new FirebaseRecyclerOptions.Builder<Restaurant>()
//                        .setQuery(restaurantRef, Restaurant.class)
//                        .build();

        FirebaseRecyclerAdapter<Restaurant, FindRestaurantViewHolder> adapter =
                new FirebaseRecyclerAdapter<Restaurant, FindRestaurantViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindRestaurantViewHolder holder, final int position, @NonNull final Restaurant model) {
                        holder.restaurant_name.setText(model.getName());
                        holder.food_category.setText(model.getFoodCategory());
                        holder.minimum_order_amount.setText(model.getMinOrder());
                        holder.delivery_cost_amount.setText(model.getDeliveryCost());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                                .child("restaurants").child(model.getId());

                        GlideApp.with(SearchRestaurantActivity.this)
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(GlideApp.with(SearchRestaurantActivity.this).load(R.drawable.personicon))
                                .into(holder.restaurant_photo);


                        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ShowToast")
                            @Override
                            public void onClick(View v) {

                                prefs = getSharedPreferences("MyData", MODE_PRIVATE);

                                if (    prefs.getString("Name", "").isEmpty() ||
                                        prefs.getString("Email", "").isEmpty() ||
                                        prefs.getString("Phone", "").isEmpty() ||
                                        prefs.getString("Address", "").isEmpty()) {

                                    //Il profilo è da riempire
//                                    Toast.makeText(SearchRestaurantActivity.this, "Your profile is not complete", Toast.LENGTH_LONG);
                                    Intent homepage = new Intent(SearchRestaurantActivity.this, ProfileActivity.class);
                                    startActivity(homepage);

                                } else {
                                    //il profilo è pieno e c'è in save preference
                                    //Avvio la seguente Activity
                                    SmartLogger.d(model.getId());
                                    RestaurantMenuActivity.start(mContext, model.getId());
                                }
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

                Query applyQuery;

                if (restaurantCategory != null) {

                    if ((!restaurantCategory.equals("All")) && (!restaurantCategory.equals("Qualsiasi"))) {

                        //restaurantRef arriva fino a profile
                        String queryText = restaurantCategory;
                        applyQuery = restaurantRef.orderByChild("foodCategory").startAt(queryText).endAt(queryText + "\uf8ff");

                        if (freeDeliveryCheckbox.isChecked()) {

                            applyQuery = restaurantRef.orderByChild("fCategoryANDdCost").startAt(queryText).endAt(queryText + "_000\uf8ff");
                        }

                    } else {

                        applyQuery = restaurantRef;

                        if (freeDeliveryCheckbox.isChecked()) {

                            applyQuery = restaurantRef.orderByChild("deliveryCost").startAt("0").endAt("00" + "\uf8ff");
                        }
                    }
                    options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                            .setQuery(applyQuery, Restaurant.class)
                            .build();
                } else {

                    applyQuery = restaurantRef;

                    if (freeDeliveryCheckbox.isChecked()) {

                        applyQuery = restaurantRef.orderByChild("deliveryCost").startAt("0").endAt("00" + "\uf8ff");
                    }

                    options = new FirebaseRecyclerOptions.Builder<Restaurant>()
                            .setQuery(applyQuery, Restaurant.class)
                            .build();
                }

                restaurantCategory = null;
                dialog.dismiss();
                onStart();
            }
        });

        dialog.show();
    }

    //todo, occuparsi del comportamento della searchView che popoli l'arrayList di ristoranti in base al nome digitato

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchItem:
                if (searchRestaurant.getVisibility() == View.GONE) {
                    searchRestaurant.setVisibility(View.VISIBLE);
                    searchRestaurant.onActionViewExpanded();
                    searchRestaurant.requestFocus();
                } else
                    searchRestaurant.setVisibility(View.GONE);

                break;
            case R.id.filterItem:
                showFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_restaurant_menu, menu);
        return true;
    }

    public void initializeNavigationDrawer(){

        // Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(        // Three lines icon on the left corner
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the photo of the Navigation Bar Icon (Need to be completed: refresh when new image is updated)
        updateNavigatorPersonalIcon(navigationView);

        //TODO
        // Set restaurant name and email on navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        String name = prefs.getString("Name", "No name inserted");
        navUsername.setText(name);
        String email = prefs.getString("Email", "No email inserted");
        navEmail.setText(email);
    }

    public void updateNavigatorPersonalIcon(NavigationView navigationView){
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);
    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search_restaurant) {
            onBackPressed();
        } else if(id == R.id.nav_orders){
            Intent myIntent = new Intent(this, OrdersActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            this.startActivity(myIntent);

        } else if (id == R.id.nav_logout) {
            // LogoutFunction
            startLogout();
        }
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
                        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
                        editor = prefs.edit();

                        editor.clear();
                        editor.apply();
                        //startLogin();
                        Intent intent = new Intent(SearchRestaurantActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(GravityCompat.START)){
            layout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}

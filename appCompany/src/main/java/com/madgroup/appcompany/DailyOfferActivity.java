package com.madgroup.appcompany;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.madgroup.sdk.Dish;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.SmartLogger;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DailyOfferActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener, DailyOfferRecyclerAdapter.DailyOfferRecyclerListener {

    public static final int THUMBSIZE = 50;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private DailyOfferRecyclerAdapter adapter;
    private Dish currentDish;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private static final int CAMERA_PERMISSIONS_CODE = 1;
    private static final int GALLERY_PERMISSIONS_CODE = 2;
    public int iteration = 0;
    private Dialog currentDialog;

    FirebaseRecyclerOptions<Dish> options;
    private DatabaseReference dishRef;
    private StorageReference mStorageRef;

    String notificationTitle = "MAD Company";
    String notificationText;

    private String restaurantUid;

    private CircleImageView dishImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // FloatingActionButton fc = findViewById(R.id.add_button);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_daily_offer);
        stub.inflate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        initializeNavigationDrawer();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        restaurantUid = prefs.getString("currentUser", "");
        dishRef = database.getReference().child("Company").child("Menu").child(restaurantUid);

        //todo: interazione con il db
        Bitmap carbonaraIcon = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(this.getResources(), R.drawable.carbonara), THUMBSIZE, THUMBSIZE);
        Bitmap gnocchiIcon = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(this.getResources(), R.drawable.gnocchi), THUMBSIZE, THUMBSIZE);

        initDailyOfferRecyclerView();

        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {

            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();
        }
    }


    private void initDailyOfferRecyclerView() {

//        Query applyQuery = dishRef;

        options = new FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(dishRef, Dish.class)
                .build();


        recyclerView = this.findViewById(R.id.my_recycler_view);
        adapter = new DailyOfferRecyclerAdapter(this, this, restaurantUid);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getItemsFromDb();

        //
//        adapter.startListening();
//
//        // Serve per modificare il campo currentDialog dall'adapter
//        adapter.adapterhandler = new AdapterHandler() {
//            @Override
//            public void setCurrentDialog(Dialog dialog) {
//                DailyOfferActivity.this.currentDialog = dialog;
//            }
//        };
    }

    void getItemsFromDb() {
        dishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adapter.setDataset(parseDataset(dataSnapshot));
                dishRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ArrayList<Dish> parseDataset(DataSnapshot dataSnapshot) {

        ArrayList<Dish> dataset = new ArrayList<>();

        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
            for (Object obj : ((HashMap<String, Object>) dataSnapshot.getValue()).values()) {
                HashMap<String, Object> data = (HashMap<String, Object>) obj;
                dataset.add(new Dish(
                        data.get("id").toString(),
                        data.get("name").toString(),
                        data.get("price").toString(),
                        data.get("availableQuantity").toString(),
                        data.get("description").toString()
                ));
            }
        }

        return dataset;
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
            Intent myIntent = new Intent(this, ProfileActivity.class);
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
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START))
            layout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void initializeNavigationDrawer() {

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
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        updateNavigatorPersonalIcon();
    }

    public void updateNavigatorPersonalIcon() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);
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
                showDialog(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Dialog per la creazione di un NUOVO piatto
    //todo creazione piatto nel db
    private void showDialog(final Dish item) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.edit_dish);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dish_dialog);
        currentDialog = dialog;

        ImageButton dialogDismiss = (ImageButton) dialog.findViewById(R.id.dialogDismiss);
        ImageButton dialogConfirm = (ImageButton) dialog.findViewById(R.id.dialogConfirm);
        dishImage = (CircleImageView) dialog.findViewById(R.id.dishImage);
        final EditText editDishName = (EditText) dialog.findViewById(R.id.editDishName);
        final EditText editDishDescription = (EditText) dialog.findViewById(R.id.editDishDescription);
        final EditText editDishQuantity = (EditText) dialog.findViewById(R.id.editDishQuantity);
        final CurrencyEditText editPrice = dialog.findViewById(R.id.editPrice);

        if (item != null) {
            editDishName.setText(item.getName());
            editDishDescription.setText(item.getDescription());
            editDishQuantity.setText(item.getAvailableQuantity());
            editPrice.setText(item.getPrice());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("dish_pics")
                    .child(restaurantUid).child(item.getId());
            GlideApp.with(this)
                    .load(storageReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(GlideApp.with(this).load(R.drawable.ic_dish))
                    .into(dishImage);
        }

        dialogDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = ((BitmapDrawable) dishImage.getDrawable()).getBitmap();

                String local = editPrice.getLocale().toString();
                Long rawVal = editPrice.getRawValue();
                String formattedVal = editPrice.formatCurrency(Long.toString(rawVal));
                String floatStringVal = "";
                float floatPrice = 0;

                if (local.equals("en_US")) {
                    floatStringVal = formattedVal.replace(",", "").replace("$", "").replaceAll("\\s", "");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if (local.equals("en_GB")) {
                    floatStringVal = formattedVal.replace(",", "").replace("£", "").replaceAll("\\s", "");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if (local.equals("it_IT")) {
                    floatStringVal = formattedVal.replace(".", "").replace(",", ".").replace("€", "").replaceAll("\\s", "");
                    floatPrice = Float.parseFloat(floatStringVal);
                }

                if (editDishName.getText().toString().isEmpty() || editDishQuantity.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.requiredString), Toast.LENGTH_SHORT).show();
                } else if (floatPrice == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.requiredPrice), Toast.LENGTH_SHORT).show();
                } else {
                    //formatto la stringa prezzo da mettere nel db
                    BigDecimal dishPrice = new BigDecimal(floatPrice).setScale(2, RoundingMode.HALF_UP);//todo: sistemare crash
                    String stringPrice = String.valueOf(dishPrice);
                    if (local.equals("en_US")) {
                        stringPrice = stringPrice + " $";
                    } else if (local.equals("en_GB")) {
                        stringPrice = stringPrice + " £";
                    } else if (local.equals("it_IT")) {
                        stringPrice = stringPrice + " €";
                    }
                    //todo: aggiungere le immagini all'oggetto
                    if (editDishDescription.getText().toString().isEmpty()) {
                        currentDish = new Dish("", editDishName.getText().toString(), stringPrice,
                                editDishQuantity.getText().toString(), "");
                    } else {
                        currentDish = new Dish("", editDishName.getText().toString(), stringPrice,
                                editDishQuantity.getText().toString(), editDishDescription.getText().toString());
                    }

                    if (item == null) {
                        addDbDish(currentDish);
                    } else {
                        currentDish.setId(item.getId());
                        updateDbDish(currentDish);
                    }
                    getItemsFromDb();

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    void updateDbDish(Dish item) {
        final Map<String, Object> childUpdates = new HashMap<>();
        String dishId = item.getId();

        childUpdates.put("/" + dishId + "/" + "name", item.getName());
        childUpdates.put("/" + dishId + "/" + "availableQuantity", item.getAvailableQuantity());
        childUpdates.put("/" + dishId + "/" + "description", item.getDescription());
        childUpdates.put("/" + dishId + "/" + "price", item.getPrice());
        dishRef.updateChildren(childUpdates);
        uploadDishPhoto(dishId, item);
    }

    void addDbDish(Dish item) {
        String key = dishRef.push().getKey();
        item.setId(key);
        dishRef.child(key).setValue(item);
        uploadDishPhoto(key, item);
    }

    private void uploadDishPhoto(String dishID, Dish item) {

        final StorageReference photoRef = FirebaseStorage.getInstance().getReference("dish_pics")
                .child(restaurantUid).child(dishID);

        // imgProgressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar

        // TODO: Fare il check con l'immagine di default e decommentare
        // Se è l'immagine di default, non salvo niente ed eventualmente elimino quella presente.
//        if (isDefaultImage) {
//            deletePic();
//            imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
//            return;
//        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap img = ((BitmapDrawable)(dishImage.getDrawable())).getBitmap();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        final UploadTask uploadTask = photoRef.putBytes(data); // Salvo l'immagine nello storage
        /* Gestione successo e insuccesso */
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(DailyOfferActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload success
                /* Ricavo Uri e lo inserisco nel db */
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (!task.isSuccessful()) {
                            // Handle failures
                            // imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(DailyOfferActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
                        } else {
                            //imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(getApplicationContext(), "Pic Saved!", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.profile_camera_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemCamera:
                int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA);
                boolean userPreviousDeniedRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA);

                if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startCamera(this);
                } else {
                    if (userPreviousDeniedRequest) {
                        Toast.makeText(getApplicationContext(), getString(R.string.camerapermission), Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_CODE);
                    }
                }
                return true;

            case R.id.itemGallery:

                int readStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                boolean userPreviousDeniedGalleryRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (readStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startGallery(this);
                } else {
                    if (userPreviousDeniedGalleryRequest) {
                        Toast.makeText(getApplicationContext(), getString(R.string.gallerypermission), Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSIONS_CODE);
                    }
                }
                return true;

            case R.id.itemDelete:
                // Set the default image
                Drawable defaultImg = getResources().getDrawable(R.drawable.ic_dish);
                CircleImageView dishImage = (CircleImageView) currentDialog.findViewById(R.id.dishImage);
                dishImage.setImageDrawable(defaultImg);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Retreiving the tumbnail: the Android Camera application encodes the photo in the return Intent
        delivered to onActivityResult() as a small Bitmap in the extras, under the key "data" */
        if (requestCode == MyImageHandler.Camera_Pick_Code && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            CircleImageView dishImage = (CircleImageView) currentDialog.findViewById(R.id.dishImage);
            dishImage.setImageBitmap(bitmap);
        }

        if (requestCode == MyImageHandler.Gallery_Pick_Code && resultCode == RESULT_OK && data != null) {
            try {
                Uri selectedImageUri = data.getData();
                /*
                    Getting the result and show it in the ImageButton
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    personalImage.setImageBitmap(bitmap);
                */
                if (selectedImageUri != null) {
                    MyImageHandler.getInstance().startCrop(selectedImageUri, iteration, this, this);
                    iteration++;
                }
            } catch (Exception e) {
                SmartLogger.e("Error in selectedImageUri: " + e.getMessage());
            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null)
                handleCropResult(data);
        }
    }

    //  Richiamata dopo il crop
    private void handleCropResult(@NonNull Intent result) {
        final Uri uri = UCrop.getOutput(result);
        if (uri != null) {
            try {
                CircleImageView dishImage = (CircleImageView) currentDialog.findViewById(R.id.dishImage);
                dishImage.setImageURI(uri);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    MyImageHandler.getInstance().startCamera(this);
                } else {
                    // permission denied!
                    Toast.makeText(getApplicationContext(), getString(R.string.camerapermission), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GALLERY_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startGallery(this);
                } else {
                    // permission denied!
                    Toast.makeText(getApplicationContext(), getString(R.string.gallerypermission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void removeItem(String id) {
        dishRef.child(id).removeValue();
        getItemsFromDb();
    }

    @Override
    public void updateItem(Dish item) {
        showDialog(item);
    }

    // Serve per modificare il campo currentDialog dall'adapter
    public abstract class AdapterHandler {
        public void setCurrentDialog(Dialog dialog) {
        }
    }

}
package com.madgroup.appcompany;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.SmartLogger;
import com.yalantis.ucrop.UCrop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class DailyOfferActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    public static final int THUMBSIZE = 50;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private DailyOfferRecyclerViewAdapter adapter;
    private Dish currentDish;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private static final int CAMERA_PERMISSIONS_CODE = 1;
    private static final int GALLERY_PERMISSIONS_CODE = 2;
    public int iteration = 0;
    private Dialog currentDialog;

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


        myList.add(new Dish("0","Spaghetti alla Carbonara", 5.50f, 5, "" +
                "Guanciale, uova, pecorino, pepe nero.",carbonaraIcon));
        myList.add(new Dish("1","Gnocchi di patet", 5.90f, 19, "Patate, " +
                "Farina, " +
                "Uova, ", gnocchiIcon));
        myList.add(new Dish("2","Lasagne alla Bolognese", 8.50f, 3, "Ragù, Besciamella." +
                "Olio extravergine d'oliva, " +
                "Pepe nero, ", gnocchiIcon));

        initDailyOfferRecyclerView();
    }


    private void initDailyOfferRecyclerView() {
        recyclerView = this.findViewById(R.id.my_recycler_view);
        adapter = new DailyOfferRecyclerViewAdapter(this, myList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Serve per modificare il campo currentDialog dall'adapter
        adapter.adapterhandler = new AdapterHandler() {
            @Override
            public void setCurrentDialog(Dialog dialog) {
                DailyOfferActivity.this.currentDialog = dialog;
            }
        };
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

    // Dialog per la creazione di un NUOVO piatto
    private void showDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.edit_dish);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dish_dialog);
        currentDialog = dialog;

        ImageButton dialogDismiss = (ImageButton) dialog.findViewById(R.id.dialogDismiss);
        ImageButton dialogConfirm = (ImageButton) dialog.findViewById(R.id.dialogConfirm);
        final CircleImageView dishImage = (CircleImageView) dialog.findViewById(R.id.dishImage);
        final EditText editDishName = (EditText) dialog.findViewById(R.id.editDishName);
        final EditText editDishDescription = (EditText) dialog.findViewById(R.id.editDishDescription);
        final EditText editDishQuantity = (EditText) dialog.findViewById(R.id.editDishQuantity);
        final CurrencyEditText editPrice = dialog.findViewById(R.id.editPrice);

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

                if(local.equals("en_US")){
                    floatStringVal = formattedVal.replace(",", "").replace("$", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if(local.equals("en_GB")){
                    floatStringVal = formattedVal.replace(",", "").replace("£", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                } else if(local.equals("it_IT")){
                    floatStringVal = formattedVal.replace(".", "").replace(",", ".").replace("€", "").replaceAll("\\s","");
                    floatPrice = Float.parseFloat(floatStringVal);
                }

                if (editDishName.getText().toString().isEmpty() || editDishQuantity.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.requiredString), Toast.LENGTH_SHORT).show();
                } else if (floatPrice==0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.requiredPrice), Toast.LENGTH_SHORT).show();
                } else {
                    if(editDishDescription.getText().toString().isEmpty()) {
                        currentDish = new Dish("1", editDishName.getText().toString(), floatPrice,
                                Integer.parseInt(editDishQuantity.getText().toString()), "", bitmap);
                    } else {
                        BigDecimal dishPrice = new BigDecimal(currentDish.getPrice()).setScale(2, RoundingMode.HALF_UP);

                        currentDish = new Dish("1", editDishName.getText().toString(), floatPrice,
                                Integer.parseInt(editDishQuantity.getText().toString()), editDishDescription.getText().toString(), bitmap);
                    }
                    myList.add(currentDish);
                    adapter.notifyItemInserted(myList.size() - 1);
                    adapter.notifyItemRangeChanged(myList.size() - 1, myList.size());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
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

                if (cameraPermission== PackageManager.PERMISSION_GRANTED) {
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

                if (readStoragePermission==PackageManager.PERMISSION_GRANTED) {
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
            if (data!=null)
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

    // Serve per modificare il campo currentDialog dall'adapter
    public abstract class AdapterHandler
    {
        public void setCurrentDialog(Dialog dialog) {}
    }

}


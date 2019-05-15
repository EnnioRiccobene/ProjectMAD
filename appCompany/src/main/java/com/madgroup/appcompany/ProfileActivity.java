package com.madgroup.appcompany;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.RestaurantProfile;
import com.madgroup.sdk.SmartLogger;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.annotation.GlideModule;

import java.io.InputStream;


public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    private EditText editCategory;
    private String[] listItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private int categoriesCount = 0;
    static public String currentUser;
    private CircleImageView personalImage;
    private EditText name;
    private EditText email;
    // private EditText password;
    private EditText phone;
    private EditText address;
    private EditText additionalInformation;
    private CurrencyEditText deliveryCost;
    private CurrencyEditText minimumOrder;
    private Boolean modifyingInfo;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SearchActivity";
    private static final int CAMERA_PERMISSIONS_CODE = 13;
    private static final int GALLERY_PERMISSIONS_CODE = 2;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    public int iteration = 0;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private static final int CONFIRM_OR_REJECT_CODE = 1;

    public static final int TEXT_REQUEST = 1;

    private static final int RC_SIGN_IN = 3;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private boolean isDefaultImage;
    private ProgressBar progressBar;
    private ProgressBar imgProgressBar;

    String notificationTitle = "MAD Company";
    String notificationText;

    TextView hours;
    ImageView arrowbtn;
    ExpandableLayout hiddenHours;
    TextView modifyHours;
    TextView mondayHour;
    TextView tuesdayHour;
    TextView wednesdayHour;
    TextView thursdayHour;
    TextView fridayHour;
    TextView saturdayHour;
    TextView sundayHour;
    NestedScrollView nestedScrollView;

    public static final String EXTRA_MESSAGE = "com.madgroup.appcompany.extra.MESSAGE";
    private LinkedHashMap<String, String> hourValue = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (FirebaseAuth.getInstance().getCurrentUser()==null) {
//            // Utente non ancora loggato
//            startLogin();
//        }

        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_profile);
        stub.inflate();
        this.setTitle("Profile");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();


        // Getting the instance of Firebase
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        hours = findViewById(R.id.hours);
        arrowbtn = findViewById(R.id.arrowbtn);
        hiddenHours = findViewById(R.id.hiddenhours);
        modifyHours = findViewById(R.id.modifyhours);
        mondayHour = findViewById(R.id.mondayhour);
        tuesdayHour = findViewById(R.id.tuesdayhour);
        wednesdayHour = findViewById(R.id.wednesdayhour);
        thursdayHour = findViewById(R.id.thursdayhour);
        fridayHour = findViewById(R.id.fridayhour);
        saturdayHour = findViewById(R.id.saturdayhour);
        sundayHour = findViewById(R.id.sundayhour);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        //Mi assicuro che l'Expandable Layout sia chiuso all'apertura dell'app
        if (!hiddenHours.isExpanded()) {
            hiddenHours.collapse();
        }

        editCategory = findViewById(R.id.editTextFoodCategory);
        listItems = getResources().getStringArray(R.array.food_categories);
        checkedItems = new boolean[listItems.length];
        categoriesCount = 0;
        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                mBuilder.setTitle(getString(R.string.dialog_title));
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (categoriesCount < 3) {
                                if (!mUserItems.contains(position)) {
                                    mUserItems.add(position);
                                    categoriesCount++;
                                }
                            } else {
                                checkedItems[position] = false;
                                Toast.makeText(getApplicationContext(), getString(R.string.toast_checkbox_limit), Toast.LENGTH_SHORT).show();
                                ((AlertDialog) dialog).getListView().setItemChecked(position, false);    // Do not select the 4th
                            }
                        } else if (mUserItems.contains(position)) {
                            int a = mUserItems.indexOf(position);
                            mUserItems.remove(a);
                            categoriesCount--;
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        editCategory.setText(item);
                    }
                });

                mBuilder.setNeutralButton(getString(R.string.clear_all_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            categoriesCount = 0;
                            editCategory.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        // Defining EditText
        personalImage = findViewById(R.id.imagePersonalPhoto);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        // password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        additionalInformation = findViewById(R.id.additionalInformation);
        deliveryCost = findViewById(R.id.deliveryCost);
        minimumOrder = findViewById(R.id.minimumOrder);
        modifyingInfo = false;
        progressBar = findViewById(R.id.progressBar);
        // imgProgressBar = findViewById(R.id.imgProgressBar);

        // Set all field to unclickable
        setFieldUnclickable();

        hideFields();
        // imgProgressBar.setVisibility(View.INVISIBLE); // Nascondo la progress bar dell'immagine
        isDefaultImage = true;
        //downloadProfilePic();
        //loadFieldsFromFirebase();

        notificationText = getResources().getString(R.string.notification_text);
        if (prefs.contains("currentUser")) {
            // Utente già loggato
            initializeNavigationDrawer();
            loadFieldsFromFirebase();
            downloadProfilePic();

            DatabaseReference newOrderRef = database.getReference().child("Company").child("Reservation").child("Pending").child("NotifyFlag").child(prefs.getString("currentUser", ""));
            NotificationHandler notify = new NotificationHandler(newOrderRef, this, this, notificationTitle, notificationText);
            notify.newOrderListner();

        } else {
            startLogin();
        }
    }

    //I seguenti due metodi sono per gestire l'animazione della freccia durante l'interazione con l'ExpandableLayout
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

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily_offer) {
            // Change activity to Daily Offer
            Intent myIntent = new Intent(this, DailyOfferActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_reservations) {
            // Change activity to Reservations
            Intent myIntent = new Intent(this, ReservationActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);

        } else if (id == R.id.nav_profile) {
            onBackPressed();
        } else if (id == R.id.nav_logout) {
            startLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Edit icon
            case R.id.editIcon:
                if (!modifyingInfo) {         // I pressed for modifying data
                    modifyingInfo = true;
                    setFieldClickable();
                    Toast.makeText(getApplicationContext(), "Edit fields", Toast.LENGTH_SHORT).show();
                } else {                      // I pressed to come back
                    modifyingInfo = false;
                    setFieldUnclickable();
                    saveFieldsOnFirebase();
                    //if (!isDefaultImage)
                        uploadProfilePic(((BitmapDrawable)personalImage.getDrawable()).getBitmap());
                    //else
                        //deleteProfilePic();
                }
                updateNavigatorInformation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // What happens if I press back button
    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
            return;
        }

        if (modifyingInfo) {
            modifyingInfo = false;
            setFieldUnclickable();
            saveFieldsOnFirebase();
            //if (!isDefaultImage)
                uploadProfilePic(((BitmapDrawable)personalImage.getDrawable()).getBitmap());
            //else
                //deleteProfilePic();
        } else
            super.onBackPressed();
    }

    // Functions
    private void setFieldUnclickable() {
        name.setEnabled(false);
        email.setEnabled(false);
        // password.setEnabled(false);
        phone.setEnabled(false);
        address.setEnabled(false);
        additionalInformation.setEnabled(false);
        deliveryCost.setEnabled(false);
        minimumOrder.setEnabled(false);
        personalImage.setEnabled(false);
        editCategory.setEnabled(false);
        modifyHours.setEnabled(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        // password.setEnabled(true);
        phone.setEnabled(true);
        address.setEnabled(true);
        additionalInformation.setEnabled(true);
        deliveryCost.setEnabled(true);
        minimumOrder.setEnabled(true);
        personalImage.setEnabled(true);
        editCategory.setEnabled(true);
        modifyHours.setEnabled(true);
    }

    private void loadFields() {
        if (prefs.contains("Name"))
            name.setText(prefs.getString("Name", ""));
        if (prefs.contains("Email"))
            email.setText(prefs.getString("Email", ""));
        // if (prefs.contains("Password"))
        // password.setText(prefs.getString("Password", ""));
        if (prefs.contains("Phone"))
            phone.setText(prefs.getString("Phone", ""));
        if (prefs.contains("Address"))
            address.setText(prefs.getString("Address", ""));
        if (prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));
        if(prefs.contains("DeliveryCost"))
            deliveryCost.setText(prefs.getString("DeliveryCost", ""));
        if(prefs.contains("MinOrder"))
            minimumOrder.setText(prefs.getString("MinOrder", ""));
        if (prefs.contains("FoodCounter"))
            categoriesCount = prefs.getInt("FoodCounter", 0);
        if (prefs.contains("checkedItems_size")) {
            int size = prefs.getInt("checkedItems_size", 0);
            for (int i = 0; i < size; i++) {
                checkedItems[i] = prefs.getBoolean("checkedItems_" + i, false);
//                SmartLogger.d("LoadFields: array["+i+"] = " + checkedItems[i]);
            }
            int listsize = prefs.getInt("mUserItems_size", 0);
            for (int i = 0; i < listsize; i++) {
                mUserItems.add(prefs.getInt("listItems_" + i, 0));
            }
        }
        if (prefs.contains("EditCategory"))
            editCategory.setText(prefs.getString("EditCategory", ""));
        if (prefs.contains("MondayHour"))
            mondayHour.setText(prefs.getString("MondayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("TuesdayHour"))
            tuesdayHour.setText(prefs.getString("TuesdayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("WednesdayHour"))
            wednesdayHour.setText(prefs.getString("WednesdayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("ThursdayHour"))
            thursdayHour.setText(prefs.getString("ThursdayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("FridayHour"))
            fridayHour.setText(prefs.getString("FridayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("SaturdayHour"))
            saturdayHour.setText(prefs.getString("SaturdayHour", getResources().getString(R.string.Closed)));
        if (prefs.contains("SundayHour"))
            sundayHour.setText(prefs.getString("SundayHour", getResources().getString(R.string.Closed)));
        // restoreImageContent();
    }

    private void saveFields() {
        editor.putString("Name", name.getText().toString());
        editor.putString("Email", email.getText().toString());
        // editor.putString("Password", password.getText().toString());
        editor.putString("Phone", phone.getText().toString());
        editor.putString("Address", address.getText().toString());
        editor.putString("Information", additionalInformation.getText().toString());
        editor.putString("DeliveryCost", deliveryCost.getText().toString());
        editor.putString("MinOrder", minimumOrder.getText().toString());
        editor.putInt("FoodCounter", categoriesCount);
        editor.putInt("checkedItems_size", checkedItems.length);
        editor.putInt("mUserItems_size", mUserItems.size());
        for (int i = 0; i < checkedItems.length; i++) {
            editor.putBoolean("checkedItems_" + i, checkedItems[i]);
//            SmartLogger.d("SaveFields: array["+i+"] = " + checkedItems[i]);
        }
        for (int i = 0; i < mUserItems.size(); i++) {
            editor.putInt("listItems_" + i, mUserItems.get(i));
        }
        editor.putString("EditCategory", editCategory.getText().toString());
        editor.putString("MondayHour", mondayHour.getText().toString());
        editor.putString("TuesdayHour", tuesdayHour.getText().toString());
        editor.putString("WednesdayHour", wednesdayHour.getText().toString());
        editor.putString("ThursdayHour", thursdayHour.getText().toString());
        editor.putString("FridayHour", fridayHour.getText().toString());
        editor.putString("SaturdayHour", saturdayHour.getText().toString());
        editor.putString("SundayHour", sundayHour.getText().toString());
        editor.apply();

        updateNavigatorInformation();
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
                updateNavigatorInformation();
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
                updateNavigatorInformation();
                return true;

            case R.id.itemDelete:
                // Set the default image
                Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                personalImage.setImageDrawable(defaultImg);
                isDefaultImage = true;
                updateNavigatorInformation();
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
            personalImage.setImageBitmap(bitmap);
            isDefaultImage = false;
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

        //Gestione dei valiri di ritorno in caso di richiesta modifica orari
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle extrasBundle = data.getExtras();

                // Aggiunto il "&& extrasBundle != null" per evitare il crash dell'app
                if (extrasBundle != null){
                    if (!extrasBundle.isEmpty()) {
                        if (extrasBundle.containsKey(getResources().getString(R.string.Monday))) {
                            mondayHour.setText(extrasBundle.getString(getResources().getString(R.string.Monday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Tuesday))) {
                            tuesdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Tuesday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Wednesday))) {
                            wednesdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Wednesday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Thursday))) {
                            thursdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Thursday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Friday))) {
                            fridayHour.setText(extrasBundle.getString(getResources().getString(R.string.Friday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Saturday))) {
                            saturdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Saturday)));
                        }
                        if (extrasBundle.containsKey(getResources().getString(R.string.Sunday))) {
                            sundayHour.setText(extrasBundle.getString(getResources().getString(R.string.Sunday)));
                        }
                    }
                }
            }
        }
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                editor.putString("currentUser", user.getUid());
                editor.putString("Name", user.getDisplayName());
                editor.putString("Email", user.getEmail());
                editor.apply();
                initializeNavigationDrawer();
                loadFieldsFromFirebase();
                downloadProfilePic();
            } else {
                if (response==null) {
                    // Back button pressed
                } else {
                    Toast.makeText(this, "Error: "+ Objects.requireNonNull(response.getError()).getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //  Richiamata dopo il crop
    private void handleCropResult(@NonNull Intent result) {
        final Uri uri = UCrop.getOutput(result);
        if (uri != null) {
            try {
                personalImage.setImageURI(uri);
                isDefaultImage = false;
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImageContent() {
        Bitmap bitmap = ((BitmapDrawable) personalImage.getDrawable()).getBitmap();
        String encoded = MyImageHandler.getInstance().fromBitmapToString(bitmap);
        editor.putString("PersonalImage", encoded);
        editor.apply();
        updateNavigatorInformation();
    }

    private void restoreImageContent() {
        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
        if (!ImageBitmap.equals("NoImage")) {
            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            personalImage.setImageBitmap(bitmap);
        } else {
            Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
            personalImage.setImageDrawable(defaultImg);
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
        updateNavigatorInformation();
    }

    public void updateNavigatorInformation() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView nav_profile_icon = (CircleImageView) headerView.findViewById(R.id.nav_profile_icon);
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(nav_profile_icon);

//        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
//        if (!ImageBitmap.equals("NoImage")) {
//            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            nav_profile_icon.setImageBitmap(bitmap);
//        } else {
//            Drawable defaultImg = getResources().getDrawable(R.mipmap.ic_launcher_round);
//            nav_profile_icon.setImageDrawable(defaultImg);
//        }

        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        String name = prefs.getString("Name", "");
        if (!name.equals(""))
            navUsername.setText(name);
        String email = prefs.getString("Email", "");
        if (!email.equals(""))
            navEmail.setText(email);
    }

    public void launchedithours(View view) {
        Intent intentHours = new Intent(this, EditOpeningHoursActivity.class);
        ArrayList<String> daysHours = new ArrayList<>();
        daysHours.add(mondayHour.getText().toString());
        daysHours.add(tuesdayHour.getText().toString());
        daysHours.add(wednesdayHour.getText().toString());
        daysHours.add(thursdayHour.getText().toString());
        daysHours.add(fridayHour.getText().toString());
        daysHours.add(saturdayHour.getText().toString());
        daysHours.add(sundayHour.getText().toString());

        SmartLogger.d("ProvaIntentSize: ", String.valueOf(daysHours.size()));
        for (int i = 0; i < daysHours.size(); i++) {
            SmartLogger.d("ProvaIntent: ", daysHours.get(i));
        }

        intentHours.putStringArrayListExtra(EXTRA_MESSAGE, daysHours);

        startActivityForResult(intentHours, TEXT_REQUEST);
    }

    //todo: aggiungere il menù nell'activity per la modifica degli orari con backbutton e conferma


//    public void populateDatabaseWithDummyValues() {
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        database.child("Company").removeValue();
//
//        ArrayList<RestaurantProfile> mRestaurantList = new ArrayList<>();
//        mRestaurantList.add(new RestaurantProfile("Name1", "111", "Via malta", "email1", "Pizzeria", "5,00", "3,00", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h"));
//        mRestaurantList.add(new RestaurantProfile("Name2", "222", "Via malta", "email2", "Pizzeria", "5,00", "3,00", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h"));
//        mRestaurantList.add(new RestaurantProfile("Name3", "333", "Via malta", "email3", "Pizzeria", "5,00", "3,00", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h"));
//        mRestaurantList.add(new RestaurantProfile("Name4", "444", "Via malta", "email4", "Pizzeria", "5,00", "3,00", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h", "Open 24h"));
//        DatabaseReference profileRef = database.child("Company").child("Profile");
//
//        for (RestaurantProfile element : mRestaurantList) {
//            String email = element.getEmail();
//            profileRef.child(email).setValue(element);
//        }
//
//        ArrayList<OrderedDish> orderedDishList = new ArrayList<>();
//        orderedDishList.add(new OrderedDish("Food1", 2, 6.4f));
//        orderedDishList.add(new OrderedDish("Food2", 6, 10.4f));
//        orderedDishList.add(new OrderedDish("Food3", 3, 6f));
//        orderedDishList.add(new OrderedDish("Food4", 5, 9.4f));
//        orderedDishList.add(new OrderedDish("Food5", 7, 1.5f));
//
//        // Compute total Price
//        float x = 0;
//        for (OrderedDish element : orderedDishList)
//            x += element.getPrice() * element.getQuantity();
//        DecimalFormat df = new DecimalFormat("#.##");
//        df.setMinimumFractionDigits(2);
//        String price = df.format(x);
//
//        ArrayList<Reservation> mReservationList = new ArrayList<>();
//        mReservationList.add(new Reservation("Via Moretta 2", "18:45", 0, price));
//        mReservationList.add(new Reservation("Piazza Sabotino 8", "19:00", 0, price));
//        mReservationList.add(new Reservation("Via Villarbasse 12", "20:45", 0, price));
//        mReservationList.add(new Reservation("Corso Rosselli 15", "21:00", 0, price));
//        mReservationList.add(new Reservation("Address5", "Delivery Time", 0, price));
//        mReservationList.add(new Reservation("Address6", "Delivery Time", 0, price));
//        mReservationList.add(new Reservation("Address7", "Delivery Time", 0, price));
//        mReservationList.add(new Reservation("Address8", "Delivery Time", 0, price));
//
//        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
//        DatabaseReference orderedFoodRef = database.child("Company").child("Reservation").child("OrderedFood");
//
//        for (int i = 1; i < 5; i++) {
//            for (Reservation element : mReservationList) {
//                String orderID = pendingReservationRef.push().getKey();
//                element.setOrderID(orderID);
//                pendingReservationRef.child("email" + i).child(orderID).setValue(element);
//                orderedFoodRef.child(orderID).setValue(orderedDishList);
//            }
//        }
//
//    }

    private void saveFieldsOnFirebase() {
        // progressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        RestaurantProfile currentUser = new RestaurantProfile(uid, name.getText().toString(), phone.getText().toString(),
                address.getText().toString(), email.getText().toString(), editCategory.getText().toString(),
                minimumOrder.getText().toString(), deliveryCost.getText().toString(), mondayHour.getText().toString(), tuesdayHour.getText().toString(),
                wednesdayHour.getText().toString(), thursdayHour.getText().toString(), fridayHour.getText().toString(),
                saturdayHour.getText().toString(), sundayHour.getText().toString(), additionalInformation.getText().toString());

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //database.getReference("Profiles").child("Restaurants")
                database.getReference("Company").child("Profile")
                .child(currentUid)
                .setValue(currentUser, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            // progressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Dati salvati correttamente nel db
                            // Aggiorno le shared prefs
                            saveFields();
                            // progressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                        }
                    }
                });
    }

    private void uploadProfilePic(Bitmap bitmap) {

        // imgProgressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar

        // TODO: Fare il check con l'immagine di default e decommentare
        // Se è l'immagine di default, non salvo niente ed eventualmente elimino quella presente.
//        if (isDefaultImage) {
//            deleteProfilePic();
//            imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
//            return;
//        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        final StorageReference fileReference = storage.getReference("profile_pics")
                .child("restaurants")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final UploadTask uploadTask = fileReference.putBytes(data); // Salvo l'immagine nello storage
        /* Gestione successo e insuccesso */
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ProfileActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
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
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (!task.isSuccessful()) {
                            // Handle failures
                            // imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(ProfileActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
                        } else {
                            // imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(getApplicationContext(), "Pic Saved!", Toast.LENGTH_SHORT).show();
                            updateNavigatorInformation();
                        }
                    }
                });
            }
        });
    }

    private void downloadProfilePic() {

        /*
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Scarico l'immagine e la setto
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                personalImage.setImageBitmap(bitmap);
                isDefaultImage = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                int errorCode = ((StorageException) exception).getErrorCode();
                if (errorCode==StorageException.ERROR_OBJECT_NOT_FOUND) {
                    // La foto non è presente: carico immagine di default
                    Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                    personalImage.setImageDrawable(defaultImg);
                    isDefaultImage = true;
                }
            }
        });
        */
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(personalImage);
    }

    private void deleteProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("restaurants")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Delete the file
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // An error occurred
            }
        });
    }

    private void hideFields() {
        nestedScrollView.setVisibility(View.GONE);
//        name.setVisibility(View.INVISIBLE);
//        email.setVisibility(View.INVISIBLE);
//        phone.setVisibility(View.INVISIBLE);
//        address.setVisibility(View.INVISIBLE);
//        additionalInformation.setVisibility(View.INVISIBLE);
//        deliveryCost.setVisibility(View.INVISIBLE);
//        minimumOrder.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar
    }

    private void showFields() {
        nestedScrollView.setVisibility(View.VISIBLE);
//        name.setVisibility(View.VISIBLE);
//        email.setVisibility(View.VISIBLE);
//        phone.setVisibility(View.VISIBLE);
//        address.setVisibility(View.VISIBLE);
//        additionalInformation.setVisibility(View.VISIBLE);
//        deliveryCost.setVisibility(View.VISIBLE);
//        minimumOrder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);  // Nascondo la progress bar
    }

    private void loadFieldsFromFirebase() {

        database.getReference("Company").child("Profile")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RestaurantProfile restaurant = dataSnapshot.getValue(RestaurantProfile.class);

                        if (restaurant!=null) {
                            // Utente già registrato: setto i campi
                            name.setText(restaurant.getName());
                            phone.setText(restaurant.getPhoneNumber());
                            address.setText(restaurant.getAddress());
                            additionalInformation.setText(restaurant.getAdditionalInformation());
                            email.setText(restaurant.getEmail());
                            deliveryCost.setText(restaurant.getDeliveryCost());
                            minimumOrder.setText(restaurant.getMinOrder());
                            editCategory.setText(restaurant.getFoodCategory());
                            mondayHour.setText(restaurant.getMondayOpeningHours());
                            tuesdayHour.setText(restaurant.getTuesdayOpeningHours());
                            wednesdayHour.setText(restaurant.getWednesdayOpeningHours());
                            thursdayHour.setText(restaurant.getThursdayOpeningHours());
                            fridayHour.setText(restaurant.getFridayOpeningHours());
                            saturdayHour.setText(restaurant.getSaturdayOpeningHours());
                            sundayHour.setText(restaurant.getSundayOpeningHours());
                            showFields();

                            // Aggiorno le shared prefs
                            editor.putString("Name", restaurant.getName());
                            editor.putString("Email", restaurant.getEmail());
                            editor.putString("Phone", restaurant.getPhoneNumber());
                            editor.putString("Address", restaurant.getAddress());
                            editor.apply();
                            updateNavigatorInformation();

                        } else {
                            // Utente appena registrato: inserisco un nodo nel database e setto i campi nome e email
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            RestaurantProfile currentUser = new RestaurantProfile(user.getUid(), user.getDisplayName(), user.getEmail());
                            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //database.getReference("Profiles").child("Restaurants")
                            database.getReference("Company").child("Profile")
                                    .child(currentUid).setValue(currentUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError!= null) {
                                        Toast.makeText(getApplicationContext(), "Connection error.", Toast.LENGTH_SHORT).show();
                                        // todo: rimuovere la registrazione dell'utente
                                    } else {
                                        name.setText(user.getDisplayName());
                                        email.setText(user.getEmail());
                                        // Aggiorno le shared prefs
                                        editor.putString("Name", user.getDisplayName());
                                        editor.putString("Email", user.getEmail());
                                        editor.apply();
                                        showFields();
                                        updateNavigatorInformation();
                                    }
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }



    private void startLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.personicon)
                        .build(),
                RC_SIGN_IN);
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
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });
    }


}

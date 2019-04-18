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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.SmartLogger;
import com.yalantis.ucrop.UCrop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener{

    private EditText editCategory;
    private String[] listItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private int categoriesCount = 0;

    private CircleImageView personalImage;
    private EditText name;
    private EditText email;
    // private EditText password;
    private EditText phone;
    private EditText address;
    private EditText additionalInformation;
    private Boolean modifyingInfo;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String TAG = "SearchActivity";
    private static final int CAMERA_PERMISSIONS_CODE = 1;
    private static final int GALLERY_PERMISSIONS_CODE = 2;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    public int iteration = 0;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    public static final int TEXT_REQUEST = 1;

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
        setContentView(R.layout.activity_main);

        createPendingReservationList();

        // START DATABASE TEST


//        ArrayList<orderedDish> orderedDishList = new ArrayList<orderedDish>();
//        orderedDishList.add(new orderedDish("Food1", 2, 6.4f));
//        orderedDishList.add(new orderedDish("Food2", 6, 10.4f));
//        orderedDishList.add(new orderedDish("Food3", 3, 6f));
//        orderedDishList.add(new orderedDish("Food4", 5, 9.4f));
//        orderedDishList.add(new orderedDish("Food5", 7, 1.5f));
//
//        // Compute total Price
//        float x = 0;
//        for (orderedDish element : orderedDishList)
//            x += element.getPrice() * element.getQuantity();
//        DecimalFormat df = new DecimalFormat("#.##");
//        df.setMinimumFractionDigits(2);
//        String price = df.format(x);
//
//        ArrayList<Reservation> mReservationList = new ArrayList<>();
//        mReservationList.add(new Reservation("Via Moretta 2", "18:45", 2, price));
//        mReservationList.add(new Reservation("Piazza Sabotino 8","19:00", 3, price));
//        mReservationList.add(new Reservation("Via Villarbasse 12" , "20:45", 2, price));
//        mReservationList.add(new Reservation("Corso Rosselli 15", "21:00", 2, price));
//        mReservationList.add(new Reservation("Address5","Delivery Time", 2, price));
//        mReservationList.add(new Reservation("Address6" , "Delivery Time", 2, price));
//        mReservationList.add(new Reservation("Address7", "Delivery Time", 2, price));
//        mReservationList.add(new Reservation("Address8","Delivery Time" , 3, price));
//        mReservationList.add(new Reservation("Address9" , "Delivery Time", 3, price));
//        mReservationList.add(new Reservation("Address10", "Delivery Time", 2, price));
//        mReservationList.add(new Reservation("Address11","Delivery Time", 2, price));
//        mReservationList.add(new Reservation("Address12" , "Delivery Time", 2, price));
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("History");
//        DatabaseReference orderedFoodRef = database.child("Company").child("Reservation").child("OrderedFood");
//
//        for (Reservation element : mReservationList) {
//            String orderID = pendingReservationRef.push().getKey();
//            element.setOrderID(orderID);
//            pendingReservationRef.child(orderID).setValue(element);
//            orderedFoodRef.child(orderID).setValue(orderedDishList);
//        }












        // END DATABASE TEST



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
        if(!hiddenHours.isExpanded()){
            hiddenHours.collapse();
        }

        editCategory = findViewById(R.id.editTextFoodCategory);
        listItems = getResources().getStringArray(R.array.food_categories);
        checkedItems = new boolean[listItems.length];
        categoriesCount = 0;
        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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
                                ((AlertDialog)dialog).getListView().setItemChecked(position, false);    // Do not select the 4th
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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // Defining EditText
        personalImage = findViewById(R.id.imagePersonalPhoto);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        // password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        additionalInformation = findViewById(R.id.additionalInformation);
        modifyingInfo = false;

        // Set all field to unclickable
        setFieldUnclickable();

        // Load saved information, if exist
        loadFields();

        initializeNavigationDrawer();

        // Set restaurant name and email on navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        if(name.getText() != null)
            navUsername.setText(name.getText());
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        if(email.getText() != null)
            navEmail.setText(email.getText());
    }

    //I seguenti due metodi sono per gestire l'animazione della freccia durante l'interazione con l'ExpandableLayout
    public void showHoursDetails(View view) {
        if (hiddenHours.isExpanded()) {
            createRotateAnimator(arrowbtn, 180f, 0f).start();
        } else {
            createRotateAnimator(arrowbtn, 0f, 180f).start();
        }
        hiddenHours.toggle();
//        nestedScrollView.scrollTo(0, view.getBottom()); todo: capire come far scrollare la vista verso il basso in automatico quando apro la view
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
                    saveFields();
                    Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                }
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
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(GravityCompat.START))
            layout.closeDrawer(GravityCompat.START);
        else if (modifyingInfo) {
            modifyingInfo = false;
            setFieldUnclickable();
            saveFields();
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
        if (prefs.contains("FoodCounter"))
            categoriesCount = prefs.getInt("FoodCounter", 0);
        if (prefs.contains("checkedItems_size")){
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
        restoreImageContent();
    }

    private void saveFields() {
        editor.putString("Name", name.getText().toString());
        editor.putString("Email", email.getText().toString());
        // editor.putString("Password", password.getText().toString());
        editor.putString("Phone", phone.getText().toString());
        editor.putString("Address", address.getText().toString());
        editor.putString("Information", additionalInformation.getText().toString());
        editor.putInt("FoodCounter", categoriesCount);
        editor.putInt("checkedItems_size", checkedItems.length);
        editor.putInt("mUserItems_size", mUserItems.size());
        for(int i = 0; i < checkedItems.length; i++) {
            editor.putBoolean("checkedItems_" + i, checkedItems[i]);
//            SmartLogger.d("SaveFields: array["+i+"] = " + checkedItems[i]);
        }
        for(int i = 0; i < mUserItems.size(); i++){
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

        // Set restaurant name and email on navigation header
        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        String username = prefs.getString("Name", "");
        navUsername.setText(username);

        String email = prefs.getString("Email", "");
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
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
                Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                personalImage.setImageDrawable(defaultImg);
                editor.remove("PersonalImage");
                editor.apply();
                updateNavigatorPersonalIcon();
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
            saveImageContent();
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

        //Gestione dei valiri di ritorno in caso di richiesta modifica orari
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bundle extrasBundle = data.getExtras();

                // Aggiunto il "&& extrasBundle != null" per evitare il crash dell'app
                if(!extrasBundle.isEmpty() && extrasBundle != null){
                    if(extrasBundle.containsKey(getResources().getString(R.string.Monday))){
                        mondayHour.setText(extrasBundle.getString(getResources().getString(R.string.Monday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Tuesday))){
                        tuesdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Tuesday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Wednesday))){
                        wednesdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Wednesday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Thursday))){
                        thursdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Thursday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Friday))){
                        fridayHour.setText(extrasBundle.getString(getResources().getString(R.string.Friday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Saturday))){
                        saturdayHour.setText(extrasBundle.getString(getResources().getString(R.string.Saturday)));
                    }
                    if(extrasBundle.containsKey(getResources().getString(R.string.Sunday))){
                        sundayHour.setText(extrasBundle.getString(getResources().getString(R.string.Sunday)));
                    }
                    saveFields();
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
                saveImageContent();
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
        updateNavigatorPersonalIcon();
    }

    private void restoreImageContent() {
        String ImageBitmap = prefs.getString("PersonalImage", "NoImage");
        if(!ImageBitmap.equals("NoImage")){
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








    public void createPendingReservationList() {
        reservationTab1.pendingReservation = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingReservationRef = database.child("Company").child("Reservation").child("Pending");
        pendingReservationRef.keepSynced(true);
//        DatabaseReference orderedFoodRef = database.child("Company").child("OrderedFood");

        pendingReservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SmartLogger.d("Getting data");
                SmartLogger.d(String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Reservation post = postSnapshot.getValue(Reservation.class);
                    SmartLogger.d("Get Data " + post.getOrderID());
                    reservationTab1.pendingReservation.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

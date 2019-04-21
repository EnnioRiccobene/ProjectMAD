package com.madgroup.appbikers;

import android.Manifest;
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
import de.hdodenhof.circleimageview.CircleImageView;

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

public class MainActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static String currentUser;
    private CircleImageView personalImage;
    private EditText name;
    private EditText email;
    // private EditText password;
    private EditText phone;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_main);
        stub.inflate();
        currentUser = "email1";

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // Defining EditText
        personalImage = findViewById(R.id.imagePersonalPhoto);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        // password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        additionalInformation = findViewById(R.id.additionalInformation);
        modifyingInfo = false;

        // Set all field to unclickable
        setFieldUnclickable();

        // Load saved information, if exist
        loadFields();

        navigationDrawerInitialization();
//        createPendingDeliveryList();



        // START DATABASE TEST
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference riderRef = database.getReference().child("Rider");
//        DatabaseReference pendingRef = riderRef.child("Delivery").child("Pending");
//        DatabaseReference profileRef = riderRef.child("Profile");
//
//        ArrayList<RiderProfile> profiles = new ArrayList<>();
//        profiles.add(new RiderProfile("Name1", "email1", "1"));
//        profiles.add(new RiderProfile("Name2", "email2", "2"));
//        profiles.add(new RiderProfile("Name3", "email3", "3"));
//        profiles.add(new RiderProfile("Name4", "email4", "4"));
//
//        for (RiderProfile element:profiles) {
//            profileRef.child(element.getEmail()).setValue(element);
//        }
//
//        ArrayList<Delivery> deliveries = new ArrayList<>();
//        deliveries.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
//        deliveries.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
//        deliveries.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
//        deliveries.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));
//
//        for (Delivery element:deliveries) {
//            String orderID = pendingRef.child(currentUser).push().getKey();
//        }
//
//
//
//        DeliveryPendingTab1.deliveriesList = new ArrayList<>();
//        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
//        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
//        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
//        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));
//
//        for (Delivery element : DeliveryPendingTab1.deliveriesList) {
//            String orderID = riderRef.child("Delivery").child("Pending").push().getKey();
//            element.setOrderID(orderID);
//            pendingRef.setValue(element);
//        }
//
//        DeliveryHistoryTab2.deliveriesList = new ArrayList<>();
//        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
//        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
//        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
//        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));

        DeliveryPendingTab1.deliveriesList = new ArrayList<>();
        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
        DeliveryPendingTab1.deliveriesList.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));

        DeliveryHistoryTab2.deliveriesList = new ArrayList<>();
        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Tano", "Via del pollo 99", "Via 123", "Cash"));
        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Michele", "Via della gallina 99", "Via 123", "Cash"));
        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Manfredi", "Via della pizza 99", "Via 123", "Cash"));
        DeliveryHistoryTab2.deliveriesList.add(new Delivery("Da Raffaele", "Via 99", "Via 123", "Cash"));


        // END DATABASE TEST

    }

    // What happens if I click on a icon on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // Edit icon
            case R.id.editIcon:
                if(!modifyingInfo){         // I pressed for modifying data
                    modifyingInfo = true;
                    setFieldClickable();
                    Toast.makeText(getApplicationContext(), "Edit fields", Toast.LENGTH_SHORT).show();
                }
                else{                      // I pressed to come back
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if(modifyingInfo){
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
        additionalInformation.setEnabled(false);
        personalImage.setEnabled(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        // password.setEnabled(true);
        phone.setEnabled(true);
        additionalInformation.setEnabled(true);
        personalImage.setEnabled(true);
    }

    private void loadFields() {
        if(prefs.contains("Name"))
            name.setText(prefs.getString("Name", ""));
        if(prefs.contains("Email"))
            email.setText(prefs.getString("Email", ""));
        // if(prefs.contains("Password"))
            // password.setText(prefs.getString("Password", ""));
        if(prefs.contains("Phone"))
            phone.setText(prefs.getString("Phone", ""));
        if(prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));
        restoreImageContent();
    }

    private void saveFields() {
        editor.putString("Name",name.getText().toString());
        editor.putString("Email",email.getText().toString());
        // editor.putString("Password",password.getText().toString());
        editor.putString("Phone",phone.getText().toString());
        editor.putString("Information",additionalInformation.getText().toString());
        editor.apply();
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
        popup.inflate(R.menu.actions);
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
    public void navigationDrawerInitialization(){
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
    }

    public void updateNavigatorInformation(NavigationView navigationView){
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

        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        if(name.getText() != null)
            navUsername.setText(name.getText());
        TextView navEmail= (TextView) headerView.findViewById(R.id.nav_email);
        if(email.getText() != null)
            navEmail.setText(email.getText());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_deliviries) {
            Intent myIntent = new Intent(this, DeliveryActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void createPendingDeliveryList() {
        DeliveryPendingTab1.deliveriesList = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingDeliveryRef = database.child("Rider").child("Delivery").child("Pending");
        pendingDeliveryRef.keepSynced(true);
//        DatabaseReference orderedFoodRef = database.child("Company").child("OrderedFood");

        pendingDeliveryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Delivery post = postSnapshot.getValue(Delivery.class);
                    DeliveryPendingTab1.deliveriesList.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

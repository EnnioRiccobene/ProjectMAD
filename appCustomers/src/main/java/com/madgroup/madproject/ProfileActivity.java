package com.madgroup.madproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.sdk.CustomAutoCompleteTextView;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.SmartLogger;
import com.mapbox.services.commons.models.Position;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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



public class ProfileActivity extends AppCompatActivity implements
        PopupMenu.OnMenuItemClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 3;
    private CircleImageView personalImage;
    private EditText name;
    private EditText email;
    // private EditText password;
    private EditText phone;
    private CustomAutoCompleteTextView address;
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
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressBar progressBar;
    private ProgressBar imgProgressBar;
    private NavigationView navigationView;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_profile);
        stub.inflate();
        this.setTitle(R.string.profile);
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        editor = prefs.edit();
        // Getting the instance of Firebase
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Defining EditText
        personalImage = (findViewById(R.id.imagePersonalPhoto));
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        // password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        additionalInformation = findViewById(R.id.additionalInformation);
        progressBar = findViewById(R.id.progressBar);
        imgProgressBar = findViewById(R.id.imgProgressBar);
        modifyingInfo = false;

        address.setAccessToken("pk.eyJ1IjoicGRvcjk1IiwiYSI6ImNqdnBkZDhwMTBrbnA0YnFsbjh6Zmh0NWoifQ.Bp-Ctr-VVIKCSbTX5kqNGg");
        address.setCountry("IT");
        address.setProximity(Position.fromCoordinates(45.062358, 7.662752));

        // Set all field to unclickable
        setFieldUnclickable();

        hideFields();
        imgProgressBar.setVisibility(View.INVISIBLE); // Nascondo la progress bar dell'immagine


        if (prefs.contains("currentUser")) {
            // Utente già loggato
            initializeNavigationDrawer();
            loadFieldsFromFirebase();
            downloadProfilePic();
        } else {
            startLogin();
        }

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
                    Toast.makeText(getApplicationContext(), getString(R.string.edit_fields), Toast.LENGTH_SHORT).show();

                } else {                      // I pressed to come back
                    modifyingInfo = false;
                    setFieldUnclickable();
                    //saveFields();
                    saveFieldsOnFirebase();
                    uploadProfilePic();
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
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(layout.isDrawerOpen(GravityCompat.START)){
            layout.closeDrawer(GravityCompat.START);
            return;
        }
        if (modifyingInfo) {
            modifyingInfo = false;
            setFieldUnclickable();
            //saveFields();
            saveFieldsOnFirebase();
            uploadProfilePic();
        } else
            super.onBackPressed();
    }

    // Functions
    private void setFieldUnclickable() {
        name.setEnabled(false);
        name.setFocusable(false);
        email.setEnabled(false);
        email.setFocusable(false);
        // password.setEnabled(false);
        phone.setEnabled(false);
        phone.setFocusable(false);
        address.setEnabled(false);
        address.setFocusable(false);
        additionalInformation.setEnabled(false);
        additionalInformation.setFocusable(false);
        personalImage.setEnabled(false);
        personalImage.setFocusable(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        name.setFocusable(true);
        name.setFocusableInTouchMode(true);
        email.setEnabled(false);
        // password.setEnabled(true);
        phone.setEnabled(true);
        phone.setFocusable(true);
        phone.setFocusableInTouchMode(true);
        address.setEnabled(true);
        address.setFocusable(true);
        address.setFocusableInTouchMode(true);
        additionalInformation.setEnabled(true);
        additionalInformation.setFocusable(true);
        additionalInformation.setFocusableInTouchMode(true);
        personalImage.setEnabled(true);
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

                if (cameraPermission==PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startCamera(this);
                } else {
                    if (userPreviousDeniedRequest) {
                        Toast.makeText(getApplicationContext(), getString(R.string.cameraPermission), Toast.LENGTH_SHORT).show();
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

                if (readStoragePermission==PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startGallery(this);
                } else {
                    if (userPreviousDeniedGalleryRequest) {
                        Toast.makeText(getApplicationContext(), getString(R.string.galleryPermission), Toast.LENGTH_SHORT).show();
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
                //isDefaultImage = true;
                updateNavigatorInformation();
                //editor.remove("PersonalImage");
                //editor.apply();
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
            //isDefaultImage = false;
            //saveImageContent();
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
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                prefs = getSharedPreferences("MyData", MODE_PRIVATE);
                editor = prefs.edit();
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
                //isDefaultImage = false;
                //saveImageContent();
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
                    Toast.makeText(getApplicationContext(), getString(R.string.cameraPermission), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GALLERY_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyImageHandler.getInstance().startGallery(this);
                } else {
                    // permission denied!
                    Toast.makeText(getApplicationContext(), getString(R.string.galleryPermission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveFieldsOnFirebase() {
        progressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Customer currentUser = new Customer(uid, name.getText().toString(), email.getText().toString(),
                phone.getText().toString(),address.getText().toString(),additionalInformation.getText().toString());
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //database.getReference("Profiles").child("Customers")
        database.getReference("Customer").child("Profile")
                .child(currentUid)
                .setValue(currentUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    progressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                    Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                } else {
                    // Dati salvati correttamente nel db
                    // Aggiorno le shared prefs
                    editor.putString("Name", name.getText().toString());
                    editor.putString("Email", email.getText().toString());
                    editor.putString("Phone", phone.getText().toString());
                    editor.putString("Address", address.getText().toString());
                    editor.apply();
                    progressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                }
            }
        });
    }

    private void uploadProfilePic() {

        imgProgressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar

        Bitmap bitmap = ((BitmapDrawable) personalImage.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        final StorageReference fileReference = storage.getReference("profile_pics")
                .child("customers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final UploadTask uploadTask = fileReference.putBytes(data); // Salvo l'immagine nello storage
        /* Gestione successo e insuccesso */
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ProfileActivity.this, getString(R.string.upload_failure), Toast.LENGTH_LONG).show();
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
                            imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(ProfileActivity.this, getString(R.string.upload_failure), Toast.LENGTH_LONG).show();
                        } else {
                            imgProgressBar.setVisibility(View.GONE);  // Nascondo la progress bar
                            Toast.makeText(getApplicationContext(), getString(R.string.pic_saved), Toast.LENGTH_SHORT).show();
                            updateNavigatorInformation();
                        }
                    }
                });
            }
        });
    }

    private void downloadProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(GlideApp.with(this).load(R.drawable.personicon))
                .into(personalImage);

    }

    private void deleteProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics")
                .child("customers")
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
        name.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        phone.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        additionalInformation.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);  // Mostro la progress bar
    }

    private void showFields() {
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        additionalInformation.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);  // Nascondo la progress bar
    }

    private void loadFieldsFromFirebase() {

        database.getReference("Customer")
                .child("Profile")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Customer customer = dataSnapshot.getValue(Customer.class); // Leggo le informazioni dal DB

                        if (customer!=null) {
                            // Utente già registrato: setto i campi
                            name.setText(customer.getName());
                            phone.setText(customer.getPhone());
                            address.setText(customer.getAddress());
                            additionalInformation.setText(customer.getInfo());
                            email.setText(customer.getEmail());
                            showFields();

                            // Aggiorno le shared prefs
                            editor.putString("Name", customer.getName());
                            editor.putString("Email", customer.getEmail());
                            editor.putString("Phone", customer.getPhone());
                            editor.putString("Address", customer.getAddress());
                            editor.apply();

                        } else {
                            // Utente appena registrato: inserisco un nodo nel database e setto i campi nome e email
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Customer currentUser = new Customer(user.getUid(), user.getDisplayName(), user.getEmail(),
                                    "","","");
                            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            database.getReference("Customer").child("Profile")
                                    .child(currentUid).setValue(currentUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError!= null) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();

                                    } else {
                                        name.setText(user.getDisplayName());
                                        email.setText(user.getEmail());
                                        // Aggiorno le shared prefs
                                        editor.putString("Name", user.getDisplayName());
                                        editor.putString("Email", user.getEmail());
                                        editor.apply();
                                        showFields();
                                    }
                                }
                            });
                        }
                        updateNavigatorInformation();
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


    public void initializeNavigationDrawer(){

        // Navigation Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(        // Three lines icon on the left corner
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the photo of the Navigation Bar Icon (Need to be completed: refresh when new image is updated)
        updateNavigatorInformation();
    }

    public void updateNavigatorInformation(){
        if(FirebaseAuth.getInstance() == null)
            return;
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

        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_profile_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
        String name = prefs.getString("Name", "No name inserted");
        navUsername.setText(name);
        String email = prefs.getString("Email", "No email inserted");
        navEmail.setText(email);
    }

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search_restaurant) {
            Intent myIntent = new Intent(this, SearchRestaurantActivity.class);
            this.startActivity(myIntent);
        } else if(id == R.id.nav_orders){
            Intent myIntent = new Intent(this, OrdersActivity.class);
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

}
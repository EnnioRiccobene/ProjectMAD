package com.madgroup.madproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madgroup.sdk.MyImageHandler;
import com.madgroup.sdk.SmartLogger;
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


public class MainActivity extends AppCompatActivity implements
        PopupMenu.OnMenuItemClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 3;
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
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private boolean isDefaultImage;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ViewStub stub = (ViewStub)findViewById(R.id.stub);
        stub.setInflatedId(R.id.inflatedActivity);
        stub.setLayoutResource(R.layout.activity_main);
        stub.inflate();
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        editor = prefs.edit();
        initializeNavigationDrawer();
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
        modifyingInfo = false;

        // Set all field to unclickable
        setFieldUnclickable();
        isDefaultImage = true;

        if (prefs.contains("currentUser")) {
            // Utente già loggato
            loadFieldsFromFirebase();
            downloadProfilePic();
        } else {
            startLogin();
        }

        // Load saved information, if exist
        //loadFields();

    }
    private void loadFieldsFromFirebase() {

        database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                        } else {
                            // Utente appena registrato: inserisco un nodo nel database e setto i campi nome e email
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Customer currentUser = new Customer(user.getDisplayName(), user.getEmail(),
                                    "","","");
                            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            database.getReference("Users").child(currentUid).setValue(currentUser);
                            name.setText(user.getDisplayName());
                            email.setText(user.getEmail());
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
                    //saveFields();
                    saveFieldsOnFirebase();
                    if (!isDefaultImage)
                        uploadProfilePic(((BitmapDrawable)personalImage.getDrawable()).getBitmap());
                    else
                        deleteProfilePic();
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
        if(layout.isDrawerOpen(GravityCompat.START)){
            layout.closeDrawer(GravityCompat.START);
            return;
        }
        if (modifyingInfo) {
            modifyingInfo = false;
            setFieldUnclickable();
            //saveFields();
            saveFieldsOnFirebase();
            if (!isDefaultImage)
                uploadProfilePic(((BitmapDrawable)personalImage.getDrawable()).getBitmap());
            else
                deleteProfilePic();

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
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        // password.setEnabled(true);
        phone.setEnabled(true);
        address.setEnabled(true);
        additionalInformation.setEnabled(true);
        personalImage.setEnabled(true);
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
        if(prefs.contains("Address"))
            address.setText(prefs.getString("Address", ""));
        if(prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));
        restoreImageContent();
    }

    private void saveFields() {
        editor.putString("Name", name.getText().toString());
        editor.putString("Email", email.getText().toString());
        // editor.putString("Password", password.getText().toString());
        editor.putString("Phone", phone.getText().toString());
        editor.putString("Address", address.getText().toString());
        editor.putString("Information", additionalInformation.getText().toString());
        editor.apply();
    }

    private void saveFieldsOnFirebase() {
        Customer currentUser = new Customer(name.getText().toString(), email.getText().toString(),
                phone.getText().toString(),address.getText().toString(),additionalInformation.getText().toString());
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("Users").child(currentUid).setValue(currentUser);
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
                return true;

            case R.id.itemDelete:
                // Set the default image
                Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                personalImage.setImageDrawable(defaultImg);
                isDefaultImage = true;
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
            isDefaultImage = false;
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
                editor.putString("currentUser", user.getUid());
                editor.apply();

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
                //saveImageContent();
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
            isDefaultImage = false;
        } else {
            Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
            personalImage.setImageDrawable(defaultImg);
            isDefaultImage = true;
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

    private void uploadProfilePic(Bitmap bitmap) {
        // Se è l'immagine di default, non salvo niente
        if (isDefaultImage)
            return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        final StorageReference fileReference = storage.getReference("profile_pics").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final UploadTask uploadTask = fileReference.putBytes(data); // Salvo l'immagine nello storage
        /* Gestione successo e insuccesso */
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(MainActivity.this, "Upload Failure", Toast.LENGTH_LONG).show();
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
                        }
                    }
                });
            }
        });
    }

    private void downloadProfilePic() {
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
    }

    private void deleteProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pics").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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

    // Navigation Drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search_restaurant) {
            Intent myIntent = new Intent(this, SearchRestaurantActivity.class);
            this.startActivity(myIntent);
        } else if (id == R.id.nav_profile) {
            onBackPressed();
        } else if (id == R.id.nav_logout) {
            // LogoutFunction
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
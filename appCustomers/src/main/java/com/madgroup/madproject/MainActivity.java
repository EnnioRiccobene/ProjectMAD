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
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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
        setContentView(R.layout.activity_main);

        // Getting the instance of Firebase
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        editor = prefs.edit();

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
        } else {
            startLogin();
        }

        // Load saved information, if exist
        loadFields();

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
                                    "","","","");
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
                    saveFields();
                    //saveFieldsOnFirebase();
                    //saveProfilePicOnFirebase(((BitmapDrawable)personalImage.getDrawable()).getBitmap());
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
        if (modifyingInfo) {
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
                phone.getText().toString(),address.getText().toString(),additionalInformation.getText().toString(),"");
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
                isDefaultImage = true;
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
            isDefaultImage = false;
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
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                editor.putString("currentUser", user.getUid());
                editor.apply();

                loadFieldsFromFirebase();

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

    /* Salva l'immagine nello storage con relativo link nel database; se l'immagine è di default carico solo una stringa vuota sul db */
    private void saveProfilePicOnFirebase(Bitmap bitmap) {

        if (isDefaultImage) {
            database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("image").setValue("");
            return;
        }

        //https://firebase.google.com/docs/storage/android/upload-files
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

                //Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();

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
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult(); // URI Dell'immagine
                            database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("image").setValue(downloadUri.toString());
                        } else {
                            // Handle failures
                        }
                    }
                });
            }
        });
    }

    private void getProfilePicFromFirebase() {

        database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String imageUri = dataSnapshot.getValue(String.class);
                        if (imageUri.equals("")) {
                            // Immagine di default
                            Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                            personalImage.setImageDrawable(defaultImg);
                            isDefaultImage = true;
                        }
                        else {
                            // Scarico l'immagine
                            final long ONE_MEGABYTE = 1024 * 1024;
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
                            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    personalImage.setImageBitmap(bitmap);
                                    isDefaultImage = false;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

}
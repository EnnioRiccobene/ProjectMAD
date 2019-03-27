package com.madgroup.madproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.madgroup.sdk.SmartLogger;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.UCropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageView personalImage;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText phone;
    private EditText address;
    private EditText additionalInformation;
    private Boolean modifyingInfo;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final int Camera_Pick_Code = 0;
    private static final int Gallery_Pick_Code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        // Defining EditText
        personalImage = (findViewById(R.id.imagePersonalPhoto));
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        additionalInformation = findViewById(R.id.additionalInformation);
        modifyingInfo = false;


        // Set all field to unclickable
        setFieldUnclickable();

        // Load saved information, if exist
        loadFields();

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
                }
                else{                      // I pressed to come back
                    modifyingInfo = false;
                    setFieldUnclickable();
                    saveFields();
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
        if(modifyingInfo){
            modifyingInfo = false;
            setFieldUnclickable();
            saveFields();
        }
        else
            super.onBackPressed();
    }

    // Functions
    private void setFieldUnclickable() {
        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        phone.setEnabled(false);
        address.setEnabled(false);
        additionalInformation.setEnabled(false);
        personalImage.setEnabled(false);
    }

    private void setFieldClickable() {
        name.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        phone.setEnabled(true);
        address.setEnabled(true);
        additionalInformation.setEnabled(true);
        personalImage.setEnabled(true);
    }

    private void loadFields() {
        if(prefs.contains("Name"))
            name.setText(prefs.getString("Name", ""));
        if(prefs.contains("Email"))
            email.setText(prefs.getString("Email", ""));
        if(prefs.contains("Password"))
            password.setText(prefs.getString("Password", ""));
        if(prefs.contains("Phone"))
            phone.setText(prefs.getString("Phone", ""));
        if(prefs.contains("Address"))
            additionalInformation.setText(prefs.getString("Address", ""));
        if(prefs.contains("Information"))
            additionalInformation.setText(prefs.getString("Information", ""));
    }

    private void saveFields() {
        editor.putString("Name",name.getText().toString());
        editor.putString("Email",email.getText().toString());
        editor.putString("Password",password.getText().toString());
        editor.putString("Phone",phone.getText().toString());
        editor.putString("Address",address.getText().toString());
        editor.putString("Information",additionalInformation.getText().toString());
        editor.apply();
    }
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

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
                // Funzione fotocamera
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /* Performing this check is important because if you call startActivityForResult()
                using an intent that no app can handle, your app will crash. */
                if (intentCamera.resolveActivity(getPackageManager()) != null) {
                   startActivityForResult(intentCamera, Camera_Pick_Code);
                }

                return true;
            case R.id.itemGallery:
                // Funzione galleria
                Intent intentGallery = new Intent();
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");   // Show only images, no videos or anything else
                if (intentGallery.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intentGallery, Gallery_Pick_Code);
                }
                return true;

            case R.id.itemDelete:
                // Set the default image
                Drawable defaultImg = getResources().getDrawable(R.drawable.personicon);
                personalImage.setImageDrawable(defaultImg);
                return true;

            default:
                return false;
        }
    }

    public int iteration = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MAD", ""+requestCode);

        /* Retreiving the tumbnail: the Android Camera application encodes the photo in the return Intent
        delivered to onActivityResult() as a small Bitmap in the extras, under the key "data" */
        if (requestCode == Camera_Pick_Code && resultCode==RESULT_OK && data!=null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            personalImage.setImageBitmap(bitmap);
            //startCrop(getImageUrl);
        }

        if (requestCode == Gallery_Pick_Code && resultCode==RESULT_OK && data!=null) {
            try {
                Uri selectedImageUri = data.getData();
                /*
                    Getting the result and show it in the ImageButton
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    personalImage.setImageBitmap(bitmap);
                */
                if (selectedImageUri != null) {
                    startCrop(selectedImageUri, iteration);
                    iteration++;
                } else {
                    // Give the error
                }
            }
            catch(Exception e) {

            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            handleCropResult(data);
        }

    }

    private void startCrop(@NonNull Uri uri, int iteration) {
        String destinationFileName = "SampleCropImage.png" + iteration;
        Uri uriDestionation = Uri.fromFile(new File(getCacheDir(), destinationFileName));   // getCacheDir è il path della cache
        UCrop uCrop = UCrop.of(uri, uriDestionation);
        uCrop = advancedConfig(uCrop);  // Modifica della configurazione
        uCrop.start(MainActivity.this);
    }

    // Richiamata dopo il crop
    private void handleCropResult(@NonNull Intent result) {
        final Uri uri = UCrop.getOutput(result);
        if (uri != null) {
            try {
                personalImage.setImageURI(uri);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Configurazione del cropper
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        //options.setFreeStyleCropEnabled(true); // Resize a runtime per l'utente

        options.withAspectRatio(1,1);
        options.setHideBottomControls(true);    // Nascondo la barra delle opzioni
        options.setStatusBarColor(Color.rgb(0,255,0));
        options.setToolbarColor(Color.rgb(0,255,125));
        options.setCircleDimmedLayer(true); // Mostro il cerchio
        return uCrop.withOptions(options);
    }

    // Conversione da Bitmap a String
    private String fromBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        String encodedString = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        return encodedString;
    }

    private void saveImageOnRotation() {
        Bitmap bitmap = ((BitmapDrawable) personalImage.getDrawable()).getBitmap();
        String encoded = fromBitmapToString(bitmap);
        editor.putString("PersonalImage", encoded);
        editor.commit();
    }

    private void restoreImageOnRotation() {
        if(prefs.contains("PersonalImage")) {
            byte[] b = Base64.decode(prefs.getString("PersonalImage", ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            personalImage.setImageBitmap(bitmap);
        }
    }

}


package com.madgroup.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MyImageHandler extends AppCompatActivity {

    public static final int Camera_Pick_Code = 0;
    public static final int Gallery_Pick_Code = 1;



    private static final MyImageHandler instance = new MyImageHandler();

    private MyImageHandler() {
    }

    public static MyImageHandler getInstance(){
        return instance;
    }

    // Conversione da Bitmap a String
    public String fromBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        String encodedString = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        return encodedString;
    }

    // Configurazione del cropper
    public UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        //options.setFreeStyleCropEnabled(true); // Resize a runtime per l'utente

        options.withAspectRatio(1, 1);
        options.setHideBottomControls(true);    // Nascondo la barra delle opzioni
        options.setStatusBarColor(Color.rgb(27, 27, 27));
        options.setToolbarColor(Color.rgb(66, 66, 66));
        options.setCircleDimmedLayer(true); // Mostro il cerchio
        return uCrop.withOptions(options);
    }

    public void startCamera(Activity activity) {
        // Funzione fotocamera
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    /* Performing this check is important because if you call startActivityForResult()
                    using an intent that no app can handle, your app will crash. */
        if (intentCamera.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intentCamera, Camera_Pick_Code);
        }
    }

    public void startGallery(Activity activity) {
        // Funzione galleria
        Intent intentGallery = new Intent();
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");   // Show only images, no videos or anything else
        if (intentGallery.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intentGallery, MyImageHandler.Gallery_Pick_Code);
        }
    }

    public void startCrop(@NonNull Uri uri, int iteration, Context context, Activity activity) {
        String destinationFileName = "SampleCropImage.png" + iteration;
        Uri uriDestionation = Uri.fromFile(new File(context.getCacheDir(), destinationFileName));   // getCacheDir è il path della cache
        UCrop uCrop = UCrop.of(uri, uriDestionation);
        uCrop = MyImageHandler.getInstance().advancedConfig(uCrop);  // Modifica della configurazione
        uCrop.start(activity);
    }

}

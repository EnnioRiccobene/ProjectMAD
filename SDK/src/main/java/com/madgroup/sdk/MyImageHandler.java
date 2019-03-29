package com.madgroup.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MyImageHandler {

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


}

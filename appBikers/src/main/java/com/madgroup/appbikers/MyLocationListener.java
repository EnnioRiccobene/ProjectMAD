package com.madgroup.appbikers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madgroup.sdk.RiderProfile;

import java.util.HashMap;
import java.util.Map;

class MyLocationListener implements LocationListener {

    String currentUserID;

    public MyLocationListener(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lon = location.getLongitude();
        double lat = location.getLatitude();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference bikerRef = database.child("Rider").child("Profile").child(currentUserID).child("position");
        final Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("/" + "lat" , lat);
        childUpdate.put("/" + "lon" , lon);
        bikerRef.updateChildren(childUpdate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

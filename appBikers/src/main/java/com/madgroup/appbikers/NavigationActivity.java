package com.madgroup.appbikers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    public static void start(Context context, String restaurantAddress, String customerAddress) {
        Intent starter = new Intent(context, NavigationActivity.class);
        starter.putExtra("restaurantAddress", restaurantAddress);
        starter.putExtra("customerAddress", customerAddress);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }
}

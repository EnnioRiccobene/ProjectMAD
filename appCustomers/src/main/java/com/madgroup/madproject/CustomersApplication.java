package com.madgroup.madproject;

import android.app.Application;

import com.madgroup.sdk.SmartLogger;

import static com.madgroup.madproject.BuildConfig.DEBUG;

public class CustomersApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmartLogger.init(DEBUG);
    }
}

package com.madgroup.appbikers;

import android.app.Application;

import com.madgroup.sdk.SmartLogger;

import static com.madgroup.appbikers.BuildConfig.DEBUG;

public class BikersApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SmartLogger.init(DEBUG);
    }
}

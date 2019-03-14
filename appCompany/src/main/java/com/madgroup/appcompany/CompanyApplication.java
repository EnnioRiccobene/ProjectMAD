package com.madgroup.appcompany;

import android.app.Application;

import com.madgroup.sdk.SmartLogger;

import static com.madgroup.appcompany.BuildConfig.DEBUG;

public class CompanyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SmartLogger.init(DEBUG);
    }
}

package com.madgroup.sdk;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public final class SmartLogger {

    public static void init(Boolean debug){
        if(debug){
            Logger.addLogAdapter(new AndroidLogAdapter());
        }
    }

    public static void d( String message, Object ... args){
        Logger.d(message, args);
    }

    public static void i( String message, Object ... args){
        Logger.i(message, args);
    }

    public static void v( String message, Object ... args){
        Logger.v(message, args);
    }

    public static void w( String message, Object ... args){
        Logger.w(message, args);
    }

    public static void e( String message, Object ... args){
        Logger.e(message, args);
    }

    public static void e( Throwable throwable, String message, Object ... args) {
        Logger.e(throwable, message, args);
    }


}

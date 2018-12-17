package com.cool.star.timerlancher;

import android.util.Log;

public class LogMgr {
    private static final String TAG = "TimerLauncher";

    public static void d(String log){
        if(isDebug){
            Log.d(TAG,log);
        }
    }

    public static void v(String log){
        if(isDebug){
            Log.v(TAG,log);
        }
    }

    public static void e(String log){
        if(isDebug){
            Log.e(TAG,log);
        }
    }

    public static void i(String log){
        if(isDebug){
            Log.i(TAG,log);
        }
    }

    private static boolean  isDebug = false;
    public static boolean isIsDebug() {
        return isDebug;
    }
    public static void setIsDebug(boolean isDebug) {
        LogMgr.isDebug = isDebug;
    }
}

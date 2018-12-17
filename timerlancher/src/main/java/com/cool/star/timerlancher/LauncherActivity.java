package com.cool.star.timerlancher;

import android.content.Context;

import com.unity3d.player.UnityPlayerActivity;

public class LauncherActivity extends UnityPlayerActivity {

    public static void printDebugLog(boolean enablePrint){
        TimerLauncher.printDebugLog(enablePrint);
    }
    public static boolean startDelayLaunch(Context context, String startPackage, int delayTime){
        return TimerLauncher.startDelayLaunch(context,startPackage,delayTime);
    }
    public static boolean clearDelaylaunch(Context appContext){
        return TimerLauncher.clearDelaylaunch(appContext);
    }
    public static void newStartAppByPackage(Context context,String packagename){
        TimerLauncher.newStartAppByPackage(context,packagename);
    }
    public static void restartAppByPackage(Context context,String packagename){
        TimerLauncher.restartAppByPackage(context,packagename);
    }
    public static void tryCloseApp(Context context,String packageName){
        TimerLauncher.tryCloseApp(context,packageName);
    }
    //------------------------------------------------------------------------------------------
    public  boolean startDelayLaunch(String startPackage, int delayTime){
        return TimerLauncher.startDelayLaunch(getApplicationContext(),startPackage,delayTime);
    }
    public  boolean clearDelaylaunch(){
        return TimerLauncher.clearDelaylaunch(getApplicationContext());
    }
    public  void newStartAppByPackage(String packagename){
        TimerLauncher.newStartAppByPackage(getApplicationContext(),packagename);
    }
    public  void restartAppByPackage(String packagename){
        TimerLauncher.restartAppByPackage(getApplicationContext(),packagename);
    }
    public  void tryCloseApp(String packageName){
        TimerLauncher.tryCloseApp(getApplicationContext(),packageName);
    }
}

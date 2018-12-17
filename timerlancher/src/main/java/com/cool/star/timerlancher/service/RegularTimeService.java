package com.cool.star.timerlancher.service;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.cool.star.timerlancher.LogMgr;
import com.cool.star.timerlancher.TimerLauncher;

public class RegularTimeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogMgr.d("--RegularTimeService--onStartCommand-");
        if(intent!=null){
            long spacetime = intent.getLongExtra("spaceTime",60000);
            if(TimerLauncher.unityCallBack!=null){
                TimerLauncher.unityCallBack.onTimeRunFinish();
            }
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager alarmManager = (PowerManager) getApplicationContext().getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = null;
            wl = alarmManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(2000); // 点亮屏幕

            PendingIntent pendIntent = TimerLauncher.buildIntent(getApplicationContext(),spacetime);
            TimerLauncher.startAlarmTask(getApplicationContext(),pendIntent,spacetime);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}

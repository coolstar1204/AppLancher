package com.cool.star.timerlancher;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;

import com.cool.star.timerlancher.service.LauncherJobService;
import com.cool.star.timerlancher.service.RegularTimeService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimerLauncher {
    private static final int MIN_DELAY_TIME = 15*60*1000;  //小于15分钟使用不同的设置函数
    public static final String TIMER_LAUNCHER_REGULAR_TIME_RUN = "TimerLauncher.regularTimeRun";
    private static int jobId = 0;   //保存发出的定时任务id，用于取消
    private static PowerManager.WakeLock sysLocker;  //系统锁，锁住系统防止其进入休眠
    private static PendingIntent operationIntent;  //定时任务的intent,用于取消
    public static IUnityCallBack unityCallBack = null;  //持有unity中的回调对象。

    /**
     * 申请系统cpu和网络不休眠，定时回调传入的接口
     * @param context
     * @param repartTime ：重复时间（分钟）
     * @param callBack ： （回调）
     */
    @SuppressLint("InvalidWakeLockTag")
    public static void regularTimeRun(Context context, int repartTime, IUnityCallBack callBack){
        if(context==null){
            return;
        }
        unityCallBack = callBack;
        PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        sysLocker = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TIMER_LAUNCHER_REGULAR_TIME_RUN);
        sysLocker.acquire();
        long spaceTime = 5 * 60 * 1000;  //参数按分钟设置
        operationIntent= buildIntent(context, spaceTime);
        startAlarmTask(context, operationIntent, spaceTime);
    }

    public static void startAlarmTask(Context context, PendingIntent pendingIntent, long spaceTime) {
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        int apiLevel = getApiLevel();
        operationIntent = pendingIntent;
        if (apiLevel < Build.VERSION_CODES.KITKAT) {
            LogMgr.d("api<19  setExactAlarmCompat "+spaceTime);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+spaceTime,
                    pendingIntent);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+spaceTime,
                        pendingIntent);
            }
            LogMgr.d("19<api<23  setExactAlarmCompat "+spaceTime);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+spaceTime,
                    pendingIntent);
            LogMgr.d("api>23  setExactAlarmCompat "+spaceTime);
        }
    }

    public static PendingIntent buildIntent(Context context, long spaceTime) {
        PendingIntent pendingIntent;Intent intent = new Intent(context,RegularTimeService.class);
        intent.putExtra("spaceTime",spaceTime);
        pendingIntent = PendingIntent.getService(context,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public static int getApiLevel() {
        try {
            Field f = Build.VERSION.class.getField("SDK_INT");
            f.setAccessible(true);
            return f.getInt(null);
        } catch (Throwable e) {
            return 3;
        }
    }

    public static void releaseRegularTimeRun(Context context){
        if(unityCallBack!=null){
            unityCallBack = null;
            LogMgr.d("release--callback");
        }
        if(operationIntent!=null){
            AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(operationIntent);
            LogMgr.d("-release----operationIntent--");
        }
        if(sysLocker!=null){
            LogMgr.d("---release---------locker----");
            sysLocker.release();
            sysLocker = null;
        }
    }

    /**
     * 控制是否要输出调试日志
     * @param enablePrint：true:输出定时任务Service相关生命周期调用日志，false:不输出日志（用于正式发布）
     */
    public static void printDebugLog(boolean enablePrint){
        LogMgr.setIsDebug(enablePrint);
        LogMgr.i(enablePrint?"TimerLauncher debug log start print !":"TimerLauncher debug log close.");
    }
    /**
     * 延迟拉起制定包名的app到前台界面
     * @param context：上下文
     * @param startPackage：要拉起的app包名
     * @param delayTime：要延迟的时间长度（秒）
     * @return：成功创建延迟任务返回true，条件错误没创建任务返回false
     */
    public static boolean startDelayLaunch(Context context,String startPackage,int delayTime){
        if(context==null||delayTime<1||TextUtils.isEmpty(startPackage)){
            return false;
        }
        if(jobId>0){
            clearDelaylaunch(context);
        }
        jobId = 161207;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context.getApplicationContext(), LauncherJobService.class));  //指定哪个JobService执行操作
        builder.setMinimumLatency(TimeUnit.SECONDS.toMillis(delayTime)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.SECONDS.toMillis(delayTime));  //执行的最长延时时间
        builder.setPersisted(true);////android8.0以下清除数据以及设置中强制停止应用也可以自动起来
        builder.setRequiresCharging(false); // 未充电状态
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);  //任何网络状态
        builder.setRequiresDeviceIdle(false);  //不需要空闲条件
        builder.setBackoffCriteria(TimeUnit.SECONDS.toMillis(60), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
        PersistableBundle extras = new PersistableBundle(1);
        extras.putString("package_name",startPackage);
        builder.setExtras(extras);
        JobScheduler jobScheduler = getJobService(context);
        jobScheduler.schedule(builder.build());
        LogMgr.d("--startDelayLaunch-->"+startPackage);
        return true;
    }

    /**
     * 主动清除延时打开app任务
     * @param appContext
     * @return
     */
    public static boolean clearDelaylaunch(Context appContext){
        if(appContext==null){
            return false;
        }
        if(jobId>0){
            LogMgr.d("--clearDelaylaunch-->"+jobId);
            JobScheduler jobScheduler = getJobService(appContext);
            jobScheduler.cancel(jobId);
            jobId = 0;
            return true;
        }
        return false;
    }

    private static JobScheduler getJobService(Context appContext) {
        return (JobScheduler) (appContext.getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE));
    }

    /**
     * 启动指定包名app，尽量保证下次启动app重新进入
     * @param context
     * @param packagename
     */
    public static void newStartAppByPackage(Context context,String packagename) {
        tryCloseApp(context,packagename);
        LogMgr.d("--newStartAppByPackage-->"+packagename);
        innerStartApp(context,packagename,Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK|Intent.FLAG_ACTIVITY_NEW_DOCUMENT|Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    /**
     * 启动指定包名app，尽量保证下次启动app保持原样
     * @param context
     * @param packagename
     */
    public static void restartAppByPackage(Context context,String packagename) {
        LogMgr.d("--restartAppByPackage-->"+packagename);
        innerStartApp(context,packagename,Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    }

    private static void innerStartApp(Context context,String packagename,int intentFlag){
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(intentFlag);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    public static void tryCloseApp(Context context,String packageName){
        try {
            LogMgr.d("tryCloseApp->"+packageName);
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(packageName);
            List<ActivityManager.RunningAppProcessInfo> info = activityManager.getRunningAppProcesses();
            if (info == null || info.size() == 0){
                return;
            }
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                if (Arrays.asList(aInfo.pkgList).contains(packageName)) {
                    activityManager.killBackgroundProcesses(packageName);
                }
            }
        }catch (Exception e){
            LogMgr.e(e.getMessage());
        }
    }
}

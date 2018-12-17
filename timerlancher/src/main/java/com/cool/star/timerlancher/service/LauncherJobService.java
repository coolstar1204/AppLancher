package com.cool.star.timerlancher.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.cool.star.timerlancher.LogMgr;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LauncherJobService extends JobService {

    private static final String TAG = "LauncherJobService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogMgr.i("JobService->onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogMgr.i("JobService->onStartCommand("+flags+","+startId+")");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogMgr.i("JobService->onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        LogMgr.i("JobService->onStartJob");
        if(params==null){
            return false;
        }
        PersistableBundle extras = params.getExtras();
        if(extras==null){
            return false;
        }
        String packageName = extras.getString("package_name");
        if(TextUtils.isEmpty(packageName)==false){
            LogMgr.i("JobService->onStartJob->run:"+packageName);
            doStartApplicationWithPackageName(packageName);
        }
        jobFinished(params,false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogMgr.i("JobService->onStopJob");
        return false;
    }


    private void doStartApplicationWithPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
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
        List<ResolveInfo> resolveinfoList = getPackageManager()
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

}

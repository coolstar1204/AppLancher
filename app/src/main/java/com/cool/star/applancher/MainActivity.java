package com.cool.star.applancher;

import android.app.ActivityManager;
import android.app.job.JobScheduler;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cool.star.applancher.adapter.AppInfoAdapter;
import com.cool.star.applancher.widget.MultipleStatusView;
import com.cool.star.timerlancher.IUnityCallBack;
import com.cool.star.timerlancher.TimerLauncher;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvTitle;
    AppInfoAdapter mAdapter;
    RecyclerView recyclerView;
    EditText etTime;
    List<AppUtils.AppInfo> selectAppList;
    private MultipleStatusView stateView;
    private FlexibleDividerDecoration divider;
    private BaseQuickAdapter.OnItemClickListener itemClickListener =  new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            AppUtils.AppInfo  item = mAdapter.getItem(position);
            delayCallSelf();
//            TimerLauncher.restartAppByPackage(MainActivity.this,item.getPackageName());
            TimerLauncher.newStartAppByPackage(MainActivity.this,item.getPackageName());

        }
    };

    @Override
    protected void onDestroy() {
        TimerLauncher.clearDelaylaunch(getApplicationContext());
        TimerLauncher.releaseRegularTimeRun(this);
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectAppList = new ArrayList<>(20);
        setContentView(R.layout.activity_main);
        etTime = findViewById(R.id.lantime);
        tvTitle = findViewById(R.id.apptitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerLauncher.regularTimeRun(MainActivity.this, 1, new IUnityCallBack() {
                    @Override
                    public void onTimeRunFinish() {

                        Log.d("TimerLauncher","--onTimeRunFinish--------");
                    }
                });
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupRecycleView();
        TimerLauncher.printDebugLog(true);

    }



    private void delayCallSelf() {
        TimerLauncher.startDelayLaunch(getApplicationContext(),AppUtils.getAppPackageName(),Integer.parseInt(etTime.getText().toString())*60);
    }


    private void setupRecycleView() {
        divider = new HorizontalDividerItemDecoration.Builder(this).color(Color.GRAY).sizeResId(R.dimen.gird_divider_height).build();
        recyclerView = findViewById(R.id.applist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(divider);
        mAdapter = new AppInfoAdapter(null);
        mAdapter.setEmptyView(loadStateView());
        mAdapter.setOnItemClickListener(itemClickListener);
        mAdapter.bindToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        stateView.showLoading();
        syncLoadAppList();
    }

    private void syncLoadAppList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<AppUtils.AppInfo> allApps = loadInstallApps();
                final List<AppUtils.AppInfo> apps = new ArrayList<>(allApps.size());
                for(int i=0;i<allApps.size();i++){
                    if(allApps.get(i).isSystem()){
                        continue;
                    }
                    apps.add(allApps.get(i));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mAdapter!=null){
                            mAdapter.setNewData(apps);
                            stateView.showContent();
                        }
                    }
                });
            }
        }).start();
    }

    private View loadStateView() {
        stateView = (MultipleStatusView) getLayoutInflater().inflate(R.layout.multiple_status_view,null);
        return stateView;
    }

    private List<AppUtils.AppInfo> loadInstallApps() {
        List<AppUtils.AppInfo>  list = AppUtils.getAppsInfo();
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

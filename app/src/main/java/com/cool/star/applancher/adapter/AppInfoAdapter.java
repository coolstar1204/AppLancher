package com.cool.star.applancher.adapter;

import android.content.pm.PackageInfo;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cool.star.applancher.R;

import java.util.List;

public class AppInfoAdapter extends BaseQuickAdapter<AppUtils.AppInfo,BaseViewHolder> {
    public AppInfoAdapter(@Nullable List<AppUtils.AppInfo> data) {
        super(R.layout.item_app_info,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppUtils.AppInfo item) {
        helper.setText(R.id.item_name,item.getName());
        helper.setText(R.id.item_package,item.getPackageName());
    }
}

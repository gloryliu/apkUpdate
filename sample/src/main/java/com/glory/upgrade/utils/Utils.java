package com.glory.upgrade.utils;

import android.app.Activity;

import com.glory.upgrade.library.ApkUpgradeTool;
import com.glory.upgrade.library.OnUpgradeListener;

/**
 * Created by liu on 2017/3/10.
 */

public class Utils {
    /**
     * 监测版本升级
     */
    public static void checkVersion(final Activity activity, final boolean isShowToast) {
        new ApkUpgradeTool.Builder(activity)
                .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                .versionCode(2)
                .onUpdateListener(new OnUpgradeListener() {
                    @Override
                    public void initDialog(ApkUpgradeTool.Builder builder) {
                        DialogUtils.showUpdateDialog(activity, builder);
                    }

                }).build().updateVersion(isShowToast);

    }
}

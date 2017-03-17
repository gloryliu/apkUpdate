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
                //apk下载地址
                .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                //版本号
                .versionCode(2)
                //升级回调用了初始化dialog
                .onUpdateListener(new OnUpgradeListener() {
                    @Override
                    public void initDialog(ApkUpgradeTool.Builder builder) {
                        DialogUtils.showUpdateDialog(activity, builder);
                    }

                }).build()
                //是否显示toast提示
                .updateVersion(isShowToast);

    }
}

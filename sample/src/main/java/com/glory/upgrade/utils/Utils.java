package com.glory.upgrade.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.glory.upgrade.bean.UpdateInfo;
import com.glory.upgrade.library.ApkUpgradeTool;
import com.glory.upgrade.library.BaseUpdateInfo;
import com.glory.upgrade.library.OnUpgradeListener;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public class Utils {
    /**
     * 监测版本升级
     */
    public static void checkVersion(final Activity activity, final boolean isShowToast) {
        ApkUpgradeTool upgradeTool = new ApkUpgradeTool.Builder(activity)
                .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                .versionCode(2)
                .onUpdateListener(new OnUpgradeListener() {
                    @Override
                    public void initDialog(ApkUpgradeTool.Builder builder) {
                        DialogUtils.showUpdateDialog(activity, builder);
                    }

                }).build();
        upgradeTool.updateVersion(isShowToast);

    }
    /**
     * 监测权限
     *
     * @return
     */
    public static boolean checkPermision(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}

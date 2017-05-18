package com.glory.upgrade.library;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * Created by liu.zhenrong on 2017-05-15.
 */

public class BsPatchUtil {
    static {
        System.loadLibrary("bspatch");
    }

    public static int patch(Context context,String patch,String newApk){
        return patch(extract(context),newApk,patch);
    }

    public static String extract(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        Log.d("hongyang", apkPath);
        return apkPath;
    }

    public static native int patch(String oldApk, String newApk, String patch);
}

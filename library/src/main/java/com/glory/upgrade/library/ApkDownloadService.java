package com.glory.upgrade.library;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liu on 2017/3/10.
 */

public class ApkDownloadService extends Service {
    private DownloadManager dm;
    private long enqueue;
    private BroadcastReceiver receiver;
    private String url;
    private String filePath = "";                       //文件的下载路径
    private String fileName = "dwonloadtemp.apk";       //会读取app的name
    private String newApk = "newApk.apk";               //打补丁后的文件
    private int updateMode = 1;
    private SharedPreferences sharedPreferences;
    private String provider = "";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateMode = intent.getIntExtra("updateMode",1);
        url = intent.getStringExtra("url");
        provider = intent.getStringExtra("provider");

        getApplicationName();
        sharedPreferences = getSharedPreferences(Config.SHARE_FILE_NAME, MODE_PRIVATE);

        if (!TextUtils.isEmpty(url)) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final File fileapk = new File(filePath + "/" + fileName);
                    if(fileapk==null){
                        return;
                    }else {

                        if(fileapk.exists()){

                            if(1 == updateMode){
                                //全量
                                installApk(context,fileapk);
                                stopSelf();
                            }else if(2 == updateMode){
                                //增量
                                File newfile = new File(filePath + "/" + newApk);
                                BsPatchUtil.patch(getApplicationContext(),fileapk.getAbsolutePath(),newfile.getAbsolutePath());
                                if(newfile.exists()){
                                    installApk(context,newfile);
                                }
                                stopSelf();
                            }

                        }
                    }
                }
            };
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            startDownload();
        } else {
            Toast.makeText(this, "下载地址为空", Toast.LENGTH_LONG).show();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 开始下载
     */
    private void startDownload() {

        File fileDir = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (fileDir != null) {
            if(!fileDir.exists()){
                fileDir.mkdir();
            }
            filePath = fileDir.getAbsolutePath();
        }

        //该文件夹下已经有的话就删除
        File apkfile = new File(fileDir, fileName);
        if (apkfile != null) {
            if (apkfile.exists()) {
                apkfile.delete();
            }
        }

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(fileName);
        request.setDescription("");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS, fileName);
        enqueue = dm.enqueue(request);
        sharedPreferences.edit().putLong("enqueue", enqueue).commit();
    }

    /**
     * 获取应用程序名称
     */
    private void getApplicationName(){
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while(i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                fileName = c.toString()+".apk";
            }catch(Exception e) {

            }
        }
    }

    /**
     * 安装apk
     * @param context
     * @param file
     */
    private void installApk(Context context,File file){
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, provider, file);
        } else {
            data = Uri.fromFile(file);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}

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
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public class ApkDownloadService extends Service {
    private DownloadManager dm;
    private long enqueue;
    private BroadcastReceiver receiver;
    private String url;
    private String filePath = "";//不用修改
    private String fileName = "jyall.apk";//会读取app的name
    private SharedPreferences sharedPreferences;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        getApplicationName();
        sharedPreferences = getSharedPreferences("apkUpdate", MODE_WORLD_WRITEABLE);
        if (!TextUtils.isEmpty(url)) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    File fileapk = new File(filePath + "/" + fileName);
                    if(fileapk==null){
                        return;
                    }else {
                        if(fileapk.exists()){
                            //下载完成后状态改变为1
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(new File(filePath + "/" + fileName)),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                            stopSelf();
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
        File filedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (filedir != null) {
            //获取文件从发的绝对路径
            filePath = filedir.getAbsoluteFile().toString();
        }
        if(TextUtils.isEmpty(fileName)){
            Toast.makeText(this,"文件名空",Toast.LENGTH_LONG).show();
            return;
        }
        //该文件夹下已经有的话就删除
        File apkfile = new File(filedir, fileName);
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
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        enqueue = dm.enqueue(request);
        sharedPreferences.edit().putLong("enqueue", enqueue).commit();
    }

    //获取应用程序名称
    private void getApplicationName(){
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while(i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                Log.w("LABEL", c.toString());
                fileName = c.toString();
            }catch(Exception e) {

            }
        }
    }
}

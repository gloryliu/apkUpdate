package com.glory.upgrade.library;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by liu on 2017/3/10.
 */

public final class ApkUpgradeTool {


    private Builder builder;
    private ApkUpgradeTool(Builder builder) {
        this.builder = builder;
    }


    /**
     * 判断当前是否最新版本
     * @param isShowToast //是否显示toast提示
     */
    public void updateVersion(boolean isShowToast) {
        //正在下载过程中就不用再显示更新的弹窗了
        if (!isNeededShowDialog()) {
            return;
        }

        //判断是否应该显示升级提示
        if (builder.versionCode > getPackageInfo().versionCode) {
            if (checkPermision(builder.mContext)) {
                builder.onUpdateListener.initDialog(builder);
            }
        } else {
            if(isShowToast){
                Toast.makeText(builder.mContext, "当前是最新版本！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取当前应用的包信息
     *
     * @return
     */
    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = builder.mContext.getPackageManager().getPackageInfo(builder.mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 是否显示dialog
     *
     * @return
     */
    private boolean isNeededShowDialog() {
        boolean result = true;
        long enqueue = builder.sharedPreferences.getLong("enqueue", -1);
        if (enqueue == -1) {
            result = true;
        } else {
            result = isNeedDownload(enqueue, builder.downloadManager);
        }
        return result;
    }

    /**
     * 根据下载状态判断是否需要重新下载
     *
     * @param id
     * @param downloadManager
     * @return
     */
    private  boolean isNeedDownload(long id, DownloadManager downloadManager) {

        boolean isNeedDownloadAgain = true;

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);

            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            //some possibly transient error occurred but we can't resume the download
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            //no external storage device was found. Typically, this is because the SD card is not mounted
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            //the requested destination file already exists (the download manager will not overwrite an existing file)
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            //a storage issue arises which doesn't fit under any other error code
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            //an error receiving or processing data occurred at the HTTP level
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE://sd卡满了
                            //here was insufficient storage space. Typically, this is because the SD card is full
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            //there were too many redirects
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            //an HTTP code was received that download manager can't handle
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            //he download has completed with an error that doesn't fit under any other error code
                            break;
                    }
                    isNeedDownloadAgain = true;

                    //AlertUtil.alert("开始重新下载更新!", mContext);
                    break;
                case DownloadManager.STATUS_PAUSED:

                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            //the download exceeds a size limit for downloads over the mobile network and the download manager is waiting for a Wi-Fi connection to proceed

                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            //the download is paused for some other reason
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            //the download is waiting for network connectivity to proceed
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            //the download is paused because some network error occurred and the download manager is waiting before retrying the request
                            break;
                    }
                    isNeedDownloadAgain = true;

                    //AlertUtil.alert("下载已暂停，请继续下载！", mContext);
                    break;
                case DownloadManager.STATUS_PENDING:
                    //the download is waiting to start
                    isNeedDownloadAgain = true;
                    //AlertUtil.alert("更新正在下载！", mContext);
                    break;
                case DownloadManager.STATUS_RUNNING:
                    //the download is currently running
                    isNeedDownloadAgain = false;
                    //AlertUtil.alert("更新正在下载！", mContext);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //the download has successfully completed
                    isNeedDownloadAgain = true;
                    //installApk(id, downloadManager, mContext);
                    break;
            }

        }
        return isNeedDownloadAgain;
    }

    /**
     * 获取sd卡权限
     * @param mContext
     * @return
     */
    private boolean checkPermision(Context mContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext,
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

    /**
     * Builder设计模式
     */
    public static final class Builder{
        private Context mContext;
        private Intent startService;
        private SharedPreferences sharedPreferences;
        private DownloadManager downloadManager;
        private OnUpgradeListener onUpdateListener;
        private int versionCode = -1;//版本号
        private String versionInfo = "";//升级文案
        private boolean forceUpdate = false;//是否强制升级
        private String apkUrl = "";//apk下载地址

        public Builder(Context mContext){
            this.mContext = mContext;
            this.sharedPreferences = mContext.getSharedPreferences("apkUpdate", Context.MODE_WORLD_WRITEABLE);
            this.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            this.startService = new Intent(mContext, ApkDownloadService.class);
        }

        /**
         * 设置回调监听器
         * @param onUpdateListener
         * @return
         */
        public Builder onUpdateListener(OnUpgradeListener onUpdateListener){
            this.onUpdateListener = onUpdateListener;
            return this;
        }

        /**
         * 是否强制升级
         * @param forceUpdate
         * @return
         */
        public Builder forceUpdate(boolean forceUpdate){
            this.forceUpdate = forceUpdate;
            return this;
        }
        /**
         * 版本说明
         * @param versionInfo
         * @return
         */
        public Builder versionInfo(String versionInfo){
            this.versionInfo = versionInfo;
            return this;
        }

        /**
         * 设置版本号
         * @param versionCode
         * @return
         */
        public Builder versionCode(int versionCode){
            this.versionCode = versionCode;
            return this;
        }

        /**
         * 设置apk下载地址
         * @param apkUrl
         * @return
         */
        public Builder apkUrl(String apkUrl){
            this.apkUrl = apkUrl;
            return this;
        }


        /**
         * 升级描述信息
         * @return
         */
        public String getVersionInfo() {
            return versionInfo;
        }

        /**
         * 是否强制升级
         * @return
         */
        public boolean isForceUpdate() {
            return forceUpdate;
        }

        /**
         * 获得apkurl
         * @return
         */
        public String getApkUrl() {
            return apkUrl;
        }

        /**
         * 启动下载服务
         */
        public void startDownload(){
            this.mContext.startService(this.startService);
        }

        public ApkUpgradeTool build(){

            if (TextUtils.isEmpty(apkUrl)) {
                throw new IllegalStateException("apkUrl isEmpty");
            }

            if (versionCode<0){
                throw  new IllegalStateException("versionCode required >0");
            }

            if (null == onUpdateListener){
                throw new NullPointerException("onUpdateListener null");
            }

            this.startService.putExtra("url", this.apkUrl);
            return new ApkUpgradeTool(this);
        }

    }
}

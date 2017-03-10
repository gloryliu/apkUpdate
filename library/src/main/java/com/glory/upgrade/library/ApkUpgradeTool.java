package com.glory.upgrade.library;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.widget.Toast;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public class ApkUpgradeTool {
    private static ApkUpgradeTool instance;
    private Context mContext;
    private OnUpgradeListener onUpdateListener;
    private BaseUpdateInfo updateInfo;
    private SharedPreferences sharedPreferences;
    private DownloadManager downloadManager;

    private ApkUpgradeTool(Context mContext, OnUpgradeListener onUpdateListener) {
        this.mContext = mContext;
        this.onUpdateListener = onUpdateListener;
        this.sharedPreferences = mContext.getSharedPreferences("apkUpdate", Context.MODE_WORLD_WRITEABLE);
        this.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static ApkUpgradeTool getInstance(Context mContext, OnUpgradeListener onUpdateListener) {
        if (instance == null) {
            instance = new ApkUpgradeTool(mContext, onUpdateListener);
        }
        return instance;
    }

    public void recycle(){
        if(null != instance){
            instance = null;
        }
    }

    /**
     * 如果有sd卡的访问权限就显示升级的弹窗
     */
    private void initDialog() {
        if (onUpdateListener != null) {
            if (onUpdateListener.checkSDPermision()) {
                onUpdateListener.initDialog(updateInfo);
            }
        } else {
            throw new NullPointerException("onUpdateListener must not be null");
        }

    }

    /**
     * 判断当前是否最新版本
     *
     * @param updateInfo
     */
    public void updateVersion(BaseUpdateInfo updateInfo, boolean isShowToast) {
        //正在下载过程中就不用再显示更新的弹窗了
        if (!isNeededShowDialog()) {
            return;
        }
        if (updateInfo == null) {
            return;
        }
        this.updateInfo = updateInfo;
        if (updateInfo.versionCode > getPackageInfo().versionCode) {
            initDialog();
        } else {
            if(isShowToast){
                Toast.makeText(mContext, "当前是最新版本！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取当前应用的包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
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
        long enqueue = sharedPreferences.getLong("enqueue", -1);
        if (enqueue == -1) {
            result = true;
        } else {
            result = isNeedDownload(enqueue, downloadManager);
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
    private static boolean isNeedDownload(long id, DownloadManager downloadManager) {

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
}

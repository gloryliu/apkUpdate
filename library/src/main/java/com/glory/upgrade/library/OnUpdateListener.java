package com.glory.upgrade.library;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public interface OnUpdateListener {
    /**
     * 初始化更新弹窗
     * @param updateInfo
     */
    public void initDialog(final UpdateInfo updateInfo);

    /**
     * 权限监测有对应权限返回true,没有对应权限返回false
     * @return
     */
    public boolean checkSDPermision();
}

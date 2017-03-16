package com.glory.upgrade.library;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public interface OnUpgradeListener {

    /**
     * 初始化更新弹窗
     * @param builder
     */
    public void initDialog(final ApkUpgradeTool.Builder builder);

}

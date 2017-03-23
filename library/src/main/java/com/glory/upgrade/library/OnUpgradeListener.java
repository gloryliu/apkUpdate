package com.glory.upgrade.library;

/**
 * Created by liu on 2017/3/10.
 */

public interface OnUpgradeListener {


    /**
     * 初始化更新弹窗
     * @param upgradeTool
     */
    public void initDialog(final ApkUpgradeTool upgradeTool);

}

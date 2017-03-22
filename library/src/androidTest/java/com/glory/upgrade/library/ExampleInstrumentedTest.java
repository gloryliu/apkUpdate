package com.glory.upgrade.library;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        ApkUpgradeTool tool =  new ApkUpgradeTool.Builder(appContext)
                //apk下载地址
                .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                //版本号
                .versionCode(1)
                //升级回调用了初始化dialog
                .onUpdateListener(new OnUpgradeListener() {
                    @Override
                    public void initDialog(ApkUpgradeTool.Builder builder) {
                    }

                }).build();
        tool.startDownload();
                //是否显示toast提示
                //updateVersion(true);
        assertEquals("com.glory.upgrade.library.test", appContext.getPackageName());
    }
}

package com.glory.upgrade.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.glory.upgrade.R;
import com.glory.upgrade.library.ApkUpgradeTool;
import com.glory.upgrade.library.OnUpgradeListener;
import com.glory.upgrade.utils.DialogUtils;
import com.glory.upgrade.utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setText("当前版本"+getPackageInfo().versionName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApkUpgradeTool.Builder(MainActivity.this)
                        //apk下载地址
                        .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                        //版本号
                        .versionCode(1)
                        //升级回调用了初始化dialog
                        .onUpdateListener(new OnUpgradeListener() {
                            @Override
                            public void initDialog(ApkUpgradeTool.Builder builder) {
                                DialogUtils.showUpdateDialog(MainActivity.this, builder);
                            }

                        }).build()
                        //是否显示toast提示
                        .updateVersion(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Utils.checkVersion(this,true);
    }

    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }
}

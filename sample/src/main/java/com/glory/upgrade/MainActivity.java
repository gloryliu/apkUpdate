package com.glory.upgrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.glory.upgrade.library.ApkUpgradeTool;
import com.glory.upgrade.library.OnUpgradeListener;

public class MainActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        //点击按钮监测是否新版本
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApkUpgradeTool tool =  new ApkUpgradeTool.Builder(MainActivity.this)
                        //apk下载地址
                        .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                        //版本号
                        .versionCode(1)
                        //是否强制升级
                        .forceUpdate(true)
                        //升级回调用了初始化dialog
                        .onUpdateListener(new OnUpgradeListener() {
                            @Override
                            public void initDialog(ApkUpgradeTool upgradeTool) {
                                //初始化提示弹窗
                                DialogUtils.showUpdateDialog(MainActivity.this, upgradeTool);
                            }
                        }).build();
                //是否有toast提示功能
                tool.checkVersion(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //进入activity监测升级
        ApkUpgradeTool tool = new ApkUpgradeTool.Builder(MainActivity.this)
                //apk下载地址
                .apkUrl("http://www.lianjia.com/client/download?ua=android&channel=homelink")
                //版本号
                .versionCode(2)
                //强制升级
                .forceUpdate(true)
                //升级回调用了初始化dialog
                .onUpdateListener(new OnUpgradeListener() {
                    @Override
                    public void initDialog(ApkUpgradeTool upgradeTool) {
                        DialogUtils.showUpdateDialog(MainActivity.this, upgradeTool);
                    }
                }).build();
        //是否有toast提示功能
        tool.checkVersion(false);
    }
}

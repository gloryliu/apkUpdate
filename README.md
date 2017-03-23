# apkUpdate
A Android upgrade Library
# How to use :
# Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
# Step 2. Add the dependency
```xml

	dependencies {
    	        compile 'com.github.gloryliu:apkUpdate:v1.0.5'
    }

```
# Step 3.Add the following code in place need to upgrade
```java

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

```


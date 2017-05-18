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
    	        compile 'com.github.gloryliu:apkUpdate:v1.0.6'
    }

```
# Step 3.Add the following code in place need to upgrade
```java

            button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                       ApkUpgradeTool tool =  new ApkUpgradeTool.Builder(MainActivity.this)
                                           //apk或者patch（差分包）下载地址
                                           .apkUrl("http://192.168.51.85:8080/examples/patch")
                                           //版本号
                                           .versionCode(2)
                                           //升级模式，全量升级Config.UpdateMode.MODE_COVER,增量升级Config.UpdateMode.MODE_INCREM
                                           .setUpdateMode(Config.UpdateMode.MODE_INCREM)
                                           //是否强制升级
                                           .forceUpdate(true)
                                           //provider节点android:authorities属性的值
                                           .setProvider("com.glory.upgrade.fileprovider")
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

```
# Other
AndroidMainfest中要有
```xml
  <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.glory.upgrade.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
```
访问路径里边增加
```xml
<external-files-path name="external_files_upgrade" path="Download/" />
```



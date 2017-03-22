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
    	        compile 'com.github.gloryliu:apkUpdate:v1.0.2'
    }

```
# Step 3.Add the following code in place need to upgrade
```java

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

```

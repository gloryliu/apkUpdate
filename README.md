# apkUpdate
A Android upgrade Library
#使用案例
1、在build.gradle中加入库引用
```xml

	dependencies {
	        compile 'com.github.gloryliu:apkUpdate:1.0.0'
	}

```
2、在需要升级版本的地方加入以下代码
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

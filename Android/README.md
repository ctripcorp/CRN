### 在现有Android工程中接入CRN

1.将CRNDemo中 crnbase， rnSource目录拷贝至待接入工程顶级目录

<img src="../resources/android_copy.jpg" width="30%"/>

2.配置相关module

引入base, ReactAndroid module

settings.gradle
```
include ':ReactAndroid',':crnbase'
project(':ReactAndroid').projectDir = new File(rootProject.projectDir, './rnSource/ReactAndroid')
```

将原有项目对ReactNative依赖改为对crn的依赖：
```
dependencies {
    ...
    // implementation "com.facebook.react:react-native:+"  // From node_modules
    implementation project(':base')
    ...
}
```

3.将用CRN-CLI打包的产物添加到assets/webapp目录

<img src="../resources/android_webapp.jpg" width="30%"/>

4.启动逻辑中添加代码处理代码并预加载框架代码，可参考CRNDemo中的启动逻辑:

```
// 安装rn_commom
PackageManager.installPackageForProduct("rn_common");

// 预加载common
CRNInstanceManager.prepareReactInstanceIfNeed();

// 打开CRN页面
CRNURL crnurl = new CRNURL(url);
Intent intent = new Intent(MainActivity.this, CRNBaseActivity.class);
intent.putExtra(CRNBaseActivity.INTENT_COMPONENT_NAME, crnurl);
startActivity(intent);
```
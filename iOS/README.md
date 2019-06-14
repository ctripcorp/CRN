### 在现有iOS工程中接入CRN

1.使用CRN项目中的React.xcodeproj工程替换现有的React工程(CRN对RN所有的修改都在React工程里面)


2.将CRNDemo/CRN目录下的CRN.xcodeproj子工程添加到现有工程里，添加工程依赖和搜索路径.

工程依赖：（注意依赖的顺序：JavaScriptCore.framework和libReact.a在最前，libCRN.a在后）
<img src="../resources/iOS_linkBinary.png" width="60%" />

搜索路径：

<img src="../resources/iOS_searchpath.png" width="60%" />

3.将用CRN-CLI打包的产物添加到webapp目录并添加到工程引用。完成之后工程结构包含下图所示部分。 


<img src="../resources/iOS_project.png" width="60%" />


4.启动逻辑中添加代码将打包产物拷贝到工作目录，并预加载框架代码，可参考CRNDemo中的启动逻辑示例。

```
//拷贝到工作目录
//[self copyPackages];

// 预加载common
[[CRNBridgeManager sharedCRNBridgeManager] prepareBridgeIfNeed];

// 打开CRN页面
NSString *urlStr = @"/rn_rntester/main.js?CRNModuleName=CRNApp&CRNType=1";
CRNURL *url = [[CRNURL alloc] initWithPath:urlStr];
[CRNURLHandler openURL:url fromViewController:self];

```

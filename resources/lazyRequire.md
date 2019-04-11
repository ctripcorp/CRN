## LazyRequire

### 背景

随着业务复杂度增加，一个CRN业务包里面包含几十个页面，整个包的大小达到5M以上。 在进入该业务时，需要配置页面路由表，如果使用`require`直接加载这几十个页面，会出现首屏加载慢、白屏的问题。为了解决该问，我们需要按需加载的功能，在进入业务首屏时候，只加载首屏的代码，非首屏的页面，在切换之前先加载页面对象。


因此我们开发了LazyRequire方案，使用上和`require`类似，功能上和RN后来新增的`inlineRequire`类似, 但是会更强大(比如配置路由表这种功能，即便是使用`inlineRequire`，还是需要加载所有页面对象)。

### 接口说明

```
//modulePath 模块路径
LazyModule lazyRequire(modulePath) 
```
按需加载模块，指模块路径或别名，在使用该模块的时候先调用load()函数，再使用。


返回对象：
```
LazyModule = {
    load();    // 执行真正的模块加载, 返回当前模块对象。
}
```

### 示例代码：

```
//index.js
let lazyModule = lazyRequire("./module.js");  // lazyRequire模块在使用CRN-CLI工具时已在全局引入

setTimeout(function(){
    let module = lazyModule.load(); 
    module.default.show();
}, 3000);

//module.js
console.log("This is module loaded");

export default function show() {
     console.log("This is module function call");
}
```
执行结果为：
```
"This is module loaded"        //3s之后输出
"This is module function call" //3s之后输出
```

如果index.js中使用require导入该模块

```
//index.js
let module = require("./module.js");

setTimeout(function(){
    module.default.show();
}, 3000);

```

执行结果为:
```
"This is module loaded"        //立即输出
"This is module function call" //3s之后输出
```
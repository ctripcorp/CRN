/** 
 * Common包入口文件
 * 注册一个空壳App，预先加载。
 * 进入到业务包页面时，监听native事件上报,加载业务模块，重新渲染页面。
*/
import React, { Component } from 'react';
import {
  AppRegistry,
  View,
  DeviceEventEmitter
} from 'react-native';


var mainComponent = null;
var _component = null;
DeviceEventEmitter.removeAllListeners();

//native访问业务包，上报事件通知需要加载的模块ID
DeviceEventEmitter.addListener("requirePackageEntry", function (event) {
  if (event && event.packagePath) {
    global.CRN_PACKAGE_PATH = event.packagePath; //设置资源加载的路径
  }
  if (event && event.moduleId) {
    mainComponent = require(event.moduleId);
    if (_component) {
      _component.setState({ trigger: true });
      _component = null;
    }
  }
});

class CommonEntryComponent extends Component {

  getInitialState() {
    return { trigger: false };
  }

  render() {
    _component = this;
    var _content = null;
    if (mainComponent) {
      _content = React.createElement(mainComponent, this.props);
    }
    return _content || <View />;
  }

}

AppRegistry.registerComponent('CRNApp', () => CommonEntryComponent); //CRNApp名字请勿修改


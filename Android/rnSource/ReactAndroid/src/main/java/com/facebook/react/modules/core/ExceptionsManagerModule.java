/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.modules.core;

import com.facebook.common.logging.FLog;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.JavascriptException;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.util.JSStackTrace;

@ReactModule(name = ExceptionsManagerModule.NAME)
public class ExceptionsManagerModule extends BaseJavaModule {

  public static final String NAME = "ExceptionsManager";

  private final DevSupportManager mDevSupportManager;
  // CRN BEGIN
  private final ReactInstanceManager mInstanceManager;

  public ExceptionsManagerModule(DevSupportManager devSupportManager) {
    mDevSupportManager = devSupportManager;
    mInstanceManager = null;
  }

  public ExceptionsManagerModule(ReactInstanceManager instanceManager) {
    mInstanceManager = instanceManager;
    mDevSupportManager = instanceManager.getDevSupportManager();
  }
  // CRN END

  @Override
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void reportFatalException(String title, ReadableArray details, int exceptionId) {
    showOrThrowError(title, details, exceptionId);
  }

  @ReactMethod
  public void reportSoftException(String title, ReadableArray details, int exceptionId) {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.showNewJSError(title, details, exceptionId);
      //CRN BEGIN
    } else if (mInstanceManager != null && mInstanceManager.getCRNInstanceInfo().errorReportListener != null){
      mInstanceManager.getCRNInstanceInfo().errorReportListener.reportSoftException(mInstanceManager, title, details, exceptionId);
    }
    //CRN END
  }

  private void showOrThrowError(String title, ReadableArray details, int exceptionId) {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.showNewJSError(title, details, exceptionId);
      //CRN BEGIN
    } else if(mInstanceManager != null && mInstanceManager.getCRNInstanceInfo().errorReportListener != null) {
      mInstanceManager.getCRNInstanceInfo().errorReportListener.reportFatalException(mInstanceManager, title, details, exceptionId);
    }
    //CRN END
  }

  @ReactMethod
  public void updateExceptionMessage(String title, ReadableArray details, int exceptionId) {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.updateJSError(title, details, exceptionId);
      //CRN BEGIN
    } else if(mInstanceManager != null && mInstanceManager.getCRNInstanceInfo().errorReportListener != null) {
      mInstanceManager.getCRNInstanceInfo().errorReportListener.reportFatalException(mInstanceManager, title, details, exceptionId);
    }
    //CRN END
  }

  @ReactMethod
  public void dismissRedbox() {
    if (mDevSupportManager.getDevSupportEnabled()) {
      mDevSupportManager.hideRedboxDialog();
    }
  }
}

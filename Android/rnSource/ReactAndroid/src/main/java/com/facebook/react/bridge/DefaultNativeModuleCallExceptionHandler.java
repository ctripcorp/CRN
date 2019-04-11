/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.bridge;

import com.facebook.common.logging.FLog;
import com.facebook.react.devsupport.JSException;

import ctrip.crn.error.ErrorConstants;

/**
 * Crashy crashy exception handler.
 */
public class DefaultNativeModuleCallExceptionHandler implements NativeModuleCallExceptionHandler {

  @Override
  public void handleException(Exception e) {
    //CRN BEGIN
    if (e == null) {
      return;
    }

    StringBuilder errorMsg = new StringBuilder("DefaultNativeModuleCallExceptionHandler_handleException_");
    errorMsg.append(e.getMessage());
    Throwable cause = e.getCause();
    while (cause != null) {
      errorMsg.append("\n\n").append(cause.getMessage());
      cause = cause.getCause();
    }
    if (e instanceof JSException) {
      errorMsg.append("\n\n").append(((JSException) e).getStack());
    }

    FLog.e(ErrorConstants.CRN_FATAL_ERROR, errorMsg.toString());

    //CRN END
  }
}

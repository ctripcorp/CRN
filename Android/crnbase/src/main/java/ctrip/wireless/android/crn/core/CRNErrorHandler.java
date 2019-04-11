/*
 * Copyright Ctrip.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ctrip.wireless.android.crn.core;

import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.ReadableArray;

import ctrip.crn.error.CRNErrorReportListener;
import ctrip.wireless.android.crn.ContextHolder;
import ctrip.wireless.android.crn.utils.LogUtil;


public class CRNErrorHandler {

    private static CRNErrorReportListener errorReportListener = new CRNErrorReportListener() {
        @Override
        public void reportFatalException(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId) {
            // TODO 可再次统计错误信息
            LogUtil.e("Fatal Error:", title + "," + JSON.toJSONString(details));
            Toast.makeText(ContextHolder.application, "Fatal Error:" + title + "," + JSON.toJSONString(details), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void reportSoftException(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId) {
            // TODO 可再次统计错误信息
            LogUtil.e("Soft Error:", title + "," + JSON.toJSONString(details));
            Toast.makeText(ContextHolder.application, "Soft Error:" + title + "," + JSON.toJSONString(details), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateExceptionMessage(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId) {
            // TODO 可再次统计错误信息
            LogUtil.e("Update Error:", title + "," + JSON.toJSONString(details));
            Toast.makeText(ContextHolder.application, "Update Error:" + title + "," + JSON.toJSONString(details), Toast.LENGTH_SHORT).show();
        }
    };

    private static NativeModuleCallExceptionHandler mNativeExceptionHandler = new NativeModuleCallExceptionHandler() {
        @Override
        public void handleException(final Exception e) {
            // TODO 可再次统计错误信息
            LogUtil.e("Native Error:", e);
            Toast.makeText(ContextHolder.application, "Native Exception:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    public static CRNErrorReportListener getErrorReportListener() {
        return  errorReportListener;
    }

    public static NativeModuleCallExceptionHandler getNativeExceptionHandler() {
        return mNativeExceptionHandler;
    }

}

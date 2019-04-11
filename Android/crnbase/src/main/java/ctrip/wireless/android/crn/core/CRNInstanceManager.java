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

import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.DisplayMetricsHolder;

import java.io.File;
import java.util.ArrayList;

import ctrip.crn.instance.CRNInstanceInfo;
import ctrip.crn.instance.CRNInstanceState;
import ctrip.crn.instance.CRNLoadReportListener;
import ctrip.crn.instance.CRNPageInfo;
import ctrip.crn.instance.CRNReactContextLoadedListener;
import ctrip.crn.utils.ReactNativeJson;
import ctrip.wireless.android.crn.extend.CRNProvider;
import ctrip.wireless.android.crn.ContextHolder;
import ctrip.wireless.android.crn.utils.FileUtil;
import ctrip.wireless.android.crn.utils.LogUtil;
import ctrip.wireless.android.crn.utils.StringUtil;
import ctrip.wireless.android.crn.utils.ThreadUtils;

public class CRNInstanceManager {


    /**
     * interface InitReactNativeCallBack
     */
    public interface ReactInstanceLoadedCallBack {

        /**
         * callback instance and status
         * @param instanceManager instanceManager
         * @param status instance状态码
         */
        void onReactInstanceLoaded(ReactInstanceManager instanceManager, int status);

    }

    private static final String REQUIRE_BUSINESS_MODULE_EVENT = "requirePackageEntry";
    private static final String PREFS_DEBUG_SERVER_HOST_KEY = "debug_http_host";
    private final static String CONTAINER_VIEW_RELEASE_MESSAGE = "containerViewDidReleased";

    private static ArrayList<String> mInUsedCRNProduct = new ArrayList<>();

    /**
     * 所有Instance性能监控回调
     */
    private static CRNLoadReportListener mPerformanReportListener = new CRNLoadReportListener() {
        @Override
        public void onLoadComponentTime(ReactInstanceManager mng, long renderTime) {
            // TODO 业务开始渲染回调
        }
    };

    /**
     * 预创建ReactInstanceManager
     */
    public static void prepareReactInstanceIfNeed() {
        int readyCount = CRNInstanceCacheManager.getCacheCommonReactInstanceCount();
        if (readyCount >= 2) {
            LogUtil.e("CRN Instance ready count ="+readyCount);
            return;
        }

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CRNInstanceInfo crnInstanceInfo = new CRNInstanceInfo();
                crnInstanceInfo.businessURL = CRNURL.COMMON_BUNDLE_PATH;
                crnInstanceInfo.instanceState = CRNInstanceState.Loading;
                crnInstanceInfo.errorReportListener = CRNErrorHandler.getErrorReportListener();
                crnInstanceInfo.loadReportListener = mPerformanReportListener;
                createBundleInstance(new CRNURL(CRNURL.COMMON_BUNDLE_PATH), null, crnInstanceInfo, null);
            }
        }, 1000);
    }

    /**
     * 创建OnlineBundle、CacheUnbundle、AllUnbundle统一入口
     * @param rnURL rnURL
     * @param bundleScript bundleScript
     * @param crnInstanceInfo crnInstanceInfo
     * @param callBack callBack
     * @return ReactInstanceManagerpre
     */
    private static ReactInstanceManager createBundleInstance(final CRNURL rnURL,
                                                             String bundleScript,
                                                             CRNInstanceInfo crnInstanceInfo,
                                                             final ReactInstanceLoadedCallBack callBack) {

        if (rnURL == null || TextUtils.isEmpty(rnURL.getUrl())) {
            //极少，没有该错误
            callBack.onReactInstanceLoaded(null, -201);
            return null;
        }

        final boolean isOnlineBundle = rnURL.getRnSourceType() == CRNURL.SourceType.Online ;
        final boolean isNormalBundle = !isOnlineBundle && !TextUtils.isEmpty(bundleScript);
        final boolean isCommonBundle = CRNURL.COMMON_BUNDLE_PATH.equalsIgnoreCase(rnURL.getUrl());
        final boolean isUnbundleBizURL =  rnURL.isUnbundleURL();
        final boolean isCRNUnbundle = isCommonBundle || isUnbundleBizURL;

        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder();
        builder.setApplication(ContextHolder.application);
        builder.setInitialLifecycleState(LifecycleState.BEFORE_CREATE);
        builder.setCRNInstanceInfo(crnInstanceInfo);
        for (ReactPackage reactPackage: CRNProvider.provideReactPackages()) {
            builder.addPackage(reactPackage);
        }

        if (isOnlineBundle) {
            builder.setUseDeveloperSupport(true);
            builder.setJSMainModulePath("index");
            builder.setBundleScript(bundleScript, rnURL.getUrl(), false);
            Uri uri = Uri.parse(rnURL.getUrl());
            String debugUrl = uri.getHost() + ":" + (uri.getPort() == -1 ? 80 : uri.getPort());
            PreferenceManager.getDefaultSharedPreferences(ContextHolder.context)
                    .edit().putString(PREFS_DEBUG_SERVER_HOST_KEY, debugUrl).apply();
        }
        else if (isNormalBundle) {
            builder.setUseDeveloperSupport(false);
            builder.setBundleScript(bundleScript, rnURL.getUrl(), false);
            builder.setNativeModuleCallExceptionHandler(CRNErrorHandler.getNativeExceptionHandler());
            PreferenceManager.getDefaultSharedPreferences(ContextHolder.context)
                    .edit().remove(PREFS_DEBUG_SERVER_HOST_KEY).apply();
        }
        else if (isCRNUnbundle){
            builder.setUseDeveloperSupport(false);
            builder.setJSBundleFile(CRNURL.COMMON_BUNDLE_PATH);
            builder.setNativeModuleCallExceptionHandler(CRNErrorHandler.getNativeExceptionHandler());
            PreferenceManager.getDefaultSharedPreferences(ContextHolder.context)
                    .edit().remove(PREFS_DEBUG_SERVER_HOST_KEY).apply();
        }
        final ReactInstanceManager instanceManager = builder.build();
        instanceManager.setReactContextLoadedListener(new CRNReactContextLoadedListener() {
            boolean isInstanceLoaded = false;

            @Override
            public void onReactContextLoaded(ReactInstanceManager reactInstance) {
                if (ContextHolder.debug) {
                    if (isInstanceLoaded) {
                        return;
                    }
                    isInstanceLoaded = true;
                }
                int resultStatus = 0;
                if (reactInstance == null || reactInstance.getCRNInstanceInfo() == null || reactInstance.getCatalystInstance() == null)  {
                    resultStatus = -301;
                }
                else if (reactInstance.getCRNInstanceInfo().instanceState == CRNInstanceState.Error) {
                    resultStatus = -505;
                }
                else {
                    CRNInstanceInfo instanceInfo = reactInstance.getCRNInstanceInfo();
                    if (isOnlineBundle || isNormalBundle) {
                        instanceInfo.instanceState = CRNInstanceState.Dirty;
                        reactInstance.getCatalystInstance().setSourceURL(rnURL.getUrl());
                    } else if (isCRNUnbundle) {
                        if (isUnbundleBizURL) {
                            instanceInfo.instanceState = CRNInstanceState.Ready;
                            CRNUnbundlePackage unbundlePackage = new CRNUnbundlePackage(rnURL);
                            if (unbundlePackage.getModuleConfigHashMap() == null || unbundlePackage.getModuleConfigHashMap().isEmpty()) {
                                //极少，无此错误
                                resultStatus = -305;
                            } else {
                                reactInstance.getCatalystInstance().setCRNModuleIdConfig(unbundlePackage.getModuleConfigHashMap());
                                resultStatus = emitReRenderMessage(reactInstance, unbundlePackage.getMainModuleId(), rnURL.getUrl(), false);
                            }
                        } else {
                            instanceInfo.instanceState = CRNInstanceState.Ready;
                            resultStatus = -306;
                        }

                        cacheReactInstance(reactInstance);
                    }
                }

                if (callBack != null) {
                    callBack.onReactInstanceLoaded(reactInstance, resultStatus);
                }
            }
        });
        instanceManager.createReactContextInBackground();
        return instanceManager;
    }

    /**
     * 构建在线onlineBundle
     * @param rnURL rnURL
     * @param callBack callBack
     */
    private static ReactInstanceManager createOnlineReactInstance(CRNURL rnURL, ReactInstanceLoadedCallBack callBack) {
        File file = new File(ContextHolder.context.getFilesDir(), "ReactNativeDevBundle.js");
        if (file.exists()) {
            file.delete();
        }
        CRNInstanceInfo crnInstanceInfo = new CRNInstanceInfo();
        crnInstanceInfo.businessURL = rnURL.getUrl();
        crnInstanceInfo.instanceState = CRNInstanceState.Loading;
        crnInstanceInfo.originalInstanceStatus = CRNInstanceState.Loading;
        crnInstanceInfo.errorReportListener = CRNErrorHandler.getErrorReportListener();
        crnInstanceInfo.loadReportListener = mPerformanReportListener;
        return createBundleInstance(rnURL, "{}", crnInstanceInfo, callBack);
    }


    /**
     * 获取ReactInstanceManager
     * @param rnURL rnURL
     * @param callBack callBack
     */
    public static ReactInstanceManager getReactInstance(final CRNURL rnURL, CRNPageInfo crnPageInfo, final ReactInstanceLoadedCallBack callBack) {
        ReactInstanceManager reactInstance = null;
        int errorStatus = 0;
        boolean needCallbackRightNow = false;
        if (rnURL == null || !CRNURL.isCRNURL(rnURL.getUrl())) {
            if (rnURL == null) {
                errorStatus = -101;
            } else if (!CRNURL.isCRNURL(rnURL.getUrl())) {
                errorStatus = -102;
            }
            needCallbackRightNow = true;
        } else if (rnURL.getRnSourceType() == CRNURL.SourceType.Online) {
            reactInstance = createOnlineReactInstance(rnURL, callBack);
        } else {
            String crnURLStr = rnURL.getUrl();
            if(rnURL.isUnbundleURL()) { //unbundle格式，处理cache策略
                ReactInstanceManager readyCachedInstance = null;
                ReactInstanceManager dirtyCachedInstance = null;

                ReactInstanceManager readyToUseInstance = CRNInstanceCacheManager.getInstanceIfExist(rnURL);
                if (readyToUseInstance != null) {
                    if (readyToUseInstance.getCRNInstanceInfo().instanceState == CRNInstanceState.Dirty) {
                        dirtyCachedInstance = readyToUseInstance;
                    } else if (readyToUseInstance.getCRNInstanceInfo().instanceState == CRNInstanceState.Ready) {
                        readyCachedInstance = readyToUseInstance;
                    }
                }

                CRNUnbundlePackage unbundlePackage = new CRNUnbundlePackage(rnURL);
                if (dirtyCachedInstance != null) {
                    reactInstance = dirtyCachedInstance;
                    reactInstance.getCRNInstanceInfo().originalInstanceStatus = CRNInstanceState.Dirty;
                    reactInstance.getCRNInstanceInfo().countTimeoutError = 0;
                    reactInstance.getCRNInstanceInfo().countJSFatalError = 0;
                    reactInstance.getCRNInstanceInfo().countLogFatalError = 0;
                    reactInstance.getCRNInstanceInfo().countNativeFatalError = 0;
                    needCallbackRightNow = true;
                } else if (readyCachedInstance != null) {
                    if (unbundlePackage.getModuleConfigHashMap() == null || unbundlePackage.getModuleConfigHashMap().isEmpty()) {
                        errorStatus = -103;
                    } else {
                        readyCachedInstance.getCRNInstanceInfo().businessURL = crnURLStr;
                        readyCachedInstance.getCRNInstanceInfo().isUnbundle = true;
                        readyCachedInstance.getCRNInstanceInfo().inUseProductName = rnURL.getProductName();
                        readyCachedInstance.getCRNInstanceInfo().loadReportListener = mPerformanReportListener;
                        readyCachedInstance.getCRNInstanceInfo().errorReportListener = CRNErrorHandler.getErrorReportListener();
                        readyCachedInstance.getCatalystInstance().setCRNModuleIdConfig(unbundlePackage.getModuleConfigHashMap());
                        readyCachedInstance.getCRNInstanceInfo().originalInstanceStatus = CRNInstanceState.Ready;
                        int emitMsgRet = emitReRenderMessage(readyCachedInstance, unbundlePackage.getMainModuleId(), crnURLStr, true);
                        if (emitMsgRet == 0) {
                            errorStatus = 0;
                            reactInstance = readyCachedInstance;
                        } else {
                            errorStatus = -104;
                        }

                        //Ready的被使用了，预创建
                        prepareReactInstanceIfNeed();
                    }

                    needCallbackRightNow = true;
                }
            }

            if (reactInstance == null && errorStatus == 0) {
                CRNInstanceInfo instanceInfo = new CRNInstanceInfo();
                instanceInfo.isUnbundle = true;
                instanceInfo.businessURL = crnURLStr;
                instanceInfo.originalInstanceStatus = CRNInstanceState.Loading;
                instanceInfo.instanceState = CRNInstanceState.Loading;
                instanceInfo.inUseProductName = rnURL.getProductName();
                instanceInfo.loadReportListener = mPerformanReportListener;
                instanceInfo.errorReportListener = CRNErrorHandler.getErrorReportListener();
                String bundleScript = null;
                if(!rnURL.isUnbundleURL()) {
                    bundleScript = FileUtil.readFileAsString(new File(rnURL.getAbsoluteFilePath()));
                }
                reactInstance = createBundleInstance(rnURL, bundleScript, instanceInfo, callBack);
                if (reactInstance == null) {
                    errorStatus = -105;
                }
            }
        }

        if (ContextHolder.debug && errorStatus != 0) {
            Toast.makeText(ContextHolder.context
                    , "createReactInstance error: status=" + errorStatus
                    , Toast.LENGTH_SHORT).show();
        }

        if (reactInstance != null && reactInstance.getCRNInstanceInfo() != null) {
            reactInstance.getCRNInstanceInfo().countTimeoutError = 0;
            reactInstance.getCRNInstanceInfo().countJSFatalError = 0;
            reactInstance.getCRNInstanceInfo().countLogFatalError = 0;
            reactInstance.getCRNInstanceInfo().countNativeFatalError = 0;
        }

        if (needCallbackRightNow) {
            callBack.onReactInstanceLoaded(reactInstance, errorStatus);
        }

        cacheReactInstance(reactInstance);

        return reactInstance;
    }

    /**
     * 缓存创建好的ReactInstanceManager
     * @param manager manager
     */
    private static void cacheReactInstance(ReactInstanceManager manager) {
        CRNInstanceCacheManager.cacheReactInstanceIfNeed(manager);
    }

    /**
     * 离开RN容器页面，减少ReactInstanceManager的引用计数
     * @param  manager manager
     */
    public static void decreaseReactInstanceRetainCount(ReactInstanceManager manager, CRNURL crnurl) {
        synchronized (CRNInstanceManager.class) {
            if (manager != null && manager.getCRNInstanceInfo() != null) {
                manager.getCRNInstanceInfo().inUseCount -= 1;
                CRNInstanceCacheManager.performLRUCheck();
            }
        }
    }

    /**
     * 进入RN容器页面，增加ReactInstanceManager的引用计数
     * @param manager manager
     */
    public static void increaseReactInstanceRetainCount(ReactInstanceManager manager) {
        synchronized (CRNInstanceManager.class) {
            if (manager != null && manager.getCRNInstanceInfo() != null) {
                manager.getCRNInstanceInfo().inUseCount += 1;
            }
        }
    }

    /**
     * 重置无法使用Dirty状态下的instance为error
     * @param url url
     */
    public static void invalidateDirtyBridgeForURL(CRNURL url) {
        CRNInstanceCacheManager.invalidateDirtyBridgeForURL(url);
    }

    /**
     * Unbundle包，通知重新刷新页面
     * @param mng mng
     * @param mainModuleId mainModuleId
     */
    private static int emitReRenderMessage(ReactInstanceManager mng, String mainModuleId, String businessUrl, boolean fromCache) {
        int status = 0;
        if (TextUtils.isEmpty(mainModuleId)) {
            mainModuleId = "666666";
        }

        if (mng.getCRNInstanceInfo() == null) {
            status = -104;
        }

        if (status == 0) {
            if (businessUrl != null && businessUrl.contains("?")) {
                mng.setModulePath(businessUrl.substring(0, businessUrl.lastIndexOf("?")));
            } else {
                mng.setModulePath(businessUrl);
            }
            com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
            params.put("moduleId", mainModuleId);
            params.put("packagePath", businessUrl == null ? "" : businessUrl);
            params.put("productName", mng.getCRNInstanceInfo().inUseProductName);
            if (!emitDeviceEventMessage(mng, REQUIRE_BUSINESS_MODULE_EVENT, ReactNativeJson.convertJsonToMap(params))) {
                status = -103;
            }

        }
        mng.getCRNInstanceInfo().instanceState = CRNInstanceState.Dirty;
        return status; //status 不为0，后续invokeError会将mng状态设置为Error
    }

    /**
     * emit message
     * @param instanceManager instanceManager
     * @param paramMap paramMap
     */
    public static boolean emitDeviceEventMessage(ReactInstanceManager instanceManager, String eventName, WritableMap paramMap) {
        if (!isReactInstanceReady(instanceManager)) {
            return false;
        }
        try {
            if (instanceManager.getCurrentReactContext() != null){
                instanceManager.getCurrentReactContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(eventName, paramMap);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * check current instance could be used
     * @param instanceManager instanceManager
     */
    public static boolean isReactInstanceReady(ReactInstanceManager instanceManager) {
        if (instanceManager != null && instanceManager.getCRNInstanceInfo() != null ) {
            CRNInstanceInfo crnInfo = instanceManager.getCRNInstanceInfo();
            if (crnInfo.instanceState == CRNInstanceState.Dirty ||
                    crnInfo.instanceState == CRNInstanceState.Ready) {
                if (crnInfo.countJSFatalError > 0 || crnInfo.countLogFatalError > 0 || crnInfo.countNativeFatalError > 0 || crnInfo.countTimeoutError > 0) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 进入CRNPage
     * @param crnurl crn业务Url
     */
    public static void enterCRNPage(ReactInstanceManager reactInstanceManager, CRNURL crnurl) {
        if (crnurl != null && crnurl.getProductName() != null) {
            mInUsedCRNProduct.add(crnurl.getProductName());
        }
        CRNInstanceManager.increaseReactInstanceRetainCount(reactInstanceManager);
    }

    /**
     * 离开CRNPage
     * @param crnurl crn业务Url
     */
    public static void exitCRNPage(ReactInstanceManager mReactInstanceManager, CRNURL crnurl) {
        if (crnurl != null && crnurl.getProductName() != null) {
            int outPageIndex = mInUsedCRNProduct.lastIndexOf(crnurl.getProductName());
            if (outPageIndex != -1 && outPageIndex >= 0 && outPageIndex < mInUsedCRNProduct.size() ) {
                mInUsedCRNProduct.remove(outPageIndex);
            }
        }
        if (mReactInstanceManager != null) {
            CRNInstanceManager.emitDeviceEventMessage(mReactInstanceManager, CONTAINER_VIEW_RELEASE_MESSAGE, null);
            CRNInstanceManager.decreaseReactInstanceRetainCount(mReactInstanceManager, crnurl);
        }
    }

    /**
     * 查询当前业务是否有使用的Page
     * @param url url
     */
    public static boolean hasCRNPage(CRNURL url) {
        if (url == null || TextUtils.isEmpty(url.getProductName())) {
            return false;
        }
        for (String productName : mInUsedCRNProduct) {
            if (StringUtil.equalsIgnoreCase(productName, url.getProductName())) {
                return true;
            }
        }
        return false;
    }


}


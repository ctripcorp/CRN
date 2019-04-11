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

import android.text.TextUtils;

import com.facebook.react.ReactInstanceManager;

import java.util.ArrayList;
import java.util.Iterator;

import ctrip.crn.instance.CRNInstanceInfo;
import ctrip.crn.instance.CRNInstanceState;
import ctrip.wireless.android.crn.utils.StringUtil;

public class CRNInstanceCacheManager {

    private static ArrayList<ReactInstanceManager> mCachedInstanceList = new ArrayList<>();
    private static final int MAX_DIRTY_INSTANCE_COUNT = 6;

    /**
     * 获取CRNInstance
     */
    public static ReactInstanceManager getInstanceIfExist(CRNURL crnurl) {
        if (crnurl == null) {
            return null;
        }
        boolean ignoreCache = crnurl.ignoreCache();

        ReactInstanceManager readyToUseInstance = null;

        if (!ignoreCache) {
            // 闲置的Dirty/ReadyInstance将被重用
            for (ReactInstanceManager mng : mCachedInstanceList) {
                CRNInstanceInfo instanceInfo = null;
                if (mng == null || ((instanceInfo = mng.getCRNInstanceInfo()) == null)) {
                    continue;
                }
                boolean instanceStatusOK = instanceInfo.instanceState == CRNInstanceState.Dirty || instanceInfo.instanceState == CRNInstanceState.Ready;
                if (instanceStatusOK && instanceInfo.inUseCount == 0
                        && StringUtil.equalsIgnoreCase(crnurl.urlStr, instanceInfo.businessURL)) {
                    readyToUseInstance = mng;
                    continue;
                }
            }
        }

        if (readyToUseInstance == null) {
            readyToUseInstance = getCacheCommonReactInstance();
        }

        return readyToUseInstance;
    }

    /**
     * 获取Common CRNInstance
     */
    public static ReactInstanceManager getCacheCommonReactInstance() {
        for (ReactInstanceManager mng : mCachedInstanceList) {
            CRNInstanceInfo instanceInfo = null;
            if (mng == null || ((instanceInfo = mng.getCRNInstanceInfo()) == null)) {
                continue;
            }

            if (instanceInfo.instanceState == CRNInstanceState.Ready
                    && instanceInfo.inUseCount == 0 && CRNURL.COMMON_BUNDLE_PATH.equalsIgnoreCase(instanceInfo.businessURL)) {
                return mng;
            }
        }
        return null;
    }

    static int getCacheCommonReactInstanceCount() {
        int readyCount = 0;
        for (ReactInstanceManager mng : mCachedInstanceList) {
            CRNInstanceState instanceState = mng.getCRNInstanceInfo().instanceState;
            if (instanceState == CRNInstanceState.Ready) {
                readyCount++;
            }
        }
        return readyCount;
    }

    /**
     * 缓存创建好的ReactInstanceManager
     *
     * @param manager manager
     */
    static void cacheReactInstanceIfNeed(ReactInstanceManager manager) {
        if (manager == null) {
            return;
        }
        synchronized (CRNInstanceCacheManager.class) {
            if (!mCachedInstanceList.contains(manager)) {
                mCachedInstanceList.add(manager);
            }
        }
    }

    /**
     * 维护Instance缓存策略，释放error状态instance
     */
    static void performLRUCheck() {
        synchronized (CRNInstanceCacheManager.class) {
            int dirtyCount = 0;
            ReactInstanceManager olderMng = null;
            Iterator<ReactInstanceManager> iterator = mCachedInstanceList.listIterator();
            while (iterator.hasNext()) {
                ReactInstanceManager manager = iterator.next();
                if (manager == null) {
                    iterator.remove();
                } else if (manager.getCRNInstanceInfo().instanceState == CRNInstanceState.Error) {
                    releaseReactInstance(manager);
                    iterator.remove();
                } else if (manager.getCRNInstanceInfo() != null
                        && manager.getCRNInstanceInfo().inUseCount <= 0
                        && manager.getCRNInstanceInfo().instanceState == CRNInstanceState.Dirty) {
                    dirtyCount++;
                    if (olderMng == null
                            || olderMng.getCRNInstanceInfo().usedTimestamp > manager.getCRNInstanceInfo().usedTimestamp) {
                        olderMng = manager;
                    }
                }
            }

            if (dirtyCount > MAX_DIRTY_INSTANCE_COUNT) {
                deleteCachedInstance(olderMng);
            }
        }
    }

    static void deleteCachedInstance(ReactInstanceManager manager) {
        mCachedInstanceList.remove(manager);
        releaseReactInstance(manager);
    }

    /**
     * 释放ReactInstance在无法获取attachRootView状态下，确保可被完全释放
     *
     * @param instanceManager instanceManager
     */
    static void releaseReactInstance(ReactInstanceManager instanceManager) {
        if (instanceManager != null) {
            try {
                if (instanceManager.getAttachedRootView() != null) {
                    instanceManager.detachRootView(instanceManager.getAttachedRootView());
                }
                instanceManager.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 重置无法使用Dirty状态下的instance为error
     *
     * @param url url
     */
    static void invalidateDirtyBridgeForURL(CRNURL url) {
        if (url == null || TextUtils.isEmpty(url.getUrl())) {
            return;
        }
        synchronized (mCachedInstanceList) {
            for (ReactInstanceManager bridge : mCachedInstanceList) {
                if ((bridge != null && bridge.getCRNInstanceInfo() != null && bridge.getCRNInstanceInfo().businessURL != null)) {
                    CRNURL bridgeUrl = new CRNURL(bridge.getCRNInstanceInfo().businessURL);
                    if (StringUtil.equalsIgnoreCase(url.getProductName(), bridgeUrl.getProductName())) {
                        bridge.getCRNInstanceInfo().instanceState = CRNInstanceState.Error;
                    }
                }
            }
        }
    }


}

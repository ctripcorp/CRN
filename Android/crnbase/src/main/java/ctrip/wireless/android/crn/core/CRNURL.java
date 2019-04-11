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

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ctrip.wireless.android.crn.utils.LogUtil;
import ctrip.wireless.android.crn.utils.StringUtil;

public class CRNURL implements Serializable {

    public static final String DEFAULT_MODULE_NAME = "CRNApp";
    public static final String CRN_MODULE_NAME_KEY = "crnmodulename";
    public static final String CRN_TYPE_URL_PARAM = "crntype=1";

    public final static String RN_COMMON_PACKAGE_NAME = "rn_common";
    public final static String COMMON_BUNDLE_PATH = getRNBundleWorkPath() + "/" + RN_COMMON_PACKAGE_NAME + "/common_android.js";

    private final static String UNBUNDLE_FILE = "_crn_unbundle";
    private final static String IGNORE_CACHE_URL_PARAM = "ignorecached=1";


    public String urlStr = "";
    private String moduleName = "";
    private String productName = "";
    private String absoluteFilePath = "";
    private Map<String, String> queryParams;
    private String mUnbundleWorkPath;
    public String initParams;

    public enum SourceType {
        Unknown,
        Assets,
        File,
        Online,
    }

    private SourceType rnSourceType;

    public CRNURL(String urlStr_) {
        this.urlStr = urlStr_;
        this.rnSourceType = getRNSourceTypeFromUrl(urlStr_);
        this.absoluteFilePath = getRNFileAbsolutePath(urlStr_, this.rnSourceType);

        if (this.rnSourceType == SourceType.File) {
            if (!this.urlStr.contains(getRNBundleWorkPath())) {
                this.urlStr = getRNBundleWorkPath() + urlStr_;
            }
            this.mUnbundleWorkPath = getUnbundleWorkPathFromURL(this.absoluteFilePath, this.rnSourceType);
        }

        this.moduleName = getModuleNameFromUrl(this.urlStr);
        this.productName = getProductName(this.absoluteFilePath);
    }

    private static SourceType getRNSourceTypeFromUrl(String urlStr) {
        SourceType sourceType = SourceType.Unknown;
        boolean isOnlineURL = urlStr != null && urlStr.toLowerCase().startsWith("http");
        if (isOnlineURL) {
            sourceType = SourceType.Online;
        } else if (urlStr.startsWith("/")) {
            sourceType = SourceType.File;
        } else {
            sourceType = SourceType.Assets;
        }

        return sourceType;
    }

    public static String getRNBundleWorkPath() {
        return PackageManager.getFileWebappPath();
    }

    private static String getRNFileAbsolutePath(String urlStr_, SourceType sourceType_) {
        if (TextUtils.isEmpty(urlStr_)) {
            return null;
        }
        String absPath = "";
        int pathEndIndex = urlStr_.indexOf("?");
        if (pathEndIndex > 0) {
            absPath = urlStr_.substring(0, pathEndIndex);
        } else {
            return null;
        }
        switch (sourceType_) {
            case File:
                if (!absPath.contains(getRNBundleWorkPath())) {
                    absPath = getRNBundleWorkPath() + absPath;
                }
                break;
            default:
                break;
        }

        return absPath;
    }

    private static String getModuleNameFromUrl(String urlStr) {
        if (TextUtils.isEmpty(urlStr)) {
            return null;
        }
        String moduleName = null;
        Map<String, String> params = getQueryMap(urlStr);
        if (params != null) {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            Map.Entry<String, String> entry;

            while (it.hasNext()) {
                entry = it.next();
                String key = entry.getKey();
                if (CRN_MODULE_NAME_KEY.equalsIgnoreCase(key)) {
                    moduleName = entry.getValue();
                    break;
                }
            }
        }

        return moduleName;
    }

    private String getUnbundleWorkPathFromURL(String absPath, SourceType type) {
        if ((type == SourceType.File) && !TextUtils.isEmpty(absPath)) {
            int index = absPath.lastIndexOf('/');
            if (index > 0) {
                return absPath.substring(0, index);
            }
        }
        return null;
    }

    /**
     * url是否是crn url
     *
     * @param url url
     * @return boolean
     */
    public static boolean isCRNURL(String url) {
        return !TextUtils.isEmpty(url)
                && url.indexOf('?') > -1
                && StringUtil.toLowerCase(url).contains(StringUtil.toLowerCase(CRN_MODULE_NAME_KEY))
                && StringUtil.toLowerCase(url).contains(StringUtil.toLowerCase(CRN_TYPE_URL_PARAM));
    }

    /**
     * 获取unbundle的bu目录
     *
     * @return String
     */
    public String getUnbundleWorkPath() {
        return mUnbundleWorkPath;
    }

    public String getUrl() {
        return this.urlStr;
    }

    public Map<String, String> getUrlQuery() {
        if (queryParams == null) {
            queryParams = getQueryMap(this.urlStr);
        }
        return queryParams;
    }

    public String getModuleName() {
        if (isUnbundleURL()) {
            return DEFAULT_MODULE_NAME;
        }
        else {
            return TextUtils.isEmpty(this.moduleName) ? DEFAULT_MODULE_NAME : this.moduleName;
        }
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public SourceType getRnSourceType() {
        return rnSourceType;
    }

    public static Map<String, String> getQueryMap(String url) {
        Map<String, String> queries = new HashMap<>();
        try {
            if (url != null && url.contains("?") && url.length() > 1) {
                url = url.substring(url.indexOf("?") + 1, url.length());
                for (String str : url.split("&")) {
                    if (TextUtils.isEmpty(str)) {
                        continue;
                    }
                    int eIndex = str.indexOf("=");
                    String query[] = new String[2];
                    if (eIndex > 0 && eIndex < str.length()) {
                        query[0] = str.substring(0, eIndex);
                    }
                    if (eIndex + 1 > 0 && eIndex + 1 < str.length()) {
                        query[1] = str.substring(eIndex + 1, str.length());
                    }
                    queries.put(query[0], (query[1] != null ? URLDecoder.decode(query[1]) : null));
                }
            }
        } catch (Exception e) {
            LogUtil.e("error when parse querymap", e);
        }
        return queries;
    }

    public String getProductName() {
        return productName != null ? productName : "unkonwn_product";
    }

    public static String getProductName(String absFilePath) {
        String productName = null;
        if (absFilePath == null) {
            return productName;
        }

        String flag = getRNBundleWorkPath();
        int findIndex = absFilePath.indexOf(flag);
        if (findIndex >= 0 && flag != null) {
            String st = absFilePath.substring(findIndex + flag.length());
            if (st.startsWith("/")) {
                st = st.substring(1);
            }
            int endIndex = st.indexOf("/");
            if (endIndex >= 0) {
                productName = st.substring(0, endIndex);
            }
        }

        return productName;
    }

    public boolean isUnbundleURL() {
        return new File(this.mUnbundleWorkPath + "/" + UNBUNDLE_FILE).exists();
    }

    public boolean ignoreCache() {
        return (urlStr != null) && StringUtil.toLowerCase(urlStr).contains(IGNORE_CACHE_URL_PARAM);
    }
}

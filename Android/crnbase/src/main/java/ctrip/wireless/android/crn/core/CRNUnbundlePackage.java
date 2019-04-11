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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;


public class CRNUnbundlePackage {

    public final static String UNBUNDLE_CONFIG_FILEV2 = "_crn_config_v2";
    private final static String UNBUNDLE_MODULE_NAME_KEY = "main_module";
    private final static String UNBUNDLE_PATH_NAME_KEY = "module_path";
    private final static String UNBUNDLE_PATH_DIFF_KEY = "module_diff";

    private CRNURL mCRNURL;
    private String mRequirePath;
    private String mMainModuleId;
    private HashMap<String, String> mModuleConfigMap;

    /**
     * CRNUnbundle
     * @param crnurl crnurl
     */
    public CRNUnbundlePackage(CRNURL crnurl) {
        mCRNURL = crnurl;
        readUnbundleConfigInfo();
    }

    private void readUnbundleConfigInfo() {
        Properties mProperties = parseUnbundleConfig();
        if (mProperties != null) {
            mMainModuleId = (String) mProperties.get(UNBUNDLE_MODULE_NAME_KEY);
            //设置js-modules
            String path = (String) mProperties.get(UNBUNDLE_PATH_NAME_KEY);
            mRequirePath = mCRNURL.getUnbundleWorkPath() + '/' + path;
            mModuleConfigMap = getModuleConfigMapFromProperties(mProperties);

            //mModuleConfigMap为空时候，赋值modulePath，记录js 模块所在的目录
            if (mModuleConfigMap.isEmpty()) {
                mModuleConfigMap.put("modulePath", mRequirePath);
            }

            //设置js-diffs
            String diffFullPath = mCRNURL.getUnbundleWorkPath() + "/js-diffs";
            mModuleConfigMap.put("moduleDiff", diffFullPath);
        }
    }

    private Properties parseUnbundleConfig() {
        Properties properties = new Properties();
        final String workPath = mCRNURL.getUnbundleWorkPath();
        File file = new File(workPath + '/' + UNBUNDLE_CONFIG_FILEV2);
        if (file.exists()) {
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return properties;
    }

    private HashMap<String, String> getModuleConfigMapFromProperties(Properties mProperties) {
        HashMap<String, String> hashMap =  new HashMap<>();
        if (mProperties != null) {
            final String basePath = mRequirePath;
            if (basePath == null) {
                return hashMap;
            }
            Enumeration<String> enumeration = (Enumeration<String>) mProperties.propertyNames();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                if (!TextUtils.isEmpty(key)
                        && !UNBUNDLE_MODULE_NAME_KEY.equals(key)
                        && !UNBUNDLE_PATH_NAME_KEY.equals(key)
                        && !UNBUNDLE_PATH_DIFF_KEY.equals(key)) {
                    final String value = mProperties.getProperty(key);
                    final String jsPath = basePath + '/' + value;
                    hashMap.put(key, jsPath);
                }
            }
        }
        return hashMap;
    }

    /**
     * getMainModuleId
     * @return String
     */
    public String getMainModuleId() {
        return TextUtils.isEmpty(mMainModuleId) ? "666666" : mMainModuleId;
    }

    /**
     * getModulesMapping
     * @return Map
     */
    public HashMap<String, String> getModuleConfigHashMap() {
        return mModuleConfigMap;
    }

}


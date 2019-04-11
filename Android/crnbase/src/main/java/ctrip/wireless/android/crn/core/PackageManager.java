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

import ctrip.wireless.android.crn.ContextHolder;
import ctrip.wireless.android.crn.utils.FileUtil;

public class PackageManager {

    public static final String RN_PACKAGE_DIR = "webapp";

    public static boolean installPackageForProduct(String productName) {
        if (TextUtils.isEmpty(productName)) {
            return false;
        }

        //检查是否安装成功
        boolean isWorkDirExist = isExistWorkDirForProduct(productName);

        //从webapp目录安装
        if (!isWorkDirExist) {
            return installFromAssets(productName);
        }

        return isWorkDirExist;
    }


    private static boolean installFromAssets(String productName) {
        if (productName == null) {
            return false;
        }
        String pkgAssetsPath = packagePathInApkAssetsDir(productName);
        String webappDir = getFileWebappPath();
        boolean installSuccess = FileUtil.copyDirFromAsset(ContextHolder.context, pkgAssetsPath, webappDir + "/" + productName);
        return installSuccess;
    }


    public static boolean isExistWorkDirForProduct(String productName) {
        String productPath = getProductDirPath(productName);
        if (new File(productPath).exists()) {
            return true;
        }
        return false;
    }

    public static String getProductDirPath(String productName) {
        return getFileWebappPath() + "/" + productName;
    }

    public static String getFileWebappPath() {
        return ContextHolder.context.getFilesDir().getAbsolutePath() + "/" + RN_PACKAGE_DIR + "_" + ContextHolder.version;
    }

    public  static  String packagePathInApkAssetsDir(String productName) {
        if (productName == null) {
            return "";
        }
        String pkgAssetsPath = RN_PACKAGE_DIR + "/" + productName + "";
        return pkgAssetsPath;
    }

}

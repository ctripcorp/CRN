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
package ctrip.wireless.android.crn.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import ctrip.wireless.android.crn.ContextHolder;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static boolean copyDirFromAsset(Context context, String assetFolderName, String desDir) {
        AssetManager assetManager = context.getAssets();
        try {
            File desDirFile = new File(desDir);
            desDirFile.delete();
            desDirFile.mkdirs();
            String[] files = assetManager.list(assetFolderName);
            for (String filename : files) {
                String[] assets = assetManager.list(assetFolderName + "/" + filename);
                if (assets == null || assets.length == 0) {
                    InputStream in = assetManager.open(assetFolderName + "/" + filename);
                    OutputStream out = new FileOutputStream(desDir + "/" + filename);
                    copyFile(in, out);
                    in.close();
                    out.flush();
                    out.close();
                } else {
                    copyDirFromAsset(context, assetFolderName + "/" + filename, desDir + "/" + filename);
                }
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public static String readFileAsString(InputStream inputStream) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            reader = new InputStreamReader(inputStream);
            char[] buffer = new char[1024];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return writer.toString();
    }

    public static String readFileAsString(File file) {
        try {
            return readFileAsString(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param path
     *            删除目录,包括自己
     */
    public static void delDir(String path) {
        delFile(path);
        new File(path).delete();
    }

    /**
     * 功能描述:删除文件夹下所有文件和文件夹
     * <p/>
     * <pre>
     *     苟俊:   2013-1-16      新建
     * </pre>
     *
     * @param path
     */
    public static void delFile(String path) {
        File cacheFile = new File(path);
        if (!cacheFile.exists()) {
            return;
        }
        File[] files = cacheFile.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            // 是文件则直接删除
            if (files[i].exists() && files[i].isFile()) {
                files[i].delete();
            } else if (files[i].exists() && files[i].isDirectory()) {
                // 递归删除文件
                delFile(files[i].getAbsolutePath());
                // 删除完目录下面的所有文件后再删除该文件夹
                files[i].delete();
            }
        }
    }

}

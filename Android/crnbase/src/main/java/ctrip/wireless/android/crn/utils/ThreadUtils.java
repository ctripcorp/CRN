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

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadUtils {

    private static Handler mainHandler;

    public static void runOnUiThread(Runnable runnable) {
        internalRunOnUiThread(runnable, 0);
    }

    public static void runOnUiThread(Runnable runnable, long delayMillis) {
        internalRunOnUiThread(runnable, delayMillis);
    }


    private static void internalRunOnUiThread(Runnable runnable, long delayMillis) {
        getMainHandler();
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void post(Runnable runnable) {
        getMainHandler().post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        getMainHandler().postDelayed(runnable, delayMillis);
    }

    public static void removeCallback(Runnable runnable) {
        getMainHandler().removeCallbacks(runnable);
    }

    public static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

}

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
package ctrip.wireless.android.crn;

import android.app.Application;


import ctrip.wireless.android.crn.ContextHolder;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.context = this;
        ContextHolder.version = "1.0.0";
        ContextHolder.debug = true;
        ContextHolder.application = this;
    }

}

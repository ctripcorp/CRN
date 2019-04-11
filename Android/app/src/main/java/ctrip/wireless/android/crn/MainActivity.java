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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ctrip.wireless.android.crn.core.CRNInstanceManager;
import ctrip.wireless.android.crn.core.CRNURL;
import ctrip.wireless.android.crn.core.PackageManager;
import ctrip.wireless.android.crn.utils.FileUtil;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    Spinner rnSpinner;
    ListView onlineListView;
    ArrayAdapter simpleAdapter;
    SharedPreferences localSP;

    private static final String CRN_DEMO_SP_NAME = "crnDemoSP";
    private static final String CRN_DEMO_PRELOAD_COMMON = "PreloadCommon";
    private static final String CRN_DEMO_ADDRESS_LIST = "addressList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localSP = getSharedPreferences(CRN_DEMO_SP_NAME, Context.MODE_PRIVATE);

        // 测试使用：删除webapp目录
        FileUtil.delDir(PackageManager.getFileWebappPath());

        // 安装rn_commom
        PackageManager.installPackageForProduct("rn_common");

        if (localSP.getBoolean(CRN_DEMO_PRELOAD_COMMON, true)) {
            CRNInstanceManager.prepareReactInstanceIfNeed();
        }

        setContentView(R.layout.activity_main);
        requestSystemAlertWindow();
        initViews();
        gotoLocalConfigIfOk();
    }

    private void initViews() {
        spinner = findViewById(R.id.crnBundleSpinner);
        rnSpinner = findViewById(R.id.rnBundleSpinner);

        initSpinner();

        findViewById(R.id.loadCRN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager.installPackageForProduct(spinner.getSelectedItem() + "");
                startCRNBaseActivity("/" + spinner.getSelectedItem() + "/_crn_config?CRNModuleName=CRNApp&CRNType=1");
            }
        });

        findViewById(R.id.loadRN).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PackageManager.installPackageForProduct(rnSpinner.getSelectedItem() + "");
                startCRNBaseActivity("/" + rnSpinner.getSelectedItem() + "/main.js?CRNModuleName=RNTesterApp&CRNType=1");
            }
        });

        final EditText onlineUrlText = findViewById(R.id.customUrl);
        findViewById(R.id.gotoCustomUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String onlineUrl = onlineUrlText.getText().toString();
                if (!TextUtils.isEmpty(onlineUrl)) {
                    onlineClick(onlineUrl);
                }
            }
        });

        initOnlineList();

        Switch preloadSwitch = findViewById(R.id.preloadCommon);
        boolean preloadCommon = localSP.getBoolean(CRN_DEMO_PRELOAD_COMMON, true);
        preloadSwitch.setChecked(preloadCommon);
        preloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean open) {
                localSP.edit().putBoolean(CRN_DEMO_PRELOAD_COMMON, open).commit();
                Toast.makeText(MainActivity.this, "已" + (open ? "打开" : "关闭") +  "CRN预加载，重启APP生效", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initOnlineList() {
        onlineListView = findViewById(R.id.onlineList);
        List<String> list = new ArrayList<>();
        String listString = localSP.getString(CRN_DEMO_ADDRESS_LIST, "");
        if (!TextUtils.isEmpty(listString)) {
            list.addAll(Arrays.asList(listString.split(",")));
        }
        simpleAdapter = new ArrayAdapter(this, R.layout.crn_spinner_item, list);
        onlineListView.setAdapter(simpleAdapter);
        onlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startCRNBaseActivity((String) simpleAdapter.getItem(i));
            }
        });
    }

    private void initSpinner() {
        try {
            String[] webappDirs = getAssets().list("webapp");
            List<String> crnBundles = new ArrayList<>();
            List<String> rnBundles = new ArrayList<>();
            for (String webappDir : webappDirs) {
                if (webappDir.toLowerCase().contains("_crn")) {
                    crnBundles.add(webappDir);
                } else if (webappDir.toLowerCase().contains("_rn")) {
                    rnBundles.add(webappDir);
                }
            }
            Collections.reverse(crnBundles);
            Collections.reverse(rnBundles);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, R.layout.crn_spinner_item, crnBundles.toArray(new String[crnBundles.size()])) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.setPadding(0, view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());
                    return view;
                }
            };

            spinner.setAdapter(adapter);

            ArrayAdapter<CharSequence> rnAdapter = new ArrayAdapter(this, R.layout.crn_spinner_item, rnBundles.toArray(new String[rnBundles.size()])) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.setPadding(0, view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());
                    return view;
                }
            };
            rnSpinner.setAdapter(rnAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onlineClick(String onlineUrl) {
        addToLocalUrls(onlineUrl);
        startCRNBaseActivity(onlineUrl);
    }

    private boolean addToLocalUrls(String url) {
        String listString = localSP.getString(CRN_DEMO_ADDRESS_LIST, "");
        if (!listString.contains(url)) {
            listString += url;
            if (TextUtils.isEmpty(listString)) {
                listString += ",";
            }
            localSP.edit().putString(CRN_DEMO_ADDRESS_LIST, listString).apply();
            simpleAdapter.add(url);
            simpleAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private void gotoLocalConfigIfOk() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            gotoLocalConfig();
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 000);
        }
    }

    private void gotoLocalConfig() {
        File entryFile = new File("/sdcard/.__RN_Debug_URL.log");
        if (entryFile != null && entryFile.exists()) {
            String url = FileUtil.readFileAsString(entryFile);
            if (!CRNURL.isCRNURL(url)) {
                Toast.makeText(MainActivity.this, "CRN URL is illegal!", Toast.LENGTH_SHORT).show();
                return;
            }

            addToLocalUrls(url);
            startCRNBaseActivity(url.trim());
            entryFile.delete();
        }
    }

    private void requestSystemAlertWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            gotoLocalConfig();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "权限失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCRNBaseActivity(String url) {
        if (!CRNURL.isCRNURL(url)) {
            Toast.makeText(MainActivity.this, "CRN URL is illegal!", Toast.LENGTH_SHORT).show();
            return;
        }
        CRNURL crnurl = new CRNURL(url);
        if (crnurl.getRnSourceType() != CRNURL.SourceType.Online) {
            PackageManager.installPackageForProduct(crnurl.getProductName());
        }
        Intent intent = new Intent(MainActivity.this, CRNBaseActivity.class);
        intent.putExtra(CRNBaseActivity.INTENT_COMPONENT_NAME, crnurl);
        startActivity(intent);
    }

}

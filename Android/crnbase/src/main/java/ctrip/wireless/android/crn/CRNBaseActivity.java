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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import ctrip.wireless.android.crn.business.R;
import ctrip.wireless.android.crn.core.CRNURL;


public class CRNBaseActivity extends FragmentActivity implements
        CRNBaseFragment.OnLoadRNErrorListener, CRNBaseFragment.OnReactViewDisplayListener {

    public static final String INTENT_COMPONENT_NAME = "ComponentName";
    private static final String CRN_FRAGMENT_TAG = "crn_fragment_tag";

    private CRNBaseFragment mCRNBaseFragment;
    private CRNURL mCRNURL;
    private View mLoadingView;
    private boolean displaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCRNURL = (CRNURL) getIntent().getSerializableExtra(INTENT_COMPONENT_NAME);
        if (mCRNURL == null || !CRNURL.isCRNURL(mCRNURL.getUrl())) {
            onErrorBrokeCallback(-1003, "");
            return;
        }

        setContentView(R.layout.rn_activity);
        mLoadingView = findViewById(R.id.rnLoadingView);
        renderCRNBaseFragment();
    }

    private void renderCRNBaseFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragmentTransaction == null) {
            return;
        }
        mCRNBaseFragment = new CRNBaseFragment();
        mCRNBaseFragment.setLoadRNErrorListener(CRNBaseActivity.this);
        mCRNBaseFragment.setReactViewDisplayListener(CRNBaseActivity.this);
        try {
            Bundle bundle = new Bundle();
            bundle.putString(CRNBaseFragment.CRNURL_KEY, mCRNURL.getUrl());
            mCRNBaseFragment.setArguments(bundle);
        } catch (Exception ignore) {
        }
        fragmentTransaction.add(R.id.rnFragmentView, mCRNBaseFragment, CRN_FRAGMENT_TAG).commitAllowingStateLoss();
    }

    @Override
    public void reactViewDisplayed() {
        mLoadingView.setVisibility(View.GONE);
        displaying = true;
    }

    @Override
    public void onErrorBrokeCallback(int errCode, String message) {
        Toast.makeText(this, "CRN错误:" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCRNBaseFragment != null) {
            mCRNBaseFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && mCRNBaseFragment != null && displaying) {
            mCRNBaseFragment.goBack();
            return true;
        } else if (KeyEvent.KEYCODE_MENU == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCRNBaseFragment != null) {
            mCRNBaseFragment.setReactViewDisplayListener(null);
            mCRNBaseFragment.setLoadRNErrorListener(null);
        }
    }

}
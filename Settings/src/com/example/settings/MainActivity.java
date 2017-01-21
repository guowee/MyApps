package com.example.settings;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends CustomPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomActionBar();
    }

    private boolean initCustomActionBar() {
        ActionBar mActionbar = getActionBar();
        if (mActionbar == null) {
            return false;
        }
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.header_layout);
        return true;
    }

    @Override
    public void onBuildHeaders(List<Header> headers) {
        super.onBuildHeaders(headers);
        loadHeadersFromResource(R.xml.settings_headers, headers);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.example.settings.bluetooth;

import com.example.settings.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class BluetoothSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bluetooth_settings);
    }

}

package com.example.settings.wifi;

import com.example.settings.MultiActivity;
import com.example.settings.R;
import com.example.settings.SingleActivity;
import com.example.settings.pref.MyEditTextPreference;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WifiSettings extends PreferenceFragment implements OnPreferenceClickListener {

    Preference getValueSingPrefs, getValueMultiPrefs, getValueEditPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_settings);
        initPrefers();
    }

    public static final int REQUEST_CODE_1 = 0x01;
    public static final int REQUEST_CODE_2 = 0x02;

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
        if (preference == getValueSingPrefs) {
            Intent intent = new Intent(getActivity(), SingleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_1);
        } else if (preference == getValueMultiPrefs) {
            Intent intent = new Intent(getActivity(), MultiActivity.class);
            startActivityForResult(intent, REQUEST_CODE_2);
        } else if (preference == getValueEditPrefs) {

            final Dialog dialog = ((MyEditTextPreference) preference).getDialog();

            final EditText et = (EditText) dialog.findViewById(R.id.edit_tv);

            Button confirm = (Button) dialog.findViewById(R.id.positiveButton);
            Button cancel = (Button) dialog.findViewById(R.id.negativeButton);

            cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    preference.setSummary(et.getText().toString());
                    dialog.dismiss();
                }
            });

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_1:
                getValueSingPrefs.setSummary(data.getExtras().getString("osNameKey"));
                break;
            case REQUEST_CODE_2:
                getValueMultiPrefs.setSummary(data.getExtras().getString("languageKey"));
                break;
            default:
                break;
        }

    }

    private void initPrefers() {
        getValueSingPrefs = findPreference("getValue_single_prefers");
        getValueMultiPrefs = findPreference("getValue_multi_prefers");
        getValueEditPrefs = findPreference("edittext_preference");
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

}

package com.example.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;

@SuppressWarnings("deprecation")
public class SingleActivity extends ChoicePrefsActivity {
    CheckBoxPreference android, ios, wp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.bgColor);
        setContentView(R.layout.prefs_list_content);
        addPreferencesFromResource(R.xml.second_level_prefs);

        initPreference();

        // 添加到单选列表中，并且指定返回的KEY。这样就能传递选中的值到概要中了
        addToSingleChoiceList("osNameKey", android, ios, wp);
    }

    private void initPreference() {
        android = (CheckBoxPreference) findPreference("android_prefs");
        ios = (CheckBoxPreference) findPreference("ios_prefs");
        wp = (CheckBoxPreference) findPreference("wp_prefs");
    }

}

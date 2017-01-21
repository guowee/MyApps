package com.example.settings;

import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

/**
 * @author:Jack Tony
 * @tips :提供一组单选、多选列表界面。如果整个界面中有多组单选、多选列表的话也可以使用
 * @date :2014-8-6
 */
public class ChoicePrefsActivity extends PreferenceActivity {

    /**
     * @author:Jack Tony
     * @tips :如果returnData为true，那么就会返回选中的值
     * @date :2014-8-6
     */
    public void addToSingleChoiceList(String key, CheckBoxPreference... boxes) {
        SingleChoiceListener listener = new SingleChoiceListener(boxes, key);
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setOnPreferenceClickListener(listener);
        }
    }

    public void addToSingleChoiceList(CheckBoxPreference... boxes) {
        SingleChoiceListener listener = new SingleChoiceListener(boxes, null);
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setOnPreferenceClickListener(listener);
        }
    }

    private class SingleChoiceListener implements OnPreferenceClickListener {

        private CheckBoxPreference[] boxes;
        private String KEY;

        public SingleChoiceListener(CheckBoxPreference[] boxes, String key) {
            this.boxes = boxes;
            KEY = key;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            for (int i = 0; i < boxes.length; i++) {
                boxes[i].setChecked(false);
            }
            ((CheckBoxPreference) preference).setChecked(true);
            if (KEY != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY, preference.getTitle());
                // 设置返回数据
                setResult(RESULT_OK, resultIntent);// 设置给之前启动它的activity的一个返回码
                // finish(); //可以设置成点击完返回上一个界面
            }
            return false;
        }

    }

    // ///////////////////////// 多选界面的方法 /////////////////////////
    private String data;

    public void addToMultiChoiceList(String key, CheckBoxPreference... boxes) {
        MultiChoiceListener listener = new MultiChoiceListener(boxes, key);
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setOnPreferenceClickListener(listener);
        }
    }

    private class MultiChoiceListener implements OnPreferenceClickListener {

        private CheckBoxPreference[] boxes;
        private String KEY;

        public MultiChoiceListener(CheckBoxPreference[] boxes, String key) {
            this.boxes = boxes;
            KEY = key;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            data = "  ";
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i].isChecked()) {
                    data += boxes[i].getTitle() + "，";
                }
            }
            data = data.substring(0, data.length() - 1);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY, data);
            // 设置返回数据
            setResult(RESULT_OK, resultIntent);// 设置给之前启动它的activity的一个返回码
            return false;
        }
    }

}

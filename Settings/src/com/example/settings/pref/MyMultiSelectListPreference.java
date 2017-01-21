package com.example.settings.pref;

import java.util.Set;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;
import android.view.View;

public class MyMultiSelectListPreference extends MultiSelectListPreference {

	public MyMultiSelectListPreference(Context context) {
		super(context);
	}

	public MyMultiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @return 列表选中状态的数组
	 */
	private boolean[] getSelectedItems() {
		final CharSequence[] entries = getEntryValues();
		final int entryCount = entries.length;
		final Set<String> values = getValues();
		boolean[] result = new boolean[entryCount];

		for (int i = 0; i < entryCount; i++) {
			result[i] = values.contains(entries[i].toString());
		}
		return result;
	}

	private void updateSummary() {
		setSummary(" ");
		CharSequence[] c = getEntries();// 得到列表值数组
		String str = (String) getSummary();// 得到列表选择状态数组
		for (int i = 0; i < getSelectedItems().length; i++) {
			if (getSelectedItems()[i]) {
				str += (String) c[i] + "，";
			}
		}
		str = str.substring(0, str.length() - 1);// 去除最后一个逗号
		// 设置摘要的内容
		setSummary(str);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		int i;
		for (i = 0; i < getSelectedItems().length; i++) {
			if (getSelectedItems()[i] == true) {
				updateSummary();
				break;
			}
		}
		if (i == getSelectedItems().length) {
			setSummary("");
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		int i;
		for (i = 0; i < getSelectedItems().length; i++) {
			if (getSelectedItems()[i] == true) {
				updateSummary();
				break;
			}
		}
		if (i == getSelectedItems().length) {
			setSummary("");
		}
	}
}

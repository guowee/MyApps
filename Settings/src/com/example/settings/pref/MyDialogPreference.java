package com.example.settings.pref;

import com.example.settings.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class MyDialogPreference extends CustomDialogPreference {

    EditText edittext;

    public MyDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDialogPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        SharedPreferences pref = getSharedPreferences();

        edittext.setText(pref.getString(getKey(), "ExampleHostname"));

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (!positiveResult)
            return;

        SharedPreferences.Editor editor = getEditor();
        editor.putString(getKey(), edittext.getText().toString());

        editor.commit();
        setSummary(edittext.getText().toString());
    }

    @Override
    public void findViewsInDialogById(View view) {
        edittext = (EditText) view.findViewById(R.id.edit_tv);
    }

    @Override
    public int setLayoutInDialog() {
        return R.layout.dialog_layout;
    }

}

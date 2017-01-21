package com.example.settings.pref;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class CustomDialogPreference extends DialogPreference {
    private Dialog mDialog;

    public CustomDialogPreference(Context context) {
        this(context, null);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs) {// 构造函数很重，能够保证执行到DialogPreference中设置到dialogPreferenceStyle，否则得不到确定和取消按钮的字符串。
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(setLayoutInDialog());
    }

    @Override
    protected void onClick() {
        if (mDialog != null && mDialog.isShowing())
            return;

        showDialog(null);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mDialog = null;
    }

    @java.lang.Override
    protected View onCreateDialogView() {
        if (getDialogLayoutResource() != 0) {
            return View.inflate(getContext(), getDialogLayoutResource(), null);
        }
        return null;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @java.lang.Override
    protected void onBindDialogView(View view) {
        findViewsInDialogById(view);
    }

    public abstract void findViewsInDialogById(View view);

    public abstract int setLayoutInDialog();

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        Context context = getContext();

        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(getDialogTitle()).setNegativeButton(getNegativeButtonText(), this)
                .setPositiveButton(getPositiveButtonText(), this);
        View contentView = onCreateDialogView();

        if (contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        }

        Dialog dialog = mDialog = builder.create();

        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }

        if (needInputMethod()) {
            Window window = dialog.getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    /** @hide */
    protected boolean needInputMethod() {
        // We want the input method to show, if possible, when dialog is displayed
        return true;
    }

    @Override
    public void onActivityDestroy() {
        if (mDialog == null || !mDialog.isShowing()) {
            return;
        }
        mDialog.dismiss();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

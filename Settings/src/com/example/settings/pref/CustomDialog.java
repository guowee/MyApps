package com.example.settings.pref;

import com.example.settings.R;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {
    private int dialogPadding;

    public CustomDialog(Context context, View view) {
        super(context, R.style.AlertDialogStyle);
        setContentView(view);
        dialogPadding = context.getResources().getDimensionPixelOffset(R.dimen.dialog_window_padding);

        Window window = getWindow();
        // 使Dialog宽度跟屏幕宽度一样，否则Dialog的宽度会跟里面内容的最大宽度一样。
        WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = display.getWidth() * 2 / 3;
        params.height = display.getHeight() / 3;
        window.setAttributes(params);
        window.getDecorView().setPadding(dialogPadding, 0, dialogPadding, 0);// 设置距离屏幕两边的距离
    }

    public static class Builder {
        private Context mContext;
        private FrameLayout mCustomerContainer;
        private View mCustomerView;
        private CharSequence mTitle;
        private CharSequence mPositiveText;
        private CharSequence mNegativeText;
        private CharSequence mMessage;
        private OnClickListener mNegativeButtonListener;
        private OnClickListener mPositiveButtonListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(int textID) {
            setTitle(mContext.getString(textID));
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(int textID) {
            mMessage = mContext.getString(textID);
            return this;
        }

        public Builder setPositiveButton(int textID, OnClickListener onClickListener) {
            setPositiveButton(mContext.getString(textID), onClickListener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener onClickListener) {
            mPositiveText = text;
            mPositiveButtonListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(int textID, OnClickListener onClickListener) {
            setNegativeButton(mContext.getString(textID), onClickListener);
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener onClickListener) {
            mNegativeText = text;
            mNegativeButtonListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            mCustomerView = view;
            return this;
        }

        public CustomDialog create() {
            View contentView = View.inflate(mContext, R.layout.alert_dialog_layout, null);
            final CustomDialog dialog = new CustomDialog(mContext, contentView);

            TextView titleView = (TextView) contentView.findViewById(R.id.alertTitle);
            if (!TextUtils.isEmpty(mTitle)) {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(mTitle);
            } else {
                titleView.setVisibility(View.GONE);
            }

            FrameLayout customerContainer = (FrameLayout) contentView.findViewById(R.id.customer_container);
            if (mCustomerView != null) {
                customerContainer.setVisibility(View.VISIBLE);
                customerContainer.addView(mCustomerView);
            } else {
                customerContainer.setVisibility(View.GONE);
            }

            Button positiveButton = (Button) contentView.findViewById(R.id.positiveButton);
            if (mPositiveText != null) {
                positiveButton.setText(mPositiveText);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPositiveButtonListener.onClick(dialog, BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
                });
            } else {
                positiveButton.setVisibility(View.GONE);
            }

            Button negativeButton = (Button) contentView.findViewById(R.id.negativeButton);
            if (mNegativeText != null) {
                negativeButton.setText(mNegativeText);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNegativeButtonListener.onClick(dialog, BUTTON_NEGATIVE);
                        dialog.dismiss();
                    }
                });
            } else {
                negativeButton.setVisibility(View.GONE);
            }

            ViewGroup buttonsContainer = (ViewGroup) contentView.findViewById(R.id.buttonPanel);
            buttonsContainer.setVisibility(shouldSetGone(buttonsContainer));

            return dialog;
        }

        private int shouldSetGone(ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i).getVisibility() != View.GONE) {
                    return View.VISIBLE;
                }
            }
            return View.GONE;
        }
    }

    public Button getPositiveButton() {
        return (Button) getWindow().getDecorView().findViewById(R.id.positiveButton);
    }
}

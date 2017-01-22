package com.hipad.tracker.dialog;

import com.hipad.tracker.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogoutDialog extends Dialog implements
		android.view.View.OnClickListener {
	private OnLogoutDialogListener mListener;
	private Button cancel;
	private Button confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logout_dialog_layout);
		getViews();
		setOnClickListener();
	}

	private void getViews() {
		cancel = (Button) findViewById(R.id.btn_cancel);
		confirm = (Button) findViewById(R.id.btn_confirm);
	}

	private void setOnClickListener() {
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	public LogoutDialog(Context context, int style,	OnLogoutDialogListener listener) {
		super(context, style);
		this.mListener = listener;
	}

	public interface OnLogoutDialogListener {
		public void OnLogoutClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			dismiss();
			break;
		case R.id.btn_confirm:
			if (mListener != null) {
				mListener.OnLogoutClick();
			}
			dismiss();
			break;
		default:
			break;
		}
	}
}
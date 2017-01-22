package com.hipad.smarthome.utils;

import com.hipad.smarthome.R;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyDialog extends Dialog implements OnClickListener {

	private OnDialogListener mListener;

	private Button submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydialog_layout);
		init();
	}

	private void init() {
		submit = (Button) findViewById(R.id.dialog_button_ok);
		submit.setOnClickListener(this);
	}

	public MyDialog(Context context, int theme, OnDialogListener listener) {
		super(context, theme);
		this.mListener = listener;
	}

	public interface OnDialogListener {
		public void OnClick(View v);
	}

	@Override
	public void onClick(View v) {
		mListener.OnClick(v);
	}
}

package com.hipad.smarthome.utils;

import com.hipad.smarthome.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog implements
		android.view.View.OnClickListener {

	private TextView dialog_title;
	private Button dialog_ok;
	private Button dialog_cancel;

	public OnCustomDialogListener  customListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_define);
		initViews();
		setOnClickListener();
	}

	public CustomDialog(Context context, int style, OnCustomDialogListener  listener) {
		super(context, style);
		this.customListener = listener;
	}

	public interface OnCustomDialogListener  {

		public void OnClick(View v);
	}

	private void initViews() {

		dialog_cancel = (Button) findViewById(R.id.dialog_button_cancel);
		dialog_ok = (Button) findViewById(R.id.dialog_button_ok);
		dialog_title = (TextView) findViewById(R.id.dialog_title);

	}

	private void setOnClickListener() {
		dialog_cancel.setOnClickListener(this);
		dialog_ok.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		customListener.OnClick(v);
	}

}

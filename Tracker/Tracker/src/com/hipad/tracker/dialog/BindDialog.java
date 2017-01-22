package com.hipad.tracker.dialog;


import com.hipad.tracker.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 * 
 * @author guowei
 *
 */
public class BindDialog extends Dialog implements
		android.view.View.OnClickListener {

	private OnBindDialogListener bindListener;
	
	private Button confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_define);
		initViews();	
		setOnClickListener();
	}

	public BindDialog(Context context, int style,OnBindDialogListener listener) {
		super(context, style);
		this.bindListener = listener;

	}

	private void initViews() {
    confirm=(Button) findViewById(R.id.dialog_button_ok);
	}

	private void setOnClickListener() {
		confirm.setOnClickListener(this);

	}

	public interface OnBindDialogListener {

		public void OnClick(View v);
	}

	@Override
	public void onClick(View v) {
		bindListener.OnClick(v);
	}

}

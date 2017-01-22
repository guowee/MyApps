package com.hipad.tracker.dialog;


import com.hipad.tracker.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UpdateDialog extends Dialog implements android.view.View.OnClickListener {

	private Button cancel;
	private Button confirm;
	private OnUpdatDialogListener updateListener;
	
	
	public UpdateDialog(Context context, int style,OnUpdatDialogListener listener) {
		super(context, style);
		this.updateListener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_dialog);
		initViews();
		setOnClickListener();
	}
	
	private void initViews(){
		cancel=(Button) findViewById(R.id.btn_cancel);
		confirm=(Button) findViewById(R.id.btn_confirm);
	}
	
	private void setOnClickListener(){
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
	
	public interface OnUpdatDialogListener {
		public void OnClick(View v);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			dismiss();
			break;
		case R.id.btn_confirm:
			if(updateListener!=null){
				updateListener.OnClick(v);
			}
			
			dismiss();
			break;
		default:
			break;
		}
	
	}
	
}

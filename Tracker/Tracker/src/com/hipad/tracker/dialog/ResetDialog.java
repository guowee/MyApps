package com.hipad.tracker.dialog;


import com.hipad.tracker.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/**
 * 
 * @author guowei
 *
 */
public class ResetDialog extends Dialog implements android.view.View.OnClickListener {

	private EditText reimei;
	private Button cancel;
	private Button reset;
	
	private String imeiStr;
	
	private OnResetDialogListener mListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_reset);
		getViews();
		setOnClickListener();
	}
	public ResetDialog(Context context,int style,OnResetDialogListener listener) {
		super(context,style);
		this.mListener=listener;
	}
	
	private void getViews() {

		reimei=(EditText) findViewById(R.id.reset_imei);
		cancel=(Button) findViewById(R.id.dialog_button_cancel);
		reset=(Button) findViewById(R.id.dialog_button_reset);
		
	}
	private void setOnClickListener(){
		cancel.setOnClickListener(this);
		reset.setOnClickListener(this);
	}
	
	public interface  OnResetDialogListener{
		public void OnResetClick(String imei);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_button_cancel:
			dismiss();
			break;
		case R.id.dialog_button_reset:
			if(mListener!=null){
				imeiStr=reimei.getText().toString().trim();
				mListener.OnResetClick(imeiStr);
			}
			dismiss();
			break;
		default:
			break;
		}
	}
	
	
	
	
}

package com.hipad.tracker.dialog;

import com.hipad.tracker.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
/**
 * 
 * @author guowei
 *
 */
public class SecurityDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	private EditText numberTxt;
    private String numberStr;
	private Button cancel;
	private Button confirm;
	
	private onSecurDialogListener mListener;


	public SecurityDialog(Context context, int style,
			onSecurDialogListener listener) {
		super(context, style);
		this.mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_number_set);
		getViews();
		setOnClickListener();

	}

	private void getViews() {

		numberTxt = (EditText) findViewById(R.id.edit_number_set);
		cancel = (Button) findViewById(R.id.btn_cancel);
		confirm = (Button) findViewById(R.id.btn_confirm);
	}

	private void setOnClickListener() {

		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	public interface onSecurDialogListener {
		
		public void onCancelClick(boolean flag);

		public void onSetNumberClick(String number);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			
			if(mListener!=null){
				mListener.onCancelClick(true);
			}
			
			dismiss();
			break;
		case R.id.btn_confirm:
			
			if (mListener != null) {
				numberStr=numberTxt.getText().toString().trim();
				mListener.onSetNumberClick(numberStr);
			}
			dismiss();
			break;
		default:
			break;
		}
	}
}

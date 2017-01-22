package com.hipad.tracker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hipad.tracker.R;
/**
 * 
 * @author guowei
 *
 */
public class SetNameDialog extends Dialog implements
		android.view.View.OnClickListener {

	private EditText nameTxt;
	
	private Button cancel;
	private Button confirm;
	
	private String nameStr;
	
	private OnSetNameDialogListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_name_set);
		getViews();
		setOnClickListener();

	}
	
	
	private void getViews(){
		
	  nameTxt=(EditText) findViewById(R.id.edit_name_set);
	  cancel=(Button) findViewById(R.id.btn_cancel);
	  confirm=(Button) findViewById(R.id.btn_confirm);
	}
	
	private void setOnClickListener(){
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	public SetNameDialog(Context context, int style,
			OnSetNameDialogListener listener) {
		super(context, style);
		this.mListener = listener;
	}

	public interface OnSetNameDialogListener {

		public void OnSetNameClick(String name);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			dismiss();
			break;
		case R.id.btn_confirm:
			if(mListener!=null){
				nameStr=nameTxt.getText().toString().trim();
				mListener.OnSetNameClick(nameStr);
			}
			dismiss();
			break;
		default:
			break;
		}

	}

}

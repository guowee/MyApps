package com.hipad.smarthome;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.UpdateDeviceInfoResponse;

/**
 * @author EthanChung
 */
public class DeviceEditActivity extends BaseActivity {
	private final static String TAG = "DeviceEditActivity";
	private EditText mEdtDevName;
	private CommonDevice mDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_edit_layout);
		Bundle bundle = this.getIntent().getExtras();
		mDevice = bundle.getParcelable(CommonDevice.EXTRA_DEVICE);
		init();
	}
	
	private void init(){
		mEdtDevName = (EditText) findViewById(R.id.device_name_edit);
		mEdtDevName.setText(mDevice.getName());
		((TextView) findViewById(R.id.titleTxt)).setText(R.string.config_device_lable_name);;
//		mEdtDevName.addTextChangedListener(new TextWatcher(){
//			@Override
//			public void afterTextChanged(Editable arg0) {}
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				Editable editable = mEdtDevName.getText();
//				int len = editable.toString().getBytes().length;
//				if(len>DeviceListActivity.MAX_DEVICE_NAME_BYTE_ARRAY_LENGTH){
//					int selEndIndex = Selection.getSelectionEnd(editable);
//					String str = editable.toString();
//					//截取新字符串
//					String newStr = str.substring(0,DeviceListActivity.MAX_DEVICE_NAME_BYTE_ARRAY_LENGTH);
//					mEdtDevName.setText(newStr);
//					editable = mEdtDevName.getText();
//					
//					//新字符串的长度
//					int newLen = editable.length();
//					//旧光标位置超过字符串长度
//					if(selEndIndex > newLen){
//						selEndIndex = editable.length();
//					}
//					//设置新光标所在的位置
//					Selection.setSelection(editable, selEndIndex);
//				}
//			}});
		
		mEdtDevName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
		
		findViewById(R.id.leftBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogShow(DeviceEditActivity.this, getString(R.string.config_device_renaming_i));
				mDevice.rename(mEdtDevName.getText().toString(), handler);
			}
		});
	}
	
	private ResponseResultHandler<UpdateDeviceInfoResponse> handler = new ResponseResultHandler<UpdateDeviceInfoResponse>(){
		@Override
		public void handle(boolean isTimeout, UpdateDeviceInfoResponse response) {
			Log.d(TAG,"is time out [" + String.valueOf(isTimeout)+ "]");
			dialogDismiss();
			//if(response.isSuccessful())
				//setResult(RESULT_OK, new Intent());
			Toast.makeText(DeviceEditActivity.this, "装置更名 "+(response.isSuccessful()?"成功":"失败"), Toast.LENGTH_SHORT).show();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			DeviceEditActivity.this.finish();
		}
	};
}
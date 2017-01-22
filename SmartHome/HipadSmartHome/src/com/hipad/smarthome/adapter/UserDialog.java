package com.hipad.smarthome.adapter;

import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.Response;
import com.hipad.smarthome.DeviceListActivity;
import com.hipad.smarthome.R;
import com.hipad.smarthome.utils.CommonViewDevice;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author EthanChung
 */
public class UserDialog extends Dialog implements OnClickListener{
	private final static String TAG = "DeviceList";
	
    Context context;
    CommonViewDevice device;
    
    private ResponseResultHandler<Response> handler = new ResponseResultHandler<Response>() {
		@Override
		public void handle(boolean isTimeout, Response response) {
			boolean result =!isTimeout && null != response && response.isSuccessful();
			Intent intent = new Intent(DeviceListActivity.DELETE_DEVICE_ACTION);
			intent.putExtra(DeviceListActivity.DELETE_DEVICE_EXTRA_RESULT, result);
			intent.putExtra(DeviceListActivity.DELETE_DEVICE_EXTRA_ID, device.getDeviceId());
			context.sendBroadcast(intent);
		}
	};
    
    public UserDialog(Context context, int theme, CommonViewDevice device){
        super(context, theme);
        this.context = context;
        this.device =  device;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.dialog_title)).setText(context.getResources().getString(R.string.delete_device_text, device.getName()));
        findViewById(R.id.dialog_button_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_button_ok).setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.dialog_button_cancel:
			this.dismiss();
			break;
		case R.id.dialog_button_ok:
			device.getCommonDevice().remove(handler);
			this.dismiss();
			break;
		}
	}
}
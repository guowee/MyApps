package com.hipad.smarthome.adapter;

/**
 * @author EthanChung
 */

import java.util.TimerTask;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.ErrorCode;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class KettleHolder extends TimerTask implements ResponseResultHandler<QueryDeviceInfoResponse>{
	private String TAG = "KettleHolder";
	public ImageView imgIcon, status_network, temperature_unit, edit,delete;
	public TextView name, status_operation, temperature;
	public Context context;
	public CommonDevice device;
	private KettleStatusInfo kettleStatusInfo;

	private void updateView(){
		
		//TODO WangBaoMin need to update API of <class>KettleStatusInfo</class>
		//byte sta = kettleStatusInfo.getState();
		//String _sta = sta==KettleStatusInfo.FUNC_STANDBY?"待機":sta==KettleStatusInfo.
		//status_operation.setText("");
		temperature.setText(String.valueOf(kettleStatusInfo.getTempCToBeKept()) + "°C");
	}
	
	@Override
	public void run() {
		device.getDeviceInfo(this);
	}
	
	@Override
	public void handle(boolean timeout, QueryDeviceInfoResponse response) {
		boolean successTag = false;
		if (!timeout) {
			if (response != null) {
				if (response.isSuccessful()) {
					DeviceInfo data = (DeviceInfo) response.getData();
					if (data != null) {
						int errorCode = data.getErrorCode();
						Log.d(TAG,"errorcode = " + data.getErrorCode());
						if (errorCode == ErrorCode.E_SUCCESS) {
							kettleStatusInfo = new KettleStatusInfo(data.getResponseBody());
							Log.d(TAG,"水壶所有状态:"
									+ "\n当前状态:"
									+ kettleStatusInfo.getState()
									+ "\n功能描述:"
									+ kettleStatusInfo.getCurrFunc()
									+ "\n保温剩余时间:"
									+ kettleStatusInfo.getRemainOfTimeToKeepTempC()
									+ "\n保温温度:"
									+ kettleStatusInfo.getTempCToBeKept()
									+ "\n当前水温:"
									+ kettleStatusInfo.getCurrentTemperature()
									+ "\n水质TDS值数据:"
									+ kettleStatusInfo.getWaterQuality()
									+ "\n烧水总时长:"
									+ kettleStatusInfo.getWorkedTime()
									+ "\n烧水次数:"
									+ kettleStatusInfo.getBoiledTimes());
							updateView();
							successTag = true;
						} else if (errorCode == ErrorCode.E_OFFLINE) {
							Log.e(TAG,"获取数据失败,设备已离线!");
						} else if (errorCode == ErrorCode.E_TIMEOUT) {
							Log.e(TAG,"获取数据失败,请求超时!");
							//Toast.makeText(context,context.getString(R.string.timeout_hint),Toast.LENGTH_SHORT).show();
						} else {
							Log.e(TAG,"获取数据失败,错误代码：" + errorCode);
						}
					} else {
						Log.e(TAG,"获取的数据为空!");
					}
				} else {
					String msgStr = response.getMsg();
					Log.e(TAG,"msgStr = " + msgStr);
					//Toast.makeText(context,"获取数据失败," + msgStr,Toast.LENGTH_SHORT).show();
				}
			} else {
				//Toast.makeText(context,context.getString(R.string.neterror_hint),Toast.LENGTH_SHORT).show();
			}
		} else {
			//Toast.makeText(context,context.getString(R.string.timeout_hint),Toast.LENGTH_SHORT).show();
		}
		if (!successTag) {
			kettleStatusInfo = null;
		}
	}
}
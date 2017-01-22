/**
 * $(#)Socket3Activity.java 2015Äê3ÔÂ18ÈÕ
 */
package com.hipad.smarthome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hipad.smart.device.Device;
import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.json.ErrorCode;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.json.util.GsonInstance;
import com.hipad.smart.socket.SocketCmd;
import com.hipad.smart.socket.SocketStatusInfo;

/**
 * @author wangbaoming
 *
 */
public class Socket3Activity extends BaseActivity implements OnClickListener{	
	private static final String TAG = "Socket3Activity";
	
	private TextView mTvInfo;
	
	private TextView mTvPlug1Info, mTvPlug2Info, mTvPlug3Info;
	private Button mBtnPlug1On, mBtnPlug2On, mBtnPlug3On, mBtnPlug1Off, mBtnPlug2Off, mBtnPlug3Off;
	
	private Device mDevice;

	private Socket3Activity mContext;

	private ResponseResultHandler<CmdResponse> mCmdHandler = new HttpUtil.ResponseResultHandler<CmdResponse>() {
		
		@Override
		public void handle(boolean timeout, CmdResponse obj) {
			StringBuilder sb = new StringBuilder();
			
			if(!timeout && obj.isSuccessful() && null != obj){
				String json = GsonInstance.get().toJson(obj);
				Log.i(TAG, String.format("timeout: %s, result: %s\n", timeout, json));
				
				ResponseData reply = (ResponseData) obj.getData();	
				if(null != reply && reply.getErrorCode() == ErrorCode.E_SUCCESS){
					SocketStatusInfo statusInfo = new SocketStatusInfo(reply.getResponseBody());
					Log.i(TAG, "len: " + statusInfo.getData().length + ", " + statusInfo.encode());
					statusInfo.dump();
					
					mTvPlug1Info.setText(statusInfo.isPlugOn(0) ? "On" : "Off"); 
					mTvPlug2Info.setText(statusInfo.isPlugOn(1) ? "On" : "Off"); 
					mTvPlug3Info.setText(statusInfo.isPlugOn(2) ? "On" : "Off"); 
					
					sb.append("Frequency: ").append(statusInfo.getPowerFrequency())
						.append("\nV: ").append(statusInfo.getVoltage())
						.append("\nC: ").append(statusInfo.getCurrency())
						.append("\nReactive Power: ").append(statusInfo.getReactivePower())
						.append("\nApparent Power: ").append(statusInfo.getApparentPower());
				}
			}else if(!obj.isSuccessful()){
				sb.append("msg: " + obj.getMsg());
			}else if(timeout){
				sb.append("timeout: " + timeout);
			}else{
				sb.append("network error");
			}

			mTvInfo.setText(sb.toString());
			dialogDismiss();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket3);
		mContext = this;
		
		getView();
		
		init();
	}
	
	private void init(){
		Intent intent = getIntent();
		mDevice = intent.getParcelableExtra("device");
		
		dialogShow(mContext, null);
		mDevice.getDeviceInfo(new ResponseResultHandler<QueryDeviceInfoResponse>() {
			
			@Override
			public void handle(boolean timeout, QueryDeviceInfoResponse obj) {
				StringBuilder sb = new StringBuilder();
				
				if(!timeout && obj.isSuccessful() && null != obj){
					String json = GsonInstance.get().toJson(obj);
					Log.i(TAG, String.format("timeout: %s, result: %s\n", timeout, json));
					
					DeviceInfo reply = (DeviceInfo) obj.getData();	
					if(null != reply && reply.getErrorCode() == ErrorCode.E_SUCCESS){
						SocketStatusInfo statusInfo = new SocketStatusInfo(reply.getResponseBody());
						Log.i(TAG, "len: " + statusInfo.getData().length + ", " + statusInfo.encode());
						statusInfo.dump();
						
						mTvPlug1Info.setText(statusInfo.isPlugOn(0) ? "On" : "Off"); 
						mTvPlug2Info.setText(statusInfo.isPlugOn(1) ? "On" : "Off"); 
						mTvPlug3Info.setText(statusInfo.isPlugOn(2) ? "On" : "Off"); 
						
						sb.append("Frequency: ").append(statusInfo.getPowerFrequency())
							.append("\nV: ").append(statusInfo.getVoltage())
							.append("\nC: ").append(statusInfo.getCurrency())
							.append("\nReactive Power: ").append(statusInfo.getReactivePower())
							.append("\nApparent Power: ").append(statusInfo.getApparentPower());
					}
				}else if(!obj.isSuccessful()){
					sb.append("msg: " + obj.getMsg());
				}else if(timeout){
					sb.append("timeout: " + timeout);
				}else{
					sb.append("network error");
				}

				mTvInfo.setText(sb.toString());
				dialogDismiss();
			}
		});
	}
	private void getView(){
		mTvInfo = (TextView) findViewById(R.id.socket3_tv_info);
		
		mTvPlug1Info = (TextView) findViewById(R.id.socket3_tv_plug1_info);
		mTvPlug2Info = (TextView) findViewById(R.id.socket3_tv_plug2_info);
		mTvPlug3Info = (TextView) findViewById(R.id.socket3_tv_plug3_info);
		
		mBtnPlug1On = (Button) findViewById(R.id.socket3_btn_plug1_on);
		mBtnPlug1Off = (Button) findViewById(R.id.socket3_btn_plug1_off);
		
		mBtnPlug2On = (Button) findViewById(R.id.socket3_btn_plug2_on);
		mBtnPlug3Off = (Button) findViewById(R.id.socket3_btn_plug3_off);
		
		mBtnPlug3On = (Button) findViewById(R.id.socket3_btn_plug3_on);
		mBtnPlug3Off = (Button) findViewById(R.id.socket3_btn_plug3_off);
	}

	@Override
	public void onClick(View v) {
		SocketCmd cmd = new SocketCmd();
		switch (v.getId()) {
		case R.id.socket3_btn_plug1_on:
			cmd.setPlugState(0, true);
			break;
		case R.id.socket3_btn_plug1_off:
			cmd.setPlugState(0, false);
			break;
		case R.id.socket3_btn_plug2_on:
			cmd.setPlugState(1, true);
			break;
		case R.id.socket3_btn_plug2_off:
			cmd.setPlugState(1, false);
			break;
		case R.id.socket3_btn_plug3_on:
			cmd.setPlugState(2, true);
			break;
		case R.id.socket3_btn_plug3_off:
			cmd.setPlugState(2, false);
			break;
		default:
			break;
		}

		dialogShow(mContext, null);
		mDevice.sendCmd(cmd, mCmdHandler );
	}
}

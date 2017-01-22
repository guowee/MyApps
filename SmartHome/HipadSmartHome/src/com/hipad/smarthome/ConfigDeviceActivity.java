/**
 * $(#)ConfigDeviceActivity.java 2014-12-31
 */
package com.hipad.smarthome;

import java.io.IOException;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.Response;
import com.hipad.smart.local.device.ApScaner;
import com.hipad.smart.local.device.CmdResponseWaitersQueue;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager;
import com.hipad.smart.local.device.DeviceManager.ResponceHandler;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.msg.GeneralCmd;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.msg.MsgUtil;
import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smart.util.StringUtil;
import com.hipad.smarthome.utils.DeviceListCache;

/**
 * @author wangbaoming
 *
 */
public class ConfigDeviceActivity extends BaseActivity {
	private final static String TAG = "ConfigDeviceActivity";
	
	private final static String ACTION_SCAN_QRCODE = "com.hipad.smarthome.zxing.SCAN";
	private final static String EXTRA_ACTIVE_CODE = "SCAN_RESULT";//"active_code";
	private final static int REQUEST_SCAN_ACTIVE_CODE = 0X1;
	
	private ConfigDeviceActivity mContext;
	
	private TextView mTvTitle;
	
	private EditText mEdtName, mEdtSsid, mEdtPasswd, mEdtActiveCode;
	private ImageButton mBtnBack;
	private Button mBtnClean, mBtnOk, mBtnScan;
	
	private DeviceManager mDeviceManager;
	private ResponceHandler mCmdResponceHandler;
	private CmdResponseWaitersQueue mCmdResponseWaitersQueue;
	
	private Device mDevice;
	private WifiInfo mDeviceAp;
	
	private DeviceUtil mDeviceUtil;
	private ApScaner mApScaner; 
	
	private int mTrialTimes = 0;
	private final static int TRIAL_MAX_TIMES = 10;
	
	private final static int MSG_CHECK_CONFIG = 0X01;
//	private final static int MSG_CONN_TIMEOUT = 0X02;
	private final static int MSG_CHECK_CONFIG_TIMEOUT = 0X03;
	private final Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CHECK_CONFIG:
				checkConfigResult();
				break;

//			case MSG_CONN_TIMEOUT:{
//				showToastShort(getString(R.string.activity_configdevice_hint_config_fail));
//				
//				Intent intent = new Intent(mContext, PreConfigActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//				
//				finish();
//				
//				break;
//			}
			case MSG_CHECK_CONFIG_TIMEOUT:
				mTrialTimes++;
				if(mTrialTimes <= TRIAL_MAX_TIMES){
					mHandler.sendEmptyMessage(MSG_CHECK_CONFIG);
				}else{ // fail, try the loop again
					mDeviceManager.stopBroadcastEngine();
					showToastShort(getString(R.string.activity_configdevice_hint_config_fail));
					
					Intent intent = new Intent(mContext, PreConfigActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					
					finish();
				}
				break;
			default:
				break;
			}
		};
	};
	
	private final HttpUtil.ResponseResultHandler<Response> mBindDeviceResultHandler = new HttpUtil.ResponseResultHandler<Response>() {
		
		@Override
		public void handle(boolean timeout, Response obj) {
			dialogDismiss();
			if(!timeout && null != obj && obj.isSuccessful()){
				showToastShort(getString(R.string.activity_configdevice_hint_succuss));
				Intent data = new Intent();
				setResult(RESULT_OK, data);
				DeviceListCache.getInstance().syncDeviceList();
			}else{
				String msgStr = obj.getMsg();
				
				if(msgStr.contains("µÇÂ¼ÒÑ¹ýÆÚ")){ // logout and re-login
					showToastShort(msgStr);
					startActivity(new Intent(mContext, LoginActivity.class));
				}else{							
					// TODO failed, sync latter when the network is good
					showToastShort(getString(R.string.activity_configdevice_hint_bind_failed));
				}
			}				

			Intent intent = new Intent(mContext, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configdevice);
		mContext = this;
		
		setResult(RESULT_CANCELED);
		
		mDeviceManager = new DeviceManager();
		mCmdResponseWaitersQueue = new CmdResponseWaitersQueue();
		
		mDevice = getIntent().getParcelableExtra(Device.EXTRA_DEVICE);
		if(null == mDevice){
			showToastShort(getString(R.string.error));
			finish();
		}
		
		mCmdResponceHandler = new ResponceHandler() {			

			@Override
			public void handleResponce(Msg response) {
				mCmdResponseWaitersQueue.accept(response, true);
				Log.i(tag, "msg: " + response.toString());				
			}
		};
		
		mDeviceUtil = new DeviceUtil();
		mApScaner = new ApScaner(mContext);
		
		getView();
		setOnClickListener();
		
		init();
		
	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		mApScaner.release();
		mDeviceManager.disconnect();
		
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		// refresh because the locale maybe has changed
		mDeviceUtil.fetchSurpportedDevice();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_SCAN_ACTIVE_CODE && resultCode == RESULT_OK){
			String activeCode = data.getStringExtra(EXTRA_ACTIVE_CODE);
			Log.d(TAG, "QRCode: " + activeCode);
			if(!StringUtil.isNullOrEmpty(activeCode)){
				mEdtActiveCode.setText(activeCode);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void init(){
		mTvTitle.setText(R.string.add_device_title_add_devices);
		mDeviceUtil.fetchSurpportedDevice();
		
		String name = mDeviceUtil.getName(mDevice.getDeviceType());
		if(null != name) mEdtName.setText(name);
		
		mDeviceManager.setCmdResponceHandler(mCmdResponceHandler);
		mDeviceManager.connect(mDevice);		
		getConfig(mDevice);
	}
	
	private void getView(){
		mTvTitle = (TextView) findViewById(R.id.titleTxt);
		mEdtName = (EditText) findViewById(R.id.popupwindow_adddevice_edt_name);
		mEdtSsid = (EditText) findViewById(R.id.popupwindow_adddevice_edt_ssid);
		mEdtPasswd = (EditText) findViewById(R.id.popupwindow_adddevice_edt_passwd);	
		mEdtActiveCode = (EditText) findViewById(R.id.popupwindow_adddevice_edt_code);		

		mBtnBack = (ImageButton) findViewById(R.id.leftBtn);
		mBtnClean = (Button) findViewById(R.id.popupwindow_adddevice_btn_clean);
		mBtnOk = (Button) findViewById(R.id.popupwindow_adddevice_btn_ok);
		mBtnScan = (Button) findViewById(R.id.popupwindow_adddevice_btn_scan);
	}
	
	private void setOnClickListener(){
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnClean.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEdtName.setText("");
			}
		});		
		mBtnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO configure the device
				String ssid = mEdtSsid.getEditableText().toString();
				String passwd = mEdtPasswd.getEditableText().toString();
				String activeCode = mEdtActiveCode.getEditableText().toString();
				
				if(StringUtil.isNullOrEmpty(activeCode)){
					showToastShort(getString(R.string.activity_configdevice_hint_active_code_illegal));
					return ;
				}
				
				config(mDevice, ssid, passwd);
			}
		});
		mBtnScan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// scan qr code to get the active code
				Intent intent = new Intent(ACTION_SCAN_QRCODE);
				try {
					startActivityForResult(intent, REQUEST_SCAN_ACTIVE_CODE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		mEdtSsid.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final int len = s.length();
				final int passwdLen = mEdtPasswd.getText().toString().length();
				if(len <= 0){
					mBtnOk.setEnabled(false);
				}else if(passwdLen >= 8){
					mBtnOk.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mEdtPasswd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final int len = s.length();
				final int ssidLen = mEdtSsid.getText().toString().length();
				if(len < 8){
					mBtnOk.setEnabled(false);
				}else if(ssidLen > 0){
					mBtnOk.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	private void getConfig(final Device device){
		dialogShow(mContext, getString(R.string.add_device_get_config));
		LocalCmd cmd = new LocalCmd(GeneralCmd.GET_DEVICE_CONFIG, device.getId(), true, "");
		ResponseWaiter waiter = new ResponseWaiter(cmd) {
			
			@Override
			public void timeOut() {
				super.timeOut();
				dialogDismiss();
				showToastShort(getString(R.string.timeout_try_aggin));
				finish();
			}
			
			@Override
			public void handleMessage(Msg response) {
				super.handleMessage(response);
				dialogDismiss();
				// TODO fill ssid and pwd with the current values
				Map<String, Object> args = MsgUtil.parseArgs(new String(response.getArgs()) );
				mEdtSsid.setText((String ) args.get(GeneralCmd.Args.SSID));
				mEdtPasswd.setText((String ) args.get(GeneralCmd.Args.PASSWORD));
				
				String name = mDeviceUtil.getName(mDevice.getDeviceType());
				if(null != name) mEdtName.setText(name);
			}
		};
		mCmdResponseWaitersQueue.enqueueWait(waiter, 5);

		mDeviceManager.send(cmd);
	}
	
	private void config(Device device, final String ssid, final String passwd){
		dialogShow(mContext, getString(R.string.add_device_configuring_i));
		LocalCmd cmd = new LocalCmd(GeneralCmd.CONFIG_DEVICE_SSID, device.getId(), true, String.format("%s=%s,%s=%s", GeneralCmd.Args.SSID, ssid, GeneralCmd.Args.PASSWORD, passwd));
		ResponseWaiter waiter = new ResponseWaiter(cmd) {
			
			@Override
			public void timeOut() {
				super.timeOut();
				dialogDismiss();
				showToastShort(getString(R.string.timeout_try_aggin));
			}
			
			@Override
			public void handleMessage(Msg response) {
				super.handleMessage(response);
				mDeviceManager.disconnect();
				Log.i(TAG, "ssid & passwd writen success.\n" + response.toString());
				
				mDeviceAp = mApScaner.getCurrentAp();
				// TODO connect to the ssid
				mApScaner.connectTarget(ssid, passwd);
				startCheckConfigResult(ssid);				

				// save the previous configured ssid and try to remove it from sys ssid list, and the next time filter it out.
				ScanDeviceActivity.setPreConfigedAp(mDeviceAp.getSSID());
				mApScaner.removeAp(mDeviceAp.getNetworkId());
			}
		};
		mCmdResponseWaitersQueue.enqueueWait(waiter, 3);

		mDeviceManager.send(cmd);
	}	

	private void startCheckConfigResult(final String ssid){
		// TODO 5 seconds latter, check if the device is connected to the ethernet
//		mApScaner.addConnectStateChangeListener(new ApScaner.ConnectStateChangeListener() {
//			
//			@Override
//			public void onConnectStateChanged(boolean connected, WifiInfo info) {
//				Log.i(TAG, "connected: " + connected + ", ssid: " + ssid);
//				if(connected && ssid.equals(info.getBSSID())){
//					mHandler.removeMessages(MSG_CONN_TIMEOUT);
//					mDeviceManager.startBroadcastEngine();
//					mHandler.sendEmptyMessageDelayed(MSG_CHECK_CONFIG, 1000 * 5);
//				}
//			}
//		});		

		mDeviceManager.setDeviceFoundReporter(new DeviceManager.DeviceFoundReporter() {
			
			@Override
			public void reportDeviceFound(Device device) {
				// found one device
				if(device.getId().equals(mDevice.getId())){ // config successes.
					mHandler.removeMessages(MSG_CHECK_CONFIG_TIMEOUT);
					mDeviceManager.stopBroadcastEngine();
					dialogDismiss();
					
					// push the bind relationship to cloud
					syncCloud();
				}
			}
		});	
		dialogShow(mContext, getString(R.string.add_device_configuring_ii));
		
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_CONFIG, 1000 * 5);
//		mHandler.sendEmptyMessageDelayed(MSG_CONN_TIMEOUT, 1000 * 10);
	}
	
	private void removeDeviceAp(){
		WifiInfo wifiInfo = mApScaner.getCurrentAp();
		mApScaner.removeAp(wifiInfo.getNetworkId());
	}
	
	private void checkConfigResult(){		
		mDeviceManager.stopBroadcastEngine();
		mDeviceManager.startBroadcastEngine();
	
		mDeviceManager.scanConnectedDevice();

		mHandler.sendEmptyMessageDelayed(MSG_CHECK_CONFIG_TIMEOUT, 1000 * 3);
	}
	
	private void syncCloud(){
		Service service = new ServiceImpl();
		dialogShow(mContext, getString(R.string.activity_configdevice_hint_sync_cloud));
		String activeCode = mEdtActiveCode.getText().toString();
		service.addDirectDevice(mDevice.getId(), activeCode, mBindDeviceResultHandler);
	}
	
	private class DeviceUtil {
		private final static String TAG_ROOT = "devices";
		private final static String TAG_ITEM = "device";
		
		private final static String ATTR_TYPE = "category";
		private final static String ATTR_NAME = "name";
		
		private SparseArray<String> mTypeName;		
		
		public DeviceUtil(){
			mTypeName = new SparseArray<String>(36);
		}
		
		public void fetchSurpportedDevice(){
			XmlResourceParser xmlParser = getResources().getXml(R.xml.surported_device);
			try {
				int event = xmlParser.getEventType();
				while (XmlPullParser.END_DOCUMENT != event) {
					switch(event){
					case XmlPullParser.START_DOCUMENT:
						Log.i(TAG, "START_DOCUMENT: " + xmlParser.getName());
						break;
					case XmlPullParser.START_TAG:
						Log.e(TAG, "START_TAG: " + xmlParser.getName());
						if(TAG_ITEM.equals(xmlParser.getName())){
							String category = xmlParser.getAttributeValue(null, ATTR_TYPE);
							String name = xmlParser.getAttributeValue(null, ATTR_NAME);
							Log.i(TAG, "category: " + category + ", name: " + name);

							mTypeName.put(Integer.valueOf(category), name);
						}
						break;
					case XmlPullParser.END_TAG:
						Log.i(TAG, "END_TAG: " + xmlParser.getName());
						break;
					case XmlPullParser.END_DOCUMENT:
						Log.i(TAG, "END_DOCUMENT: " + xmlParser.getName());
						break;
					}					
					
					xmlParser.next();
					event = xmlParser.getEventType();
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public String getName(int type){
			return mTypeName.get(type);
		}
	}
}

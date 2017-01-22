package com.hipad.smarthome;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hipad.smart.local.device.ApScaner;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager;

public class ScanDeviceActivity extends BaseActivity implements OnClickListener {
	
	private final static int STATE_SCANING_DEVICES = 1;
	private final static int STATE_ADDING_DEVICES = 2;
	
	private final static int REQUEST_CODE = 1;

	private ImageButton mImgbtnBack, mImgbtnRefresh;	
	private TextView mTvPrompt, mTvTitle;
	private ListView mLstvScanedDevices;	
	
	private ProgressBar mPrgsScaning;
	
	private ApScaner mApScaner;
	private List<ScanResult> mScanedDeviceAps;
	private DeviceApsListAdapter mDeviceApsListAdapter;
	
	private ScanDeviceActivity mContext;
	
	private String mDefaultPasswd = "12345678";
	private ScanResult mApToBeConn;	

	private DeviceManager mDeviceManager;
	
	// the ap of wifi module has one random part, which is generated randomly every time when changed to ap mode
	private static String sPreConfigedAp = "";
	public static void setPreConfigedAp(String ssid){
		sPreConfigedAp = ssid;
	}

	private final static int MSG_CONN_TIMEOUT = 0X01;
	private final static int MSG_FIND_TIMEOUT = 0X02;
	private final Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CONN_TIMEOUT:
				mApToBeConn = null;
				dialogDismiss();
				showToastShort(getString(R.string.timeout));
				break;
			case MSG_FIND_TIMEOUT:
				mDeviceManager.stopBroadcastEngine();
				dialogDismiss();
				showToastShort(getString(R.string.timeout));
				break;
			default:
				break;
			}
		};
	};
	
	private final ApScaner.ScanStateListener mScanStateListener = new ApScaner.ScanStateListener() {
		
		@Override
		public void onScanCompleted(List<ScanResult> apList) {
			// do not listen to the scanning state
			mApScaner.removeScanStateListener(mScanStateListener);
			
			// TODO filter the result to only remain the hipad device
			for(ScanResult ap : apList){
				if(ap.SSID.contains("HIPAD_") && !sPreConfigedAp.contains(ap.SSID)) {
					mScanedDeviceAps.add(ap);
				}
			}
			apList = null;

			mDeviceApsListAdapter.setData(mScanedDeviceAps);
			mDeviceApsListAdapter.notifyDataSetChanged();

			mPrgsScaning.setVisibility(View.INVISIBLE);
			updateUi(STATE_ADDING_DEVICES);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_device_layout);	
		mContext = this;
		
		mApScaner = new ApScaner(this);		
		mScanedDeviceAps = new ArrayList<ScanResult>(16);
		mDeviceApsListAdapter = new DeviceApsListAdapter(mContext);			

		mDeviceManager = new DeviceManager();
		
		getView();
		setOnClickListener();
		
		init();
		
		updateUi(STATE_SCANING_DEVICES);
	}
	
	@Override
	protected void onDestroy() {
		mApScaner.release();
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(REQUEST_CODE == requestCode){
			if(RESULT_OK == resultCode){
				// TODO mark the device has configured
				mDeviceApsListAdapter.notifyConfigured((Integer) mLstvScanedDevices.getTag(), true);
				
				Log.i(tag, "success.");
			}else{
				Log.i(tag, "fail.");
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.rightBtn:
			mScanedDeviceAps.clear();
			mApScaner.addScanStateListener(mScanStateListener);
			scanDevices();
			break;
		default:
			break;
		}
	}
	
	private void init(){
		mTvTitle.setText(R.string.add_device_title_scan_devices);
		mImgbtnRefresh.setVisibility(View.VISIBLE);
		mImgbtnRefresh.setBackgroundResource(R.drawable.main_btn_update);

		mDeviceApsListAdapter.setData(mScanedDeviceAps);
		mLstvScanedDevices.setAdapter(mDeviceApsListAdapter);
		
		mApScaner.addScanStateListener(mScanStateListener);
		mApScaner.addConnectStateChangeListener(new ApScaner.ConnectStateChangeListener() {
			
			@Override
			public void onConnectStateChanged(boolean connected, WifiInfo info) {
				if(null != mApToBeConn && connected && mApToBeConn.BSSID.equals(info.getBSSID())){
					mApToBeConn = null;
					mHandler.removeMessages(MSG_CONN_TIMEOUT);
					dialogShow(mContext, getString(R.string.add_device_connecting_ii));
					
					// broadcast to find the device
					mDeviceManager.startBroadcastEngine();
					mDeviceManager.scanConnectedDevice();
					mHandler.sendEmptyMessageDelayed(MSG_FIND_TIMEOUT, 1000 * 5);
				}
			}
		});		
		
		mDeviceManager.setDeviceFoundReporter(new DeviceManager.DeviceFoundReporter() {
			
			@Override
			public void reportDeviceFound(Device device) {
				// found one device
				mHandler.removeMessages(MSG_FIND_TIMEOUT);
				mDeviceManager.stopBroadcastEngine();
				dialogDismiss();
				
				Intent intent = new Intent(mContext, ConfigDeviceActivity.class);
				intent.putExtra(Device.EXTRA_DEVICE, device);
//				startActivityForResult(intent, REQUEST_CODE);
				startActivity(intent);
			}
		});		
//		mDeviceManager.startBroadcastEngine();
		
		scanDevices();
	}
	
	private void updateUi(int state){
		if(STATE_SCANING_DEVICES == state){
			mTvTitle.setText(R.string.add_device_title_scan_devices);	
			mTvPrompt.setText(R.string.add_device_scaning);
		}else if(STATE_ADDING_DEVICES == state){
			mTvTitle.setText(R.string.add_device_title_add_devices);	
			mTvPrompt.setText(R.string.add_device_please_choose);
		}
	}
	
	private void scanDevices(){
		mPrgsScaning.setVisibility(View.VISIBLE);
		mApScaner.scanAp();
	}
	
	private void getView(){
		mImgbtnBack = (ImageButton) findViewById(R.id.leftBtn);
		mImgbtnRefresh = (ImageButton) findViewById(R.id.rightBtn);
		
		mTvPrompt = (TextView) findViewById(R.id.adddevice_tv_prompt);
		mTvTitle = (TextView) findViewById(R.id.titleTxt);
		
		mPrgsScaning = (ProgressBar) findViewById(R.id.adddevice_prgs_scaning);
		
		mLstvScanedDevices = (ListView) findViewById(R.id.adddevice_lstv_scaneddevices);
	}
	
	private void setOnClickListener(){
		mImgbtnBack.setOnClickListener(this);
		mImgbtnRefresh.setOnClickListener(this);
		
		mLstvScanedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				parent.setTag(position); // record the clicked item
				
				mApToBeConn = mScanedDeviceAps.get(position);				
				
				if(mApScaner.isWifiConnected() && mApToBeConn.BSSID.equals(mApScaner.getCurrentAp().getBSSID())) { // connected
					mApToBeConn = null;
					mHandler.removeMessages(MSG_CONN_TIMEOUT);
					dialogShow(mContext, getString(R.string.add_device_connecting_ii));
					// TODO broadcast to find the device
					mDeviceManager.startBroadcastEngine();
					mDeviceManager.scanConnectedDevice();
					mHandler.sendEmptyMessageDelayed(MSG_FIND_TIMEOUT, 1000 * 5);
					
					return;
				}
				
				mApScaner.connect(mApToBeConn, mDefaultPasswd);
				dialogShow(mContext, getString(R.string.add_device_connecting_i));
				
				mHandler.sendEmptyMessageDelayed(MSG_CONN_TIMEOUT, 1000 * 15);
			}
		});
	}
	
	private static class DeviceApsListAdapter extends BaseAdapter {
		
		private class ItemViewHolder{
			public ImageView imgvAddedSign;
			public TextView tvApName;
			public ImageView imgvInfo;
		}

		private Context mContext;
		private List<ScanResult> mScanedDeviceAps;
		private ArrayList<String> mConfiguredRecord;
		
		public DeviceApsListAdapter(Context context) {
			mContext = context;
			mConfiguredRecord = new ArrayList<String>();
		}
		
		public void setData(List<ScanResult> scanedAps){
			mScanedDeviceAps = scanedAps;
			mConfiguredRecord.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mScanedDeviceAps.size();
		}

		@Override
		public Object getItem(int position) {
			return mScanedDeviceAps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}		
		
		public void notifyConfigured(int position, boolean configured){
			if(configured){
				mConfiguredRecord.add(mScanedDeviceAps.get(position).BSSID);
			}else{
				mConfiguredRecord.remove(mScanedDeviceAps.get(position).BSSID);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ItemViewHolder viewHolder = null;
			if(null == view){
				view = View.inflate(mContext, R.layout.view_adddevice_list_item, null);
				
				viewHolder = new ItemViewHolder();
				viewHolder.imgvAddedSign = (ImageView) view.findViewById(R.id.view_add_device_list_item_imgv_addedsign);
				viewHolder.tvApName = (TextView) view.findViewById(R.id.view_add_device_list_item_tv_devicename);
				viewHolder.imgvInfo = (ImageView) view.findViewById(R.id.view_add_device_list_item_imgv_info);
				
				view.setTag(viewHolder);
			}else{
				viewHolder = (ItemViewHolder) view.getTag();
			}
			
			ScanResult deviceAp = mScanedDeviceAps.get(position);
			
			viewHolder.tvApName.setText(deviceAp.SSID);
			// mark the configured
			if(mConfiguredRecord.contains(deviceAp.BSSID)){
				viewHolder.imgvAddedSign.setVisibility(View.VISIBLE);
			}else{
				viewHolder.imgvAddedSign.setVisibility(View.INVISIBLE);
			}
			
			return view;
		}
		
	}
}

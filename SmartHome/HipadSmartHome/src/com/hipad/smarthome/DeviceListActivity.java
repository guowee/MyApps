package com.hipad.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.CommonDevice.Group;
import com.hipad.smart.device.Device.Category;
import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.QueryDevicesResponse;
import com.hipad.smart.kettle.v14.KettleCmd;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.device.DeviceManager.DeviceFoundReporter;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.adapter.DevListAdapter;
import com.hipad.smarthome.utils.CommonViewDevice;
import com.hipad.smarthome.utils.DeviceListCache;
import com.hipad.smarthome.utils.SmallTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.os.Handler;
//import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author EthanChung
 */
public class DeviceListActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	public static boolean D = false;
	public static final String DELETE_DEVICE_ACTION = "delete_device_action";
	public static final String DELETE_DEVICE_EXTRA_RESULT = "delete_device_extra_result";
	public static final String DELETE_DEVICE_EXTRA_ID = "delete_device_extra_id";
	public static final String EDIT_DEVICE_EXTRA_ID = "edit_device_extra_id";
	public static final String EDIT_DEVICE_EXTRA_NAME = "edit_device_extra_name";
	public static final String UPDATE_DEVICE_ACTION = "update_device_action";
	public static final String RELOAD_DEVICE_LIST_ACTION = "reload_device_list_action";
	public static final int MAX_DEVICE_NAME_BYTE_ARRAY_LENGTH = 24;

	private String TAG = "DeviceList";
	private int INTERVAL_RELOAD = 30;
	private int INTERVAL_TIMEOUT = 2;

	private ListView listView;
	private ArrayList<CommonViewDevice> deviceList;
	private DevListAdapter dListAdapter;
	private int currentType = 1;
	private QueryDeviceListHander queryHander;
	private Map<String, View> deviceViewMap;
	private Group curGroup;

	private boolean isMoreClicked = false;
	// private boolean isFirstLoading = true;
	private boolean isCloudAvailable = false;
	private int localDevCount = 0;
	private BroadcastReceiver receiver;

	private DeviceManager mDeviceManager;
	private DeviceFoundReporter mDeviceFoundReporter;
	private List<Device> mConnectedDevice;

	private String[] titles;
	
	private final static int MSG_RELOAD_DEVICE_LIST = 0x01;
	private final static int MSG_UPDATE_DEVICE = 0x02;	
	private final Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RELOAD_DEVICE_LIST:
				if (deviceList.size() != 0){
					DeviceListActivity.this.sendBroadcast(new Intent(RELOAD_DEVICE_LIST_ACTION));
				}
				break;

			case MSG_UPDATE_DEVICE:
				if (localDevCount == 0){
					sendBroadcast(new Intent(UPDATE_DEVICE_ACTION));
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list_layout);
		Bundle bundle = getIntent().getExtras();
		currentType = bundle.getInt("type");
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		reload(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(DELETE_DEVICE_ACTION);
		filter.addAction(UPDATE_DEVICE_ACTION);
		filter.addAction(RELOAD_DEVICE_LIST_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}

	@Override
	public void onDestroy() {
		mDeviceManager.release();
		// mLocalService.stopSelf();
		mHandler.removeCallbacksAndMessages(null);
		
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isMoreClicked) {
				for (View vi : deviceViewMap.values()) {
					vi.findViewById(R.id.info_layout).setVisibility(
							View.VISIBLE);
					vi.findViewById(R.id.more_layout).setVisibility(View.GONE);
				}
				isMoreClicked = !isMoreClicked;
				return false;
			} else
				finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CommonViewDevice commonViewDevice = (CommonViewDevice) parent
				.getItemAtPosition(position);
		
		if (isMoreClicked) {
			showToastShort("设备编辑中!");
			return; 
		
		}else if(commonViewDevice.getNetworkState() == CommonViewDevice.NETWORK_STATE_OFFLINE) {
			showToastShort("设备已离线!");
			return;

		} else if (commonViewDevice.getNetworkState() == CommonViewDevice.NETWORK_STATE_ONLINE_LOCAL) {
			Intent intent = new Intent(DeviceListActivity.this,
					KettleLocalActivity.class);
			intent.putExtra("title", commonViewDevice.getName());
			intent.putExtra(Device.EXTRA_DEVICE,
					commonViewDevice.getLocalDevice());
			startActivity(intent);

		} else if (currentType == 2) {
			int categoryId = commonViewDevice.getCommonDevice().getCategory();

			if (categoryId == Category.ShuiHu) {
				Intent intent = new Intent(DeviceListActivity.this,
						KettleDevActivity.class);
				intent.putExtra("title", commonViewDevice.getName());
				intent.putExtra("device", commonViewDevice.getCommonDevice());
				startActivity(intent);
			} else {
				showToastShort("暂未开通，开发中...");
			}

		} else {
			showToastShort("暂未开通，开发中...");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_icon:
			this.finish();
			break;

		case R.id.dev_refresh_icon:
			this.sendBroadcast(new Intent(RELOAD_DEVICE_LIST_ACTION));
			break;

		case R.id.add_terminal:
			this.startActivity(new Intent(this, AddChooserActivity.class));
			break;

		case R.id.dev_more_icon:
			isMoreClicked = !isMoreClicked;
			for (View vi : deviceViewMap.values()) {
				if (isMoreClicked) {
					vi.findViewById(R.id.info_layout).setVisibility(View.GONE);
					vi.findViewById(R.id.more_layout).setVisibility(
							View.VISIBLE);
				} else {
					vi.findViewById(R.id.info_layout).setVisibility(
							View.VISIBLE);
					vi.findViewById(R.id.more_layout).setVisibility(View.GONE);
				}
			}
			break;
		}
	}

	private void init() {
		deviceList = new ArrayList<CommonViewDevice>();
		deviceViewMap = new HashMap<String, View>();
		queryHander =  new QueryDeviceListHander();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				String action = intent.getAction();
				if (action.equals(DELETE_DEVICE_ACTION)) {
					boolean isDelSuccessful = intent.getBooleanExtra(
							DELETE_DEVICE_EXTRA_RESULT, false);
					// String id =
					// intent.getStringExtra(DELETE_DEVICE_EXTRA_ID);
					if (isDelSuccessful)
						DeviceListActivity.this.reload(true);
					showToastShort("删除" + (isDelSuccessful ? "成功" : "失败"));

				} else if (action.equals(UPDATE_DEVICE_ACTION)) {
					updateView();

				} else if (action.equals(RELOAD_DEVICE_LIST_ACTION)) {
					DeviceListActivity.this.reload(false);
				}
			}
		};
		this.initLayout();
		this.initLocalDeviceScan();
	}

	private void initLayout() {
		titles = getResources().getStringArray(R.array.type_title_item);
		TextView title = (TextView) findViewById(R.id.title_center_text);
		title.setText(titles[currentType - 1]);
		/**
		 * 暂时定义娱乐电子为全部分类 之后在query server时必须增加此选项
		 */
		currentType = currentType == 5 ? 2 : currentType;
		curGroup = SmallTools.getGroup(currentType - 1);

		findViewById(R.id.title_left_icon).setOnClickListener(this);
		findViewById(R.id.dev_refresh_icon).setOnClickListener(this);
		findViewById(R.id.add_terminal).setOnClickListener(this);
		findViewById(R.id.dev_more_icon).setOnClickListener(this);
		(listView = (ListView) findViewById(R.id.dev_list))
				.setOnItemClickListener(this);
		listView.setDivider(null);
	}

	private void initLocalDeviceScan() {
		mConnectedDevice = new ArrayList<Device>(0);
		mDeviceManager = new DeviceManager();
		mDeviceManager.startBroadcastEngine();
		mDeviceFoundReporter = new DeviceFoundReporter() {
			@Override
			public void reportDeviceFound(Device device) {
				if (D)
					Log.i(TAG, "device found: " + "\nid:" + device.getId()
							+ "\nvendor:" + device.getVendor() + "\nmodel:"
							+ device.getModel());
				boolean contained = false;
				for (Device dev : mConnectedDevice) {
					if (device.getIp().equals(dev.getIp())) {
						contained = true;
						break;
					}
				}
				// if have been added, ignore
				if (!contained) {
					localDevCount++;
					mConnectedDevice.add(device);
					boolean isRepeatable = false;
					for (int i = 0; i < deviceList.size(); i++) {
						CommonViewDevice commonViewdev = deviceList.get(i);
						if (commonViewdev.getDeviceId().equals(device.getId())) {
							// device.setName(commonViewdev.getName());
							// CommonViewDevice newDev = new
							// CommonViewDevice(device);
							// newDev.setCloudLinked(commonViewdev.isCloudLinked());
							commonViewdev.setLocalDevice(device);
							deviceList.set(i, commonViewdev);
							isRepeatable = true;
							break;
						}
					}

					CommonDevice cacheDev = DeviceListCache.getInstance()
							.getDevice(device.getId());
					if (!isRepeatable && !isCloudAvailable && cacheDev != null) {
						// CommonViewDevice d = new CommonViewDevice(device);
						// d.setName(cacheDev.getName());
						deviceList.add(new CommonViewDevice(cacheDev));
					}
					queryLocalDeviceInfo(device);
				}
			}
		};
		mDeviceManager.setDeviceFoundReporter(mDeviceFoundReporter);
	}

	private void reload(boolean isForceExecute) {
		mHandler.removeMessages(MSG_UPDATE_DEVICE);
		mHandler.removeMessages(MSG_RELOAD_DEVICE_LIST);
		
		if (isForceExecute || !isMoreClicked) {
			localDevCount = 0;
			deviceList.clear();
			deviceViewMap.clear();
			// isFirstLoading = true;
			isMoreClicked = false;
			isCloudAvailable = false;
			mConnectedDevice.clear();
			try {
				dialogShow(DeviceListActivity.this,
						getString(R.string.device_list_loading));
			} catch (Exception e) {
			}
			requestCloudData(curGroup);
		}	
		
		mHandler.sendEmptyMessageDelayed(MSG_RELOAD_DEVICE_LIST, INTERVAL_RELOAD * 1000);
	}

	private void requestCloudData(Group group) {
		try {
			service.getDevices(MyApplication.user.getDefaultGateway(), group, queryHander);
		} catch (Exception e) {
			Log.e(TAG, "Query Cloud Error: " + e.getMessage());
			updateView();
		}
	}

	private void requestLocalData() {
		// TODO
		// should be identified type of device while scanning inside local
		// network
		if (currentType == 2) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			mDeviceManager.scanConnectedDevice();
			
			mHandler.sendEmptyMessageDelayed(MSG_UPDATE_DEVICE, INTERVAL_TIMEOUT * 1000);
		} else {
			updateView();
		}
	}

	private class QueryDeviceListHander implements
			HttpUtil.ResponseResultHandler<QueryDevicesResponse> {
		@Override
		public void handle(boolean timeout, QueryDevicesResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						if (response.getData() != null) {
							isCloudAvailable = true;
							if (response.getData().size() > 0) {
								if (deviceList.size() == 0) {
									// isFirstLoading = true;
									for (CommonDevice d : response.getData())
										// TODO
										// add other device of Category
										if (d.getCategory() == Category.ShuiHu)
											deviceList
													.add(new CommonViewDevice(d));
								} else {
									// isFirstLoading = false;
									for (int i = 0; i < deviceList.size(); i++) {
										CommonViewDevice commonViewdev = deviceList
												.get(i);
										for (CommonDevice _d : response
												.getData())
											if (commonViewdev.getDeviceId()
													.equals(_d.getDeviceId()))
												deviceList
														.set(i,
																new CommonViewDevice(
																		_d));
									}
								}
							}
						} else {
							// data inside response is null
							if (D)
								Log.d(TAG, "data inside response is null, msg="
										+ response.getMsg());
							// showToastShort(response.getMsg());
						}
					} else {
						// request is not success
						String msgStr = response.getMsg();
						if (D)
							Log.d(TAG, "respose msg= " + msgStr);
						if (msgStr.contains("登录已过期")) { // logout and re-login
							dialogDismiss();
							showToastShort(msgStr);
							startActivity(new Intent(DeviceListActivity.this,
									LoginActivity.class));
						} else {
							showToastShort(getString(R.string.clouderror_hint));
						}
					}
				} else {
					// no response
					if (D)
						Log.d(TAG, "QueryDevicesResponse is Null !!");
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				// time out
				if (D)
					Log.d(TAG, "Query devices time out !!");
				// showToastShort(getString(R.string.timeout_hint));
			}
			// if(isCloudTimeout)
			// updateView();
			requestLocalData();
		}
	}

	private void queryLocalDeviceInfo(
			final com.hipad.smart.local.device.Device dev) {
		final DeviceController mLocalService = new DeviceController();
		mLocalService.init(dev);
		LocalCmd cmd = new LocalCmd(KettleCmd.CMD_QUERY, dev.getId(), true, "");
		ResponseWaiter waiter = new ResponseWaiter(cmd) {
			boolean isTimeout = false;

			@Override
			public void timeOut() {
				isTimeout = true;
				updateView();
				// showToastShort(getString(R.string.timeout_hint));
				super.timeOut();
				mLocalService.stopSelf();
			}

			@Override
			public void handleMessage(Msg response) {
				if (D)
					Log.d(TAG, "Local Device Info, id=" + dev.getId());
				super.handleMessage(response);
				if (!isTimeout && response.isSuccessful()) {
					KettleStatusInfo status = new KettleStatusInfo(
							response.getArgs());
					// status.dump();
					for (CommonViewDevice d : deviceList) {
						if (d.getDeviceId().equals(dev.getId()))
							d.setInfo(status);
					}
				}
				mLocalService.stopSelf();
				updateView();
			}
		};
		mLocalService.sendCmd(cmd, waiter);
	}

	private synchronized void updateView() {

		if (deviceList == null || deviceList.size() == 0) {
			/**
			 * show no-device image while list is empty
			 */
			findViewById(R.id.show_list_layout).setVisibility(View.GONE);
			findViewById(R.id.no_device_layout).setVisibility(View.VISIBLE);

		} else /* if (isFirstLoading) */{
			/**
			 * generate list view for the first time loading
			 */
			findViewById(R.id.show_list_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.no_device_layout).setVisibility(View.GONE);
			dListAdapter = new DevListAdapter(DeviceListActivity.this,
					R.layout.device_list_item, deviceViewMap, deviceList);
			listView.setAdapter(dListAdapter);
		}
		dialogDismiss();
	}
}
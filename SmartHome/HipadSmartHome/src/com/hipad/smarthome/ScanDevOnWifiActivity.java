package com.hipad.smarthome;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.CommonDevice.Group;
import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.QueryDevicesResponse;
import com.hipad.smart.json.Response;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager;
import com.hipad.smart.local.device.DeviceManager.DeviceFoundReporter;
import com.hipad.smart.util.StringUtil;
import com.hipad.smarthome.utils.CustomDialog;
import com.hipad.smarthome.utils.DeviceListCache;

public class ScanDevOnWifiActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = ScanDevOnWifiActivity.class.getSimpleName();

	private final static String ACTION_SCAN_QRCODE = "com.hipad.smarthome.zxing.SCAN";
	private final static String EXTRA_ACTIVE_CODE = "SCAN_RESULT";//"active_code";
	private final static int REQUEST_SCAN_ACTIVE_CODE = 0X1;
	
	private Context mContext;
	private ImageButton mImgbtnBack, mImgbtnRefresh;
	private TextView mTvPrompt, mTvTitle;
	private ListView mLstvScanedDevices;

	private ProgressBar mPrgsScaning;

	private List<Device> mConnectDevice;
	private List<CommonDevice> bundDevices;
	private DeviceManager mDeviceManger;
	private DeviceFoundReporter mDeviceFoundReporter;

	private ScanDevOnWiFiAdapter adapter;
	private QueryDeviceListHander queryHandler;
	private CustomDialog dialog;
	
	private final static int DLG_INPUT_ACTIVE_CODE = 1;
	private final static String KEY_DEV_ID = "dev_id";
	private String deviceIdToBeBind = "";

	public static final int MSG_SCAN_TIMEOUT = 0x01;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_SCAN_TIMEOUT:
				onScanFinished();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_device_layout);
		mContext = this;
		
		mConnectDevice = new ArrayList<Device>();
		bundDevices = new ArrayList<CommonDevice>();

		queryHandler = new QueryDeviceListHander();
		adapter = new ScanDevOnWiFiAdapter();		

		mDeviceManger = new DeviceManager();
		mDeviceManger.startBroadcastEngine();

		initViews();
		setOnClickListener();
		mLstvScanedDevices.setAdapter(adapter);

		mDeviceFoundReporter = new DeviceFoundReporter() {

			@Override
			public void reportDeviceFound(Device device) {

				mConnectDevice.add(device);

				adapter.notifyDataSetChanged();
			}
		};
		mDeviceManger.setDeviceFoundReporter(mDeviceFoundReporter);
		
		obtainCloudDevices();
	}

	@Override
	protected void onDestroy() {
		handler.removeMessages(MSG_SCAN_TIMEOUT);

		mDeviceManger.release();
		super.onDestroy();
	}

	private void initViews() {

		mImgbtnBack = (ImageButton) findViewById(R.id.leftBtn);
		mImgbtnRefresh = (ImageButton) findViewById(R.id.rightBtn);
		mImgbtnRefresh.setVisibility(View.VISIBLE);
		mImgbtnRefresh.setBackgroundResource(R.drawable.main_btn_update);
		mTvPrompt = (TextView) findViewById(R.id.scandevice_tv_prompt);
		mTvTitle = (TextView) findViewById(R.id.titleTxt);
		mTvTitle.setText("搜索设备");
		mPrgsScaning = (ProgressBar) findViewById(R.id.scandevice_prgs_scaning);
		mLstvScanedDevices = (ListView) findViewById(R.id.scandevice_lstv_scaneddevices);

	}

	private void onScanFinished(){
		mTvPrompt.setText("请选择...");
		mTvTitle.setText("设备列表");
		mPrgsScaning.setVisibility(View.INVISIBLE);

		dialogDismiss();
	}
	
	private void setOnClickListener() {
		mImgbtnBack.setOnClickListener(this);
		mImgbtnRefresh.setOnClickListener(this);
		
		mLstvScanedDevices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				ScanDevOnWiFiAdapter.ItemViewHolder itemViewHolder = (ScanDevOnWiFiAdapter.ItemViewHolder) view.getTag();
				if(View.VISIBLE == itemViewHolder.imgvAddedSign.getVisibility()) {
					showToastShort("该设备已经绑定！");
					return ;
				}
				
				dialog = new CustomDialog(mContext, R.style.MyDialog, 
						new CustomDialog.OnCustomDialogListener() {

							@Override
							public void OnClick(View v) {

								switch (v.getId()) {
								case R.id.dialog_button_cancel:
									dialog.dismiss();
									break;
								case R.id.dialog_button_ok:
									dialog.dismiss();
									Bundle data = new Bundle();
									data.putString(KEY_DEV_ID, mConnectDevice.get(position).getId());
									deviceIdToBeBind = mConnectDevice.get(position).getId();
									showDialog(DLG_INPUT_ACTIVE_CODE, data);
									break;
								default:
									break;
								}
							}
						});
				dialog.show();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_SCAN_ACTIVE_CODE && resultCode == RESULT_OK){
			String activeCode = data.getStringExtra(EXTRA_ACTIVE_CODE);
			Log.d(TAG, "QRCode: " + activeCode);
			if(!StringUtil.isNullOrEmpty(activeCode)){
				Bundle dlgdata = new Bundle();
				dlgdata.putString(EXTRA_ACTIVE_CODE, activeCode);
				showDialog(DLG_INPUT_ACTIVE_CODE, dlgdata);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {

		if(DLG_INPUT_ACTIVE_CODE == id){

			InputActiveCodeDlg inputActiveCodeDlg = (InputActiveCodeDlg) dialog;
			String activeCode = args.getString(EXTRA_ACTIVE_CODE);
			inputActiveCodeDlg.setActiveCode(activeCode);
		}			
		
		super.onPrepareDialog(id, dialog, args);
	}
		
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if(DLG_INPUT_ACTIVE_CODE == id){
			final String devId = args.getString(KEY_DEV_ID);
			
			InputActiveCodeDlg inputActiveCodeDlg = new InputActiveCodeDlg(mContext);
			inputActiveCodeDlg.setDevId(devId);	

			return inputActiveCodeDlg;
		}		
		
		return super.onCreateDialog(id, args);
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.rightBtn:
			obtainCloudDevices();
			break;
		default:
			break;
		}

	}
	
	private void startScanLocalDevices(){
		mPrgsScaning.setVisibility(View.VISIBLE);
		mTvPrompt.setText("扫描...");
		mTvTitle.setText("搜索设备");
		
		mConnectDevice.clear();
		mDeviceManger.scanConnectedDevice();
		
		handler.sendEmptyMessageDelayed(MSG_SCAN_TIMEOUT, 1000 * 3);
	}

	private void obtainCloudDevices() {
		dialogShow(mContext, null);
		service.getDevices("", Group.None, queryHandler);
	}

	private final HttpUtil.ResponseResultHandler<Response> mBindDeviceResultHandler = new HttpUtil.ResponseResultHandler<Response>() {

		@Override
		public void handle(boolean timeout, Response response) {
			dialogDismiss();
			if (!timeout && null != response && response.isSuccessful()) {
				showToastShort(getString(R.string.activity_bundledevice_hint_succuss));

//				obtainCloudDevices();
				Intent intent = new Intent(mContext, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				
				DeviceListCache.getInstance().syncDeviceList();
			} else {
				String msgStr = response.getMsg();
				
				if(msgStr.contains("登录已过期")){ // logout and re-login
					showToastShort(msgStr);
					startActivity(new Intent(mContext, LoginActivity.class));
				}else{							
					// TODO failed, sync latter when the network is good
					showToastShort(getString(R.string.activity_configdevice_hint_bind_failed));
				}				
			}
		}
	};

	private class ScanDevOnWiFiAdapter extends BaseAdapter {
		private class ItemViewHolder {
			public ImageView imgvAddedSign;
			public TextView tvApName;
			public ImageView imgvInfo;
		}
		
		@Override
		public int getCount() {
			return mConnectDevice.size();
		}

		@Override
		public Object getItem(int position) {
			return mConnectDevice.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			ItemViewHolder viewHolder = null;
			if (null == view) {
				view = View.inflate(mContext, R.layout.view_adddevice_list_item, null);

				viewHolder = new ItemViewHolder();
				viewHolder.imgvAddedSign = (ImageView) view .findViewById(R.id.view_add_device_list_item_imgv_addedsign);
				viewHolder.tvApName = (TextView) view .findViewById(R.id.view_add_device_list_item_tv_devicename);
				viewHolder.imgvInfo = (ImageView) view .findViewById(R.id.view_add_device_list_item_imgv_info);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ItemViewHolder) view.getTag();
			}

			Device device = mConnectDevice.get(position);
			String devId = device.getId();
			String apName = String.format("HIPAD_%02X_%s_%s_%4s", device.getDeviceType(), device.getVendor(), device.getModel(), devId.substring(devId.length() - 4));
			Log.d(TAG, "device: " + apName);
			viewHolder.tvApName.setText(apName);
			viewHolder.imgvAddedSign.setVisibility(View.INVISIBLE);
			
			for (CommonDevice dev : bundDevices) {
				if (dev.getDeviceId().equals(device.getId())) {
					viewHolder.imgvAddedSign.setVisibility(View.VISIBLE);
				}

			}
			return view;
		}

	}

	private class QueryDeviceListHander implements HttpUtil.ResponseResultHandler<QueryDevicesResponse> {
		@Override
		public void handle(boolean timeout, QueryDevicesResponse response) {
			dialogDismiss();
			
			if (timeout){
				showToastShort(getString(R.string.timeout_hint));
				onScanFinished();
				return;
			}
			
			if (response == null) {
				// network error
				showToastShort(getString(R.string.neterror_hint));
				onScanFinished();
				return;
			}
			
			if (!response.isSuccessful()) {
				// no response
				onScanFinished();

				String msgStr = response.getMsg();
				if(msgStr.contains("登录已过期")){ // logout and re-login
					showToastShort(msgStr);
					startActivity(new Intent(mContext, LoginActivity.class));
				}else{							
					showToastShort(getString(R.string.clouderror_hint));
				}
				return;
			}

			ArrayList<CommonDevice> devList = response.getData();
			if (null == devList) {
				// request is not success
				String msgStr = response.getMsg();
				showInfoLog("msgStr = " + msgStr);
				showToastShort(getString(R.string.clouderror_hint));
				return;
			} 

			if (devList.size() > 0) {
				bundDevices = devList;
			} else {
				// empty
				showToastShort(response.getMsg());
			}			

			startScanLocalDevices();
		}
	}
	
	
	private class InputActiveCodeDlg extends Dialog {
		private String devId;
		
		private Button btnOk, btnCancel, btnScan;
		private EditText edtActiveCode;

		public InputActiveCodeDlg(Context context) {
			super(context, R.style.MyDialog);
			
			View contentView = View.inflate(mContext, R.layout.dialog_input_active_code, null);
			btnOk = (Button) contentView.findViewById(R.id.dialog_button_ok);
			btnCancel = (Button) contentView.findViewById(R.id.dialog_button_cancel);
			btnScan = (Button) contentView.findViewById(R.id.dialog_btn_scan);
			edtActiveCode = (EditText) contentView.findViewById(R.id.dialog_edt_code);			
			
			btnScan.setOnClickListener(new View.OnClickListener() {
				
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
			btnOk.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String activeCode = edtActiveCode.getText().toString();
					
					if(StringUtil.isNullOrEmpty(activeCode)){
						showToastShort(getString(R.string.activity_configdevice_hint_active_code_illegal));
						return ;
					}

					dialogShow(mContext, getString(R.string.activity_configdevice_hint_sync_cloud));
					service.addDirectDevice(devId, activeCode, mBindDeviceResultHandler);
					dismissDialog(DLG_INPUT_ACTIVE_CODE);
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissDialog(DLG_INPUT_ACTIVE_CODE);
				}
			});
			
			setContentView(contentView);
		}	
		
		public void setDevId(String id){
			devId = id;
		}
		
		public void setActiveCode(String code){
			edtActiveCode.setText(code);
		}
	}
}
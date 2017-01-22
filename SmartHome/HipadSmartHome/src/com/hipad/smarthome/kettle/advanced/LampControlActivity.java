package com.hipad.smarthome.kettle.advanced;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.ErrorCode;
import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.kettle.v14.LightCmd;
import com.hipad.smart.kettle.v14.ResetWorkedRecordCmd;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.R;

public class LampControlActivity extends BaseActivity implements IFunction, OnClickListener {

	private static final String MYACTION = "LampControlActivity_action";
	private ImageView leftBtn;
	private TextView title;
	private ImageView commit;
	private Context mContext;
	private String titleStr;

	private ListView lampListView;
	private List<String> nameList;
	private ChooseLampAdapter adapter;
	
	private String name;
	
	private Device localDevice = null;
	private CommonDevice cloudDevice = null;
	private DeviceController controller;
	
	private ResponseResultHandler<CmdResponse> queryHandler = new ResponseResultHandler<CmdResponse>() {

		@Override
		public void handle(boolean timeout, CmdResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						ResponseData data = response.getData();
						if (data != null) {
							int errorCode = data.getErrorCode();
							if (errorCode == ErrorCode.E_SUCCESS) {
								LightCmd.LightStatus status = new LightCmd.LightStatus(data.getResponseBody());
								int mode = status.getLightMode();
								Log.d(tag, "mode: " + mode);
								
								adapter.notifyDataSetChanged(mode - 1);								
							}

						}
					}
				}
			}			

			dialogDismiss();
		}

	};
	
	private ResponseResultHandler<CmdResponse> cmdHandler = new ResponseResultHandler<CmdResponse>() {

		@Override
		public void handle(boolean timeout, CmdResponse response) {
			dialogDismiss();
			
			if (timeout){
				showToastShort(getString(R.string.timeout_hint));
				return;
			}
			
			if (response == null){
				showToastShort(getString(R.string.neterror_hint));
				return;
			}
			
			if (!response.isSuccessful()){
				showToastShort(getString(R.string.clouderror_hint));
				return;
			}
			
			ResponseData data = response.getData();
			if (data == null){
				showToastShort(getString(R.string.clouderror_hint));
				return;
			}
			int errorCode = data.getErrorCode();
			if (errorCode == ErrorCode.E_SUCCESS) {
				LightCmd.LightStatus status = new LightCmd.LightStatus(data.getResponseBody());
				int mode = status.getLightMode();
				Log.d(tag, "mode: " + mode);
				
				adapter.notifyDataSetChanged(mode - 1);	
				
				finish();
			}else{
				showToastShort(getString(R.string.device_error_hint));
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lamp_control_layout);
		mContext = this;

		controller = new DeviceController();
		
		getData();
		initViews();
		
		init();
	}

	private void init() {
		LightCmd cmd = new LightCmd();
		cmd.set(true, (byte) 0);

		dialogShow(mContext, null);
		if (localDevice != null) {
			LocalCmd localCmd = DeviceController.CmdAdapter.convert(localDevice.getId(), cmd);
			ResponseWaiter waiter = new ResponseWaiter(localCmd) {
				@Override
				public void timeOut() {
					super.timeOut();
					dialogDismiss();
				}

				@Override
				public void handleMessage(Msg response) {
					super.handleMessage(response);
					if (response.isSuccessful()) {
						LightCmd.LightStatus status = new LightCmd.LightStatus(response.getArgs());
						int mode = status.getLightMode();
						Log.d(tag, "mode: " + mode);
						
						adapter.notifyDataSetChanged(mode - 1);
					}

					dialogDismiss();
				}
			};
			controller.sendCmd(localCmd, waiter);
		}
		if (cloudDevice != null) {
			cloudDevice.sendCmd(cmd, queryHandler);
		}
	}

	private void getData() {
		Intent intent = getIntent();
		Object device = intent.getParcelableExtra("device");
		if (device instanceof Device) {
			localDevice = (Device) device;
			controller.init(localDevice);
		} else {
			cloudDevice = (CommonDevice) device;
		}

		titleStr = getIntent().getStringExtra("title");
		nameList = new ArrayList<String>();
		
	    nameList.add("≤ µ∆1");
	    nameList.add("≤ µ∆2");
	    nameList.add("≤ µ∆3");
	    nameList.add("≤ µ∆4");
	    nameList.add("≤ µ∆5");
	    nameList.add("≤ µ∆6");
	    nameList.add("≤ µ∆7");
	    nameList.add("≤ µ∆8");
	    nameList.add("≤ µ∆9");
	    nameList.add("≤ µ∆10");
	}

	private void initViews() {

		leftBtn = (ImageView) findViewById(R.id.title_left_icon);
		leftBtn.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title_center_text);
		title.setText(titleStr);
		commit = (ImageView) findViewById(R.id.control_icon_confirm);
		commit.setOnClickListener(this);
		lampListView = (ListView) findViewById(R.id.choose_lamp_list);
		adapter = new ChooseLampAdapter(mContext, nameList);
		lampListView.setAdapter(adapter);

	}

	@Override
	public String getName() {
		return "≤ µ∆øÿ÷∆";
	}

	@Override
	public Intent execute(Context context) {
		Intent intent = new Intent();
		intent.setAction(MYACTION);
		return intent;
	}

	@Override
	public boolean isForResult() {
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.title_left_icon:
			finish();

			break;
		case R.id.control_icon_confirm:
			int choice = adapter.getChoice();
			if(-1 == choice) break;
			
			name = nameList.get(choice);
			Log.i(tag, "ƒ„—°‘Ò¡À" + name);
			
			LightCmd cmd = new LightCmd();
			cmd.set(false, (byte) (choice + 1) );

			dialogShow(mContext, null);
			if (localDevice != null) {
				LocalCmd localCmd = DeviceController.CmdAdapter.convert(localDevice.getId(), cmd);
				ResponseWaiter waiter = new ResponseWaiter(localCmd) {
					@Override
					public void timeOut() {
						super.timeOut();
						dialogDismiss();
						showToastShort(getString(R.string.timeout_hint));
					}

					@Override
					public void handleMessage(Msg response) {
						super.handleMessage(response);

						dialogDismiss();
						if (response.isSuccessful()) {
							LightCmd.LightStatus status = new LightCmd.LightStatus(response.getArgs());
							int mode = status.getLightMode();
							Log.d(tag, "mode: " + mode);
							
							adapter.notifyDataSetChanged(mode - 1);		

							finish();
						}else{
							showToastShort("…Ë÷√ ß∞‹");
						}
					}
				};
				controller.sendCmd(localCmd, waiter);
			}
			if (cloudDevice != null) {
				cloudDevice.sendCmd(cmd, cmdHandler);
			}
			
			break;
		default:
			break;
		}

	}
	
	private static class ChooseLampAdapter extends BaseAdapter {

		private Context context;
		private List<String> data;
		
		private int checkedPos = -1;

		public ChooseLampAdapter(Context context, List<String> data) {
			this.context = context;
			this.data = data;

		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public int getChoice(){
			return checkedPos;
		}
		
		public void notifyDataSetChanged(int position) {
			checkedPos = position;			
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.lamp_list_item, null);
				
				holder = new ViewHolder();
				holder.lampName = (TextView) convertView.findViewById(R.id.lamp_name);
				holder.rdBtn = (RadioButton) convertView.findViewById(R.id.radio_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.lampName.setText(data.get(position));

			holder.rdBtn.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					checkedPos = position;
					notifyDataSetChanged(checkedPos);
				}
			});
			
			holder.rdBtn.setChecked(checkedPos == position);

			return convertView;
		}

		private class ViewHolder {
			private TextView lampName;
			private RadioButton rdBtn;

		}

	}
}

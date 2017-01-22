package com.hipad.smarthome.kettle.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.ErrorCode;
import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.kettle.v14.ResetWorkedRecordCmd;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.R;

/**
 * 清洗提醒
 * 
 * @author guowei
 *
 */
public class CleanRemainderActivity extends Activity implements IFunction,
		OnClickListener {

	public static final String MYACTION = "CleanRemainderActivity_action";

	private TextView title;
	private TextView usedTime;

	private Button reset;

	String titleStr = null;

	private Device localDevice = null;

	private CommonDevice cloudDevice = null;
	private DeviceController controller;

	private KettleStatusInfo kettleStatusInfo;

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:

				usedTime.setText(kettleStatusInfo.getWorkedTime() + " 小时");

				break;

			default:
				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(true);
		setContentView(R.layout.clean_remainder_layout);

		controller = new DeviceController();

		getData();
		initViews();
	}

	@Override
	protected void onDestroy() {
		controller.stopSelf();
		super.onDestroy();
	}

	private void getData() {

		if (getIntent().getParcelableExtra("device") instanceof Device) {

			localDevice = getIntent().getParcelableExtra("device");
			controller.init(localDevice);
		} else {

			cloudDevice = getIntent().getParcelableExtra("device");
		}

		titleStr = getIntent().getStringExtra("title");
		byte[] info = getIntent().getByteArrayExtra("kettleStatusInfo");

		kettleStatusInfo = new KettleStatusInfo(info);

	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		title.setText(titleStr);

		usedTime = (TextView) findViewById(R.id.used_time);

		usedTime.setText(kettleStatusInfo.getWorkedTime() + " 小时");

		reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(this);
	}

	@Override
	public String getName() {

		return "清洗提醒";
	}

	@Override
	public Intent execute(Context context) {

		Intent intent = new Intent();
		intent.setAction(MYACTION);
		return intent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset:

			ResetWorkedRecordCmd cmd = new ResetWorkedRecordCmd();

			if (localDevice != null) {
				LocalCmd localCmd = DeviceController.CmdAdapter.convert(
						localDevice.getId(), cmd);
				ResponseWaiter waiter = new ResponseWaiter(localCmd) {
					@Override
					public void timeOut() {
						super.timeOut();
					}

					@Override
					public void handleMessage(Msg response) {
						super.handleMessage(response);
						if (response.isSuccessful()) {
							kettleStatusInfo = new KettleStatusInfo(
									response.getArgs());
							kettleStatusInfo.dump();

							Message msg = new Message();
							msg.what = 0x01;
							handler.sendMessage(msg);
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

	private ResponseResultHandler<CmdResponse> cmdHandler = new ResponseResultHandler<CmdResponse>() {

		@Override
		public void handle(boolean timeout, CmdResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						ResponseData data = (ResponseData) response.getData();
						if (data != null) {
							int errorCode = data.getErrorCode();
							if (errorCode == ErrorCode.E_SUCCESS) {

								kettleStatusInfo = new KettleStatusInfo(
										data.getResponseBody());
								Message msg = new Message();
								msg.what = 0x01;
								handler.sendMessage(msg);
							}

						}
					}
				}
			}
		}

	};

	@Override
	public boolean isForResult() {
		return false;
	}
}

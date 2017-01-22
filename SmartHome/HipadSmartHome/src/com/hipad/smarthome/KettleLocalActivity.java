package com.hipad.smarthome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.kettle.v14.KettleCmd;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.kettle.v14.KettleStatusInfo.KettleState;
import com.hipad.smart.kettle.v14.OkBoilCmd;
import com.hipad.smart.kettle.v14.SpecificBoilCmd;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager.DeviceReportHandler;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.KettleDevActivity.SceneInfo;
import com.hipad.smarthome.adapter.Scene;
import com.hipad.smarthome.kettle.advanced.AdvancedSettingActivity;
import com.hipad.smarthome.kettle.dao.TempCDao;
import com.hipad.smarthome.kettle.dao.TimeQDao;
import com.hipad.smarthome.kettle.dao.WaterLevelDao;
import com.hipad.smarthome.kettle.statistics.entity.Temperature;
import com.hipad.smarthome.kettle.statistics.entity.TimeQuantum;
import com.hipad.smarthome.kettle.statistics.entity.WaterLevel;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.CanvasUtils;
import com.hipad.smarthome.utils.DeviceListCache;
import com.hipad.smarthome.utils.MyDialog;
import com.hipad.smarthome.utils.TargetTemperatureChooser;
import com.hipad.smarthome.utils.TargetTemperatureChooser.TempChosenListener;
import com.hipad.smarthome.utils.TimeSetPopWindow;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;

/**
 * 
 * @author wangbaoming
 *
 */
public class KettleLocalActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	private final static String TAG = "KettleLocalActivity";

	public final static String EXTRA_TARGET_TEMP = "temp";
	public final static String EXTRA_KEEP_TEMP_PERIOD = "keep_temp_period";
	public final static String EXTRA_MENU = "menu";

	private TextView title, waterLevel, temperature, workStateTv, tipsTv, sceneNameTv, sceneTempTv, sceneWarmPeroid, /*warmTv, */celsius;
	private ImageButton backBtn, boilBtn, keepwarmBtn, stopBtn;
	private RadioGroup radioGroup;
	private RadioButton rdbtnHobby, rdbtnScene, rdbtnAppoint, rdbtnAdvanceed;

	private ImageView statusImg, netWork, imgTips, sceneIcon, imageTemp,
			imageTds, appointIcon, imgTitleSign;
	private View notifyView;

	private AlphaAnimation alpAnima = new AlphaAnimation(1.0f, 0.0f);

	private KettleStatusInfo kettleStatusInfo;

	private LinearLayout layoutTab, layoutInfo, layoutButton, layoutStop, layoutTime;
	private AnimationDrawable statusDrawable;

	private String titleStr = null;

	private TargetTemperatureChooser mTmpch;
	private TimeSetPopWindow mTimeSetPopWindow;

	private Device device;
	private DeviceController deviceController;
	private DeviceReportHandler deviceReportHandler;

	private KettleLocalActivity context;

	private int mCountOfException;

	private Dialog noWaterWarningDlg;

	private WaterLevelDao levelDao = null;
	private TempCDao tempCDao = null;
	private TimeQDao timeQDao = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("HH");

	double[] arr_tempC = new double[8];

	double[] arr_period = new double[12];
	
	private final static int REQUEST_CODE_FOR_SCENE = 0X01;

	private SceneInfo mTargetScene = new SceneInfo();
	private SceneInfo mCustomScene = new SceneInfo();

	private final static int MSG_UPDATE = 0X01;
	private final static int MSG_UPDATE_TEMP_GRAPH = 0X02;
	private final static int MSG_UPDATE_TDS_GRAPH = 0X03;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE:
				if (checkNetworkState()) {
					getKettleStatusInfo(false);
					handler.sendEmptyMessageDelayed(MSG_UPDATE, 1000 * 5);
				}
				break;
			case MSG_UPDATE_TEMP_GRAPH:
				if (msg.arg1 <= 100 && msg.arg1 >= 0) {
					CanvasUtils.getProgressBitmap(imageTemp, getResources(),
							R.drawable.doughnutchart_temp, msg.arg1, 2.7f);
				}
				break;
			case MSG_UPDATE_TDS_GRAPH:
				float progress = 0f;
				switch (msg.arg2) {
				case 1:
					progress = 67.5f;
					break;
				case 2:
					progress = 135f;
					break;
				case 3:
					progress = 202.5f;
					break;
				case 4:
					progress = 270f;
					break;
				default:
					break;
				}

				CanvasUtils.getProgressBitmap(imageTds, getResources(),
						R.drawable.doughnutchart_tds, progress, 1f);
				break;

			default:
				break;
			}
		};
	};

	private void saveSceneInfo() {
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(getCacheDir()
					+ File.separator + MyApplication.user.getName() + "_"
					+ device.getId());
			oos = new ObjectOutputStream(fos);
			if (null != mTargetScene)
				oos.writeObject(mTargetScene);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != oos)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void restoreSceneInfo() {
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(getCacheDir()
					+ File.separator + MyApplication.user.getName() + "_"
					+ device.getId());
			ois = new ObjectInputStream(fis);
			mTargetScene = (SceneInfo) ois.readObject();

			// restore successfully
			if (-1 != mTargetScene.temperature)
				rdbtnHobby.setText(mTargetScene.nameResId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != ois)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void storeDatasForAnalysis(Temperature temp, TimeQuantum time, WaterLevel level) {
		tempCDao.insertTempC(temp);
		timeQDao.insertTimes(time);
		levelDao.insertWaterLevel(level);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kettle_layout4);
		context = this;
		levelDao = new WaterLevelDao(context);
		tempCDao = new TempCDao(context);
		timeQDao = new TimeQDao(context);

		Intent intent = getIntent();
		titleStr = intent.getStringExtra("title");
		device = getIntent().getParcelableExtra(Device.EXTRA_DEVICE);

		Log.i(TAG, "device: " + device.toString());
		if (null == device) {
			finish();
			return;
		}

		deviceController = new DeviceController();
		deviceReportHandler = new DeviceReportHandler() {

			@Override
			public void handleDeviceReport(Msg report) {
				if (report.isSuccessful()
						&& Msg.Type.REPORT == report.getType()) {
					KettleStatusInfo statusInfo = new KettleStatusInfo(
							report.getArgs());
					Log.e(TAG, "report");
					statusInfo.dump();
				}
			}
		};

		mTmpch = new TargetTemperatureChooser(this);
		mTimeSetPopWindow = new TimeSetPopWindow(this);

		noWaterWarningDlg = new MyDialog(context, R.style.MyDialog,
				new MyDialog.OnDialogListener() {
					@Override
					public void OnClick(View v) {
						switch (v.getId()) {
						case R.id.dialog_button_ok:
							layoutInfo.setVisibility(View.INVISIBLE);
							SpecificBoilCmd cmd = new SpecificBoilCmd();
							cmd.boil(false);

							LocalCmd localCmd = DeviceController.CmdAdapter
									.convert(device.getId(), cmd);
							ResponseWaiter waiter = new ResponseWaiter(localCmd) {
								@Override
								public void timeOut() {
									Log.e(TAG, "timeout");
									super.timeOut();
									updateUi(kettleStatusInfo);
									dialogDismiss();
								}

								@Override
								public void handleMessage(Msg response) {
									super.handleMessage(response);
									if (response.isSuccessful()) {
										Log.e(TAG, "success");
										KettleStatusInfo kettleStatusInfo = new KettleStatusInfo(
												response.getArgs());
										kettleStatusInfo.dump();
										updateUi(kettleStatusInfo);
									}
									dialogDismiss();
								}
							};
							deviceController.sendCmd(localCmd, waiter);
							dialogShow(context, null);
							break;
						default:
							break;
						}

					}
				});

		getView();
		setOnClickListener();
		init();
		restoreSceneInfo();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!deviceController.isInited())
			deviceController.init(device);
		getKettleStatusInfo(true);

		// check if there is appointment on the device
		AppointMentDataBase db = AppointMentDataBase.getInstance(this);
		boolean isAppointed = db.hasEffectiveAppoint(MyApplication.user.getUserId(),device.getId());
		Log.i(TAG, "预约: " + device.getId() + " " + isAppointed);
		appointIcon.setVisibility(isAppointed ? View.VISIBLE : View.INVISIBLE);

		handler.sendEmptyMessage(MSG_UPDATE);
	}

	@Override
	protected void onStop() {
		handler.removeMessages(MSG_UPDATE);
		deviceController.stopSelf();
		super.onStop();
	}

	private void notify(int time) {
		if (time > 200) { // reach 200h, show notify
			imgTips.setImageResource(R.drawable.ico_alert1);
			tipsTv.setText("水壶使用时间较长，请清洗水壶！");
			alpAnima.setDuration(5000);
			notifyView.setAnimation(alpAnima);
			alpAnima.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					notifyView.setVisibility(View.INVISIBLE);
				}
			});
		}

	}

	private void initArrayOfTempC(double tp) {
		if (tp >= 30 && tp < 40) {
			arr_tempC[0] += 1;
		}
		if (tp >= 40 && tp < 50) {
			arr_tempC[1] += 1;
		}
		if (tp >= 50 && tp < 60) {
			arr_tempC[2] += 1;
		}
		if (tp >= 60 && tp < 70) {
			arr_tempC[3] += 1;
		}
		if (tp >= 70 && tp < 80) {
			arr_tempC[4] += 1;
		}
		if (tp >= 80 && tp < 90) {
			arr_tempC[5] += 1;
		}
		if (tp >= 90 && tp < 100) {
			arr_tempC[6] += 1;
		}
		if (tp == 100) {
			arr_tempC[7] += 1;
		}
	}

	private void initArrayPeriod(int t) {
		if (t >= 0 && t < 2) {
			arr_period[0] += 1;
		} else if (t < 4) {
			arr_period[1] += 1;
		} else if (t < 6) {
			arr_period[2] += 1;
		} else if (t < 8) {
			arr_period[3] += 1;
		} else if (t < 10) {
			arr_period[4] += 1;
		} else if (t < 12) {
			arr_period[5] += 1;
		} else if (t < 14) {
			arr_period[6] += 1;
		} else if (t < 16) {
			arr_period[7] += 1;
		} else if (t < 18) {
			arr_period[8] += 1;
		} else if (t < 20) {
			arr_period[9] += 1;
		} else if (t < 22) {
			arr_period[10] += 1;
		} else {
			arr_period[11] += 1;
		}

	}

	private void updateUi(KettleStatusInfo kettleStatusInfo) {
		if (null == kettleStatusInfo)
			return;

		updateWaterTemp(kettleStatusInfo.getCurrentTemperature());
		updateWaterLevel(kettleStatusInfo.getWaterQuality());

		checkWarningAndError(kettleStatusInfo);

		switch (kettleStatusInfo.getState()) {
		case KettleState.STATE_STANDBY:
			layoutInfo.setVisibility(View.GONE);

			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);

			setWorkIndicator(false);
			workStateTv.setText("空闲");
			layoutTab.setVisibility(View.VISIBLE);

			notify(kettleStatusInfo.getWorkedTime());
			break;
		case KettleState.STATE_HEATING:

			syncSceneInfo(kettleStatusInfo);

			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);

			setWorkIndicator(true);
			workStateTv.setText("煮水中");
			layoutTab.setVisibility(View.INVISIBLE);
			break;
		case KettleState.STATE_BOILED_TO_KEEP_TEMPC:
			syncSceneInfo(kettleStatusInfo);

			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);

			setWorkIndicator(false);
			workStateTv.setText("冷却");
			layoutTab.setVisibility(View.INVISIBLE);
			break;
		case KettleState.STATE_KEEP_TEMPC:

			syncSceneInfo(kettleStatusInfo);

			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);

			setWorkIndicator(false);
			workStateTv.setText("保温");
			layoutTab.setVisibility(View.INVISIBLE);
			break;
		case KettleState.NOTIFY_HUNG:

			setWorkIndicator(false);

			int func = kettleStatusInfo.getCurrFunc();
			if (KettleStatusInfo.FUNC_STANDBY == func) {
				workStateTv.setText("空闲");
				layoutInfo.setVisibility(View.INVISIBLE);

				boilBtn.setVisibility(View.VISIBLE);
				keepwarmBtn.setVisibility(View.VISIBLE);
				stopBtn.setVisibility(View.GONE);
				layoutTab.setVisibility(View.VISIBLE);
			} else {
				workStateTv.setText("暂停");
				syncSceneInfo(kettleStatusInfo);

				boilBtn.setVisibility(View.GONE);
				keepwarmBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.VISIBLE);

				layoutTab.setVisibility(View.INVISIBLE);
			}

			break;
		case KettleState.WARN_BAD_WATER:

			setWorkIndicator(false);

			workStateTv.setText("空闲");
			layoutInfo.setVisibility(View.INVISIBLE);

			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);
			layoutTab.setVisibility(View.VISIBLE);

			break;
		case KettleState.ERROR_NTC:

			setWorkIndicator(false);

			workStateTv.setText("严重故障");
			layoutInfo.setVisibility(View.INVISIBLE);

			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);
			layoutTab.setVisibility(View.VISIBLE);

			break;

		default:
			break;
		}

	}

	private void getView() {
		layoutTab = (LinearLayout) findViewById(R.id.layout_tab);
		layoutInfo = (LinearLayout) findViewById(R.id.layout_info);
		layoutButton = (LinearLayout) findViewById(R.id.layout_btn);
		layoutStop = (LinearLayout) findViewById(R.id.layout_stop);
		layoutTime = (LinearLayout) findViewById(R.id.linearlayout_time);

		statusImg = (ImageView) findViewById(R.id.statusImg);

		imgTips = (ImageView) findViewById(R.id.kettle_imgv_tips);
		tipsTv = (TextView) findViewById(R.id.kettle_tv_prompt);
		celsius = (TextView) findViewById(R.id.temp_icon);
		workStateTv = (TextView) findViewById(R.id.kettle_tv_workstate);

		waterLevel = (TextView) findViewById(R.id.water_level);
		temperature = (TextView) findViewById(R.id.water_temp);

		title = (TextView) findViewById(R.id.titleTxt);
		imgTitleSign = (ImageView) findViewById(R.id.titleSign);

		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		boilBtn = (ImageButton) findViewById(R.id.boilBtn);
		keepwarmBtn = (ImageButton) findViewById(R.id.keepwarmBtn);
		stopBtn = (ImageButton) findViewById(R.id.stopBtn);

		notifyView = findViewById(R.id.kettle_rl_notify);

		radioGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		rdbtnHobby = (RadioButton) findViewById(R.id.main_tab_hobby);
		rdbtnScene = (RadioButton) findViewById(R.id.main_tab_scene);
		rdbtnAppoint = (RadioButton) findViewById(R.id.main_tab_appoint);
		rdbtnAdvanceed = (RadioButton) findViewById(R.id.main_tab_advance);

		sceneNameTv = (TextView) findViewById(R.id.kettle_tv_scene_name);
		sceneTempTv = (TextView) findViewById(R.id.kettle_tv_scene_temp);
		sceneWarmPeroid = (TextView) findViewById(R.id.kettle_tv_scene_keepwarmperoid);
		sceneIcon = (ImageView) findViewById(R.id.kettle_imgv_scene_icon);
//		warmTv = (TextView) findViewById(R.id.tv_warm);

		appointIcon = (ImageView) findViewById(R.id.kettle_imgv_appointsign);

		imageTemp = (ImageView) findViewById(R.id.image_Temp);
		imageTds = (ImageView) findViewById(R.id.image_Tds);
	}

	private void setOnClickListener() {

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		radioGroup.setOnCheckedChangeListener(this);
		rdbtnHobby.setOnClickListener(this);
		rdbtnScene.setOnClickListener(this);
		rdbtnAppoint.setOnClickListener(this);
		rdbtnAdvanceed.setOnClickListener(this);

		boilBtn.setOnClickListener(this);
		keepwarmBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

		mTmpch.setTempChoseListener(new TempChosenListener() {

			@Override
			public void onTempChosen(int temp) {
				// then choose the period to keep warm
				mCustomScene.temperature = temp;
				mTimeSetPopWindow.showPopupWindow();
			}
		});
		mTimeSetPopWindow.setTimeChoseListener(new TimeChoseListener() {

			@Override
			public void onTimeChoose(int minute) {
				int mins = minute;
				mCustomScene.period = mins;

				mCustomScene.imgResId = R.drawable.scene_0_150x150;
				mCustomScene.nameResId = R.string.scene_0;
				mCustomScene.menu = new byte[] { 0x0, 0x01 };

				customBoil(mCustomScene, true);

				//记录水壶的水质，用水时间段和用水温度
				storeDatas(mCustomScene.temperature);

				layoutTab.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void init() {
		statusDrawable = (AnimationDrawable) statusImg.getBackground();
		title.setText(titleStr);

		// network sign
		imgTitleSign.setImageResource(R.drawable.lan);

		// deviceController.init(device);
		deviceController.setReportHandler(deviceReportHandler);

		// getKettleStatusInfo(true);
	}

	private void getKettleStatusInfo(boolean first) {
		if (first)
			dialogShow(context, null);
		LocalCmd cmd = new LocalCmd(KettleCmd.CMD_QUERY, device.getId(), true,
				"");
		ResponseWaiter waiter = new ResponseWaiter(cmd) {
			@Override
			public void timeOut() {
				Log.e(TAG, "timeout");
				super.timeOut();
				dialogShow(context, null);
				updateUi(kettleStatusInfo);
				mCountOfException++;
			}

			@Override
			public void handleMessage(Msg response) {
				super.handleMessage(response);
				if (response.isSuccessful()) {
					Log.e(TAG, "success");
					kettleStatusInfo = new KettleStatusInfo(response.getArgs());
					kettleStatusInfo.dump();

					mCountOfException = 0;

					updateUi(kettleStatusInfo);
					dialogDismiss();
				} else {
					dialogShow(context, null);
					mCountOfException++;
				}
			}
		};

		deviceController.sendCmd(cmd, waiter);
	}

	/**
	 * check network state.
	 * 
	 * @return false if error, true if normal
	 */
	private boolean checkNetworkState() {
		if (mCountOfException > 3) {
			mCountOfException = 0;
			showToastShort("网络异常，设备已离线！");
			finish();
			return false;
		} else {
			return true;
		}
	}

	private void checkWarningAndError(final KettleStatusInfo kettleStatusInfo) {
		String info = "";
		int state = kettleStatusInfo.getState();

		if (KettleState.WARN_NO_WATER == state) {
			noWaterWarningDlg.show();
		} else {
			noWaterWarningDlg.dismiss();
		}

		switch (state) {
		case KettleState.NOTIFY_HUNG:
			info = "水壶提起";
			waterLevel.setText("--");
			temperature.setText("--");
			break;
		case KettleState.WARN_NO_WATER:
			info = "水壶没水";
			waterLevel.setText("--");
			temperature.setText("--");
			break;
		case KettleState.WARN_BAD_WATER:
			info = "水质低劣";
			break;
		case KettleState.ERROR_NTC:
			info = "NTC出错";
			temperature.setText("--");
			break;

		default:
			notifyView.setVisibility(View.INVISIBLE);
			tipsTv.setTextColor(Color.WHITE);
			return;
			// break;
		}
		// 水壶处于非待机状态，水温显示为"--℃"
		// temperature.setText("--");
		// waterLevel.setText("--");

		tipsTv.setText(info);
		tipsTv.setTextColor(Color.YELLOW);
		imgTips.setImageResource(R.drawable.ico_alert2);
		notifyView.setVisibility(View.VISIBLE);

	}

	private void updateWaterTemp(int tmp) {
		temperature.setText(String.valueOf(tmp));
		if (tmp >= 0 && tmp <= 30) {
			temperature.setTextColor(Color.rgb(37, 183, 188));
			celsius.setTextColor(Color.rgb(37, 183, 188));
		} else if (tmp <= 70) {
			temperature.setTextColor(Color.rgb(255, 188, 0));
			celsius.setTextColor(Color.rgb(255, 188, 0));
		} else {
			temperature.setTextColor(Color.rgb(229, 0, 17));
			celsius.setTextColor(Color.rgb(229, 0, 17));
		}

		// upd the temp graph
		Message msg = new Message();
		msg.what = MSG_UPDATE_TEMP_GRAPH;
		msg.arg1 = tmp;
		handler.sendMessage(msg);

	}

	private void updateWaterLevel(int n) {
		int tds = n * 10;
		int level = 0;
		String waterStr = "优秀";
		int color = Color.rgb(111, 203, 125);
		if (tds <= 200) {
			waterStr = "优秀";
			color = Color.rgb(111, 203, 125);
			level = 1;
		} else if (tds <= 500) {
			waterStr = "良好";
			color = Color.rgb(37, 183, 188);
			level = 2;
		} else if (tds <= 1000) {
			waterStr = "较差";
			color = Color.rgb(255, 188, 0);
			level = 3;
		} else {
			waterStr = "低劣";
			color = Color.rgb(229, 0, 17);
			level = 4;
		}

		waterLevel.setTextColor(color);
		waterLevel.setText(waterStr);

		// upd the tsd graph
		Message msg = new Message();
		msg.what = MSG_UPDATE_TDS_GRAPH;
		msg.arg2 = level;
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View view) {

		if (null == kettleStatusInfo)
			return;
		int state = kettleStatusInfo.getState();
		// if (state == KettleState.NOTIFY_HUNG
		// || state == KettleState.WARN_NO_WATER
		// || state == KettleState.WARN_BAD_WATER
		// || state == KettleState.ERROR_NTC) {
		// showToastShort("当前水壶不可操作！");
		// return;
		// }
		if (KettleState.ERROR_NTC == state) { // ntc
			showToastShort(getString(R.string.kettle_hint_ntc));
			return;
		}
		int viewId = view.getId();
		if (R.id.boilBtn == viewId || R.id.keepwarmBtn == viewId
				|| R.id.stopBtn == viewId || R.id.main_tab_scene == viewId
				|| R.id.main_tab_hobby == viewId) {
			boolean noOperation = true;

			switch (state) {
			case KettleState.NOTIFY_HUNG:
				showNotifyDialog(context,
						getString(R.string.kettle_hint_hungup));
				break;

			case KettleState.WARN_NO_WATER:
				break;
			case KettleState.WARN_BAD_WATER:
				showNotifyDialog(context,
						getString(R.string.kettle_hint_bad_water));
				break;
			case KettleState.ERROR_NTC:
				showNotifyDialog(context, getString(R.string.kettle_hint_ntc));
				break;
			default:
				noOperation = false;
				break;
			}

			if (noOperation)
				return;
		}

		switch (viewId) {
		/*
		 * case R.id.leftBtn: finish(); break;
		 */

		case R.id.boilBtn: {

			mCustomScene.imgResId = R.drawable.scene_0_150x150;
			mCustomScene.nameResId = R.string.scene_0;
			mCustomScene.menu = new byte[] { 0x0, 0x02 };

			customBoil(mCustomScene, false);

			storeDatas(100);

			// SpecificBoilCmd cmd = new SpecificBoilCmd();
			// cmd.boil(true);
			//
			// LocalCmd localCmd =
			// DeviceController.CmdAdapter.convert(device.getId(), cmd);
			// ResponseWaiter waiter = new ResponseWaiter(localCmd) {
			// @Override
			// public void timeOut() {
			// Log.e(TAG, "timeout");
			// super.timeOut();
			// updateUi(kettleStatusInfo);
			// }
			//
			// @Override
			// public void handleMessage(Msg response) {
			// super.handleMessage(response);
			// if(response.isSuccessful()){
			// Log.e(TAG, "success");
			// kettleStatusInfo = new KettleStatusInfo(response.getArgs() );
			// kettleStatusInfo.dump();
			//
			// updateUi(kettleStatusInfo);
			// }
			// }
			// };

			// deviceController.sendCmd(localCmd, waiter);

			layoutTab.setVisibility(View.INVISIBLE);

			break;
		}
		case R.id.keepwarmBtn:

			mTmpch.show();
			break;
		case R.id.stopBtn: {
			layoutInfo.setVisibility(View.INVISIBLE);

			SpecificBoilCmd cmd = new SpecificBoilCmd();
			cmd.boil(false);

			LocalCmd localCmd = DeviceController.CmdAdapter.convert(
					device.getId(), cmd);
			ResponseWaiter waiter = new ResponseWaiter(localCmd) {
				@Override
				public void timeOut() {
					Log.e(TAG, "timeout");
					super.timeOut();
					updateUi(kettleStatusInfo);
					dialogDismiss();
				}

				@Override
				public void handleMessage(Msg response) {
					super.handleMessage(response);
					if (response.isSuccessful()) {
						Log.e(TAG, "success");
						kettleStatusInfo = new KettleStatusInfo(
								response.getArgs());
						kettleStatusInfo.dump();

						updateUi(kettleStatusInfo);
					}

					dialogDismiss();
				}
			};

			deviceController.sendCmd(localCmd, waiter);
			dialogShow(context, null);

			// layoutTab.setVisibility(View.VISIBLE);

			break;
		}
		case R.id.main_tab_hobby: {
			if (-1 == mTargetScene.temperature) {
				rdbtnScene.setChecked(true);

				Intent intent = new Intent(this, SceneLayerOneActivity.class);
				startActivityForResult(intent, REQUEST_CODE_FOR_SCENE);
				return;
			}

			specificBoil(mTargetScene);
			//记录水壶的水质，用水时间段和用水温度
			storeDatas(mTargetScene.temperature);

			layoutTab.setVisibility(View.INVISIBLE);
			break;
		}
		case R.id.main_tab_scene: {
			// startActivity(new Intent(this, SceneActivity.class));

			Intent intent = new Intent(this, SceneLayerOneActivity.class);
			startActivityForResult(intent, REQUEST_CODE_FOR_SCENE);
			break;
		}
		case R.id.main_tab_appoint: {
			// showToastShort("暂未开通，开发中...");
			CommonDevice cloudDevice = DeviceListCache.getInstance().getDevice(
					device.getId());
			Intent intent = new Intent();
			intent.putExtra(CommonDevice.EXTRA_DEVICE, cloudDevice);
			intent.setClass(context, AppointMentActivity.class);
			startActivity(intent);

			break;
		}
		case R.id.main_tab_advance:
			Intent intent = new Intent(this, AdvancedSettingActivity.class);
			byte[] info = kettleStatusInfo.getData();
			intent.putExtra("kettleStatusInfo", info);
			intent.putExtra("device", device);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private void syncSceneInfo(KettleStatusInfo kettleStatusInfo) {
		SceneInfo sceneInfo = new SceneInfo();
		byte[] menu = kettleStatusInfo.getMenu();

		// Scene scene = Scene.getSceneByCmd(menu);
		// sceneInfo.temp = scene.getWarmTemperatureC();
		// sceneInfo.period = kettleStatusInfo.getRemainOfTimeToKeepTempC(); //
		// remaining time in mins
		// sceneInfo.nameResId = Scene.getSceneTitleResId(context, scene);
		// sceneInfo.imgResId = Scene.getSceneImgResId(application, scene,
		// R.string.img_suffix_150x150);

		if (0 == menu[0]) { // custom
			sceneInfo.nameResId = R.string.scene_0;
			sceneInfo.imgResId = R.drawable.scene_0_150x150;
			sceneInfo.temperature = kettleStatusInfo.getTempCToBeKept();

			if (1 == menu[1]) { // keep temp
				sceneInfo.period = kettleStatusInfo.getRemainOfTimeToKeepTempC(); // remaining time in mins
				layoutTime.setVisibility(View.VISIBLE);
			} else { // boil directly
				sceneInfo.temperature = 100;
				layoutTime.setVisibility(View.GONE);
			}
		} else { // scene
			Scene scene = Scene.getSceneByCmd(menu);
			sceneInfo.temperature = scene.getWarmTemperatureC();
			sceneInfo.period = kettleStatusInfo.getRemainOfTimeToKeepTempC(); // remaining time in mins
			sceneInfo.nameResId = Scene.getSceneTitleResId(context, scene);
			sceneInfo.imgResId = Scene.getSceneImgResId(application, scene, R.string.img_suffix_150x150);
			layoutTime.setVisibility(View.VISIBLE);
		}

		// update scene ui
		layoutInfo.setVisibility(View.VISIBLE);

		sceneNameTv.setText(sceneInfo.nameResId);
		sceneTempTv.setText(String.valueOf(sceneInfo.temperature) + "℃");
		sceneWarmPeroid.setText(String.valueOf(sceneInfo.period) + "分钟"); // remaining times in mins
		sceneIcon.setImageResource(sceneInfo.imgResId);
	}

	private void specificBoil(SceneInfo sceneInfo) {
		// update scene ui
		sceneNameTv.setText(sceneInfo.nameResId);
		sceneTempTv.setText(String.valueOf(sceneInfo.temperature) + "℃");
		sceneWarmPeroid.setText(String.valueOf(sceneInfo.period) + "分钟");
		layoutTime.setVisibility(View.VISIBLE);
		sceneIcon.setImageResource(sceneInfo.imgResId);

		// layoutInfo.setVisibility(View.VISIBLE);

		rdbtnHobby.setText(sceneInfo.nameResId);

		// send cmd
		SpecificBoilCmd cmd = new SpecificBoilCmd();
		cmd.boil(true);
		cmd.setMinutesToKeepTempC(sceneInfo.period);
		cmd.setTempCToBeKept(sceneInfo.temperature);
		cmd.syncMenu(sceneInfo.menu[0], sceneInfo.menu[1]);

		LocalCmd localCmd = DeviceController.CmdAdapter.convert(device.getId(),
				cmd);
		ResponseWaiter waiter = new ResponseWaiter(localCmd) {
			@Override
			public void timeOut() {
				Log.e(TAG, "timeout");
				super.timeOut();
				updateUi(kettleStatusInfo);
				dialogDismiss();
			}

			@Override
			public void handleMessage(Msg response) {
				super.handleMessage(response);
				if (response.isSuccessful()) {
					Log.e(TAG, "success");
					kettleStatusInfo = new KettleStatusInfo(response.getArgs());
					kettleStatusInfo.dump();

					updateUi(kettleStatusInfo);
				}
				dialogDismiss();
			}
		};

		deviceController.sendCmd(localCmd, waiter);
		dialogShow(context, null);
	}

	private void customBoil(SceneInfo sceneInfo, boolean keepTemp) {
		// update scene ui
		sceneNameTv.setText(sceneInfo.nameResId);

		if (keepTemp) {
			sceneTempTv.setText(String.valueOf(sceneInfo.temperature) + "℃");
			sceneWarmPeroid.setText(String.valueOf(sceneInfo.period) + "分钟");
			layoutTime.setVisibility(View.VISIBLE);
		} else {
			sceneTempTv.setText("100℃");
			layoutTime.setVisibility(View.GONE);
		}
		sceneIcon.setImageResource(sceneInfo.imgResId);

		// layoutInfo.setVisibility(View.VISIBLE);

		// send cmd
		OkBoilCmd cmd = new OkBoilCmd();
		if (keepTemp) {
			cmd.setFunc(KettleStatusInfo.FUNC_KEEP_TEMPC);
			cmd.setMinutesToKeepTempC(sceneInfo.period);
			cmd.setTempCToBeKept(sceneInfo.temperature);
		} else {
			cmd.setFunc(KettleStatusInfo.FUNC_BOIL);
		}
		cmd.syncMenu(sceneInfo.menu[0], sceneInfo.menu[1]);

		LocalCmd localCmd = DeviceController.CmdAdapter.convert(device.getId(),
				cmd);
		ResponseWaiter waiter = new ResponseWaiter(localCmd) {
			@Override
			public void timeOut() {
				Log.e(TAG, "timeout");
				super.timeOut();
				updateUi(kettleStatusInfo);
				dialogDismiss();
			}

			@Override
			public void handleMessage(Msg response) {
				super.handleMessage(response);
				if (response.isSuccessful()) {
					Log.e(TAG, "success");
					kettleStatusInfo = new KettleStatusInfo(response.getArgs());
					kettleStatusInfo.dump();

					updateUi(kettleStatusInfo);
				}
				dialogDismiss();
			}
		};

		deviceController.sendCmd(localCmd, waiter);		
		
		dialogShow(context, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!deviceController.isInited())
			deviceController.init(device);

		if (RESULT_OK == resultCode) {
			if (REQUEST_CODE_FOR_SCENE == requestCode) {
				// fetch the scene data
				mTargetScene.temperature = data.getIntExtra(EXTRA_TARGET_TEMP,
						0);
				mTargetScene.period = data.getIntExtra(EXTRA_KEEP_TEMP_PERIOD,
						0);

				byte[] menu = data.getByteArrayExtra(EXTRA_MENU);

				Scene scene = Scene.getSceneByCmd(menu);
				mTargetScene.nameResId = Scene.getSceneTitleResId(context,
						scene);
				mTargetScene.imgResId = Scene.getSceneImgResId(application,
						scene, R.string.img_suffix_150x150);

				mTargetScene.menu = menu;

				specificBoil(mTargetScene);
				//记录水壶的水质，用水时间段和用水温度
				storeDatas(mTargetScene.temperature);

				layoutTab.setVisibility(View.INVISIBLE);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setWorkIndicator(boolean heat) {
		if (heat) {
			statusImg.setVisibility(View.VISIBLE);
			statusDrawable.start();
		} else {
			statusImg.setVisibility(View.INVISIBLE);
			statusDrawable.stop();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch (checkedId) {
		case R.id.main_tab_scene:
			break;
		case R.id.main_tab_hobby:
			break;
		case R.id.main_tab_appoint:
			break;
		case R.id.main_tab_advance:
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		saveSceneInfo();

		// deviceController.stopSelf();

		super.onDestroy();

	}

	private void storeDatas(double temperature){
		
		 Editor editor=getSharedPreferences("waterAlarm", Context.MODE_PRIVATE).edit();
		 editor.putLong("boil_time", System.currentTimeMillis());
		 editor.commit();
		 
		String userid = MyApplication.user.getUserId();
		String deviceId = device.getId();
		String currDate = sdf.format(new Date());
		int currtime = Integer.valueOf(df.format(new Date()));
		initArrayOfTempC(temperature);
		initArrayPeriod(currtime);
			
		WaterLevel quality = new WaterLevel(userid, deviceId,kettleStatusInfo.getWaterQuality());
	     //quality=new WaterLevel(userid, deviceId,Math.random()*500);
		TimeQuantum timeQuan = new TimeQuantum(userid, deviceId, currDate, arr_period[0], arr_period[1],arr_period[2],arr_period[3], arr_period[4], arr_period[5], arr_period[6], arr_period[7],arr_period[8], arr_period[9], arr_period[10],arr_period[11]);
        for (int i = 0; i < arr_period.length; i++) {
				arr_period[i]=0;
		 }
        Temperature temper=new Temperature(userid, deviceId, currDate, arr_tempC[0],arr_tempC[1], arr_tempC[2], arr_tempC[3], arr_tempC[4],arr_tempC[5],arr_tempC[6],arr_tempC[7]);
		 for (int j = 0; j < arr_tempC.length; j++) {
				arr_tempC[j]=0;
		}
		storeDatasForAnalysis(temper, timeQuan,quality);
		
	}
}

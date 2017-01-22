package com.hipad.smarthome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.json.ErrorCode;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.kettle.v14.KettleStatusInfo.KettleState;
import com.hipad.smart.kettle.v14.OkBoilCmd;
import com.hipad.smart.kettle.v14.SpecificBoilCmd;
import com.hipad.smarthome.kettle.advanced.AdvancedSettingActivity;
import com.hipad.smarthome.kettle.dao.TempCDao;
import com.hipad.smarthome.kettle.dao.TimeQDao;
import com.hipad.smarthome.kettle.dao.WaterLevelDao;
import com.hipad.smarthome.kettle.statistics.entity.Temperature;
import com.hipad.smarthome.kettle.statistics.entity.TimeQuantum;
import com.hipad.smarthome.kettle.statistics.entity.WaterLevel;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.CanvasUtils;
import com.hipad.smarthome.utils.MyDialog;
import com.hipad.smarthome.utils.TargetTemperatureChooser;
import com.hipad.smarthome.utils.TargetTemperatureChooser.TempChosenListener;
import com.hipad.smarthome.utils.TimeSetPopWindow;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;
import com.hipad.smarthome.adapter.Scene;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

/**
 * 
 * @author guowei
 *
 */
public class KettleDevActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private LinearLayout layoutTab, layoutInfo, layoutTime, layoutButton,
			layoutStop;
	private TextView title, waterLevel, temperature, workStateTv, tipsTv,
			sceneNameTv, sceneTempTv, sceneWarmPeroid, warmTv, temp;

	private ImageView statusImg, imageTemp, imageTds, imgTips, sceneIcon,
			appointIcon, imgTitleSign;
	private View notifyView;
	private ImageButton backBtn, boilBtn, keepwarmBtn, stopBtn;
	private RadioGroup radioGroup;
	private RadioButton rdbtnHobby, rdbtnScene, rdbtnAppoint, rdbtnAdvanceed;

	private AnimationDrawable statusDrawable;
	private String titleStr;

	private CommonDevice device;
	private KettleStatusInfo kettleStatusInfo;

	private GetHandler getHandler;
	private Dialog dialog = null;
	private TargetTemperatureChooser mTmpch;
	private TimeSetPopWindow mTimeSetPopWindow;
	private AlphaAnimation alp = new AlphaAnimation(1.0f, 0.0f);
	public final static String EXTRA_TARGET_TEMP = "temp";
	public final static String EXTRA_KEEP_TEMP_PERIOD = "keep_temp_period";
	public final static String EXTRA_MENU = "menu";

	private SceneInfo mTargetScene = new SceneInfo();
	private SceneInfo mCustomScene = new SceneInfo();

	private static int currentTempC = 0;
	private static int countOfException = 0;

	private final static int SUCCESS_FLAG = 0x00;
	private final static int MSG_UPDATE = 0x01;
	private final static int MSG_TEMP = 0x02;
	private final static int MSG_TDS = 0x03;
	private final static int REQUEST_CODE_FOR_SCENE = 0x04;

	private WaterLevel quality = new WaterLevel();
	private WaterLevelDao levelDao = null;
	private Temperature temper = new Temperature();
	private TempCDao tempCDao = null;
	private TimeQuantum timeQuan = new TimeQuantum();
	private TimeQDao timeQDao = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat df = new SimpleDateFormat("HH");
	double[] arr_tempC = new double[8];
	double[] arr_period = new double[12];

	

	static class SceneInfo implements Serializable {
		/** temperature */
		int temperature = -1;
		/** minutes */
		int period;
		/** tow levels menu */
		byte[] menu = new byte[2];
		int imgResId;
		int nameResId;

		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			// write 'this' to 'out'...
			out.writeInt(temperature);
			out.writeInt(period);
			out.write(menu);
			out.writeInt(imgResId);
			out.writeInt(nameResId);
		}

		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			// populate the fields of 'this' from the data in 'in'...
			temperature = in.readInt();
			period = in.readInt();
			menu = new byte[2];
			in.read(menu);
			imgResId = in.readInt();
			nameResId = in.readInt();

		}

	}

	public void storeDatasForAnalysis(Temperature temp, TimeQuantum time,
			WaterLevel level) {
		tempCDao.insertTempC(temp);
		timeQDao.insertTimes(time);
		levelDao.insertWaterLevel(level);

	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case SUCCESS_FLAG:
				dialogDismiss();
				break;
			case MSG_UPDATE:

				device.getDeviceInfo(getHandler);

				if (kettleStatusInfo != null
						&& kettleStatusInfo.getState() == KettleState.STATE_HEATING
						&& currentTempC > 95) {
					handler.sendEmptyMessageDelayed(MSG_UPDATE, 1000 * 1);
				} else {
					handler.sendEmptyMessageDelayed(MSG_UPDATE, 1000 * 5);
				}

				break;

			case MSG_TEMP:
				if (msg.arg1 <= 100 && msg.arg1 >= 0) {
					CanvasUtils.getProgressBitmap(imageTemp, getResources(),
							R.drawable.doughnutchart_temp, msg.arg1, 2.7f);
				}
				break;
			case MSG_TDS:
				float progress = 0f;
				if (msg.arg2 <= 200) {
					progress = 67.5f;
				} else if (msg.arg2 < 500) {
					progress = 135f;
				} else if (msg.arg2 < 1000) {
					progress = 202.5f;
				} else {
					progress = 270f;
				}

				CanvasUtils.getProgressBitmap(imageTds, getResources(),
						R.drawable.doughnutchart_tds, progress, 1f);
				break;
			default:
				break;
			}

		};
	};

	public void saveSceneInfo() {

		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(getCacheDir()
					+ File.separator + MyApplication.user.getName() + "_"
					+ device.getDeviceId());
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

	public void restoreSceneInfo() {
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(getCacheDir()
					+ File.separator + MyApplication.user.getName() + "_"
					+ device.getDeviceId());
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kettle_layout4);
		mContext = this;

		levelDao = new WaterLevelDao(mContext);
		tempCDao = new TempCDao(mContext);
		timeQDao = new TimeQDao(mContext);

		Bundle bundle = getIntent().getExtras();
		titleStr = bundle.getString("title");
		device = (CommonDevice) bundle.get("device");
		dialogShow(this, null);
		mTmpch = new TargetTemperatureChooser(this);
		mTimeSetPopWindow = new TimeSetPopWindow(this);
		getHandler = new GetHandler();

		dialog = new MyDialog(this, R.style.MyDialog,
				new MyDialog.OnDialogListener() {

					@Override
					public void OnClick(View v) {
						switch (v.getId()) {
						case R.id.dialog_button_ok:
							layoutInfo.setVisibility(View.INVISIBLE);
							SpecificBoilCmd cmd = new SpecificBoilCmd();
							cmd.boil(false);
							device.sendCmd(cmd, cmdHandler);
							dialogShow(mContext, null);
							break;

						default:
							break;
						}

					}
				});

		getView();
		init();
		setOnClickListener();

		restoreSceneInfo();
	}

	@Override
	protected void onStart() {
		AppointMentDataBase db = AppointMentDataBase.getInstance(this);
		boolean isAppoint = db.hasEffectiveAppoint(MyApplication.user.getUserId(),device.getDeviceId());
		if (isAppoint) {
			appointIcon.setVisibility(View.VISIBLE);
		} else {
			appointIcon.setVisibility(View.INVISIBLE);
		}

		handler.sendEmptyMessage(MSG_UPDATE);
		super.onStart();
	}

	@Override
	protected void onPause() {
		alp = null;
		super.onPause();
	}

	@Override
	protected void onStop() {
		handler.removeMessages(MSG_UPDATE);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		saveSceneInfo();

		super.onDestroy();
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

	private void getView() {

		layoutTab = (LinearLayout) findViewById(R.id.layout_tab);
		layoutInfo = (LinearLayout) findViewById(R.id.layout_info);
		layoutTime = (LinearLayout) findViewById(R.id.linearlayout_time);
		layoutButton = (LinearLayout) findViewById(R.id.layout_btn);
		layoutStop = (LinearLayout) findViewById(R.id.layout_stop);
		title = (TextView) findViewById(R.id.titleTxt);
		imgTitleSign = (ImageView) findViewById(R.id.titleSign);

		warmTv = (TextView) findViewById(R.id.tv_warm);
		tipsTv = (TextView) findViewById(R.id.kettle_tv_prompt);
		workStateTv = (TextView) findViewById(R.id.kettle_tv_workstate);
		waterLevel = (TextView) findViewById(R.id.water_level);
		temperature = (TextView) findViewById(R.id.water_temp);
		sceneNameTv = (TextView) findViewById(R.id.kettle_tv_scene_name);
		sceneTempTv = (TextView) findViewById(R.id.kettle_tv_scene_temp);
		sceneWarmPeroid = (TextView) findViewById(R.id.kettle_tv_scene_keepwarmperoid);
		temp = (TextView) findViewById(R.id.temp_icon);
		statusImg = (ImageView) findViewById(R.id.statusImg);
		imageTemp = (ImageView) findViewById(R.id.image_Temp);
		imageTds = (ImageView) findViewById(R.id.image_Tds);
		imgTips = (ImageView) findViewById(R.id.kettle_imgv_tips);
		sceneIcon = (ImageView) findViewById(R.id.kettle_imgv_scene_icon);
		appointIcon = (ImageView) findViewById(R.id.kettle_imgv_appointsign);

		notifyView = findViewById(R.id.kettle_rl_notify);

		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		boilBtn = (ImageButton) findViewById(R.id.boilBtn);
		keepwarmBtn = (ImageButton) findViewById(R.id.keepwarmBtn);
		stopBtn = (ImageButton) findViewById(R.id.stopBtn);

		radioGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		rdbtnHobby = (RadioButton) findViewById(R.id.main_tab_hobby);
		rdbtnScene = (RadioButton) findViewById(R.id.main_tab_scene);
		rdbtnAppoint = (RadioButton) findViewById(R.id.main_tab_appoint);
		rdbtnAdvanceed = (RadioButton) findViewById(R.id.main_tab_advance);

	}

	private void setOnClickListener() {

		backBtn.setOnClickListener(this);
		boilBtn.setOnClickListener(this);
		keepwarmBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		rdbtnAdvanceed.setOnClickListener(this);
		rdbtnAppoint.setOnClickListener(this);
		rdbtnHobby.setOnClickListener(this);
		rdbtnScene.setOnClickListener(this);

		mTmpch.setTempChoseListener(new TempChosenListener() {

			@Override
			public void onTempChosen(int temp) {

				mCustomScene.imgResId = R.drawable.scene_0_150x150;
				mCustomScene.nameResId = R.string.scene_0;
				mCustomScene.temperature = temp;
				mTimeSetPopWindow.showPopupWindow();

			}
		});
		mTimeSetPopWindow.setTimeChoseListener(new TimeChoseListener() {

			@Override
			public void onTimeChoose(int minute) {

				mCustomScene.period = minute;
				// SpecificBoilCmd cmd = new SpecificBoilCmd();
				// cmd.boil(true);
				OkBoilCmd cmd = new OkBoilCmd();
				cmd.setFunc(KettleStatusInfo.FUNC_KEEP_TEMPC);
				// click keep warm button,set the value of time and temperature
				cmd.setTempCToBeKept(mCustomScene.temperature);
				cmd.setMinutesToKeepTempC(mCustomScene.period);
				cmd.syncMenu(0x00, 0x01);

				device.sendCmd(cmd, cmdHandler);
				layoutTab.setVisibility(View.INVISIBLE);
				//记录水壶的水质，用水时间段和用水温度
				storeDatas(mCustomScene.temperature);
				dialogShow(KettleDevActivity.this, null);

			}
		});

	}

	private void init() {

		statusDrawable = (AnimationDrawable) statusImg.getBackground();
		title.setText(titleStr);

		// network sign
		imgTitleSign.setImageResource(R.drawable.network_remote);
	}

	private boolean isNormalWork() {
		if (null == kettleStatusInfo)
			return false;
		int state = kettleStatusInfo.getState();
		if (state == KettleState.NOTIFY_HUNG
				|| state == KettleState.WARN_NO_WATER
				|| state == KettleState.WARN_BAD_WATER
				|| state == KettleState.ERROR_NTC)

			return false;

		return true;
	}

	// 水壶不正常工作状态下的提示
	private void notifyUnNormal() {
		int state = kettleStatusInfo.getState();

		if (state == KettleState.NOTIFY_HUNG) {
			showNotifyDialog(mContext, getString(R.string.kettle_hint_hungup));
		} else if (state == KettleState.WARN_BAD_WATER) {
			showNotifyDialog(mContext,
					getString(R.string.kettle_hint_bad_water));

		} else if (state == KettleState.ERROR_NTC) {

			showNotifyDialog(mContext, getString(R.string.kettle_hint_ntc));
		}

	}

	@Override
	public void onClick(View v) {

		int typeId = v.getId();
		switch (typeId) {

		case R.id.leftBtn:
			finish();
			break;
		case R.id.boilBtn: {

			if (isNormalWork()) {


				OkBoilCmd cmd = new OkBoilCmd();
				// SpecificBoilCmd cmd = new SpecificBoilCmd();
				// cmd.boil(true);
				cmd.setFunc(KettleStatusInfo.FUNC_BOIL);
				cmd.syncMenu(0x00, 0x02);
				device.sendCmd(cmd, cmdHandler);
				layoutTab.setVisibility(View.INVISIBLE);
				
				 //记录水壶的水质，用水时间段和用水温度
				 
                storeDatas(100);
				
				dialogShow(this, null);
			} else {
				notifyUnNormal();
			}

			break;
		}
		case R.id.keepwarmBtn:
			if (isNormalWork()) {
				mTmpch.show();
			} else {
				notifyUnNormal();
			}
			break;
		case R.id.stopBtn: {

			if (isNormalWork()) {
				layoutInfo.setVisibility(View.INVISIBLE);

				SpecificBoilCmd cmd = new SpecificBoilCmd();
				cmd.boil(false);

				device.sendCmd(cmd, cmdHandler);
				dialogShow(this, null);
			} else {
				notifyUnNormal();
			}

			break;
		}
		case R.id.main_tab_hobby:

			if (isNormalWork()) {

				if (-1 == mTargetScene.temperature) {
					rdbtnScene.setChecked(true);

					Intent intent = new Intent(this,
							SceneLayerOneActivity.class);
					startActivityForResult(intent, REQUEST_CODE_FOR_SCENE);
					return;
				}

				SpecificBoilCmd cmd = new SpecificBoilCmd();
				cmd.boil(true);

				cmd.setTempCToBeKept(mTargetScene.temperature);
				cmd.setMinutesToKeepTempC(mTargetScene.period);
				// 添加场景菜单项
				cmd.syncMenu(mTargetScene.menu[0], mTargetScene.menu[1]);

				device.sendCmd(cmd, cmdHandler);
				layoutTab.setVisibility(View.INVISIBLE);
				 
				//记录水壶的水质，用水时间段和用水温度
				storeDatas(mTargetScene.temperature);
				
				
				dialogShow(this, null);
			} else {
				notifyUnNormal();
			}

			break;
		case R.id.main_tab_scene:

			if (isNormalWork()) {
				Intent intent = new Intent(this, SceneLayerOneActivity.class);
				startActivityForResult(intent, REQUEST_CODE_FOR_SCENE);
			} else {
				notifyUnNormal();
			}

			break;
		case R.id.main_tab_appoint:

			if (kettleStatusInfo.getState() == KettleState.ERROR_NTC) {
				showToastShort("水壶出现严重错误，请断电后重新上电!");
			} else {
				Bundle bundle = new Bundle();
				bundle.putParcelable(CommonDevice.EXTRA_DEVICE, device);
				Intent intent = new Intent();
				intent.setClass(KettleDevActivity.this,
						AppointMentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}

			break;
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

	private void updateWaterTemp(final int tmp) {

		temperature.setText(String.valueOf(tmp));
		if (tmp >= 0 && tmp <= 30) {
			temperature.setTextColor(Color.rgb(37, 183, 188));
			temp.setTextColor(Color.rgb(37, 183, 188));
		} else if (tmp <= 70) {
			temperature.setTextColor(Color.rgb(255, 188, 0));
			temp.setTextColor(Color.rgb(255, 188, 0));
		} else {
			temperature.setTextColor(Color.rgb(229, 0, 17));
			temp.setTextColor(Color.rgb(229, 0, 17));
		}

		Message msg = new Message();
		msg.what = MSG_TEMP;
		msg.arg1 = tmp;
		handler.sendMessage(msg);

	}

	private void checkWarningAndError(KettleStatusInfo kettleStatusInfo) {

		if (dialog != null
				&& kettleStatusInfo.getState() == KettleState.WARN_NO_WATER) {
			dialog.show();
		} else {
			dialog.dismiss();
		}
		String info = "";
		switch (kettleStatusInfo.getState()) {
		case KettleState.NOTIFY_HUNG:
			info = "水壶提起";
			temperature.setText("--");
			break;
		case KettleState.WARN_NO_WATER:
			info = "水壶没水";
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
		tipsTv.setText(info);
		tipsTv.setTextColor(Color.YELLOW);
		imgTips.setImageResource(R.drawable.ico_alert2);
		notifyView.setVisibility(View.VISIBLE);

	}

	private void notify(KettleStatusInfo kettleStatusInfo) {
		if (kettleStatusInfo.getWorkedTime() > 200) {

			if (alp == null) {
				return;
			}
			imgTips.setImageResource(R.drawable.ico_alert1);
			tipsTv.setText("水壶使用时间较长，请清洗水壶！");
			alp.setDuration(5000);
			notifyView.setAnimation(alp);
			alp.setAnimationListener(new AnimationListener() {
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

	private void updateWaterLevel(final int tds) {

		String waterStr = "优秀";
		int color = Color.rgb(111, 203, 125);
		if (tds * 10 <= 200) {
			waterStr = "优秀";
			color = Color.rgb(111, 203, 125);
		} else if (tds * 10 <= 500) {
			waterStr = "良好";
			color = Color.rgb(37, 183, 188);
		} else if (tds * 10 <= 1000) {
			waterStr = "较差";
			color = Color.rgb(255, 188, 0);
		} else {
			waterStr = "低劣";
			color = Color.rgb(229, 0, 17);
		}

		waterLevel.setTextColor(color);
		waterLevel.setText(waterStr);

		Message msg = new Message();
		msg.what = MSG_TDS;
		msg.arg2 = tds * 10;
		handler.sendMessage(msg);

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

	@SuppressWarnings("static-access")
	private SceneInfo getSceneInfo() {
		SceneInfo info = new SceneInfo();
		byte[] menu = kettleStatusInfo.getMenu();

		Scene scene = Scene.getSceneByCmd(menu);

		if (scene.getSceneCmd()[0] == 0x00) {
			info.menu = scene.getSceneCmd();
			info.imgResId = R.drawable.scene_0_150x150;
			info.nameResId = R.string.scene_0;
			if (scene.getSceneCmd()[1] == 0x02) {
				// click boil button
				info.temperature = 100;

			} else {
				// click keep warm button
				info.temperature = kettleStatusInfo.getTempCToBeKept();
				info.period = kettleStatusInfo.getRemainOfTimeToKeepTempC();
			}

		} else {
			info.temperature = scene.getWarmTemperatureC();
			info.menu = scene.getSceneCmd();
			info.period = kettleStatusInfo.getRemainOfTimeToKeepTempC();// 保温剩余时间
			info.imgResId = scene.getSceneImgResId(this, scene,
					R.string.img_suffix_150x150);
			info.nameResId = scene.getSceneTitleResId(this, scene);

		}

		return info;

	}

	/**
	 * update UI
	 * 
	 * @param kettleStatusInfo
	 */
	private void updateUi(KettleStatusInfo kettleStatusInfo) {

		checkWarningAndError(kettleStatusInfo);
		updateWaterTemp(kettleStatusInfo.getCurrentTemperature());
		updateWaterLevel(kettleStatusInfo.getWaterQuality());

		int state = kettleStatusInfo.getState();

		switch (state) {
		/*
		 * 待机状态
		 */
		case KettleState.STATE_STANDBY:
			layoutInfo.setVisibility(View.GONE);
			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);
			notify(kettleStatusInfo);
			setWorkIndicator(false);
			workStateTv.setText("空闲");
			layoutTab.setVisibility(View.VISIBLE);

			break;
		/*
		 * 煮水状态
		 */
		case KettleState.STATE_HEATING: {


			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);

			SceneInfo info = getSceneInfo();
			if (info.menu[0] == 0x00) {
				if (info.menu[1] == 0x02) {
					layoutTime.setVisibility(View.GONE);
					layoutInfo.setVisibility(View.VISIBLE);
					sceneIcon.setImageResource(info.imgResId);
					sceneNameTv.setText(getString(info.nameResId));
					sceneTempTv.setText(info.temperature + "℃");
				} else {
					layoutInfo.setVisibility(View.VISIBLE);
					sceneIcon.setImageResource(info.imgResId);
					sceneNameTv.setText(getString(info.nameResId));
					sceneTempTv.setText(info.temperature + "℃");
					layoutTime.setVisibility(View.VISIBLE);
					sceneWarmPeroid.setText(info.period + "分钟");
				}

			} else {

				layoutInfo.setVisibility(View.VISIBLE);
				sceneIcon.setImageResource(info.imgResId);
				sceneTempTv.setText(info.temperature + "℃");
				sceneNameTv.setText(getString(info.nameResId));
				layoutTime.setVisibility(View.VISIBLE);
				sceneWarmPeroid.setText(info.period + "分钟");
			}

			workStateTv.setText("煮水中");
			setWorkIndicator(true);
			layoutTab.setVisibility(View.INVISIBLE);

			break;
		}
		/*
		 * 冷却状态
		 */
		case KettleState.STATE_BOILED_TO_KEEP_TEMPC: {
			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);
			setWorkIndicator(false);
			SceneInfo info = getSceneInfo();
			layoutInfo.setVisibility(View.VISIBLE);
			sceneIcon.setImageResource(info.imgResId);
			sceneNameTv.setText(getString(info.nameResId));
			sceneTempTv.setText(info.temperature + "℃");
			layoutTime.setVisibility(View.VISIBLE);
			sceneWarmPeroid.setText(info.period + "分钟");

			workStateTv.setText("冷却");
			layoutTab.setVisibility(View.INVISIBLE);
			break;
		}
		/*
		 * 保温状态
		 */
		case KettleState.STATE_KEEP_TEMPC:


			boilBtn.setVisibility(View.GONE);
			keepwarmBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.VISIBLE);
			setWorkIndicator(false);

			SceneInfo info = getSceneInfo();
			layoutInfo.setVisibility(View.VISIBLE);
			sceneIcon.setImageResource(info.imgResId);
			sceneNameTv.setText(getString(info.nameResId));
			sceneTempTv.setText(info.temperature + "℃");
			layoutTime.setVisibility(View.VISIBLE);
			sceneWarmPeroid.setText(info.period + "分钟");

			workStateTv.setText("保温");
			layoutTab.setVisibility(View.INVISIBLE);
			break;
		/*
		 * 水壶被提起
		 */
		case KettleState.NOTIFY_HUNG:

			temperature.setText("--");
			waterLevel.setText("--");
			workStateTv.setText("暂停");
			setWorkIndicator(false);
			break;

		/*
		 * 水壶缺水
		 */
		case KettleState.WARN_NO_WATER:

			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);
			workStateTv.setText("空闲");
			temperature.setText("--");
			waterLevel.setText("--");
			setWorkIndicator(false);
			layoutTab.setVisibility(View.VISIBLE);
			break;
		/*
		 * 水壶NTC出错
		 */
		case KettleState.ERROR_NTC:
			boilBtn.setVisibility(View.VISIBLE);
			keepwarmBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.GONE);
			workStateTv.setText("严重故障");
			setWorkIndicator(false);
			layoutTab.setVisibility(View.VISIBLE);
			break;

		/*
		 * 水壶水质低劣
		 */
		case KettleState.WARN_BAD_WATER:

			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (RESULT_OK == resultCode) {
			if (REQUEST_CODE_FOR_SCENE == requestCode) {
				// fetch the scene data
				mTargetScene.temperature = data.getIntExtra(EXTRA_TARGET_TEMP,
						0);
				mTargetScene.period = data.getIntExtra(EXTRA_KEEP_TEMP_PERIOD,
						0);

				byte[] menu = data.getByteArrayExtra(EXTRA_MENU);

				mTargetScene.nameResId = getResources().getIdentifier(
						"scene_" + Scene.getIdBySceneCmd(menu), "string",
						this.getPackageName());
				mTargetScene.imgResId = getResources().getIdentifier(
						"scene_" + Scene.getIdBySceneCmd(menu) + "_"
								+ this.getString(R.string.img_suffix_150x150),
						"drawable", this.getPackageName());
				mTargetScene.menu = menu;

				rdbtnHobby.setText(getString(mTargetScene.nameResId));

				SpecificBoilCmd cmd = new SpecificBoilCmd();
				cmd.boil(true);
				cmd.setTempCToBeKept(mTargetScene.temperature);
				cmd.setMinutesToKeepTempC(mTargetScene.period);
				// 添加场景菜单项
				cmd.syncMenu(mTargetScene.menu[0], mTargetScene.menu[1]);
				device.sendCmd(cmd, cmdHandler);
				layoutTab.setVisibility(View.INVISIBLE);
				 
				// 记录水壶的水质，用水时间段和用水温度
				storeDatas(mTargetScene.temperature);
				dialogShow(this, null);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	private ResponseResultHandler<CmdResponse> cmdHandler = new ResponseResultHandler<CmdResponse>() {

		@Override
		public void handle(boolean timeout, CmdResponse response) {
			boolean successTag = false;
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						ResponseData data = (ResponseData) response.getData();
						if (data != null) {
							int errorCode = data.getErrorCode();
							showInfoLog("errorcode = " + data.getErrorCode());
							if (errorCode == ErrorCode.E_SUCCESS) {
								kettleStatusInfo = new KettleStatusInfo(
										data.getResponseBody());
								showInfoLog("水壶所有状态:"
										+ "\n当前状态:"
										+ kettleStatusInfo.getState()
										+ "\n功能描述:"
										+ kettleStatusInfo.getCurrFunc()
										+ "\n保温剩余时间:"
										+ kettleStatusInfo
												.getRemainOfTimeToKeepTempC()
										+ "\n保温温度:"
										+ kettleStatusInfo.getTempCToBeKept()
										+ "\n当前水温:"
										+ kettleStatusInfo
												.getCurrentTemperature()
										+ "\n水质TDS值数据:"
										+ kettleStatusInfo.getWaterQuality()
										+ "\n烧水总时长:"
										+ kettleStatusInfo.getWorkedTime()
										+ "\n烧水次数:"
										+ kettleStatusInfo.getBoiledTimes());

								updateUi(kettleStatusInfo);
								successTag = true;
							} else if (errorCode == ErrorCode.E_OFFLINE) {
								showToastShort("获取数据失败,设备已离线!");
							} else if (errorCode == ErrorCode.E_TIMEOUT) {
								showInfoLog("获取数据失败,请求超时!");
								showToastShort(getString(R.string.timeout_hint));
							} else {
								showToastShort("获取数据失败,错误代码：" + errorCode);
							}
						} else {
							showToastShort("获取的数据为空!");
						}
					} else {						
						String msgStr = response.getMsg();
						showInfoLog("msgStr = " + msgStr);
						
						if(msgStr.contains("登录已过期")){ // logout and re-login
							showToastShort(msgStr);
							startActivity(new Intent(mContext, LoginActivity.class));
						}else{
							showToastShort("获取数据失败," + msgStr);
						}
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}

			dialogDismiss();

			if (!successTag) {
				if (null != kettleStatusInfo) {
					updateUi(kettleStatusInfo);
				} else {
					layoutTab.setVisibility(View.VISIBLE);
				}
			}

		}

	};

	private class GetHandler implements
			ResponseResultHandler<QueryDeviceInfoResponse> {

		@Override
		public void handle(boolean timeout, QueryDeviceInfoResponse response) {
			boolean successTag = false;

			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						DeviceInfo data = (DeviceInfo) response.getData();
						if (data != null) {
							int errorCode = data.getErrorCode();
							showInfoLog("errorcode = " + data.getErrorCode());
							if (errorCode == ErrorCode.E_SUCCESS) {
								kettleStatusInfo = new KettleStatusInfo(
										data.getResponseBody());
								currentTempC = kettleStatusInfo
										.getCurrentTemperature();

								showInfoLog("水壶所有状态:"
										+ "\n当前状态:"
										+ kettleStatusInfo.getState()
										+ "\n功能描述:"
										+ kettleStatusInfo.getCurrFunc()
										+ "\n保温剩余时间:"
										+ kettleStatusInfo
												.getRemainOfTimeToKeepTempC()
										+ "\n保温温度:"
										+ kettleStatusInfo.getTempCToBeKept()
										+ "\n当前水温:"
										+ kettleStatusInfo
												.getCurrentTemperature()
										+ "\n水质TDS值数据:"
										+ kettleStatusInfo.getWaterQuality()
										+ "\n烧水总时长:"
										+ kettleStatusInfo.getWorkedTime()
										+ "\n烧水次数:"
										+ kettleStatusInfo.getBoiledTimes());

								updateUi(kettleStatusInfo);
								successTag = true;
								countOfException = 0;
								Message msg = new Message();
								msg.what = SUCCESS_FLAG;
								handler.sendMessage(msg);

							} else if (errorCode == ErrorCode.E_OFFLINE) {
								// showToastShort("获取数据失败,设备已离线!");
								countOfException++;

							} else if (errorCode == ErrorCode.E_TIMEOUT) {
								showInfoLog("获取数据失败,请求超时!");
								// showToastShort(getString(R.string.timeout_hint));
								countOfException++;

							} else {
								// showToastShort("获取数据失败,错误代码：" + errorCode);
								countOfException++;
							}
						} else {
							// showToastShort("获取的数据为空!");
							countOfException++;
						}
					} else {
						String msgStr = response.getMsg();
						showInfoLog("msgStr = " + msgStr);
						if(msgStr.contains("登录已过期")){ // logout and re-login
							showToastShort(msgStr);
							startActivity(new Intent(mContext, LoginActivity.class));
						}else{							
//							 showToastShort("获取数据失败," + msgStr);
						}
						countOfException++;

					}
				} else {
					// showToastShort(getString(R.string.neterror_hint));
					countOfException++;

				}
			} else {
				// showToastShort(getString(R.string.timeout_hint));
				countOfException++;

			}

			if (countOfException > 3) {
				showToastLong("云端通讯故障！");
				countOfException = 0;
				finish();
			}

			if (!successTag) {
				if (null != kettleStatusInfo) {
					updateUi(kettleStatusInfo);
				} else {
					layoutTab.setVisibility(View.VISIBLE);
				}
			}

		}
	}

	
	private void storeDatas(double temperature){
		 Editor editor=getSharedPreferences("waterAlarm", Context.MODE_PRIVATE).edit();
		 editor.putLong("boil_time", System.currentTimeMillis());
		 editor.commit();
		 String userid=MyApplication.user.getUserId();
         String deviceId=device.getDeviceId();
		 String currDate=sdf.format(new Date());
		 int currtime=Integer.valueOf(df.format(new Date()));
		 initArrayOfTempC(temperature);
		 initArrayPeriod(currtime);
			
		 quality=new WaterLevel(userid, deviceId,kettleStatusInfo.getWaterQuality());
			//quality=new WaterLevel(userid, deviceId,Math.random()*500);
		 timeQuan=new TimeQuantum(userid, deviceId, currDate, arr_period[0], arr_period[1],arr_period[2],arr_period[3], arr_period[4], arr_period[5], arr_period[6], arr_period[7],arr_period[8], arr_period[9], arr_period[10],arr_period[11]);
         for (int i = 0; i < arr_period.length; i++) {
				arr_period[i]=0;
		 }
		 temper=new Temperature(userid, deviceId, currDate, arr_tempC[0],arr_tempC[1], arr_tempC[2], arr_tempC[3], arr_tempC[4],arr_tempC[5],arr_tempC[6],arr_tempC[7]);
		 for (int j = 0; j < arr_tempC.length; j++) {
				arr_tempC[j]=0;
		}
		storeDatasForAnalysis(temper, timeQuan,quality);
		
	}
	
}

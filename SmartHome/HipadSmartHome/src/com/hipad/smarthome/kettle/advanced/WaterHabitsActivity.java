package com.hipad.smarthome.kettle.advanced;

import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.service.DeviceController;
import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;
import com.hipad.smarthome.chart.Chart;
import com.hipad.smarthome.chart.TemperatureAnalysisChart;
import com.hipad.smarthome.chart.UsageAnalysisChart;
import com.hipad.smarthome.kettle.dao.TempCDao;
import com.hipad.smarthome.kettle.dao.TimeQDao;
import com.hipad.smarthome.kettle.statistics.entity.Temperature;
import com.hipad.smarthome.kettle.statistics.entity.TimeQuantum;

/**
 * @author EthanChung
 */
public class WaterHabitsActivity extends BaseActivity implements IFunction,
		OnClickListener {

	public static final String MYACTION = "WaterHabitsActivity_action";
	
	private boolean isDebug = false;
	
	private ImageButton backBtn;
	private Button usageBtn, tempBtn;
	private TextView chartTitle;
	private RelativeLayout replacedView;
	private boolean isUsageAnalysisMode = true;
	private RelativeLayout.LayoutParams layoutParams;

	private Device localDevice = null;
	private CommonDevice cloudDevice = null;
	private DeviceController controller;
	String deviceId = null;
	Context mContext;

	TempCDao tempDao = null;
	TimeQDao timeDao = null;

	String usageXTitle = "时间段 (小时)";
	String tempXTitle = "温度范围 (℃)";
	String yTitle = "次数";
	String unitTitle = "使用率";
	String[] usageBlocks = {"0-2", "2-4", "4-6", "6-8", "8-10", "10-12", "12-14", "14-16", "16-18", "18-20", "20-22", "22-24"};
	String[] tempBlocks = {"30-40°","40-50°","50-60°","60-70°","70-80°","80-90°","90-100°","100°"};
	double[] usageData,TempData;
	Chart chart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_raw_layout);
		mContext = this;
		tempDao = new TempCDao(mContext);
		timeDao = new TimeQDao(mContext);
		controller = new DeviceController();
		initViews();
		init();
	}

	private void initViews() {

		if(!isDebug){
			if (getIntent().getParcelableExtra("device") instanceof Device) {
				localDevice = getIntent().getParcelableExtra("device");
				deviceId = localDevice.getId();
				controller.init(localDevice);
			} else {
				cloudDevice = getIntent().getParcelableExtra("device");
				deviceId = cloudDevice.getDeviceId();
			}
		}

		String titleStr = getIntent().getStringExtra("title");
		((TextView) findViewById(R.id.titleTxt)).setText(titleStr);
		chartTitle = (TextView) findViewById(R.id.chart_title);
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(this);
		usageBtn = (Button) findViewById(R.id.usage_chart_btn);
		usageBtn.setOnClickListener(this);
		tempBtn = (Button) findViewById(R.id.temp_chart_btn);
		tempBtn.setOnClickListener(this);

		layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(28, 0, 0, 5);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		replacedView = (RelativeLayout) findViewById(R.id.achart_relativeLayout);
	}

	private double[] queryTempCDatasFromDatabase(String userId, String deviceid) {

		double[] arr_temp = new double[8];
		List<Temperature> list = tempDao.obtainTemperatureList(userId, deviceid);

		for (Temperature temperature : list) {
			arr_temp[0] += temperature.getTempc_1();
			arr_temp[1] += temperature.getTempc_2();
			arr_temp[2] += temperature.getTempc_3();
			arr_temp[3] += temperature.getTempc_4();
			arr_temp[4] += temperature.getTempc_5();
			arr_temp[5] += temperature.getTempc_6();
			arr_temp[6] += temperature.getTempc_7();
			arr_temp[7] += temperature.getTempc_8();
		}
		return arr_temp;
	}

	private double[] queryPeriodDatasFromDatabase(String userId, String deviceId) {

		double[] arr_period = new double[12];
		List<TimeQuantum> list = timeDao.obtainTimeQuantumList(userId, deviceId);

		for (TimeQuantum timeQuantum : list) {
			arr_period[0] += timeQuantum.getPeriod_1();
			arr_period[1] += timeQuantum.getPeriod_2();
			arr_period[2] += timeQuantum.getPeriod_3();
			arr_period[3] += timeQuantum.getPeriod_4();
			arr_period[4] += timeQuantum.getPeriod_5();
			arr_period[5] += timeQuantum.getPeriod_6();
			arr_period[6] += timeQuantum.getPeriod_7();
			arr_period[7] += timeQuantum.getPeriod_8();
			arr_period[8] += timeQuantum.getPeriod_9();
			arr_period[9] += timeQuantum.getPeriod_10();
			arr_period[10] += timeQuantum.getPeriod_11();
			arr_period[11] += timeQuantum.getPeriod_12();
		}

		return arr_period;
	}

	public void init() {

		//for test
		if(isDebug){
			usageData = new double[] {1, 1, 2, 5, 5, 20, 10, 10, 34, 10, 5, 3};
			TempData = new double[] {10, 5, 7, 36, 12, 5, 7, 7};
		
		}else{
			usageData = queryPeriodDatasFromDatabase(MyApplication.user.getUserId(), deviceId);
			TempData = queryTempCDatasFromDatabase(MyApplication.user.getUserId(), deviceId);
		}

		if (isUsageAnalysisMode) {

			chart = new UsageAnalysisChart(this);
			replacedView.addView(chart.getView(usageBlocks, usageData, usageXTitle, yTitle, unitTitle));
			if (chart.getMaxXTitle() != null && chart.getMaxYValue()!=0)
				((TextView) findViewById(R.id.chartTitleText)).setText(this
						.getString(R.string.usage_analysis_notify_text)
						+ chart.getMaxXTitle()+ "时");
			else
				((TextView) findViewById(R.id.chartTitleText)).setText(this
						.getString(R.string.analysis_no_data_text));
			
		} else {
			chart = new TemperatureAnalysisChart(this);
			replacedView.addView(chart.getView(tempBlocks, TempData, tempXTitle, yTitle, unitTitle));
			if (chart.getMaxXTitle() != null && chart.getMaxYValue()!=0)
				((TextView) findViewById(R.id.chartTitleText)).setText(this
						.getString(R.string.temperature_analysis_notify_text)
						+ chart.getMaxXTitle());
			else
				((TextView) findViewById(R.id.chartTitleText)).setText(this
						.getString(R.string.analysis_no_data_text));
			
		}
	}

	@Override
	public String getName() {
		return "用水习惯";
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
		case R.id.leftBtn:
			finish();
			break;

		case R.id.usage_chart_btn:
			if (isUsageAnalysisMode)
				return;
			isUsageAnalysisMode = true;

			chartTitle.setText(R.string.usage_chart);
			usageBtn.setBackgroundResource(R.drawable.chart_left_btn_pressed);
			usageBtn.setTextColor(getResources().getColor(R.color.text_default));
			tempBtn.setBackgroundResource(R.drawable.chart_right_btn_default);
			tempBtn.setTextColor(getResources().getColor(R.color.waterHabitBtn));
			replacedView.removeAllViews();
			init();
			break;

		case R.id.temp_chart_btn:
			if (!isUsageAnalysisMode)
				return;
			isUsageAnalysisMode = false;
			chartTitle.setText(R.string.temp_chart);
			tempBtn.setBackgroundResource(R.drawable.chart_right_btn_pressed);
			tempBtn.setTextColor(getResources().getColor(R.color.text_default));
			usageBtn.setBackgroundResource(R.drawable.chart_left_btn_default);
			usageBtn.setTextColor(getResources()
					.getColor(R.color.waterHabitBtn));
			replacedView.removeAllViews();
			init();
			break;
		}
	}

	@Override
	public boolean isForResult() {
		return false;
	}
}
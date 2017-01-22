package com.hipad.tracker;

import java.util.Calendar;

import com.hipad.tracker.chart.Chart;
import com.hipad.tracker.chart.ChartView;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.StepResponse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StepCountActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private ImageButton leftBtn;
	private TextView titleText;
	private TextView stepNumber;
	private TextView dailyAverage;
	private TextView todayDistence;
	private TextView dailyAvgDistence;
	private TextView today_time, today_time_Distence;
	private RelativeLayout replacedView, replacedView2;
	private Button Day;
	private Button Week;
	private Button Month;
	
	private StepHandler sHandler;
	
	private String XTitle = "";
	private String yTitle = "";
	private String unitTitle = "";
	private String[] DailyBlock = { "","12 AM","","", "", "", "", "","","","","","", "12 PM","","","","","","","", "", "", "","" };
	private String[] WeekBlock = { "","Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
	private String[] MonthBlock = {"","1", "2", "", "", "", "", "", "", "", "10","", "", "", "", "", "", "", "", "", "20", "", "", "", "","", "", "" ,"28","","",""};
	private Chart chart_top, chart_bottom;
	
	Calendar c = Calendar.getInstance();
	int hour = c.get(Calendar.HOUR_OF_DAY);
	int minute = c.get(Calendar.MINUTE);
	
	
	private int DAY_STEP_MAX = 2000;
	private int DAY_DISTENCE_MAX = 250;
	private int WEEK_STEP_MAX = 10000;
	private int WEEK_DISTENCE_MAX = 1000;
	private int MONTH_STEP_MAX = 10000;
	private int MONTH_DISTENCE_MAX = 1000;
	
	public static final int UI_DAY=0x01;
    public static final int UI_WEEK=0x02;
    public static final int UI_MONTH=0x03;
    
    double[] xStepData = new double[25]; 
	double[] xDistanceData = new double[25] ;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UI_DAY: {
				Bundle bundle = msg.getData();
				double[] dailyStepData = bundle.getDoubleArray("DailyStepData");
				double[] dailyDistenceData = bundle.getDoubleArray("DailyDistanceData");
				double distanceTotal = bundle.getDouble("distanceTotal");
				updateUI(dailyStepData, dailyDistenceData, distanceTotal);
				replacedView.addView(chart_top.getView(DailyBlock, dailyStepData,XTitle, yTitle, unitTitle, DAY_STEP_MAX));
				replacedView2.addView(chart_bottom.getView(DailyBlock,dailyDistenceData, XTitle, yTitle, unitTitle,	DAY_DISTENCE_MAX));
				break;
			}
			case UI_WEEK: {
				Bundle bundle = msg.getData();
				double[] weekStepData = bundle.getDoubleArray("WeekStepData");
				double[] weekDistenceData = bundle.getDoubleArray("WeekDistanceData");
				double distanceTotal = bundle.getDouble("distanceTotal");
				updateUI(weekStepData, weekDistenceData, distanceTotal);
				replacedView.addView(chart_top.getView(WeekBlock, weekStepData,XTitle, yTitle, unitTitle, WEEK_STEP_MAX));
				replacedView2.addView(chart_bottom.getView(WeekBlock,weekDistenceData, XTitle, yTitle, unitTitle,WEEK_DISTENCE_MAX));
				break;
			}
			case UI_MONTH:{
				Bundle bundle = msg.getData();
				double[] monthStepData = bundle.getDoubleArray("MonthStepData");
				double[] monthDistenceData = bundle.getDoubleArray("MonthDistanceData");
				double distanceTotal = bundle.getDouble("distanceTotal");
				updateUI(monthStepData, monthDistenceData, distanceTotal);
				replacedView.addView(chart_top.getView(MonthBlock, monthStepData,XTitle, yTitle, unitTitle, MONTH_STEP_MAX));
				replacedView2.addView(chart_bottom.getView(MonthBlock,monthDistenceData, XTitle, yTitle, unitTitle,	MONTH_DISTENCE_MAX));
				break;
				}
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.step_count_layout);
		mContext=this;
		getViews();
		initParams();
		setClick();
	}
	
	private void initParams(){
		chart_top = new ChartView(this);
		chart_bottom = new ChartView(this);
		sHandler=new StepHandler();
		replacedView.addView(chart_top.getView(DailyBlock, xStepData,XTitle, yTitle, unitTitle, DAY_STEP_MAX));
		replacedView2.addView(chart_bottom.getView(DailyBlock, xDistanceData,XTitle, yTitle, unitTitle, DAY_DISTENCE_MAX));
		service.getSteps(MyApplication.account, MyApplication.imei, "1", sHandler);
	}
	
	
	private void getViews(){
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.VISIBLE);
		
		titleText = (TextView) findViewById(R.id.titleTxt);
		titleText.setText(getString(R.string.step_counter));
		
		stepNumber = (TextView) findViewById(R.id.Step_num);
		Day = (Button) findViewById(R.id.btn_day);
		Week = (Button) findViewById(R.id.btn_week);
		Month = (Button) findViewById(R.id.btn_Month);
		
		dailyAverage = (TextView) findViewById(R.id.DailyAverage);
		today_time = (TextView) findViewById(R.id.today_time);
		replacedView = (RelativeLayout) findViewById(R.id.chaw_top_day_table);
		
		todayDistence = (TextView) findViewById(R.id.Today_Distence);
		dailyAvgDistence = (TextView) findViewById(R.id.DailyAverage_Distence);
		today_time_Distence = (TextView) findViewById(R.id.today_time_Distence);
		replacedView2 = (RelativeLayout) findViewById(R.id.chaw_bottom_day_table);
	}

	private void setClick(){
		leftBtn.setOnClickListener(this);
		Day.setOnClickListener(this);
		Week.setOnClickListener(this);
		Month.setOnClickListener(this);
	}
	
	private void updateUI(double[] steps,double[] distances,double distanceTotal ){
		double stepSum=0;
		for (int i = 0; i < steps.length; i++) {
			stepSum+=steps[i];
		}
		stepNumber.setText((int)stepSum+"");
		dailyAverage.setText("Daily Average:"+(int)(stepSum/(steps.length-1)));
		if (hour <= 12) {
			today_time.setText("Today:" + hour + "" + ":" + minute + "" + "AM");
			today_time_Distence.setText("Today:" + hour + "" + ":" + minute
					+ "" + "AM");
		} else {
			today_time.setText("Today:" + hour + "" + ":" + minute + "" + "PM");
			today_time_Distence.setText("Today:" + hour + "" + ":" + minute
					+ "" + "PM");
		}
	    
	    todayDistence.setText(distanceTotal+ "KM");
	    java.text.DecimalFormat   df=new java.text.DecimalFormat("#.##"); 
        dailyAvgDistence.setText("Daily Average:"+df.format(distanceTotal/(distances.length-1)));   
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.btn_day:
			Day.setBackgroundResource(R.drawable.tab_left_h);
			Day.setTextColor(Color.WHITE);
			Week.setBackgroundResource(R.drawable.tab_middle_bg);
			Week.setTextColor(Color.BLACK);
			Month.setBackgroundResource(R.drawable.tab_right_bg);
			Month.setTextColor(Color.BLACK);
			service.getSteps(MyApplication.account, MyApplication.imei, "1", sHandler);
			break;
		case R.id.btn_week:
			Day.setBackgroundResource(R.drawable.tab_left_bg);
			Week.setBackgroundResource(R.drawable.tab_middle_h);
			Month.setBackgroundResource(R.drawable.tab_right_bg);
			Day.setTextColor(Color.BLACK);
			Week.setTextColor(Color.WHITE);
			Month.setTextColor(Color.BLACK);
			service.getSteps(MyApplication.account, MyApplication.imei, "2", sHandler);
			break;
		case R.id.btn_Month:
			Day.setBackgroundResource(R.drawable.tab_left_bg);
			Week.setBackgroundResource(R.drawable.tab_middle_bg);
			Month.setBackgroundResource(R.drawable.tab_right_h);
			Day.setTextColor(Color.BLACK);
			Week.setTextColor(Color.BLACK);
			Month.setTextColor(Color.WHITE);
			service.getSteps(MyApplication.account, MyApplication.imei, "3", sHandler);
			break;
		default:
			break;
		}
	}
	
	private double[] getStepData(String result){
		String[] res=result.split(",");
		double[] steps=new double[res.length+1];
		for (int i = 0; i < res.length; i++) {
			steps[i+1]=Double.valueOf(res[i].substring(res[i].indexOf(":")+1));
		}
	  return steps;
	}
	
	private double[] getDistanceData(String result){
		String[] res=result.split(",");
		double[] distances=new double[res.length+1];
		for (int i = 0; i < res.length; i++) {
			distances[i+1]=Double.valueOf(res[i].substring(res[i].indexOf(":")+1));
		}
		return distances;
	}
	private class StepHandler implements HttpUtil.ResponseResultHandler<StepResponse>{

		@Override
		public void handle(boolean timeout, StepResponse response) {

			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {

						if ("Day".equals(response.getStepType())) {

							String result1 = response.getStep();
							double[] DailyStepData = getStepData(result1);
							String result2 = response.getDistance();
							double[] DailyDistanceData = getDistanceData(result2);
							double distanceTotal = response.getDistanceTotal();
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putDoubleArray("DailyStepData",
									DailyStepData);
							bundle.putDoubleArray("DailyDistanceData",
									DailyDistanceData);
							bundle.putDouble("distanceTotal", distanceTotal);
							msg.setData(bundle);
							msg.what = UI_DAY;
							mHandler.sendMessage(msg);

						}

						if ("Week".equals(response.getStepType())) {
							String result1 = response.getStep();
							double[] WeekStepData = getStepData(result1);
							String result2 = response.getDistance();
							double[] WeekDistanceData = getDistanceData(result2);
							double distanceTotal = response.getDistanceTotal();
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putDoubleArray("WeekStepData", WeekStepData);
							bundle.putDoubleArray("WeekDistanceData",
									WeekDistanceData);
							bundle.putDouble("distanceTotal", distanceTotal);
							msg.setData(bundle);
							msg.what = UI_WEEK;
							mHandler.sendMessage(msg);
						}

						if ("Month".equals(response.getStepType())) {
							String result1 = response.getStep();
							double[] MonthStepData = getStepData(result1);
							String result2 = response.getDistance();
							double[] MonthDistanceData = getDistanceData(result2);
							double distanceTotal = response.getDistanceTotal();
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putDoubleArray("MonthStepData",
									MonthStepData);
							bundle.putDoubleArray("MonthDistanceData",
									MonthDistanceData);
							bundle.putDouble("distanceTotal", distanceTotal);
							msg.setData(bundle);
							msg.what = UI_MONTH;
							mHandler.sendMessage(msg);
						}
					} else {
						execute(mContext, response.getMsg());
					}
				}else{
					showToastShort(getString(R.string.neterror_hint));	
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
}

package com.hipad.smarthome;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.kettle.v14.SpecificBoilCmd;
import com.hipad.smarthome.KettleDevActivity.SceneInfo;
import com.hipad.smarthome.adapter.Scene;
import com.hipad.smarthome.utils.TargetTemperatureChooser;
import com.hipad.smarthome.utils.TargetTemperatureChooser.TempChosenListener;
import com.hipad.smarthome.utils.TimeSetPopWindow;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

public class AppointMentContentActivity extends BaseActivity{

	private RelativeLayout boilView,boilWarmView,sceneView;
	private ImageView leftBtn;
	
	private byte[] menu = new byte[2];
	private SceneInfo mScene = new SceneInfo();
	private TargetTemperatureChooser mTmpChooser;
	private TimeSetPopWindow mTimePop;
	private int warmTime = 0,warmTmp = 0;
	private String sceneName = null;
	public final static String EXTRA_TARGET_TEMP = "temp";
	public final static String EXTRA_KEEP_TEMP_PERIOD = "keep_temp_period";
	public final static String EXTRA_MENU = "menu";
	private static final int RESULT_CODE = 0x000006;
	private int appointType = 0;//0:boil  1:boil&warm 2:scene
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_appointment_content);
		Intent intent = getIntent();		
		initView();
	}

	private void initView() {
		boilView = (RelativeLayout) findViewById(R.id.boil_view);
		boilWarmView = (RelativeLayout) findViewById(R.id.boil_warm_view);
		sceneView = (RelativeLayout) findViewById(R.id.scene_view);
		leftBtn = (ImageView) findViewById(R.id.title_left_icon);
		mTmpChooser = new TargetTemperatureChooser(this);
		mTimePop = new TimeSetPopWindow(this);
		setClickListener();
	}
	
	private void setClickListener() {
		boilView.setOnClickListener(new boilListener());
		boilWarmView.setOnClickListener(new warmListener());
		sceneView.setOnClickListener(new sceneListener());
		leftBtn.setOnClickListener(new leftlListener());
		mTmpChooser.setTempChoseListener(new TempListener());
		mTimePop.setTimeChoseListener(new TimeListener());
	}
	
	private class TempListener implements TempChosenListener {

		@Override
		public void onTempChosen(int temp) {
			// TODO Auto-generated method stub
			warmTmp = temp;
			mTimePop.showPopupWindow();
		}
		
	}
	
	private class TimeListener implements  TimeChoseListener{

		@Override
		public void onTimeChoose(int minute) {
			// TODO Auto-generated method stub
			warmTime = minute;
			menu[0] = (byte)0;
			menu[1] = (byte)1;
			appointType = 1;
			setResult();
		}
		
	}
	
	private class boilListener implements View.OnClickListener {

		public void onClick(View view) {
			// TODO Auto-generated method stub
			final AlertDialog dialog = new AlertDialog.Builder(AppointMentContentActivity.this).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			dialog.getWindow().setContentView(R.layout.boil_dialog_layout);
			dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					menu[0] = (byte)0;
					menu[1] = (byte)2;
					warmTmp = 0;
					warmTime = 0;
					appointType = 0;
					dialog.dismiss();
					setResult();
				}
			});
					
		}
		
	}
	
	private class warmListener implements View.OnClickListener {

		@SuppressLint("ResourceAsColor")
		public void onClick(View view) {
			// TODO Auto-generated method stub
			mTmpChooser.show();
		}
		
	}
	
	private class sceneListener implements View.OnClickListener {

		public void onClick(View view) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(AppointMentContentActivity.this, SceneLayerOneActivity.class);
			startActivityForResult(intent, 1);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			warmTmp = mScene.temperature = data.getIntExtra(EXTRA_TARGET_TEMP,0);
			warmTime = mScene.period = data.getIntExtra(EXTRA_KEEP_TEMP_PERIOD,0);
	
			byte[] g_menu = data.getByteArrayExtra(EXTRA_MENU);
			menu = g_menu;
			mScene.nameResId = getResources().getIdentifier("scene_" + Scene.getIdBySceneCmd(g_menu), "string",this.getPackageName());
			sceneName = getString(mScene.nameResId);
			appointType = 2;
			setResult();
		} else {
			return;
		}
	}

	private void setResult() {
		Intent intent = new Intent();
		intent.putExtra("menu",menu);
		intent.putExtra("appointtype", appointType);
		intent.putExtra("scenename", sceneName);
		intent.putExtra("warmTime", warmTime);
		intent.putExtra("warmTmp", warmTmp);
		setResult(AppointMentSetActivity.MSG_CONTENT_ACTIVITY_RES, intent);
		finish();
	}
	private class leftlListener implements View.OnClickListener {

		public void onClick(View view) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
}

package com.hipad.smarthome;

import java.util.ArrayList;

import com.hipad.smarthome.adapter.Scene;
import com.hipad.smarthome.adapter.SceneGridViewAdapter;
import com.hipad.smarthome.utils.TimeSetPopWindow;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author EthanChung
 */
public class SceneLayerOneActivity extends BaseActivity implements OnItemClickListener, OnClickListener, TextWatcher, OnTouchListener, OnKeyListener {

	private final static int REQUEST_CODE_FOR_SCENE = 0X01;	
	
	private GridView gridView;
	private EditText searchText;
	private SceneGridViewAdapter customGridAdapter;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.imgClear:
			searchText.setText("");
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		final Scene s = (Scene) parent.getItemAtPosition(position);
		if (Scene.getLayerTwoSceneList(s).size() < 1) {
			TimeSetPopWindow timeSetPopWindow = new TimeSetPopWindow(this);
			timeSetPopWindow.setTimeChoseListener(new TimeChoseListener() {
				@Override
				public void onTimeChoose(int minute) {
					Intent data = new Intent();
					data.putExtra(KettleDevActivity.EXTRA_TARGET_TEMP, s.getWarmTemperatureC());
					data.putExtra(KettleDevActivity.EXTRA_KEEP_TEMP_PERIOD,  minute);
					// ... two levels menu
					data.putExtra(KettleDevActivity.EXTRA_MENU, s.getSceneCmd());
					setResult(RESULT_OK, data);
					finish();
				}
			});

			timeSetPopWindow.showPopupWindow();
		} else {
			Intent intent = new Intent(SceneLayerOneActivity.this, SceneLayerTwoActivity.class);
			intent.putExtra("id", Scene.getIdBySceneCmd(s.getSceneCmd()));
			startActivityForResult(intent, REQUEST_CODE_FOR_SCENE);
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			if (REQUEST_CODE_FOR_SCENE == requestCode) {
				// forward the result
				setResult(RESULT_OK, data);
				finish();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scene_layer_1_layout);
		setResult(RESULT_CANCELED);
		findViewById(R.id.leftBtn).setOnClickListener(this);
		findViewById(R.id.imgClear).setOnClickListener(this);
		((TextView) findViewById(R.id.titleTxt)).setText("³åÅÝÑ¡Ôñ");
		searchText = (EditText)findViewById(R.id.txtSearch);
		searchText.addTextChangedListener(this);
		//searchText.setOnKeyListener(this);
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(this);
		gridView.setOnTouchListener(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		this.updateView(Scene.getLayerOneSceneList(), R.string.img_suffix_125x125);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence input, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence input, int arg1, int arg2, int arg3) {
		if(input.toString()==null||input.toString().equals(""))
			this.updateView(Scene.getLayerOneSceneList(), R.string.img_suffix_125x125);
		else
			this.updateView(Scene.searchSenseByKeyword(this, input.toString()), R.string.img_suffix_130x130);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.getId()==R.id.gridView)
			hideSoftKeyboard();
		return false;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode<KeyEvent.KEYCODE_A||keyCode>KeyEvent.KEYCODE_Z)
//			return false;
//		return super.onKeyDown(keyCode, event);
//	}
	
	private void updateView(ArrayList<Scene> list, int size){
		customGridAdapter = new SceneGridViewAdapter(this, R.layout.scene_item, list, size);
		gridView.setAdapter(customGridAdapter);
	}

	private void hideSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
	}

	@Override
	public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
		if(keyCode<KeyEvent.KEYCODE_A||keyCode>KeyEvent.KEYCODE_Z)
			return false;
		return true;
	}
}
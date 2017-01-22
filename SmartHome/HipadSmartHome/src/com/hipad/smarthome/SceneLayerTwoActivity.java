package com.hipad.smarthome;

import java.util.ArrayList;

import com.hipad.smarthome.adapter.Scene;
import com.hipad.smarthome.adapter.SceneGridViewAdapter;
import com.hipad.smarthome.utils.TimeSetPopWindow;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author EthanChung
 */
public class SceneLayerTwoActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	GridView gridView;
	ArrayList<Scene> gridArray = new ArrayList<Scene>();
	SceneGridViewAdapter customGridAdapter;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		final Scene s = (Scene) parent.getItemAtPosition(position);
//		DialogFragment newFragment = new SceneTimepicker(s);
//		newFragment.show(getFragmentManager(), "timePicker");
		TimeSetPopWindow timeSetPopWindow = new TimeSetPopWindow(this);
		timeSetPopWindow.setTimeChoseListener(new TimeChoseListener() {
			
			@Override
			public void onTimeChoose( int minute) {
				Intent data = new Intent();
				// TODO put the scene data
				data.putExtra(KettleDevActivity.EXTRA_TARGET_TEMP, s.getWarmTemperatureC());
				data.putExtra(KettleDevActivity.EXTRA_KEEP_TEMP_PERIOD, minute);
				// ... two levels menu
				data.putExtra(KettleDevActivity.EXTRA_MENU, s.getSceneCmd());
				setResult(RESULT_OK, data);
								
				finish();
			}
		});
		
		timeSetPopWindow.showPopupWindow();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scene_layer_2_layout);
		
		setResult(RESULT_CANCELED);
		
		findViewById(R.id.leftBtn).setOnClickListener(this);
		((TextView) findViewById(R.id.titleTxt)).setText("≥Â≈›—°‘Ò");

		Bundle bundle = this.getIntent().getExtras();
		int id = bundle.getInt("id");
		Scene one = Scene.getSceneById(id);
		for (Scene s : Scene.getLayerTwoSceneList(one)) {
			gridArray.add(s);
		}
		gridView = (GridView) findViewById(R.id.gridView);
		customGridAdapter = new SceneGridViewAdapter(this, R.layout.scene_item,
				gridArray, R.string.img_suffix_130x130);
		gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(this);
	}
}
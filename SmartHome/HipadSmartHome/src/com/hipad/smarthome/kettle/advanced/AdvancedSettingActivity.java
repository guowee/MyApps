package com.hipad.smarthome.kettle.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.local.device.Device;
import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;
import com.hipad.smarthome.receiver.DrinkAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 高级设置
 * 
 * @author guowei
 * 
 */
public class AdvancedSettingActivity extends BaseActivity {

	public static final int REQUEST_CODE = 1;
	private static ArrayList<IFunction> functions = new ArrayList<IFunction>();
	static {
		functions.add(new WaterHabitsActivity());
		functions.add(new QualityReportActivity());
		functions.add(new WaterAlarmActivity());
		functions.add(new KettleLifeActivity());
		functions.add(new CleanRemainderActivity());
		functions.add(new LampControlActivity());
		functions.add(new HelpActivity());
	}

	private ListView funcList;
	private Context mContext;
	private ImageButton backBtn;
	private TextView title;
	
	
	private AlarmManager mAlarManager;
	private Intent intent;
	private PendingIntent pendIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.advanced_layout);
		mAlarManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
		initViews();
	}

	private void initViews() {
		final byte[] info = getIntent().getByteArrayExtra("kettleStatusInfo");
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title = (TextView) findViewById(R.id.titleTxt);
		title.setText("高级设置");

		funcList = (ListView) findViewById(R.id.advance_function);

		SimpleAdapter adapter = new SimpleAdapter(mContext, getListValues(),
				R.layout.list_item_function, new String[] { IFunction.NAME },
				new int[] { R.id.func_name });
		funcList.setAdapter(adapter);
		funcList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = functions.get(position).execute(mContext);
				intent.putExtra("title", functions.get(position).getName());
				intent.putExtra("kettleStatusInfo", info);
				intent.putExtra("device", getIntent().getParcelableExtra("device"));

				if (functions.get(position).isForResult()) {

					startActivityForResult(intent, REQUEST_CODE);
				} else {
					startActivity(intent);
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			float time = data.getFloatExtra("period", 0);
			long triggerAtMillis=(long) (time*60*60*1000);
			SharedPreferences preferences = getSharedPreferences("waterAlarm",Context.MODE_PRIVATE);
			Editor editor = preferences.edit();

			String userid = MyApplication.user.getUserId();
			boolean flag = true;

			editor.putFloat("time", time);
			editor.putString("user", userid);
			editor.putBoolean("flag", flag);
			editor.commit();			

			intent=new Intent(DrinkAlarmReceiver.ACTION_DRINK_ALARM);
			pendIntent=PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if(time == 0f){//off
				editor.putBoolean("flag", false);
				editor.commit();
				
				mAlarManager.cancel(pendIntent);
				return ;
			}
			mAlarManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerAtMillis, pendIntent);

		}
	}

	private List<Map<String, String>> getListValues() {

		List<Map<String, String>> values = new ArrayList<Map<String, String>>();

		for (IFunction f : functions) {
			Map<String, String> v = new HashMap<String, String>();
			v.put(IFunction.NAME, f.getName());
			values.add(v);
		}

		return values;
	}

}

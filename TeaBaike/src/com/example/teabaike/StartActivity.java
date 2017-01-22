package com.example.teabaike;

import com.example.config.MyConfig;
import com.example.helper.SharedPreferencesHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class StartActivity extends Activity {
	/**
	 * 每次启动显示3秒钟的界面，然后根据是不是第一次运行，如果是第一次运行，则开启引导页面。否则，进入主界面
	 */

	private SharedPreferencesHelper sph;

	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 0x123:
				// 程序不是第一次运行，跳转到主程序界面
				SkipActivity(MainActivity.class);

				break;

			case 0x234:
				// 第一次运行
				SkipActivity(GuideActivity.class);

				break;

			default:
				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 设置全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_start);

		initialData();

	}

	private void initialData() {

		sph = new SharedPreferencesHelper(StartActivity.this);

		int isFirst = sph.getInt(MyConfig.IS_FIRST_RUN);

		if (isFirst == MyConfig.NOT_FIRST) {
			// 不是第一次运行
			handler.sendEmptyMessageDelayed(0x123, 2000);

		} else {
			// 第一次运行
			handler.sendEmptyMessageDelayed(0x234, 2000);

		}

	}

	/**
	 * 跳转到下一个Activity
	 * 
	 * @param clazz
	 */
	public void SkipActivity(Class<?> clazz) {

		Intent intent = new Intent(this, clazz);

		startActivity(intent);

		this.finish();
	}

}

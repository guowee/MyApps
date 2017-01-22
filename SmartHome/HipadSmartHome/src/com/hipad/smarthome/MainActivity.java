package com.hipad.smarthome;

import java.io.File;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.Response;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.fragment.HomeFragment;
import com.hipad.smarthome.receiver.DrinkAlarmReceiver;
import com.hipad.smarthome.utils.SlidingLayout;
import com.hipad.smarthome.utils.TargetTemperatureChooser;

public class MainActivity extends BaseActivity implements OnClickListener {

	/**
	 * 侧滑布局对象，用于通过手指滑动将左侧的菜单布局进行显示或隐藏。
	 */
	public static SlidingLayout slidingLayout;

	/**
	 * 定义主要显示内容布局
	 */
	private RelativeLayout contentLayout;

	private Fragment[] fragments;

	private FragmentTransaction fragTransaction;
	public FragmentManager fragmentManager;

	private RelativeLayout headLayout;
	private ImageView imgvProfile;
	private TextView tvUpdate, tvShop, tvAbout, tvMaitain, tvNickname;
	private Button btnLogout;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		context = this;

		// WindowManager.LayoutParams lp = getWindow().getAttributes();
		// lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// getWindow().setAttributes(lp);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

		fragmentManager = getFragmentManager();
		fragTransaction = fragmentManager.beginTransaction();

		slidingLayout = (SlidingLayout) findViewById(R.id.slidingLayout);
		contentLayout = (RelativeLayout) findViewById(R.id.contentLayout);
		slidingLayout.setScrollEvent(contentLayout);

		if (slidingLayout.isLeftLayoutVisible()) {
			slidingLayout.scrollToLeftLayout();
		}

		Fragment fragment = new HomeFragment();
		fragTransaction.replace(R.id.contentLayout, fragment, "home");
		fragTransaction.commit();
		
		initView();
		init();

		Editor edit = sp.edit();
		edit.putBoolean("isFirst", false);
		edit.commit();		

		if(!DeamonService.isRegistered()){
			Intent intent = new Intent("com.hipad.smarthome.action.RESTART_APPOINTMENT");
			sendBroadcast(intent);
			
			Intent checkDrinkAlarmIntent = new Intent(DrinkAlarmReceiver.ACTION_DRINK_ALARM);
			checkDrinkAlarmIntent.putExtra(DrinkAlarmReceiver.EXTRA_CHECK_DRINK_ALARM, true);
			sendBroadcast(checkDrinkAlarmIntent);
			
			DeamonService.setRegistered(true);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		init();
	}
	
	private void init(){
		File file = new File(application.getSettings().getUserProfileCachePath(MyApplication.user));
		if (file.exists()) {
			Bitmap profile = BitmapFactory.decodeFile(file.getAbsolutePath());
			imgvProfile.setImageBitmap(profile);
		}
		tvNickname.setText(MyApplication.user.getNickName());
	}
	
	private void initView(){
		headLayout = (RelativeLayout) findViewById(R.id.headLayout);
		headLayout.setOnClickListener(this);
		imgvProfile = (ImageView) findViewById(R.id.headImg);
		tvNickname = (TextView) findViewById(R.id.headTxt);

		btnLogout = (Button) findViewById(R.id.main_layout_btn_logout);
		btnLogout.setOnClickListener(this);

		tvUpdate = (TextView) findViewById(R.id.main_layout_btn_update);
		tvUpdate.setOnClickListener(this);
		tvShop = (TextView) findViewById(R.id.main_layout_btn_shop);
		tvShop.setOnClickListener(this);
		tvAbout = (TextView) findViewById(R.id.main_layout_btn_about);
		tvAbout.setOnClickListener(this);
		tvMaitain = (TextView) findViewById(R.id.main_layout_btn_maitain);
		tvUpdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int keyId = view.getId();
		switch (keyId) {
		case R.id.leftBtn:
			// if (slidingLayout.isLeftLayoutVisible()) {
			// slidingLayout.scrollToRightLayout();
			// } else {
			// slidingLayout.scrollToLeftLayout();
			// }
			break;
		case R.id.headLayout:

			startActivity(new Intent(context,UserInfoActivity.class));
			break;
		case R.id.main_layout_btn_logout:
			// when login page occurs, we will do the work about logout.
			startActivity(new Intent(context, LoginActivity.class));
			finish();
			break;

		case R.id.main_layout_btn_about:
			
			Intent intent=new Intent(this,AboutActivity.class);
			
			startActivity(intent);
			
			
			break;
		case R.id.main_layout_btn_update:
		case R.id.main_layout_btn_shop:
		case R.id.main_layout_btn_maitain:
			showToastShort("暂未开通，开发中...");
			break;
		// case R.id.cookBtn:
		//
		// break;
		default:
			break;
		}
	}

	private long firstTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - firstTime) > 2000) {
				firstTime = System.currentTimeMillis();
				showToastShort("再按一次退出");
				return true;
			} else {
				application.exit(false);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}

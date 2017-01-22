package com.hipad.smarthome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.utils.DeviceListCache;

public class StartActivity extends BaseActivity {

	private ImageView welcomeImg = null;
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.start_layout);

//		this.getWindow().setBackgroundDrawable(null);

		welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);
		Animation anima = AnimationUtils.loadAnimation(this, R.anim.start_anim);
		welcomeImg.startAnimation(anima);
		anima.setAnimationListener(new AnimationImpl());
	}

	private class AnimationImpl implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
			welcomeImg.setBackgroundResource(R.drawable.start);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 动画结束后跳转到别的页面			
			boolean isFirst = sp.getBoolean("isFirst", false);
			if(isFirst){
				startActivity(new Intent(StartActivity.this,NavigationActivity.class));
			}else{
				if(ServiceImpl.verifyLocally(getApplicationContext())) {
					startActivity(new Intent(context, MainActivity.class));
					// TODO restore the user info from local
					MyApplication app = (MyApplication) getApplication();
					app.restoreUserInfo();					

					DeviceListCache deviceListCache = DeviceListCache.getInstance();
					deviceListCache.syncDeviceList();
				}else{
					startActivity(new Intent(context, LoginActivity.class));
				}
			}
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}
}

package com.hipad.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
/**
 * 
 * @author guowei
 *
 */
public class StartActivity extends BaseActivity {

	private Context mContext;

	private LinearLayout welcomeImg = null;

	//private BindDialog bindDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		mContext = this;
		getViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void getViews() {
		welcomeImg = (LinearLayout) findViewById(R.id.welcome_layout);
		Animation anima = AnimationUtils.loadAnimation(this,R.anim.start_anim);
		welcomeImg.startAnimation(anima);
		anima.setAnimationListener(new AnimationImpl());
	}

	private class AnimationImpl implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			startActivity(new Intent(mContext,LoginActivity.class));
			finish();
			
			// 动画结束后跳转到别的页面
			//判断设备是否绑定
			/*boolean flag = sph.getBoolean(MyConfig.IS_BOUND_DEVICE,
					MyConfig.NOT_BOUND);

			if (flag) {
     			startActivity(new Intent(mContext,MainActivity.class));
				finish();
			} else {
				bindDialog = new BindDialog(mContext, R.style.MyDialog,
						new OnBindDialogListener() {
							@Override
							public void OnClick(View v) {
								startActivity(new Intent(mContext,RegisterActivity.class));
								bindDialog.dismiss();
								finish();
							}
						});
				bindDialog.setCancelable(false) ;//设置Dialog不消失
				bindDialog.show();
			}*/
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}

	
}

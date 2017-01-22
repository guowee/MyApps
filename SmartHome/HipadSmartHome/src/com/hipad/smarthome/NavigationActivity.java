package com.hipad.smarthome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hipad.smarthome.utils.ScrollLayout;
import com.hipad.smarthome.utils.ScrollLayout.OnScreenChangeListener;

public class NavigationActivity extends BaseActivity implements
		OnScreenChangeListener, OnClickListener {

	private Button loginBtn, signupBtn;

	private ScrollLayout scrollLayout;
	private LinearLayout itemTagLayout;

	private ImageView logoImg;
	
	private int[] imgs = { R.drawable.navi_logo_envir,
			R.drawable.navi_logo_cook, R.drawable.navi_logo_health,
			R.drawable.navi_logo_safe, R.drawable.navi_logo_entertain, };

	private LayoutInflater inflater;

	private int curIndex = 0;
	
	private String[] itemTxt1,itemTxt2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_layout);

		initView();
	}

	private void initView() {
		itemTagLayout = (LinearLayout) findViewById(R.id.ItemTagLayout);

		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);

		signupBtn = (Button) findViewById(R.id.signupBtn);
		signupBtn.setOnClickListener(this);

		logoImg = (ImageView)findViewById(R.id.itemImg);
		logoImg.setBackgroundResource(imgs[curIndex]);
		
		
		itemTxt1 = getResources().getStringArray(R.array.navi_logo_itemtxt1);
		itemTxt2 = getResources().getStringArray(R.array.navi_logo_itemtxt2);
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		scrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
		scrollLayout.setOnScreenChangeListener(this);
		for (int i = 0; i < imgs.length; i++) {
			RelativeLayout v = (RelativeLayout) inflater.inflate(
					R.layout.navigation_item_layout, null);
			TextView text1 = (TextView) v.findViewById(R.id.txt1);
			text1.setText(itemTxt1[i]);

			TextView text2 = (TextView) v.findViewById(R.id.txt2);
			text2.setText(itemTxt2[i]);
			scrollLayout.addView(v);
		}
		ImageView imgView = new ImageView(this);
		imgView.setBackgroundResource(R.drawable.start);
		scrollLayout.addView(imgView);

		
		for (int i = 0; i < scrollLayout.getChildCount(); i++) {
			ImageView img = new ImageView(this);
			img.setBackgroundResource(R.drawable.status_unselect);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(  
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,  
                            LayoutParams.WRAP_CONTENT));  
            layoutParams.leftMargin = 5;  
            layoutParams.rightMargin = 5;
            if(i == 0){
            	img.setBackgroundResource(R.drawable.status_select);
            }
            itemTagLayout.addView(img, layoutParams);
		}
	}

	@Override
	public void onClick(View view) {
		int key = view.getId();
		switch (key) {
		case R.id.loginBtn:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.signupBtn:
			startActivity(new Intent(this, SignupActivity.class));
			break;
		default:
			break;
		}

	}

	@Override
	public void onScreenChange(int currentIndex,float slideScale) {
		if(curIndex != currentIndex){
			ImageView imgView = (ImageView) itemTagLayout.getChildAt(currentIndex);
			imgView.setBackgroundResource(R.drawable.status_select);
			ImageView imgView1 = (ImageView) itemTagLayout.getChildAt(curIndex);
			imgView1.setBackgroundResource(R.drawable.status_unselect);
			
			if(currentIndex < scrollLayout.getChildCount()-1){
				logoImg.setBackgroundResource(imgs[currentIndex]);
				logoImg.setVisibility(View.VISIBLE);
			}else{
				logoImg.setVisibility(View.INVISIBLE);
			}
		}
		
		if(currentIndex < scrollLayout.getChildCount()-1){
			float alpha = logoImg.getAlpha();
			while (logoImg.getAlpha() < 1) {
				alpha +=  0.0001;
				if(alpha > 1){
					alpha = 1.0f;
				}
				logoImg.setAlpha(alpha);
			}
//			long time = (long) (Math.abs(slideScale) * 2);
//			showInfoLog("onScreenChange imgView.getAlpha()1 = " + logoImg.getAlpha());
//			AlphaAnimation animation = new AlphaAnimation(logoImg.getAlpha(), 1.0f);s
//			animation.setDuration(time);//设置动画持续时间 
//			logoImg.startAnimation(animation);
		}
		
		showInfoLog("onScreenChange index = " + currentIndex);
		curIndex = currentIndex;
	}

	@Override
	public void onSlideChange(float slideScale) {
		if(curIndex < scrollLayout.getChildCount()-1){
			showInfoLog("onSlideChange slideScale = "+slideScale);
			
			float alphaDelt = slideScale / 3000;
			float alpha = 0;
			if(slideScale > 0){
				alpha = logoImg.getAlpha() - alphaDelt;
			}else{
				alpha = logoImg.getAlpha() + alphaDelt;
			}
			
			showInfoLog("onSlideChange curAlpha = "+logoImg.getAlpha()+",setAlpha = "+alpha+",alphaDelt = "+alphaDelt);
			if (alpha > 1.0) {
				alpha = 1.0f;
			} else if (alpha < 0) {
				alpha = 0.0f;
			}
			logoImg.setAlpha(alpha);
		}
	}
}

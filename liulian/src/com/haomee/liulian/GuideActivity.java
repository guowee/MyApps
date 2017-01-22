package com.haomee.liulian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

	private ViewPager viewPager;
	private View[] views;
	private ImageView[] dots;
	// 记录当前选中位置
	private int currentIndex;

	private TextView bt_exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		SharedPreferences preferences_is_first = getSharedPreferences("preferences_is_first", Activity.MODE_PRIVATE);
		preferences_is_first.edit().putBoolean("is_first_new_version", true).commit();
		initPagers();
		initDots();

		// 最后一页，点击跳转
		bt_exit = (TextView) findViewById(R.id.bt_exit);

		bt_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到主页面

				if (currentIndex == 3) {
					Intent intent_main = new Intent();
					if (LiuLianApplication.current_user == null) {
						intent_main.setClass(GuideActivity.this, LoginPageActivity.class);
					} else {
						if (LiuLianApplication.current_user.getName().equals("")) {
							intent_main.setClass(GuideActivity.this, UserInfoActivity.class);
						} else {
							intent_main.setClass(GuideActivity.this, MainActivity.class);
						}
					}
					GuideActivity.this.finish();
					GuideActivity.this.startActivity(intent_main);
				}

			}
		});

	}

	private View getPager(int resId) {
		ImageView item = new ImageView(this);
		item.setImageResource(resId);
		item.setScaleType(ScaleType.FIT_XY);
		return item;
	}

	private void initPagers() {

		views = new View[4];
		views[0] = getPager(R.drawable.guide_1);
		views[1] = getPager(R.drawable.guide_2);
		views[2] = getPager(R.drawable.guide_3);
		views[3] = getPager(R.drawable.guide_4);

		viewPager = (ViewPager) this.findViewById(R.id.viewpager);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.length;
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views[position]);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views[position]);
				return views[position];
			}
		};

		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(this);

	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.length];

		// 循环取得小点图片
		for (int i = 0; i < views.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.length) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > views.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}
	

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		setCurDot(arg0);
	}

}

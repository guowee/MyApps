package com.example.teabaike;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.GuidePagerAdapter;
import com.example.config.MyConfig;
import com.example.helper.SharedPreferencesHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity {

	/** viewPgaer控件 */
	private ViewPager viewPager_guide;
	/** 引导页小点 */
	private LinearLayout linear_guide_dots;
	/** 底部图片 */
	private ImageView[] dots;
	/** 已知的引导页个数 */
	private int pageCount;
	/** 保存每次引导上一种状态变量 */
	private int currentIndex;

	/** viewPager适配器 */
	private GuidePagerAdapter guidePagerAdapter;

	/** 适配器数据源 */
	private List<View> views;

	SharedPreferencesHelper sph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_guide);

		initialData();

		initialLayout();

	}

	/**
	 * 初始化数据
	 */
	private void initialData() {
		/* 设置标识，表示不再启动这个引导页 */
		sph = new SharedPreferencesHelper(GuideActivity.this);

		sph.putInt(MyConfig.IS_FIRST_RUN, MyConfig.NOT_FIRST);

		/* 小点数据源 */
		pageCount = 3;

		dots = new ImageView[pageCount];

		currentIndex = 0;
		/* 设置viewpager数据源 */
		views = new ArrayList<View>();
	}

	@SuppressLint("InflateParams")
	private void initialLayout() {

		viewPager_guide = (ViewPager) findViewById(R.id.viewPager_guide);

		LayoutInflater inflater = LayoutInflater.from(this);

		Class<R.drawable> clazz = R.drawable.class;// 准备反射R.drawable下资源

		for (int i = 0; i < pageCount; i++) {

			View view = inflater.inflate(R.layout.guide_content, null);

			LinearLayout linear_guide_showImg = (LinearLayout) view
					.findViewById(R.id.linear_guide_showImg);

			int imageID = 0;

			try {

				imageID = clazz.getDeclaredField("slide" + (i + 1)).getInt(
						R.drawable.slide1);

			} catch (Exception e) {

				e.printStackTrace();

			}

			linear_guide_showImg.setBackgroundResource(imageID);

			views.add(view);

		}

		guidePagerAdapter = new GuidePagerAdapter(views, this);

		viewPager_guide.setAdapter(guidePagerAdapter);

		viewPager_guide.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				dots[arg0].setEnabled(false);

				dots[currentIndex].setEnabled(true);

				currentIndex = arg0;

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		/* 引导小点设置 */
		linear_guide_dots = (LinearLayout) findViewById(R.id.linear_guide_dots);

		for (int i = 0; i < pageCount; i++) {// 循环取得小点图片

			dots[i] = (ImageView) linear_guide_dots.getChildAt(i);

			if (i == 0) {
				dots[i].setEnabled(false);// 设置为白色，即选中状态
			} else {
				dots[i].setEnabled(true);// 都设为灰色
			}
		}

	}

}

package com.hipad.tracker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hipad.tracker.adapter.NavigatePagerAdapter;
import com.hipad.tracker.config.MyConfig;
import com.hipad.tracker.utils.SharedPreferencesHelper;

public class NavigationActivity extends BaseActivity {

	private ViewPager viewPager_guide;
	private LinearLayout linear_guide_dots;
	private ImageView[] dots;
	private int pageCount;
	private int currentIndex;

	private NavigatePagerAdapter guidePagerAdapter;

	private List<View> views;

	private SharedPreferencesHelper sph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		initData();
		getViews();
	}

	private void initData() {
		sph = new SharedPreferencesHelper(NavigationActivity.this);
		sph.putInt(MyConfig.IS_FIRST_RUN, MyConfig.NOT_FIRST);
		pageCount = 2;
		dots = new ImageView[pageCount];
		currentIndex = 0;
		views = new ArrayList<View>();
	}

	private void getViews() {
		viewPager_guide = (ViewPager) findViewById(R.id.viewPager_guide);
		LayoutInflater inflater = LayoutInflater.from(this);
		Class<R.drawable> clazz = R.drawable.class;
		for (int i = 0; i < pageCount; i++) {
			View view = inflater.inflate(R.layout.guide_content, null);
			LinearLayout linear_guide_showImg = (LinearLayout) view
					.findViewById(R.id.linear_guide_showImg);
			int imageID = 0;
			try {
				//imageID = clazz.getDeclaredField("slide" + (i + 1)).getInt(
				//		R.drawable.slide1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			linear_guide_showImg.setBackgroundResource(imageID);
			views.add(view);
		}
		guidePagerAdapter = new NavigatePagerAdapter(views, this);
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
		linear_guide_dots = (LinearLayout) findViewById(R.id.linear_guide_dots);
		for (int i = 0; i < pageCount; i++) {
			dots[i] = (ImageView) linear_guide_dots.getChildAt(i);
			if (i == 0) {
				dots[i].setEnabled(false);
			} else {
				dots[i].setEnabled(true);
			}
		}
	}
}

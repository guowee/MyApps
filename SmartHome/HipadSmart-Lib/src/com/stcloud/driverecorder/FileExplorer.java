package com.stcloud.driverecorder;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class FileExplorer extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager mViewPager;
	private SlidePagerAdapter mSlidePagerAdapter;
	
	public static final String DIR_VIDEO_UNLOCK = "/storage/external/Hipad/Video/";
	public static final String DIR_PHOTO_UNLOCK = "/storage/external/Hipad/Photo/";
	public static final String DIR_VIDEO_LOCK = "/storage/external/Hipad/Video_lock/";
	public static final String DIR_PHOTO_LOCK = "/storage/external/Hipad/Photo_lock/";
	public static final String DIR_THUMBNAIL = "/storage/external/Hipad/Thumbnail/";

	/*
	public static final String DIR_VIDEO_UNLOCK = "/sdcard/Movies/";
	public static final String DIR_PHOTO_UNLOCK = "/sdcard/DCIM/";
	public static final String DIR_VIDEO_LOCK = "/sdcard/Movies_lock/";
	public static final String DIR_PHOTO_LOCK = "/sdcard/DCIM_lock/";
	public static final String DIR_THUMBNAIL = "/sdcard/Thumbnail/";
	*/
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer_activity);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSlidePagerAdapter = new SlidePagerAdapter(this, getSupportFragmentManager());
		
		// Set up the ViewPager, attaching the adapter and setting up a listener for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSlidePagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections, select the corresponding tab.
				// We can also use ActionBar.Tab#select() to do this if we have a reference to the
				// Tab.
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSlidePagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the
			// listener for when this tab is selected.
			if (i ==0) {
				actionBar.addTab(
						actionBar.newTab()
						.setText(mSlidePagerAdapter.getPageTitle(i))
						.setTabListener(this));
			} else {
				actionBar.addTab(
						actionBar.newTab()
						.setText(mSlidePagerAdapter.getPageTitle(i))
						.setTabListener(this));
			}
		}
		
		String prodName = Utils.getProductName();
		if (prodName.equals("CW203")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); 
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		}
	}
	
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	
	}

	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}	
	
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	
	}
}

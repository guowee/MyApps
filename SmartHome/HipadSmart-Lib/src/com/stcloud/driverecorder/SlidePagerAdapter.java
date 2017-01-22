package com.stcloud.driverecorder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlidePagerAdapter extends FragmentPagerAdapter {

	private Context mContext;
	
	private static final String DIR_VIDEO = "/storage/external/Hipad/Video";
	private static final String DIR_PHOTO = "/storage/external/Hipad/Photo";
	
	public SlidePagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}
	
	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case 0:
		{
			return VideoFragment.newInstance();
		}
		default:
			return PhotoFragment.newInstance();
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String title = "";
		
		switch (position) {
		case 0:
			title = mContext.getString(R.string.video);
			break;
		case 1:
			title = mContext.getString(R.string.photo);
			break;
		}
		
		return title;
	}
}

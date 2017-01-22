package com.hipad.tracker;

import java.util.ArrayList;
import java.util.List;

import com.hipad.tracker.adapter.GuidePagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GuideActivity extends BaseActivity implements OnClickListener{

	private Context mContext;
	private ViewPager viewPager;
	private Button skipBtn;
	private ImageButton backBtn;
	private TextView titleTxt;
	private List<View> views=new ArrayList<View>();
	
	private GuidePagerAdapter guidePagerAdapter;
	
	
	private String simNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		mContext=this;
		initData();
		getViews();
		
	}
	
	private void initData(){
		Intent intent=getIntent();
		simNumber=intent.getStringExtra("simNum");
	}
	
    private void getViews(){
    	viewPager=(ViewPager) findViewById(R.id.viewPager_nav);
    	skipBtn=(Button) findViewById(R.id.skip_to_scan);
    	skipBtn.setOnClickListener(this);
    	backBtn=(ImageButton) findViewById(R.id.leftBtn);
    	backBtn.setOnClickListener(this);
    	titleTxt=(TextView) findViewById(R.id.titleTxt);
    	titleTxt.setText(getString(R.string.add_tracker));
    	LayoutInflater inflater = LayoutInflater.from(this);
	    View view1 = inflater.inflate(R.layout.step_one_content, null);
		View view2=inflater.inflate(R.layout.step_two_content, null);
	    views.add(view1);
	    views.add(view2);
		
    	guidePagerAdapter=new GuidePagerAdapter(views, this);
    	viewPager.setAdapter(guidePagerAdapter);
    	viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.skip_to_scan:
			Intent intent=new Intent(mContext, CaptureActivity.class);
			intent.putExtra("simNum", simNumber);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
}

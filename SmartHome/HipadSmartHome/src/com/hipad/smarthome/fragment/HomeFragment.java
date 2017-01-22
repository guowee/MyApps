package com.hipad.smarthome.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
//import android.view.animation.Animation;
import android.widget.ImageButton;
//import android.widget.RelativeLayout;

import com.hipad.smarthome.AddChooserActivity;
import com.hipad.smarthome.DeviceListActivity;
import com.hipad.smarthome.MainActivity;
import com.hipad.smarthome.R;
//import com.hipad.smarthome.RouteListActivity;
//import com.hipad.smarthome.utils.AddDevicePopWindow;

public class HomeFragment extends Fragment implements OnClickListener {

	private Activity context;
	private ImageButton leftBtn, rightBtn;

	//private RelativeLayout envirType, cookType, healthType, safeType, playType;
	public ImageButton envirBar, cookBar, healthBar, safeBar, playBar;

	//private Animation anim;
	
	//private AddDevicePopWindow popView;

	private MainActivity main;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		main = (MainActivity) context;
		
		View v = inflater.inflate(R.layout.home_content, null);
		leftBtn = (ImageButton) v.findViewById(R.id.leftBtn);
		leftBtn.setOnClickListener(this);
		rightBtn = (ImageButton) v.findViewById(R.id.rightBtn);
		rightBtn.setOnClickListener(this);

//		envirType = (RelativeLayout) v.findViewById(R.id.envirType);
//		envirType.setOnClickListener(this);
		envirBar = (ImageButton) v.findViewById(R.id.envirBar);
		envirBar.setOnClickListener(this);

//		cookType = (RelativeLayout) v.findViewById(R.id.cookType);
//		cookType.setOnClickListener(this);
		cookBar = (ImageButton) v.findViewById(R.id.cookBar);
		cookBar.setOnClickListener(this);

//		healthType = (RelativeLayout) v.findViewById(R.id.healthType);
//		healthType.setOnClickListener(this);
		healthBar = (ImageButton) v.findViewById(R.id.healthBar);
		healthBar.setOnClickListener(this);

//		safeType = (RelativeLayout) v.findViewById(R.id.safeType);
//		safeType.setOnClickListener(this);
		safeBar = (ImageButton) v.findViewById(R.id.safeBar);
		safeBar.setOnClickListener(this);

//		playType = (RelativeLayout) v.findViewById(R.id.playType);
//		playType.setOnClickListener(this);
		playBar = (ImageButton) v.findViewById(R.id.playBar);
		playBar.setOnClickListener(this);

		//popView = new AddDevicePopWindow(context, itemsOnClick);
		
//		main.service.getDevices(null, Category.Cook, new HttpUtil.ResponseResultHandler<QueryDevicesResponse>(){
//			@Override
//			public void handle(boolean arg0, QueryDevicesResponse arg1) {
//			}
//		});
		
		return v;
	}

//	private void init() {
		// anim = AnimationUtils.loadAnimation(context,
		// R.anim.typebtn_anim);
		// anim.setAnimationListener(new AnimationListener() {
		// @Override
		// public void onAnimationStart(Animation arg0) {
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation arg0) {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation arg0) {
		// Intent intent = new Intent(context, CookTypeActivity.class);
		// startActivity(intent);
		// }
		// });

		// cookBar = (ImageView) v.findViewById(R.id.cookBar);
		// cookBar.setOnClickListener(this);
		// cookBtn.setBackgroundResource(R.drawable.mainbtn_click_408_90);
		// cookType.startAnimation(anim);
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View view) {
		int keyId = view.getId();
		int type = 0;
		switch (keyId) {
		case R.id.leftBtn:
			if (main.slidingLayout.isLeftLayoutVisible()) {
				main.slidingLayout.scrollToRightLayout();
			} else {
				main.slidingLayout.scrollToLeftLayout();
			}
			break;
		case R.id.rightBtn:
			//int xoffInPixels =popView.getWidth() / 2 - rightBtn.getWidth() / 2;
//			-SmallTools.widthToChangePixel(context, xoffInPixels)
			//EthanChung 2015.03.16
			//popView.showAsDropDown(rightBtn, -xoffInPixels, SmallTools.widthToChangePixel(context, 4));
//			startActivity(new Intent(context, DeviceAddActivity.class));			
			
			// added by wangbaoming
			startActivity(new Intent(context, AddChooserActivity.class));
			break;
		case R.id.envirBar:
//		case R.id.envirType:
			type = 1;
//			ViewWrapper wrapper = new ViewWrapper(envirBar);
//		    ObjectAnimator.ofInt(wrapper, "width", 500).setDuration(5000).start();
			break;
		case R.id.cookBar:
//		case R.id.cookType:
			type = 2;
			break;
		case R.id.healthBar:
//		case R.id.healthType:
			type = 3;
			break;
		case R.id.safeBar:
//		case R.id.safeType:
			type = 4;
			break;
		case R.id.playBar:
//		case R.id.playType:
			type = 5;
			break;
		default:
			break;
		}
		if(type > 0){
			//EthanChung 2015.03.14
			//Intent intent = new Intent(context, CookTypeActivity.class);
			Intent intent = new Intent(context, DeviceListActivity.class);
//			Intent intent = new Intent(context, LocalDeviceList.class);
			intent.putExtra("type", type);
			startActivity(intent);
		}
		
		if(main.slidingLayout.isLeftLayoutVisible()){
			main.slidingLayout.scrollToRightLayout();
		}
	}

	// 为弹出窗口实现监听类
//	private OnClickListener itemsOnClick = new OnClickListener() {
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.routerLayout:
//				popView.dismiss();
//				Intent intent = new Intent(context, RouteListActivity.class);
//				startActivity(intent);
//				break;
//			case R.id.deviceLayout:
//				popView.dismiss();
//				// modified by wangbaoming
////				startActivity(new Intent(context, ScanDeviceActivity.class));
//				startActivity(new Intent(context, AddChooserActivity.class));
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	private void showToast() {
//		main.showToastShort("暂未开通，开发中...");
//	}

}
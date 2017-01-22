package com.hipad.tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hipad.tracker.adapter.DrawerAdapter;
import com.hipad.tracker.entity.TrackerMsg;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.DevInfoResponse;
import com.hipad.tracker.json.LocResponse;
import com.hipad.tracker.model.ContentModel;
import com.hipad.tracker.service.Service;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.SharedPreferencesHelper;
import com.hipad.tracker.widget.DrawerArrowDrawable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import static android.view.Gravity.START;

/**
 * 
 * @author guowei
 *
 */
public class MainTestActivity extends FragmentActivity implements OnClickListener,OnMapReadyCallback {

	public static final String LAST_ONE="com.hipad.tracker.LoginActivity";
	public static final String LAST_TWO="com.hipad.tracker.BindingActivity";

	private int area = CHINA;

	public static final int CHINA = 0;
	public static final int TAIWAN = 1;
	public MyApplication application;
	public SharedPreferencesHelper sph;
	private Context mContext;
	private DrawerLayout drawer;
	private RelativeLayout left_menu_layout,loading_layout;
	private List<ContentModel> list;
	private DrawerAdapter adapter;

	private DrawerArrowDrawable drawerArrowDrawable;
	private float offset;
	private boolean flipped;
	private Dialog mDialog;
	private ImageView imageView,batteryView;

	private ImageButton menuBtn, callBtn, locateBtn;
	private TextView titleTxt, districtTxt, cityTxt;
	private ListView listView;
	private GoogleMap map;
	private Geocoder geocoder;
	private LayoutInflater inflater;
	
	private Service service;
	private LocHandler cHandler;
	private LocResultHandler crHandler;
	private DevInfoHandler inHandler;
	
	private double longitude;
	private double latitude;
	
	private String imei;
	private static String bizSn;
	private static String battery;
	
	private TrackerMsg trackerInfo = new TrackerMsg();

	private boolean isOpen = true;
	//public static final LatLng LATLNG_SHANGHAI = new LatLng(31.1734800,121.4065400);
	//public static final LatLng LATLNG_TAIPEI_101 = new LatLng(25.033408,121.564099);
	public static LatLng LATLNG_DEFAULT = new LatLng(0, 0);

	private final static int MSG_SHOW=0x00;
	private final static int MSG_LOCRESULT=0x01;
	private final static int MSG_SUCCESS=0x02;
	private final static int MSG_LOC=0x03;
	private final static int MSG_DIALOG_STATE = 0x04;
	private final static int MSG_INFO=0x05;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW:
				String address = String.valueOf(msg.obj);
				addMarker(map, LATLNG_DEFAULT, address);
				break;
			case MSG_LOCRESULT:
				calcBattery(battery);
				service.locResult(imei, MyApplication.account, bizSn, crHandler);
				break;
			case MSG_SUCCESS:
				calcBattery(battery);
                loading_layout.setVisibility(View.GONE);
				break;
			case MSG_LOC:
				service.loc(imei, MyApplication.account, cHandler);
				break;
			case MSG_DIALOG_STATE:
				mDialog.dismiss();
				break;
			case MSG_INFO:
				service.getDevInfo(MyApplication.account, MyApplication.imei, inHandler);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (MyApplication) getApplication();
		application.addActivity(this);
		mContext = this;
		initData();
		getViews();
	}
	
	@Override
	protected void onStart() {
		getDataFromIntent();
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		mHandler.sendEmptyMessage(MSG_INFO);
		super.onResume();
	}
	
	private void initData() {
		service=new ServiceImpl();
		cHandler=new LocHandler();
		crHandler=new LocResultHandler();
		inHandler=new DevInfoHandler();
		sph=new SharedPreferencesHelper(mContext);
		list = new ArrayList<ContentModel>();
		list.add(new ContentModel(R.drawable.step_counter,getString(R.string.step_counter)));
		list.add(new ContentModel(R.drawable.security_number,getString(R.string.security_number)));
		list.add(new ContentModel(R.drawable.baby_information,getString(R.string.baby_information)));
		list.add(new ContentModel(R.drawable.system_settings,getString(R.string.system_settings)));
	}
	private void  getDataFromIntent(){
		Intent intent=getIntent();
		imei=MyApplication.imei;
		trackerInfo=intent.getParcelableExtra("tracker");
		
		String classname=intent.getStringExtra("classname");
		boolean isBinding=sph.getBoolean("isBinding");
		if(isBinding){
			showNotifyDialog(mContext, getString(R.string.welcome_use_tracker));
		    sph.putBoolean("isBinding", false);
		}
		if(trackerInfo==null){
			if(classname!=null&&(LAST_ONE.equals(classname)||LAST_TWO.equals(classname))&&isOpen){
			  mHandler.sendEmptyMessage(MSG_LOC);
			  isOpen=false; 
			}
		}else{
			String locStr=trackerInfo.getLoc();
			
			String[] data = locStr.split(",");
			String[] pos = new String[2];
			for (int i = 0; i < data.length; i++) {
				pos[i] = data[i].split(":")[1];
			}
			longitude = Double.parseDouble(pos[0]);
			latitude=Double.parseDouble(pos[1]);
			LATLNG_DEFAULT = new LatLng(latitude, longitude);
			moveMap(map, LATLNG_DEFAULT);
			
		}
		
	}

	private void getViews() {
		loading_layout=(RelativeLayout) findViewById(R.id.loading_layout);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		imageView = (ImageView) findViewById(R.id.drawer_indicator);
		final Resources resources = getResources();

		drawerArrowDrawable = new DrawerArrowDrawable(resources);
		drawerArrowDrawable.setStrokeColor(resources.getColor(android.R.color.white));
		imageView.setImageDrawable(drawerArrowDrawable);
		imageView.setOnClickListener(this);

		inflater = LayoutInflater.from(mContext);
		initGoogleMap();
		batteryView=(ImageView) findViewById(R.id.batteryIcon);
		//batteryTxt=(TextView) findViewById(R.id.battery_txt);
		titleTxt = (TextView) findViewById(R.id.title_text);
		cityTxt = (TextView) findViewById(R.id.city_txt);
		districtTxt = (TextView) findViewById(R.id.district_txt);
		menuBtn = (ImageButton) findViewById(R.id.menu_btn);
		callBtn = (ImageButton) findViewById(R.id.call_btn);
		locateBtn = (ImageButton) findViewById(R.id.local_btn);
		initLeftLayout();
		setClickListener();
		menuBtn.setBackground(getResources().getDrawable(R.drawable.user_personal_zone_menu));
		drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				offset = slideOffset;

				// Sometimes slideOffset ends up so close to but not quite 1 or 0.
				if (slideOffset >= .995) {
					flipped = true;
					drawerArrowDrawable.setFlip(flipped);
				} else if (slideOffset <= .005) {
					flipped = false;
					drawerArrowDrawable.setFlip(flipped);
				}

				drawerArrowDrawable.setParameter(offset);
			}
		});
	}

	private void setClickListener() {
		menuBtn.setOnClickListener(this);
		titleTxt.setOnClickListener(this);
		callBtn.setOnClickListener(this);
		locateBtn.setOnClickListener(this);
		
	}

	private void initGoogleMap() {

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
		fragment.getMapAsync(this);

		geocoder=new Geocoder(mContext, Locale.getDefault());
		//LATLNG_DEFAULT=new LatLng(latitude, longitude);
		
		/*switch (area) {
		case CHINA:
			geocoder = new Geocoder(this, Locale.CHINA);
			LATLNG_DEFAULT = LATLNG_SHANGHAI;
			break;
		case TAIWAN:
			geocoder = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
			LATLNG_DEFAULT = LATLNG_TAIPEI_101;
			break;
		default:
			geocoder = new Geocoder(this, Locale.getDefault());
		}*/
	}
	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		moveMap(map, LATLNG_DEFAULT);
		new ReverseGeocodingTask().execute(LATLNG_DEFAULT);
	}
	private void initLeftLayout() {
		left_menu_layout = (RelativeLayout) findViewById(R.id.left);
		listView = (ListView) left_menu_layout.findViewById(R.id.left_listview);
		adapter = new DrawerAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				switch (position) {
				case 0: {
					Intent intent = new Intent(mContext,StepCountActivity.class);
					startActivity(intent);
					break;
				}
				case 1: {
					Intent intent = new Intent(mContext,SecurityNumberActivity.class);
					startActivity(intent);
					break;
				}
				case 2: {
					Intent intent = new Intent(mContext,BabyInfoActivity.class);
					startActivity(intent);
					break;
				}
				case 3: {
					Intent intent = new Intent(mContext,SystemSettingActivity.class);
					startActivity(intent);
					break;
				}
				default:
					break;
				}

			}
		});
	}
    /*public void openLeftLayout() {
		if (drawer.isDrawerOpen(left_menu_layout)) {
			drawer.closeDrawer(left_menu_layout);
		} else {
			drawer.openDrawer(left_menu_layout);
		}
	}*/
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.drawer_indicator:
			if (drawer.isDrawerVisible(START)) {
				drawer.closeDrawer(START);
			} else {
				drawer.openDrawer(START);
			}
			break;
		case R.id.title_text:
			//initPopupWindow();
			break;
		case R.id.call_btn:
			startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+sph.getString("info_devSim"))));
			break;
		case R.id.local_btn:
			/*loading_layout.setVisibility(View.VISIBLE);
			service.loc(imei, MyApplication.account, cHandler);*/
			new ReverseGeocodingTask().execute(new LatLng(Math.round(Math.random()*90), Math.round(Math.random()*180)));
			
			break;
		default:
			break;
		}
	}

	private void moveMap(GoogleMap map, LatLng place) {
		CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(17).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void addMarker(GoogleMap map, LatLng place, String title) {
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_nowpos);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.visible(true);
		markerOptions.position(place).icon(icon);/*.title(title);*/

		CircleOptions circleOptions = new CircleOptions();
		circleOptions.visible(true);
		circleOptions.center(place).fillColor(Color.rgb(206, 226, 214)).strokeColor(Color.argb(1, 135, 206, 250));

		map.addCircle(circleOptions).setVisible(true);
		map.addMarker(markerOptions).showInfoWindow();

		if (title != null) {
			String[] detail = title.split(",");
			districtTxt.setText(detail[1]);
			cityTxt.setText(detail[0]);
		}
	}

	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, Void> {

		@Override
		protected Void doInBackground(LatLng... params) {
			LatLng loc = params[0];
			List<Address> addresses = null;
			try {
				// Call the synchronous getFromLocation() method by passing in
				// the lat/long values.
				addresses = geocoder.getFromLocation(loc.latitude,loc.longitude, 1);

			} catch (IOException e) {
				e.printStackTrace();
				// Update UI field with the exception.
				Message.obtain(mHandler, MSG_SHOW, "Warning,No address found !").sendToTarget();
			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				String addressText;

			    addressText = String.format("%s, %s%s%s%s",address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getAdminArea()!=null?address.getAdminArea():"", address.getLocality()!=null?address.getLocality():"", address.getThoroughfare()!=null?address.getThoroughfare():"", address.getFeatureName()!=null?address.getFeatureName():"");

				// Update the UI via a message handler.
				Message.obtain(mHandler, MSG_SHOW, addressText).sendToTarget();
			}
			return null;
		}
	}

	private class LocHandler implements	HttpUtil.ResponseResultHandler<LocResponse> {
		@Override
		public void handle(boolean timeout, LocResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						if (getString(R.string.loc_request_complete).equals(response.getMsg())) {
							
							bizSn=response.getBizSN();
							mHandler.sendEmptyMessage(MSG_LOCRESULT);
							
						}
						if (getString(R.string.loc_ack_start).equals(response.getMsg())) {
							String loc = response.getLoc();
							String[] data = loc.split(",");
							String[] pos = new String[3];
							for (int i = 0; i < data.length; i++) {
								pos[i] = data[i].split(":")[1];
							}
							String lon = pos[0];
							String lat = pos[1];
							String range = pos[2];
							
							//Initial latitude and longitude 
                            longitude=Double.parseDouble(lon);
                            latitude=Double.parseDouble(lat);
                           
                            LATLNG_DEFAULT=new LatLng(latitude, longitude);
                            moveMap(map, LATLNG_DEFAULT);
                            new ReverseGeocodingTask().execute(LATLNG_DEFAULT);
						}
						if (getString(R.string.loc_success).equals(response.getMsg())) {
							String locType = response.getLocType();
							String loc = response.getLoc();
							String[] data = loc.split(",");
							String[] pos = new String[2];
							for (int i = 0; i < data.length; i++) {
								pos[i] = data[i].split(":")[1];
							}
							String lon = pos[0];
							String lat = pos[1];
							latitude=Double.parseDouble(lat);
							longitude=Double.parseDouble(lon);
							
							
							LATLNG_DEFAULT=new LatLng(latitude, longitude);
							moveMap(map, LATLNG_DEFAULT);
                            new ReverseGeocodingTask().execute(LATLNG_DEFAULT);
						}
					} else {
						//showToastShort(response.getMsg());
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	private class LocResultHandler implements HttpUtil.ResponseResultHandler<LocResponse> {
		@Override
		public void handle(boolean timeout, LocResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						if (getString(R.string.locResult_request_complete).equals(response.getMsg())) {
							bizSn=response.getBizSN();
							mHandler.sendEmptyMessageDelayed(MSG_LOCRESULT,5000);
						}
						if (getString(R.string.loc_ack_start).equals(response.getMsg())) {
							String loc = response.getLoc();
							String[] data = loc.split(",");
							String[] pos = new String[3];
							for (int i = 0; i < data.length; i++) {
								pos[i] = data[i].split(":")[1];
							}
							String lon = pos[0];
							String lat = pos[1];
							String range = pos[2];
							//Initial latitude and longitude
                            longitude=Double.parseDouble(lon);
                            latitude=Double.parseDouble(lat);
                            moveMap(map, new LatLng(latitude, longitude));
                            new ReverseGeocodingTask().execute(new LatLng(latitude, longitude));
                            
                            bizSn=response.getBizSN();
                            battery=response.getBattery();
                            
                            mHandler.sendEmptyMessageDelayed(MSG_LOCRESULT, 5000);
							
						}
						if (getString(R.string.loc_ack_data).equals(response.getMsg())) {
							
							battery=response.getBattery();
							
							//Initial latitude and longitude
    						latitude=response.getLat();
							longitude=response.getLon();
							moveMap(map, new LatLng(latitude, longitude));
							new ReverseGeocodingTask().execute(new LatLng(latitude, longitude));
							mHandler.sendEmptyMessage(MSG_SUCCESS);
							
						}
						
					} else {
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	private class DevInfoHandler implements	HttpUtil.ResponseResultHandler<DevInfoResponse> {

		@Override
		public void handle(boolean timeout, DevInfoResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						
						String devSim=response.getDevSIM();
						String version=response.getDevVersion();
						String spn2=response.getSpn2();
						
						sph.putString("info_devSim", devSim);
						sph.putString("info_spn2",spn2);
						sph.putString("info_version", version);

					} else {
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}

	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MSG_SHOW);
        mHandler.removeMessages(MSG_LOC);
        mHandler.removeMessages(MSG_LOCRESULT);
        mHandler.removeMessages(MSG_SUCCESS);
        mHandler.removeMessages(MSG_DIALOG_STATE);
		super.onDestroy();
	}
	
	public void showToastShort(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_layout, null);
		TextView title = (TextView) layout.findViewById(R.id.toastTxt);
		title.setText(msg);
		Toast toast = new Toast(mContext);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private void calcBattery(String battery){

		if (battery == null) {
			batteryView.setImageResource(R.drawable.watcher0);
		} else {
			String batt = "";
			switch (battery) {
			case "0":
				batteryView.setImageResource(R.drawable.watcher0);
				break;
			case "1":
				batteryView.setImageResource(R.drawable.watcher1);
				break;
			case "2":
				batteryView.setImageResource(R.drawable.watcher2);
				break;
			case "3":
				batteryView.setImageResource(R.drawable.watcher3);
				break;
			case "4":
				batteryView.setImageResource(R.drawable.watcher4);
				break;
			case "5":
				batteryView.setImageResource(R.drawable.watcher5);
				break;
			case "6":
				batteryView.setImageResource(R.drawable.watcher6);
				break;
			default:
				break;
			}
		}
	}
	private void initPopupWindow() {
		View popView = inflater.inflate(R.layout.pop_window, null);
		PopupWindow popupWindow = new PopupWindow(popView, 300,ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		ListView titleList = (ListView) popView.findViewById(R.id.pop_title_list);

		final List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
		for (int i = 0; i < 5; i++) {
			Map<String, String> item = new HashMap<String, String>();

			item.put("babyName", "Baby_" + i);
			listItems.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(mContext, listItems,R.layout.pop_list_item, new String[] { "babyName" },new int[] { R.id.baby_title });
		titleList.setAdapter(adapter);
		titleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				titleTxt.setText(listItems.get(position).get("babyName"));
			}
		});
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(titleTxt);
			popupWindow.update();
		}
	}
	
	public void showNotifyDialog(Context context, String str) {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.custom_toast_layout, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.toastTxt);
		if (str != null) {
			// 设置加载信息e
			tipTextView.setText(str);
		}
		if (mDialog == null) {
			// 创建自定义样式dialog
			Dialog _dialog = new Dialog(context, R.style.CustomProgressDialog);
			_dialog.setCancelable(true);
			LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			v.setLayoutParams(params);
			v.setGravity(Gravity.CENTER);
			_dialog.onBackPressed();
			_dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
						if (mDialog != null && mDialog.isShowing()) {
							mDialog.dismiss();
						}
					}
					return false;
				}
			});
			mDialog = _dialog;
			mDialog.setContentView(v);
		}
		mDialog.show();
		Message msg = new Message();
		msg.what = MSG_DIALOG_STATE;
		mHandler.sendMessageDelayed(msg, 3000);
	}
	public void execute(Context context,String string){
		if(string.equals(getString(R.string.status_error))){
		 showToastLong(getString(R.string.account_login));
		 startActivity(new Intent(context,LoginActivity.class));	
		}else{
			showToastShort(string);
		}
	}
	public void showToastLong(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_layout, null);
		TextView title = (TextView) layout.findViewById(R.id.toastTxt);
		title.setText(msg);
		Toast toast = new Toast(mContext);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}

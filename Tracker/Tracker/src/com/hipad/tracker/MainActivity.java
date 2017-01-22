package com.hipad.tracker;

import static android.view.Gravity.START;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hipad.tracker.adapter.DrawerAdapter;
import com.hipad.tracker.entity.Site;
import com.hipad.tracker.entity.TrackerMsg;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.DevInfoResponse;
import com.hipad.tracker.json.LocResponse;
import com.hipad.tracker.model.ContentModel;
import com.hipad.tracker.service.Service;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.Converter;
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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
/**
 * 
 * @author Guowei
 *
 */
@SuppressLint("InflateParams")
public class MainActivity extends FragmentActivity  implements OnClickListener{

	public static final String TAG=MainActivity.class.getSimpleName();
	public static final String LAST_ONE="com.hipad.tracker.LoginActivity";
	public static final String LAST_TWO="com.hipad.tracker.BindingActivity";
	public static LatLng LATLNG_DEFAULT = new LatLng(39.9388838,116.3974589);
	public MyApplication application;
	public SharedPreferencesHelper sph;
	private GoogleMap mMap;
	private CameraPosition cameraPosition;
	private MarkerOptions markerOpt;
	private CircleOptions circleOptions;
	private Geocoder geocoder;
	private LocationManager mLocManager;
	private MyLocationListener mListener;
	private TimeCount timer;
	private Location location;
	private String bestProvider;
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
	private TextView titleTxt, districtTxt, cityTxt,loadTxt;
	private ListView listView;
	
	private double longitude=0;
	private double latitude=0;
	
	//whether in mainland of China
	private boolean isLand=true;
	
	private static String bizSn;
	private static String battery;
	private String imei;
	private boolean isOpen = true;
	
	private TrackerMsg trackerInfo = new TrackerMsg();
	private Service service;
	private LocHandler cHandler;
	private LocResultHandler crHandler;
	private DevInfoHandler inHandler;
	
	public static final int MSG_SHOW_ADDRESS=0x01;
	public static final int MSG_LOC=0x02;
	public static final int MSG_LOCRESULT=0x03;
	public static final int MSG_DIALOG_STATE=0x04;
	public static final int MSG_INFO=0x05;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_ADDRESS:
				String address = String.valueOf(msg.obj);
				if (address != null) {
					String[] detail = address.split(",");
					districtTxt.setText(detail[1]);
					cityTxt.setText(detail[0]);
				}
				showBattery(battery);
				break;
			case MSG_LOC:
				service.loc(imei, MyApplication.account, cHandler);
				break;
			case MSG_LOCRESULT:
				showBattery(battery);
				service.locResult(imei, MyApplication.account, bizSn, crHandler);
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
		};
	};
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);
		application = (MyApplication) getApplication();
		application.addActivity(this);
		mContext=this;
		initDatas();
		getViews();
		initManager();
		initProvider();
		initGoogleMap();
		updateToNewLocation(location);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		getDataFromIntent();
		mHandler.sendEmptyMessage(MSG_INFO);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mLocManager.requestLocationUpdates(bestProvider, 2 * 1000, 1,mListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mLocManager!=null){
			mLocManager.removeUpdates(mListener);
			//mLocManager.setTestProviderEnabled(bestProvider,false);
		}
	}
	
	private void initDatas(){
		service=new ServiceImpl();
		cHandler=new LocHandler();
		crHandler=new LocResultHandler();
		inHandler=new DevInfoHandler();
		sph=new SharedPreferencesHelper(mContext);
		
		timer=new TimeCount(60000,1000);
		list = new ArrayList<ContentModel>();
		list.add(new ContentModel(R.drawable.step_counter,getString(R.string.step_counter)));
		list.add(new ContentModel(R.drawable.security_number,getString(R.string.security_number)));
		list.add(new ContentModel(R.drawable.baby_information,getString(R.string.baby_information)));
		list.add(new ContentModel(R.drawable.system_settings,getString(R.string.system_settings)));
	}
	
	private void getViews()
	{
		locateBtn=(ImageButton) findViewById(R.id.local_btn);
		loading_layout=(RelativeLayout) findViewById(R.id.loading_layout);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		imageView = (ImageView) findViewById(R.id.drawer_indicator);
		batteryView=(ImageView) findViewById(R.id.batteryIcon);
		titleTxt = (TextView) findViewById(R.id.title_txt);
		String name=sph.getString("babyName");	
		if(name!=null&&name.length()>0){
			titleTxt.setText(name);
		}else{
			titleTxt.setText("Baby");
		}
		cityTxt = (TextView) findViewById(R.id.city_txt);
		districtTxt = (TextView) findViewById(R.id.district_txt);
		loadTxt=(TextView) findViewById(R.id.load_txt);
		menuBtn = (ImageButton) findViewById(R.id.menu_btn);
		callBtn = (ImageButton) findViewById(R.id.call_btn);
		locateBtn = (ImageButton) findViewById(R.id.local_btn);
		final Resources resources = getResources();
		drawerArrowDrawable = new DrawerArrowDrawable(resources);
		drawerArrowDrawable.setStrokeColor(resources.getColor(android.R.color.white));
		imageView.setImageDrawable(drawerArrowDrawable);
		initLeftLayout();
		setClickListener();
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
	
	private void setClickListener(){
		imageView.setOnClickListener(this);
		menuBtn.setOnClickListener(this);
		titleTxt.setOnClickListener(this);
		callBtn.setOnClickListener(this);
		locateBtn.setOnClickListener(this);
	}
	
	private void initLeftLayout(){
		
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
	
	private void initManager(){
		 //创建LocationManager对象
	    mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    //打开GPS
	    openGPS();
	    Criteria criteria = new Criteria();
	    bestProvider = mLocManager.getBestProvider(criteria, false);
	    mListener=new MyLocationListener();
	    // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N
	    mLocManager.requestLocationUpdates(bestProvider, 2 * 1000, 1,mListener);
	}
	
	private void initProvider() {
	    if(bestProvider!=null){
	    	location = mLocManager.getLastKnownLocation(bestProvider);
	    	
	    }else{
	    	location = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	//location = mLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    }
	    if(location==null){
	    	location=new Location(bestProvider);
	    	location.setLatitude((double)sph.getFloat("latitude"));
	    	location.setLongitude((double)sph.getFloat("longitude"));
	    }
	    while(location==null){
	    	//location = mLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            location = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    }
	   Log.i(TAG,"latitude："+location.getLatitude()+"  longitude：" + location.getLongitude());
	}

	// 判断是否开启GPS，若未开启，打开GPS设置界面
    private void openGPS() {       
        if (mLocManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        ||mLocManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        ) {
            return;
        }
        showToastLong(getString(R.string.open_gps));
        // 转至GPS设置界面
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0);
    }
	private void initGoogleMap(){
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
		mMap=fragment.getMap();
		Log.i(TAG, mMap+"");
		if(mMap!=null){
		  mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		}
		geocoder=new Geocoder(mContext, Locale.getDefault());
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
			battery=trackerInfo.getBattery();
			String[] data = locStr.split(",");
			String[] pos = new String[2];
			for (int i = 0; i < data.length; i++) {
				pos[i] = data[i].split(":")[1];
			}
			
			if(pos[0]!=null){
				longitude = Double.parseDouble(pos[0]);
			}
			if(pos[1]!=null){
				latitude=Double.parseDouble(pos[1]);
			}
		
            if(isLand){
			//convert GPS to Mars
			Site site=Converter.transform2Mars(latitude, longitude);
			sph.putFloat("latitude", (float)site.getLat());
			sph.putFloat("longitude", (float)site.getLon());
			LATLNG_DEFAULT = new LatLng(site.getLat(),site.getLon());
            }else{
            sph.putFloat("latitude", (float)latitude);
    		sph.putFloat("longitude", (float)longitude);
    		LATLNG_DEFAULT=new LatLng(latitude,longitude);
            }
			
			moveToNewLocation(LATLNG_DEFAULT,0);
			new ReverseGeocodingTask().execute(new LatLng(latitude,longitude));
			
		}
	}
	
	public void updateToNewLocation(Location location){
		
		mMap.clear();
		markerOpt = new MarkerOptions();
		if (location != null) {
			LATLNG_DEFAULT=new LatLng(location.getLatitude(),location.getLongitude());
		}
		markerOpt.position(LATLNG_DEFAULT);
		markerOpt.draggable(false);
		markerOpt.visible(true);
		markerOpt.anchor(0.5f, 0.5f);// 设为图片中心
		boolean isBoy=sph.getBoolean("isboy");
		if(isBoy){
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_boy));
		}else{
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_girl));
		}
		//markerOpt.title(title);
		mMap.addMarker(markerOpt);//.showInfoWindow();
		
		// 将摄影机移动到指定的地理位置
		cameraPosition = new CameraPosition.Builder()
				.target(LATLNG_DEFAULT) // Sets the center of the map
				.zoom(17) // 缩放比例
				.bearing(0) // Sets the orientation of the camera to east
				.tilt(30) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void moveToNewLocation(LatLng latlng,double range ){
		mMap.clear();
		markerOpt = new MarkerOptions();
		markerOpt.position(latlng);
		markerOpt.draggable(false);
		markerOpt.visible(true);
		markerOpt.anchor(0.5f, 0.5f);// 设为图片中心
        boolean isBoy=sph.getBoolean("isboy");
		if(isBoy){
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_boy));
		}else{
			markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_girl));
		}
		//markerOpt.title(title);
		mMap.addMarker(markerOpt);//.showInfoWindow();
		
		if(range>0){
		    circleOptions = new CircleOptions()
	        .center(LATLNG_DEFAULT)
	        .radius(range)
	        .strokeWidth(1)
	        .strokeColor(Color.parseColor("#8DEEEE"))
	        .fillColor(Color.parseColor("#5036B9AF"));
			mMap.addCircle(circleOptions);
		}
		// 将摄影机移动到指定的地理位置
		cameraPosition = new CameraPosition.Builder()
				.target(LATLNG_DEFAULT) // Sets the center of the map
				.zoom(17) // 缩放比例
				.bearing(0) // Sets the orientation of the camera to east
				.tilt(30) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
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
			break;
		case R.id.call_btn:
			startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+sph.getString("info_devSim"))));
			break;
		case R.id.local_btn:
			loading_layout.setVisibility(View.VISIBLE);
			loadTxt.setText("Starting...");
			timer.start();
			mHandler.sendEmptyMessage(MSG_LOC);
			
			
			
			break;
		default:
			break;
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
				Message.obtain(mHandler, MSG_SHOW_ADDRESS, "Warning,No address found !").sendToTarget();
			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				String addressText;

			    addressText = String.format("%s, %s%s%s%s",address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getAdminArea()!=null?address.getAdminArea():"", address.getLocality()!=null?address.getLocality():"", address.getThoroughfare()!=null?address.getThoroughfare():"", address.getFeatureName()!=null?address.getFeatureName():"");

				// Update the UI via a message handler.
				Message.obtain(mHandler, MSG_SHOW_ADDRESS, addressText).sendToTarget();
			}else{
				Message.obtain(mHandler, MSG_SHOW_ADDRESS, "Warning,No address found !").sendToTarget();
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
							
							if(lon!=null){
								longitude=Double.parseDouble(lon);
							}
							if(lat!=null){
								latitude=Double.parseDouble(lat);
							}
							if (isLand) {
								// convert GPS to Mars
								Site site = Converter.transform2Mars(latitude,
										longitude);
								sph.putFloat("latitude", (float) site.getLat());
								sph.putFloat("longitude", (float) site.getLon());
								LATLNG_DEFAULT = new LatLng(site.getLat(),site.getLon());
							} else {
								sph.putFloat("latitude", (float) latitude);
								sph.putFloat("longitude", (float) longitude);
								LATLNG_DEFAULT = new LatLng(latitude, longitude);
							}
                            moveToNewLocation(LATLNG_DEFAULT,Double.valueOf(range));
                            new ReverseGeocodingTask().execute(new LatLng(latitude,longitude));
						}
						if (getString(R.string.loc_success).equals(response.getMsg())) {
							String loc = response.getLoc();
							if (loc != null && loc.length()!= 0) {
								String[] data = loc.split(",");
								String[] pos = new String[2];
								for (int i = 0; i < data.length; i++) {
									pos[i] = data[i].split(":")[1];
								}
								String lon = pos[0];
								String lat = pos[1];
								if (lon != null) {
									longitude = Double.parseDouble(lon);
								}
								if (lat != null) {
									latitude = Double.parseDouble(lat);
								}
							}
							if (isLand) {
								// convert GPS to Mars
								Site site = Converter.transform2Mars(latitude,
										longitude);
								sph.putFloat("latitude", (float) site.getLat());
								sph.putFloat("longitude", (float) site.getLon());
								LATLNG_DEFAULT = new LatLng(site.getLat(),site.getLon());
							} else {
								sph.putFloat("latitude", (float) latitude);
								sph.putFloat("longitude", (float) longitude);
								LATLNG_DEFAULT = new LatLng(latitude, longitude);
							}
							moveToNewLocation(LATLNG_DEFAULT,0);
							new ReverseGeocodingTask().execute(new LatLng(latitude,longitude));
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
							String range="0";
							if (loc != null && loc.length() != 0) {
								String[] data = loc.split(",");
								String[] pos = new String[3];
								for (int i = 0; i < data.length; i++) {
									pos[i] = data[i].split(":")[1];
								}
								String lon = pos[0];
								String lat = pos[1];
								range = pos[2];
								if (lon != null) {
									longitude = Double.parseDouble(lon);
								}
								if (lat != null) {
									latitude = Double.parseDouble(lat);
								}
							}
							
							if (isLand) {
								// convert GPS to Mars
								Site site = Converter.transform2Mars(latitude,
										longitude);
								sph.putFloat("latitude", (float) site.getLat());
								sph.putFloat("longitude", (float) site.getLon());
								LATLNG_DEFAULT = new LatLng(site.getLat(),site.getLon());
							} else {
								sph.putFloat("latitude", (float) latitude);
								sph.putFloat("longitude", (float) longitude);
								LATLNG_DEFAULT = new LatLng(latitude, longitude);
							}
                            moveToNewLocation(LATLNG_DEFAULT,Double.valueOf(range));
                            new ReverseGeocodingTask().execute(new LatLng(latitude,longitude));                     
                            bizSn=response.getBizSN();
                            battery=response.getBattery();
                            loadTxt.setText("Locating...");
                            mHandler.sendEmptyMessageDelayed(MSG_LOCRESULT, 5000);
							
						}
						if (getString(R.string.loc_ack_data).equals(response.getMsg())) {
							battery=response.getBattery();
    						latitude=response.getLat();
							longitude=response.getLon();
							
							if (isLand) {
								// convert GPS to Mars
								Site site = Converter.transform2Mars(latitude,longitude);
								sph.putFloat("latitude", (float) site.getLat());
								sph.putFloat("longitude", (float) site.getLon());
								LATLNG_DEFAULT = new LatLng(site.getLat(),site.getLon());
							} else {
								sph.putFloat("latitude", (float) latitude);
								sph.putFloat("longitude", (float) longitude);
								LATLNG_DEFAULT = new LatLng(latitude, longitude);
							}
							moveToNewLocation(LATLNG_DEFAULT,0);
							loading_layout.setVisibility(View.GONE);
							locateBtn.setClickable(true);
							locateBtn.setEnabled(true);
							new ReverseGeocodingTask().execute(new LatLng(latitude,longitude));
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
						
						sph.putString("info_devSim", response.getDevSIM());
						sph.putString("info_spn2",response.getSpn2());
						sph.putString("info_version", response.getDevVersion());
						sph.putString("info_startTime", response.getCurfewBeginTime());
						sph.putString("info_endTime", response.getCurfewEndTime());
						sph.putString("info_status",response.getCurfewStatus());
						
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
	
	private class MyLocationListener implements LocationListener{

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 当GPS LocationProvider可用时，更新位置
			location = mLocManager.getLastKnownLocation(provider);
		}
		@Override
		public void onProviderDisabled(String provider) {
			updateToNewLocation(null);
		}
		@Override
		public void onLocationChanged(Location location) {
			// 当GPS定位信息发生改变时，更新位置
			//updateToNewLocation(location);
			
		}
	}
	
	
	private void showBattery(String battery){
		if (battery == null) {
			batteryView.setImageResource(R.drawable.watcher0);
		} else {
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
	
	
	public void showNotifyDialog(Context context, String str) {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.custom_toast_layout, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.toastTxt);
		if (str != null) {
			// 设置加载信息e
			tipTextView.setText(str);
		}

		// 创建自定义样式dialog
		Dialog _dialog = new Dialog(context, R.style.CustomProgressDialog);
		_dialog.setCancelable(true);
		LinearLayout.LayoutParams params = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
		_dialog.onBackPressed();
		_dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
					}
				}
				return false;
			}
		});
		mDialog = _dialog;
		mDialog.setContentView(v);

		mDialog.show();
		Message msg = new Message();
		msg.what = MSG_DIALOG_STATE;
		mHandler.sendMessageDelayed(msg, 3000);
	}
	
	public void execute(Context context,String string){
		if(string.equals(getString(R.string.status_error))){
		 showToastShort(getString(R.string.account_login));
		 startActivity(new Intent(context,LoginActivity.class));	
		}/*else{
			showToastShort(string);
		}*/
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
	
	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			locateBtn.setClickable(true);
			locateBtn.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			locateBtn.setClickable(false);
			locateBtn.setEnabled(false);
		}
	}
	
	
	@Override
	protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}

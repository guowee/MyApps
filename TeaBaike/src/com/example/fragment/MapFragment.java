package com.example.fragment;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.example.teabaike.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {

	private MapView mapView;

	private BaiduMap mBaidumap;// 地图对应的类

	private GeoCoder gc;// 地理编码的核心类

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getActivity().getApplicationContext());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_map, null);

		mapView = (MapView) view.findViewById(R.id.bmapView);

		mBaidumap = mapView.getMap();

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mapView.onPause();
	}

}

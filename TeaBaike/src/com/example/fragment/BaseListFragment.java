package com.example.fragment;



import com.example.teabaike.ContentActivity;
import com.example.teabaike.R;
import com.example.widget.XListView;
import com.example.widget.XListView.IXListViewListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("InflateParams")
public abstract class BaseListFragment extends Fragment implements
		IXListViewListener {

	/** 自定义listView */
	protected XListView listview;

	/** 用来填充整个listview布局 */
	protected View view;
	/** inflater填充布局器 */

	LayoutInflater mInflater;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mInflater = inflater;

		view = inflater.inflate(R.layout.fragment_content, null);

		listview = (XListView) view.findViewById(R.id.listView_contentfragment);

		listview.setPullLoadEnable(true);

		listview.setPullRefreshEnable(false);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 跳转到ContentActivity页面
	 * 
	 * @param idStr
	 */

	public void SkipContentActivity(String idStr) {

		Intent intent = new Intent(getActivity(), ContentActivity.class);

		intent.putExtra("id", idStr);

		startActivity(intent);
	}

}

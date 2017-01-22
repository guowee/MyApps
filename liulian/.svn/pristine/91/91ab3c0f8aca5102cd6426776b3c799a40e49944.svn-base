package com.haomee.liulian;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haomee.adapter.ChestAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Chest;
import com.haomee.entity.ShareContent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

// 宝箱

public class ChestActivity extends BaseActivity {

	private ListView listview;
	private TextView chest_count;
	private List<Chest> list_chest;
	private ChestAdapter chestAdapter;
	private ImageView tv_back;
	private String uid;
	private TextView title;
	private ImageView iv_chest_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chest);
		
		list_chest = new ArrayList<Chest>();
		chestAdapter = new ChestAdapter(this);

		uid = getIntent().getStringExtra("uid");

		listview = (ListView) findViewById(R.id.listview);
		tv_back = (ImageView) findViewById(R.id.tv_back);
		title = (TextView) findViewById(R.id.title);
		
		
		View item_head = this.getLayoutInflater().inflate(R.layout.head_chest, null);
		chest_count = (TextView) item_head.findViewById(R.id.chest_count);
		iv_chest_text = (ImageView) item_head.findViewById(R.id.iv_chest_text);
		
		if(LiuLianApplication.current_user.getUid().equals(uid)){
			title.setText("我的宝箱");
			iv_chest_text.setBackgroundResource(R.drawable.chest_text);			
		}else{
			title.setText("TA的宝箱");
			iv_chest_text.setBackgroundResource(R.drawable.chest_text_ta);	
		}
		listview.addHeaderView(item_head);
		initData();
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				
				if(position!=0){
					Intent intent_send = new Intent();
					intent_send.setClass(ChestActivity.this, ShareMedalActivity.class);
					ShareContent share = new ShareContent();
					share.setId(list_chest.get(position-1).getId());
					share.setTitle(list_chest.get(position-1).getName());
					share.setSummary(list_chest.get(position-1).getDesc());
					share.setImg_url(list_chest.get(position-1).getIcon());
					share.setRedirect_url(CommonConst.GOV_URL);
					intent_send.putExtra("share", share);
					ChestActivity.this.startActivity(intent_send);
				}
			}
		});

	}

	public void initData() {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", uid);
		asyncHttp.get(PathConst.URL_GET_MEDAL, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					JSONArray json_array = new JSONArray(json.getString("list"));
					if (null != json_array) {
						for (int i = 0; i < json_array.length(); i++) {
							JSONObject json_object = (JSONObject) json_array.get(i);
							Chest chest = new Chest();
							chest.setId(json_object.optString("id"));
							chest.setIcon(json_object.optString("icon"));
							chest.setName(json_object.optString("name"));
							chest.setDesc(json_object.optString("desc"));
							chest.setTime(json_object.optString("time"));
							chest.setYear(json_object.optString("year"));
							chest.setDay(json_object.optString("day"));
							list_chest.add(chest);
						}
						chest_count.setText(list_chest.size()+"");
						chestAdapter.setData(list_chest);
						listview.setAdapter(chestAdapter);
					}
					if (json.has("egg")) {
						JSONArray json_arr = json.getJSONArray("egg");
						JSONObject egg_obj = json_arr.getJSONObject(0);
						Intent intent_send = new Intent();
						intent_send.setClass(ChestActivity.this, ShareMedalActivity.class);
						ShareContent share = new ShareContent();
						share.setId(egg_obj.getString("id"));
						share.setTitle(egg_obj.getString("name"));
						share.setSummary(egg_obj.getString("desc"));
						share.setImg_url(egg_obj.getString("icon"));
						share.setRedirect_url(CommonConst.GOV_URL);
						intent_send.putExtra("share", share);
						ChestActivity.this.startActivity(intent_send);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

}

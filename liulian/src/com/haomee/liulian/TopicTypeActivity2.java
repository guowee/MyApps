package com.haomee.liulian;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.haomee.adapter.TopicTypeSearchAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.TopicType_Search_Interest;
import com.haomee.entity.TopicType_Search_User;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.haomee.view.UnScrollableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TopicTypeActivity2 extends BaseActivity {
	private TextView tv_search,tv_back;
	private EditText et_search_content;
	private ListView lv_search_list;
	private LinearLayout ll_search_list,ll_yonghu,ll_topic;
	private RelativeLayout rl_no_cotent;
	private ScrollView  scrol_search_list_content;
	private UnScrollableListView unscroll_list_yonghu,unscroll_list_topic;
	private InputMethodManager imm;

	private LoadingDialog loadingDialog;
	private Activity activity_context;
	private List<String> hot_list;
	private List<String> interest_list;
	private List<TopicType_Search_User> search_user_list;
	private List<TopicType_Search_Interest> search_interest_list;

	private TopicTypeSearchAdapter user_adapter;
	private ArrayAdapter<String> adapter_topic;
	private boolean isINVISIBLE=false;//标示热门搜索词是否显示,默认false显示
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_type2);
		loadingDialog = new LoadingDialog(this);
		activity_context=TopicTypeActivity2.this;
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}
	private void initView() {
		rl_no_cotent=(RelativeLayout) findViewById(R.id.rl_search_null);
		ll_yonghu=(LinearLayout) findViewById(R.id.ll_yonghu);
		ll_topic=(LinearLayout) findViewById(R.id.ll_topic);
		tv_search=(TextView) findViewById(R.id.bt_search);
		et_search_content=(EditText) findViewById(R.id.et_search);
		lv_search_list=(ListView) findViewById(R.id.lv_search_list);
		lv_search_list.setDivider(null);
		lv_search_list.setCacheColorHint(0);
		ll_search_list=(LinearLayout) findViewById(R.id.ll_search_content);
		scrol_search_list_content=(ScrollView) findViewById(R.id.scroll_search_content);

		unscroll_list_yonghu=(UnScrollableListView) findViewById(R.id.listview_search_yonghu);
		user_adapter=new TopicTypeSearchAdapter(activity_context);
		unscroll_list_yonghu.setAdapter(user_adapter);

		unscroll_list_topic=(UnScrollableListView) findViewById(R.id.listview_search_topic);

		unscroll_list_yonghu.setOnItemClickListener(myItemClickListener);
		unscroll_list_topic.setOnItemClickListener(myItemClickListener);
		lv_search_list.setOnItemClickListener(myItemClickListener);
		loadingDialog.show();

		/**
		 * 通过焦点的来判断hiht的现实
		 */
		et_search_content.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String hint = "";
				if (!hasFocus) {//失去焦点时现实提示信息
					hint = et_search_content.getTag().toString();
					et_search_content.setHint(hint);
				} else {//获得焦点时提示信息清空
					et_search_content.setFocusable(true);
					hint = et_search_content.getHint().toString();
					et_search_content.setTag(hint);
					et_search_content.setHint("");

				}				
			}
		});
		//处理返回键
		tv_back=(TextView) findViewById(R.id.tv_search_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imm.isActive(et_search_content)) {
					imm.hideSoftInputFromWindow(et_search_content.getWindowToken(), 0);
					et_search_content.clearFocus();
					return;
				}else if(isINVISIBLE){
					ll_search_list.setVisibility(View.VISIBLE);
					scrol_search_list_content.setVisibility(View.GONE);
					rl_no_cotent.setVisibility(View.GONE);
					isINVISIBLE=false;
				}
				else {
					finish();
				}
			}
		});
		/**
		 * 搜索点击
		 */
		tv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name=et_search_content.getText().toString();
				if (name.equals("")) {
					MyToast.makeText(activity_context, "请输入搜索词！", Toast.LENGTH_SHORT).show();
					return;
				}
				getSearchResult(name);
			}
		});
		getHotSearch();//获取热门搜索词
	}

	private void getHotSearch(){
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return;
		}
		String url = PathConst.URL_TOPIC_TYPE_HOTSEARCH;
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					if(arg0==null||arg0.length()==0){
						loadingDialog.dismiss();
						return ;
					}
					JSONArray array=new JSONArray(arg0);
					if(array==null||array.length()==0){
						loadingDialog.dismiss();
						return;
					}
					hot_list=new ArrayList<String>();
					for(int i=0;i<array.length();i++){
						hot_list.add(array.getString(i));
					}
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(activity_context,R.layout.search_list , hot_list);
					lv_search_list.setAdapter(adapter);
					loadingDialog.dismiss();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private OnItemClickListener myItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent=new Intent();
			switch (parent.getId()) {
			case R.id.lv_search_list://热门词点击搜索
				getSearchResult(hot_list.get(position));
				break;
			case R.id.listview_search_yonghu://用户
				intent.setClass(activity_context, UserInfoDetail.class);
				intent.putExtra("uid", search_user_list.get(position).getUid());
				activity_context.startActivity(intent);
				break;
			case R.id.listview_search_topic://话题
				intent.setClass(activity_context, TopicDetailActivity.class);
				intent.putExtra("topic_id", search_interest_list.get(position).getId());
				startActivity(intent);
				break;
			}
		}
	};
	/**
	 * 搜索,热门话题
	 */
	private void getSearchResult(String name){
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return;
		}
		String url = PathConst.URL_TOPIC_TYPE_SEARCH;
		RequestParams rp=new RequestParams();
		rp.put("name", name);
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url,rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					if(arg0==null||arg0.length()==0){
						return ;
					}
					JSONObject json=new JSONObject(arg0);
					if(json==null||"".equals(json)){
						return;//防止网络连接超时出现空指针异常
					}
					if("0".equals(json.getString("flag"))){

						ll_search_list.setVisibility(View.GONE);//隐藏热门搜索词
						Toast.makeText(activity_context, json.getString("msg"), Toast.LENGTH_SHORT).show();
					}else if("1".equals(json.getString("flag"))){
						
						rl_no_cotent.setVisibility(View.GONE);
						ll_search_list.setVisibility(View.GONE);//隐藏热门搜索词
						
						scrol_search_list_content.setVisibility(View.VISIBLE);
						unscroll_list_yonghu.setVisibility(View.VISIBLE);
						JSONArray array_user=json.getJSONArray("user");
						if(array_user.length()!=0){
							search_user_list=new ArrayList<TopicType_Search_User>();
							for(int i=0;i<array_user.length();i++){
								JSONObject obj=array_user.getJSONObject(i);
								TopicType_Search_User user=new TopicType_Search_User();
								user.setUid(obj.getString("uid"));
								user.setUsername(obj.getString("username"));
								user.setHead_pic(obj.getString("head_pic"));
								user.setSex(obj.getString("sex"));
								user.setAge(obj.getInt("age"));
								user.setStart(obj.getString("star"));
								user.setBirthday(obj.getString("birthday"));
								user.setHx_username(obj.getString("hx_username"));
								user.setSignature(obj.getString("signature"));
								user.setCity(obj.getString("city"));
								user.setBack_pic(obj.getString("back_pic"));
								user.setIs_sayHi(obj.getBoolean("is_sayHi"));
								user.setTime(obj.getString("time"));
								user.setIs_online(obj.getBoolean("is_online"));
								search_user_list.add(user);
							}
							ll_yonghu.setVisibility(View.VISIBLE);
							user_adapter.setData(search_user_list);
						}else if(array_user.length()==0){
							unscroll_list_yonghu.setVisibility(View.GONE);
							ll_yonghu.setVisibility(View.GONE);
						}
						JSONArray array_interest=json.getJSONArray("interest");
						interest_list=new ArrayList<String>();
						adapter_topic=new ArrayAdapter<String>(activity_context,R.layout.search_interest_list , interest_list);
						unscroll_list_topic.setAdapter(adapter_topic);
						if(array_interest.length()!=0){
							search_interest_list=new ArrayList<TopicType_Search_Interest>();
							for(int i=0;i<array_interest.length();i++){
								JSONObject obj=array_interest.getJSONObject(i);
								TopicType_Search_Interest inter=new TopicType_Search_Interest();
								inter.setId(obj.getString("id"));
								String title=obj.getString("title");
								inter.setTitle(title);
								interest_list.add(title);
								search_interest_list.add(inter);
							}
							ll_topic.setVisibility(View.VISIBLE);
							adapter_topic.notifyDataSetChanged();

						}else if(array_interest.length()==0){
							interest_list.clear();
							adapter_topic.notifyDataSetChanged();
							ll_topic.setVisibility(View.GONE);
						}
						
						if(array_user.length()==0&&array_interest.length()==0){//都为空的时候显示提示信息
							ll_search_list.setVisibility(View.GONE);
							rl_no_cotent.setVisibility(View.VISIBLE);
						}
					}
					isINVISIBLE=true;
					loadingDialog.dismiss();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 处理按键返回键
	 */
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        	if (imm.isActive(et_search_content)) {//判断软键盘是否显示
					imm.hideSoftInputFromWindow(et_search_content.getWindowToken(), 0);
					et_search_content.clearFocus();
				}else if(isINVISIBLE){
					ll_search_list.setVisibility(View.VISIBLE);
					scrol_search_list_content.setVisibility(View.GONE);
					rl_no_cotent.setVisibility(View.GONE);
					isINVISIBLE=false;
				}
				else {
					finish();
				}
	        }  
	          
	        return false;  
	          
	    }
}

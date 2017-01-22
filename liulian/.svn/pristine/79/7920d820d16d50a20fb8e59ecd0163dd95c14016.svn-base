package com.haomee.liulian;

import java.util.List;
import java.util.Random;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.TextItem;
import com.haomee.entity.UserTextList;
import com.haomee.entity.Users;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TestActivity extends BaseActivity {
	private LinearLayout ll_left,ll_right;
	private TextView text_left,text_right,current_finish;
	private ImageView iv_back;
	private Activity activity_context;
	private UserTextList user_text_info;
	private List<TextItem> text_list;
	private int current_text_page=0;//当前测试页面
	private LayoutInflater inflater;
	private View view;

	private LinearLayout ll_content;
	private android.widget.LinearLayout.LayoutParams layoutParams; 
	private int screen_width;//屏幕的宽度
	private TranslateAnimation transAnimation_out;//平移动画
	private TranslateAnimation transAnimation_in;
	private ScaleAnimation scaleAnimation_left;
	private ScaleAnimation scaleAnimation_right;

	private Intent intent;
	private int total_page=0;
	private int current_page=0;

	private int LEFT_CHOSE=1;//选择左边
	private int RIGHT_CHOSE=2;//选择右边
	private TextItem item;
	private ViewGroup.LayoutParams params_left;
	private ViewGroup.LayoutParams params_right;
	private LoadingDialog loadingDialog;
	private Random rdm;
	private String Luid;
	private boolean is_no_first=false;//第一次加载

	private int TEST_FLAG=0;//设置标识位,0是自己测试,1是匹配测试
	private int test_answer=0;//匹配测试时统计匹配的数量
	private Users user = null;

	private View left_view,left_equal_view,left_small,left_equal,left_big;
	private TextView left_person_number_left;
	private View right_view,right_equal_view,right_small,right_equal,right_big;
	private TextView right_person_number_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_layout);
		activity_context=TestActivity.this;
		loadingDialog = new LoadingDialog(this);
		loadingDialog.setFlag(true);
		intent=getIntent();
		rdm = new Random();
		Luid=LiuLianApplication.current_user.getUid();
		user_text_info=(UserTextList) intent.getSerializableExtra("user_text_info");
		TEST_FLAG=intent.getIntExtra("test_flag", 0);
		if(0!=TEST_FLAG){//匹配测试
			user=(Users) intent.getSerializableExtra("user");
		}
		text_list=user_text_info.getList();

		total_page=Integer.parseInt(intent.getStringExtra("total"));
		current_page=total_page-text_list.size()+1;

		inflater=LayoutInflater.from(this);
		ll_content=(LinearLayout) findViewById(R.id.ll_content);
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		screen_width=ViewUtil.getScreenWidth(activity_context);
		transAnimation_out = new TranslateAnimation(0, 0, 0, -1920);
		transAnimation_out.setDuration(500);
		//		transAnimation.setStartOffset(1000);
		transAnimation_in=new TranslateAnimation(0, 0,  1000, 0);
		transAnimation_in.setDuration(500);

		scaleAnimation_left =new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		scaleAnimation_left.setDuration(500);//设置动画播放时间 毫秒单位  
		scaleAnimation_left.setFillAfter(true); 

		scaleAnimation_right=new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		scaleAnimation_right.setDuration(500);//设置动画播放时间 毫秒单位  
		scaleAnimation_right.setFillAfter(true); 

		iv_back=(ImageView) findViewById(R.id.bt_back);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadingDialog.setFlag(false);
				finish();
				Intent intent = new Intent("MyReceiver_Action");
				activity_context.sendBroadcast(intent);
			}
		});
		current_finish=(TextView) findViewById(R.id.current_finish);

		initView();

		ll_left.setClickable(true);
		ll_right.setClickable(true);

		//设置进入动画的监听
		transAnimation_in.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				ll_left.setClickable(true);
				ll_right.setClickable(true);
			}
		});
	}
	private void initView() {
		view = inflater.inflate(R.layout.activity_user_text, null);
		if(is_no_first){//如果不是第一次加载就执行该动画
			view.startAnimation(transAnimation_in);
		}
		ll_content.addView(view, layoutParams);


		ll_left=(LinearLayout) view.findViewById(R.id.ll_left);
		ll_right=(LinearLayout) view.findViewById(R.id.ll_right);

		left_equal_view=view.findViewById(R.id.left_view_equal);
		left_view=view.findViewById(R.id.left_view);
		left_small=view.findViewById(R.id.left_small);
		left_equal=view.findViewById(R.id.left_equal);
		left_big=view.findViewById(R.id.left_big);
		left_person_number_left=(TextView) view.findViewById(R.id.left_person_number);

		right_equal_view=view.findViewById(R.id.right_equal_view);
		right_view=view.findViewById(R.id.right_view);
		right_small=view.findViewById(R.id.right_small);
		right_equal=view.findViewById(R.id.right_equal);
		right_big=view.findViewById(R.id.right_big);
		right_person_number_right=(TextView) view.findViewById(R.id.right_person_number);

		int int_point = rdm.nextInt(CommonConst.test_colors.length);

		ll_left.setBackgroundColor(CommonConst.test_colors[int_point][0]);
		ll_right.setBackgroundColor(CommonConst.test_colors[int_point][1]);

		ll_left.setOnClickListener(clickListener);
		ll_right.setOnClickListener(clickListener);

		text_left=(TextView) view.findViewById(R.id.text_left);
		text_right=(TextView) view.findViewById(R.id.text_right);


		current_finish.setText(""+current_page+"/"+total_page);

		item = text_list.get(current_text_page);

		left_person_number_left.setText(item.getLeft_num());
		right_person_number_right.setText(item.getRight_num());

		text_left.setText(item.getLeft_title());
		text_right.setText(item.getRight_title());




	}

	OnClickListener clickListener=new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_left://左侧
				ll_left.setClickable(false);
				ll_right.setClickable(false);
				if(0==TEST_FLAG){//自己测试
					comit_text_result(LEFT_CHOSE);
				}else {//匹配测试
					show_next_text(LEFT_CHOSE, item);
				}

				break;
			case R.id.ll_right://右侧
				ll_left.setClickable(false);
				ll_right.setClickable(false);
				if(0==TEST_FLAG){//自己测试
					comit_text_result(RIGHT_CHOSE);
				}else {
					show_next_text(LEFT_CHOSE,item);
				}

				break;
			}

		}
	};

	/**
	 * 提交答题结果
	 */
	private void comit_text_result(final int tag) {
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return ;
		}
		String url = PathConst.URL_QUESTION_ANSWER;
		RequestParams rp=new RequestParams();
		final TextItem item = text_list.get(current_text_page);
		rp.put("qid", item.getId());
		if(tag==1){//选择了左边
			rp.put("item_id", item.getLeft_id());
		}else if(tag==2){//选择了右边
			rp.put("item_id", item.getRight_id());
		}
		if(Luid!=null){
			rp.put("Luid",Luid);
		}
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url,rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				try {
					if(arg0==null||arg0.length()==0){
						loadingDialog.dismiss();
						return ;
					}
					JSONObject json = new JSONObject(arg0);
					if(json==null||"".equals(json)){
						loadingDialog.dismiss();
						return ;//防止网络连接超时出现空指针异常
					}
					if(1==json.optInt("flag")){//答题成功跳到下一题
						loadingDialog.dismiss();
						show_next_text(tag,item);
					}else if(0==json.optInt("flag")){
						loadingDialog.dismiss();
						show_next_text(tag,item);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					loadingDialog.dismiss();
					e.printStackTrace();
				}
			}

		});
	}
	/**
	 * 显示选后结果
	 */
	private void show_next_text(final int tag,final TextItem item) {

		final int left_peolpe_number=Integer.parseInt(item.getLeft_num());
		final int right_peolpe_number=Integer.parseInt(item.getRight_num());

		params_left = left_person_number_left.getLayoutParams();
		params_right = right_person_number_right.getLayoutParams();

		int left_weight=0;
		int right_weight=0;
		if(left_peolpe_number>right_peolpe_number){//左边人数多
			left_weight=2;
			right_weight=3;
			left_big.setVisibility(View.VISIBLE);
			right_small.setVisibility(View.VISIBLE);


			params_left.width=screen_width/left_weight+screen_width/left_weight/6;
			params_left.height=screen_width/left_weight+screen_width/left_weight/6;
			params_right.width=screen_width/right_weight+screen_width/right_weight/4;
			params_right.height=screen_width/right_weight+screen_width/right_weight/4;
			left_view.setVisibility(View.GONE);
			right_view.setVisibility(View.VISIBLE);
		}else if(left_peolpe_number<right_peolpe_number){//右边人数多
			left_weight=3;
			right_weight=2;

			left_view.setVisibility(View.VISIBLE);
			left_small.setVisibility(View.VISIBLE);
			right_big.setVisibility(View.VISIBLE);

			params_left.width=screen_width/left_weight+screen_width/left_weight/4;
			params_left.height=screen_width/left_weight+screen_width/left_weight/4;
			params_right.width=screen_width/right_weight+screen_width/right_weight/6;
			params_right.height=screen_width/right_weight+screen_width/right_weight/6;

		}else {//相同
			left_weight=2;
			right_weight=2;

			left_equal.setVisibility(View.VISIBLE);
			right_equal.setVisibility(View.VISIBLE);

			params_left.width=screen_width/left_weight;
			params_left.height=screen_width/left_weight;
			params_right.width=screen_width/right_weight;
			params_right.height=screen_width/right_weight;

			left_equal_view.setVisibility(View.VISIBLE);
			right_equal_view.setVisibility(View.VISIBLE);
		}

		left_person_number_left.setLayoutParams(params_left);
		right_person_number_right.setLayoutParams(params_right);

		left_person_number_left.startAnimation(scaleAnimation_left);
		right_person_number_right.startAnimation(scaleAnimation_right);

		left_person_number_left.setVisibility(View.VISIBLE);
		right_person_number_right.setVisibility(View.VISIBLE);
		scaleAnimation_left.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if(1==tag){//左边
					left_person_number_left.setText(""+(left_peolpe_number+1));
					if(item.getLeft_id().equals(item.getAnwser())){
						test_answer++;//统计匹配的数量
					}
				}else if(2==tag){//右边
					right_person_number_right.setText(""+(right_peolpe_number+1));
					if(item.getRight_id().equals(item.getAnwser())){
						test_answer++;//统计匹配的数量
					}
				}
				if(current_page<total_page){
					view.startAnimation(transAnimation_out);
					current_page++;
					current_text_page++;
					ll_content.removeView(view);
					is_no_first=true;
					initView();
				}else if(0==TEST_FLAG) {//问题答完显示结果
					//					showDialog();
					Intent intent=new Intent();
					intent.setClass(activity_context, TestResultActivity.class);
					intent.putExtra("Luid", Luid);
					activity_context.startActivity(intent);
					//回调刷新
					Intent intent_action = new Intent("MyReceiver_Action");
					activity_context.sendBroadcast(intent_action);
					finish();
				}else {//显示匹配情况
					check_test_result();
				}

			}
		});

	}

	/**
	 * 查看匹配情况
	 */
	private void check_test_result(){
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return ;
		}
		String url = PathConst.URL_ANSWER_USER_TEST;
		RequestParams rp=new RequestParams();
		if(Luid!=null){
			rp.put("Luid",Luid);
		}
		if(user!=null){
			rp.put("uid", user.getUid());
		}
		rp.put("num", ""+test_answer);
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, rp, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				try {
					if(arg0==null||arg0.length()==0){
						loadingDialog.dismiss();
						return ;
					}
					JSONObject json = new JSONObject(arg0);
					if(json==null||"".equals(json)){
						loadingDialog.dismiss();
						return ;//防止网络连接超时出现空指针异常
					}
					Intent intent=new Intent();
					intent.setClass(activity_context, DialogAcitvity.class);
					intent.putExtra("flag", json.optInt("flag"));
					intent.putExtra("per", json.optInt("per"));
					intent.putExtra("user", user);
					activity_context.startActivity(intent);
					activity_context.finish();
					loadingDialog.dismiss();
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 答题结果
	 */
	private void showDialog() {
		final Intent intent_tiao=new Intent();
		View view=inflater.inflate(R.layout.dialog_text_result, null);
		final TextView total_peopel;
		final TextView current_level, next_level_day;
		final CircleImageView imag_icon;

		total_peopel=(TextView) view.findViewById(R.id.total_person);
		current_level=(TextView) view.findViewById(R.id.current_level);
		next_level_day=(TextView) view.findViewById(R.id.next_level_day);
		imag_icon=(CircleImageView) view.findViewById(R.id.img_icon);

		final AlertDialog dialog;
		dialog = new AlertDialog.Builder(activity_context).show(); 
		dialog.setContentView(view);//对比
		view.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadingDialog.setFlag(false);
				dialog.dismiss();

				Intent intent = new Intent("MyReceiver_Action");
				// 可通过Intent携带消息
				activity_context.sendBroadcast(intent);
				finish();
			}
		});
		view.findViewById(R.id.linearLayout_text_chakan).setOnClickListener(new OnClickListener() {//查看全部

			@Override
			public void onClick(View v) {
				loadingDialog.setFlag(false);
				dialog.dismiss();
			}
		});
		view.findViewById(R.id.linearLayout_text_huigu).setOnClickListener(new OnClickListener() {//历史回顾

			@Override
			public void onClick(View v) {
				loadingDialog.setFlag(false);
				dialog.dismiss();
				intent_tiao.setClass(activity_context, TestHuiGuActivity.class);
				activity_context.startActivity(intent_tiao);
				activity_context.finish();
			}
		});
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return ;
		}
		String url = PathConst.URL_QUESTION_RESULT;
		RequestParams rp=new RequestParams();
		if(Luid!=null){
			rp.put("Luid",Luid);
		}
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url,rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				try {
					if(arg0==null||arg0.length()==0){
						loadingDialog.dismiss();
						return ;
					}
					JSONObject json = new JSONObject(arg0);
					if(json==null||"".equals(json)){
						loadingDialog.dismiss();
						return ;//防止网络连接超时出现空指针异常
					}
					total_peopel.setText("人数:"+json.optString("num"));
					JSONObject user = json.getJSONObject("user");
					if(user!=null){
						current_level.setText("Level "+user.optString("user_level"));
						next_level_day.setText("距离下次升级还有"+user.optString("left_days")+"天");
						ImageLoaderCharles.getInstance(TestActivity.this).addTask(user.optString("head_pic"),imag_icon);
					}
					loadingDialog.dismiss();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	/*
	 * 处理返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent("MyReceiver_Action");
			// 可通过Intent携带消息
			activity_context.sendBroadcast(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
